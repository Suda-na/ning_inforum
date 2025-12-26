package com.example.lnforum.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 简单的HTTP请求工具类
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";
    private static final String BASE_URL = "http://192.168.172.1:8081"; // 默认使用8080端口，请根据实际情况修改

    /**
     * POST请求
     * @param endpoint 接口路径，例如 "/api/android/post/publish/circle"
     * @param jsonData 请求的JSON数据
     * @param callback 回调接口
     */
    public static void post(String endpoint, JSONObject jsonData, HttpCallback callback) {
        new HttpTask(endpoint, jsonData, callback).execute();
    }

    /**
     * HTTP回调接口
     */
    public interface HttpCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    /**
     * 异步HTTP任务
     */
    private static class HttpTask extends AsyncTask<Void, Void, String> {
        private String endpoint;
        private JSONObject jsonData;
        private HttpCallback callback;
        private String errorMessage;

        public HttpTask(String endpoint, JSONObject jsonData, HttpCallback callback) {
            this.endpoint = endpoint;
            this.jsonData = jsonData;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(BASE_URL + endpoint);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                // 发送请求数据
                if (jsonData != null) {
                    String jsonString = jsonData.toString();
                    Log.d(TAG, "Request URL: " + url.toString());
                    Log.d(TAG, "Request Data: " + jsonString);

                    OutputStream os = connection.getOutputStream();
                    os.write(jsonString.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();
                }

                // 读取响应
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    errorMessage = "HTTP Error: " + responseCode;
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Request failed", e);
                errorMessage = e.getMessage();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            } else {
                if (callback != null) {
                    callback.onError(errorMessage != null ? errorMessage : "请求失败");
                }
            }
        }
    }
}

