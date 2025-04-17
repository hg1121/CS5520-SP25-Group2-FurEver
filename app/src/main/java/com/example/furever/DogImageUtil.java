package com.example.furever;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DogImageUtil {

    public interface ImageDownloadCallback {
        void onSuccess(String localPath);
        void onFailure(String error);
    }

    // 主方法：外部调用这个即可
    public static void getDogImage(Context context, String breed, ImageDownloadCallback callback) {
        File imageFile = getCachedBreedImageFile(context, breed);
        if (imageFile.exists()) {
            Log.d("DogImageUtil", "Loaded from cache: " + imageFile.getAbsolutePath());
            callback.onSuccess(imageFile.getAbsolutePath());
            return;
        }

        fetchImageFromApi(context, breed, imageFile, callback);
    }

    // 文件名规范化
    private static String normalizeBreed(String breed) {
        return breed.toLowerCase().trim().replaceAll("[^a-z]", ""); // 仅保留字母
    }

    // 缓存路径
    private static File getCachedBreedImageFile(Context context, String breed) {
        String fileName = normalizeBreed(breed) + ".jpg";
        return new File(context.getFilesDir(), fileName);
    }

    // 修正 API 路径映射（复合 breed）
    private static String getBreedApiPath(String breed) {
        Map<String, String> map = new HashMap<>();
        map.put("french bulldog", "bulldog/french");
        map.put("cavalier king charles spaniel", "spaniel/cavalier");
        map.put("german shepherd", "germanshepherd");
        map.put("shiba inu", "shiba"); // 简化别名
        map.put("golden retriever", "retriever/golden");
        map.put("labrador retriever", "retriever/labrador");

        String key = breed.toLowerCase().trim();
        if (map.containsKey(key)) {
            Log.d("DogImageUtil", "Mapped '" + key + "' → " + map.get(key));
            return map.get(key);
        }

        return key.replace(" ", "");
    }

    // API 下载流程
    private static void fetchImageFromApi(Context context, String breed, File imageFile, ImageDownloadCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String apiPath = getBreedApiPath(breed);
        String apiUrl = "https://dog.ceo/api/breed/" + apiPath + "/images";

        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Failed to fetch breed list: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                try {
                    JSONObject json = new JSONObject(body);

                    // 检查 message 是数组还是错误字符串
                    Object message = json.get("message");
                    if (!(message instanceof JSONArray)) {
                        callback.onFailure("Parse error: " + message.toString());
                        return;
                    }

                    JSONArray imageArray = (JSONArray) message;
                    if (imageArray.length() == 0) {
                        callback.onFailure("No images found for breed: " + breed);
                        return;
                    }

                    String imageUrl = imageArray.getString(0); // 使用第一个图
                    downloadAndCacheImage(imageUrl, imageFile, callback);
                } catch (Exception e) {
                    callback.onFailure("Parse error: " + e.getMessage());
                }
            }
        });
    }

    // 下载并缓存图片
    private static void downloadAndCacheImage(String url, File file, ImageDownloadCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Image download failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] imageBytes = response.body().bytes();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(imageBytes);
                fos.close();
                callback.onSuccess(file.getAbsolutePath());
            }
        });
    }
}
