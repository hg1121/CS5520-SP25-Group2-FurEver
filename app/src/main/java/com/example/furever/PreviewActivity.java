package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PreviewActivity extends AppCompatActivity {

    private TextView tvPreferencesSummary;
    private ProgressBar progressBar;
    private Button btnConfirm;

    private DogPreference dogPreference; // 用户偏好对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // 绑定视图 / Bind views
        tvPreferencesSummary = findViewById(R.id.tv_preferences_summary);
        progressBar = findViewById(R.id.progress_bar);
        btnConfirm = findViewById(R.id.btn_confirm);

        // 获取从 Q6Fragment 传来的偏好对象 / Receive DogPreference from previous activity
        dogPreference = (DogPreference) getIntent().getSerializableExtra("dog_pref");
        Log.d("PreviewActivity", "exercise = " + dogPreference.exercise);
        Log.d("PreviewActivity", "homeType = " + dogPreference.homeType);

        // 构造偏好总结内容 / Build preview summary
        String summary = buildPreferenceSummary(dogPreference);
        tvPreferencesSummary.setText(summary);

        // 点击确认：显示加载动画，跳转到结果页 / Confirm button behavior
//        btnConfirm.setOnClickListener(v -> {
//            progressBar.setVisibility(View.VISIBLE);
//            btnConfirm.setEnabled(false);
//
//            v.postDelayed(() -> {
//                // 启动结果页并传递偏好数据 / Pass preference to ResultsActivity
//                Intent intent = new Intent(PreviewActivity.this, ResultsActivity.class);
//                intent.putExtra("dog_pref", dogPreference);
//                startActivity(intent);
//                finish();
//            }, 1500);
//        });

        btnConfirm.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            btnConfirm.setEnabled(false);
            
            // Then call OpenAI
            String prompt = buildPrompt(dogPreference);
            callOpenAI(prompt);
        });
    }

    private void callOpenAI(String prompt) {
        Log.d("GenAI", "Calling OpenAI API...");

        OkHttpClient client = new OkHttpClient();
        String apiKey = "sk-proj--";  // 填Key

        JSONObject body = new JSONObject();
        try {
            body.put("model", "gpt-3.5-turbo");

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.put(userMessage);

            body.put("messages", messages);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();


        Log.d("GenAI", "Sending JSON: " + body.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("GenAI", "Request failed: " + e.getMessage());
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnConfirm.setEnabled(true);
                    tvPreferencesSummary.setText("Failed to get recommendation.\n" + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("GenAI", "Received response with status: " + response.code());
                String responseBody = response.body().string();
                Log.d("GenAI", "Raw response: " + responseBody);

                if (response.isSuccessful()) {
                    String content = parseContentFromJson(responseBody);
                    Log.d("GenAI", "Parsed content: " + content);
                    
                    // Save the GenAI recommendations to Firestore
                    saveGenAIRecommendations(content);

                    Intent intent = new Intent(PreviewActivity.this, ResultsActivity.class);
                    intent.putExtra("genai_result", content);
                    intent.putExtra("dog_pref", dogPreference);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("GenAI", "Unsuccessful response: " + responseBody);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnConfirm.setEnabled(true);
                        tvPreferencesSummary.setText("❌ OpenAI returned an error.");
                    });
                }
            }
        });
    }


    // 解析 OpenAI 返回的文字内容
    private String parseContentFromJson(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray choices = obj.getJSONArray("choices");
            JSONObject first = choices.getJSONObject(0);
            JSONObject message = first.getJSONObject("message");
            return message.getString("content");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing GenAI response";
        }
    }
    //
    private String buildPrompt(DogPreference pref) {
        return "I am looking for a dog recommendation.\n\n" +
                "Here are my preferences:\n" +
                "- Size: " + pref.size + "\n" +
                "- Exercise: " + pref.exercise + "\n" +
                "- Coat Length: " + pref.coatLength + "\n" +
                "- Home Type: " + pref.homeType + "\n" +
                "- Have Children: " + pref.haveChildren + "\n" +
                "- Monthly Budget: " + pref.budget + "\n\n" +
                "Please recommend 3 dog breeds that suit me in the following structured JSON format:\n" +
                "[\n" +
                "  {\n" +
                "    \"breed\": \"\",\n" +
                "    \"match_percent\": \"\",\n" +
                "    \"why\": \"\",\n" +
                "    \"care_tips\": \"\"\n" +
                "  },\n" +
                "  ... (2 more entries)\n" +
                "]\n" +
                "Only return valid JSON. Do not include any commentary.";
    }

    // 构建用户偏好摘要文本 / Build readable summary of user selections
    private String buildPreferenceSummary(DogPreference pref) {
        return
                "Dog Size: " + pref.size + "\n" +
                        "Exercise Time: " + pref.exercise + "\n" +
                        "Coat Length: " + pref.coatLength + "\n" +
                        "Home Type: " + pref.homeType + "\n" +
                        "Has Children: " + pref.haveChildren + "\n" +
                        "Monthly Budget: " + pref.budget;
    }

    // save recommendations
    private void saveGenAIRecommendations(String recommendations) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            
            // Create a map with the recommendation data
            Map<String, Object> recommendationData = new HashMap<>();
            recommendationData.put("recommendations", recommendations);
            recommendationData.put("timestamp", new Date());
            
            // Save to Firestore
            db.collection("users")
                .document(currentUser.getUid())
                .collection("recommendations")
                .add(recommendationData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("PreviewActivity", "Recommendations saved with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("PreviewActivity", "Error saving recommendations", e);
                });
        }
    }
}
