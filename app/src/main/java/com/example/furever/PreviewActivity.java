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
import java.util.concurrent.TimeUnit;

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

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
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

                    // Save recommendation to Firestore
                    saveGenAIRecommendations(content);

                    // 拉取头像路径
                    JSONArray breedArray = parseBreedList(content);
                    String[] breedNames = new String[3];
                    String[] imagePaths = new String[3];

                    for (int i = 0; i < 3; i++) {
                        try {
                            JSONObject obj = breedArray.getJSONObject(i);
                            breedNames[i] = obj.getString("breed");
                        } catch (Exception e) {
                            breedNames[i] = "unknown";
                        }
                    }

                    final int[] finishedCount = {0};
                    for (int i = 0; i < 3; i++) {
                        int index = i;
                        DogImageUtil.getDogImage(PreviewActivity.this, breedNames[i], new DogImageUtil.ImageDownloadCallback() {
                            @Override
                            public void onSuccess(String localPath) {
                                Log.d("DogImageUtil", "✅ Image downloaded: " + localPath);
                                imagePaths[index] = localPath;
                                checkAndProceed();
                            }
                            @Override
                            public void onFailure(String error) {
                                Log.e("PreviewActivity", "头像获取失败：" + error);
                                imagePaths[index] = ""; // fallback 值
                                checkAndProceed(); // ✅ 关键：失败也要调用
                            }

                            private void checkAndProceed() {
                                finishedCount[0]++;
                                if (finishedCount[0] == 3) {
                                    // 三个头像都处理完后再跳转页面
                                    Intent intent = new Intent(PreviewActivity.this, ResultsActivity.class);
                                    intent.putExtra("genai_result", content);
                                    intent.putExtra("dog_pref", dogPreference);
                                    intent.putExtra("breed_image_0", imagePaths[0]);
                                    intent.putExtra("breed_image_1", imagePaths[1]);
                                    intent.putExtra("breed_image_2", imagePaths[2]);
                                    Log.d("DebugPayload", "genai_result length = " + content.length());
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
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
        String allowedBreeds = "[\"Affenpinscher\", \"African\", \"Airedale\", \"Akita\", \"Appenzeller\", \"Australian Kelpie\", \"Australian Shepherd\", " +
                "\"Bakharwal Indian\", \"Basenji\", \"Beagle\", \"Bluetick\", \"Borzoi\", \"Bouvier\", \"Boxer\", \"Brabancon\", \"Briard\", " +
                "\"Buhund Norwegian\", \"Bulldog Boston\", \"Bulldog English\", \"Bulldog French\", \"Bullterrier Staffordshire\", " +
                "\"Cattledog Australian\", \"Cavapoo\", \"Chihuahua\", \"Chippiparai Indian\", \"Chow\", \"Clumber\", \"Cockapoo\", " +
                "\"Collie Border\", \"Coonhound\", \"Corgi Cardigan\", \"Cotondetulear\", \"Dachshund\", \"Dalmatian\", \"Dane Great\", " +
                "\"Danish Swedish\", \"Deerhound Scottish\", \"Dhole\", \"Dingo\", \"Doberman\", \"Elkhound Norwegian\", \"Entlebucher\", " +
                "\"Eskimo\", \"Finnish Lapphund\", \"Frise Bichon\", \"Gaddi Indian\", \"Germanshepherd\", \"Greyhound Indian\", " +
                "\"Greyhound Italian\", \"Groenendael\", \"Havanese\", \"Hound Afghan\", \"Hound Basset\", \"Hound Blood\", " +
                "\"Hound English\", \"Hound Ibizan\", \"Hound Plott\", \"Hound Walker\", \"Husky\", \"Keeshond\", \"Kelpie\", \"Kombai\", " +
                "\"Komondor\", \"Kuvasz\", \"Labradoodle\", \"Labrador\", \"Leonberg\", \"Lhasa\", \"Malamute\", \"Malinois\", \"Maltese\", " +
                "\"Mastiff Bull\", \"Mastiff English\", \"Mastiff Indian\", \"Mastiff Tibetan\", \"Mexicanhairless\", \"Mix\", " +
                "\"Mountain Bernese\", \"Mountain Swiss\", \"Mudhol Indian\", \"Newfoundland\", \"Otterhound\", \"Ovcharka Caucasian\", " +
                "\"Papillon\", \"Pariah Indian\", \"Pekinese\", \"Pembroke\", \"Pinscher Miniature\", \"Pitbull\", \"Pointer German\", " +
                "\"Pointer Germanlonghair\", \"Pomeranian\", \"Poodle Medium\", \"Poodle Miniature\", \"Poodle Standard\", " +
                "\"Poodle Toy\", \"Pug\", \"Puggle\", \"Pyrenees\", \"Rajapalayam Indian\", \"Redbone\", \"Retriever Chesapeake\", " +
                "\"Retriever Curly\", \"Retriever Flatcoated\", \"Retriever Golden\", \"Ridgeback Rhodesian\", \"Rottweiler\", " +
                "\"Saluki\", \"Samoyed\", \"Schipperke\", \"Schnauzer Giant\", \"Schnauzer Miniature\", \"Segugio Italian\", " +
                "\"Setter English\", \"Setter Gordon\", \"Setter Irish\", \"Sharpei\", \"Sheepdog English\", \"Sheepdog Indian\", " +
                "\"Sheepdog Shetland\", \"Shiba\", \"Shihtzu\", \"Spaniel Blenheim\", \"Spaniel Brittany\", \"Spaniel Cocker\", " +
                "\"Spaniel Irish\", \"Spaniel Japanese\", \"Spaniel Sussex\", \"Spaniel Welsh\", \"Spitz Indian\", \"Spitz Japanese\", " +
                "\"Springer English\", \"Stbernard\", \"Terrier American\", \"Terrier Australian\", \"Terrier Bedlington\", " +
                "\"Terrier Border\", \"Terrier Cairn\", \"Terrier Dandie\", \"Terrier Fox\", \"Terrier Irish\", \"Terrier Kerryblue\", " +
                "\"Terrier Lakeland\", \"Terrier Norfolk\", \"Terrier Norwich\", \"Terrier Patterdale\", \"Terrier Russell\", " +
                "\"Terrier Scottish\", \"Terrier Sealyham\", \"Terrier Silky\", \"Terrier Tibetan\", \"Terrier Toy\", " +
                "\"Terrier Welsh\", \"Terrier Westhighland\", \"Terrier Wheaten\", \"Terrier Yorkshire\", \"Tervuren\", \"Vizsla\", " +
                "\"Waterdog Spanish\", \"Weimaraner\", \"Whippet\", \"Wolfhound Irish\"]";

        return "I am looking for a dog recommendation.\n\n" +
                "Here are my preferences:\n" +
                "- Size: " + pref.size + "\n" +
                "- Exercise: " + pref.exercise + "\n" +
                "- Coat Length: " + pref.coatLength + "\n" +
                "- Home Type: " + pref.homeType + "\n" +
                "- Have Children: " + pref.haveChildren + "\n" +
                "- Monthly Budget: " + pref.budget + "\n\n" +
                "Please recommend 3 dog breeds that suit me, in the following structured JSON format:\n" +
                "[\n" +
                "  {\n" +
                "    \"breed\": \"\",\n" +
                "    \"match_percent\": \"\",\n" +
                "    \"why\": \"\",\n" +
                "    \"care_tips\": \"\"\n" +
                "  },\n" +
                "  ... (2 more entries)\n" +
                "]\n\n" +
                "**Important:** Only select dog breeds from this allowed list:\n" + allowedBreeds + "\n\n" +
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

    //解析推荐 JSON  parse JSON
    private JSONArray parseBreedList(String json) {
        try {
            return new JSONArray(json); // json 是 OpenAI 返回的 structured JSON array
        } catch (Exception e) {
            Log.e("ParseBreedList", "JSON error: " + e.getMessage());
            return new JSONArray();
        }
    }
}
