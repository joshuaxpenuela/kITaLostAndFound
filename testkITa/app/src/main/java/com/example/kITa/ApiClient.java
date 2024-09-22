package com.example.kITa;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2/lost_found_db/";

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public static void createMessage(Message message, ApiCallback<Integer> callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "create_message.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setDoOutput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("sender_id", message.getSenderId());
                    jsonParam.put("receiver_id", message.getReceiverId());
                    jsonParam.put("message", message.getText());
                    jsonParam.put("is_admin_sender", message.isAdminSender() ? 1 : 0);
                    jsonParam.put("media_url", message.getMediaUrl());

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes("UTF-8"));
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();
                    conn.disconnect();

                    return response.toString();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        if (jsonResult.getBoolean("success")) {
                            callback.onSuccess(jsonResult.getInt("message_id"));
                        } else {
                            callback.onError(jsonResult.getString("error"));
                        }
                    } catch (Exception e) {
                        callback.onError("Failed to parse server response");
                    }
                } else {
                    callback.onError("Network error");
                }
            }
        }.execute();
    }

    public static void getMessages(int userId, ApiCallback<List<Message>> callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "get_messages.php?user_id=" + userId);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();
                    conn.disconnect();

                    return response.toString();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        List<Message> messages = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonMessage = jsonArray.getJSONObject(i);
                            messages.add(new Message(
                                    jsonMessage.getInt("id"),
                                    jsonMessage.getInt("sender_id"),
                                    jsonMessage.getInt("receiver_id"),
                                    jsonMessage.getString("message"),
                                    jsonMessage.getInt("is_admin_sender") == 1,
                                    new Date(jsonMessage.getLong("created_at")),
                                    jsonMessage.getString("media_url")
                            ));
                        }
                        callback.onSuccess(messages);
                    } catch (Exception e) {
                        callback.onError("Failed to parse server response");
                    }
                } else {
                    callback.onError("Network error");
                }
            }
        }.execute();
    }

    public static void getLatestAdminMessage(ApiCallback<Message> callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "get_latest_admin_message.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();
                    conn.disconnect();

                    return response.toString();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null && !result.equals("null")) {
                    try {
                        JSONObject jsonMessage = new JSONObject(result);
                        Message message = new Message(
                                jsonMessage.getInt("id"),
                                jsonMessage.getInt("sender_id"),
                                jsonMessage.getInt("receiver_id"),
                                jsonMessage.getString("message"),
                                jsonMessage.getInt("is_admin_sender") == 1,
                                new Date(jsonMessage.getLong("created_at")),
                                jsonMessage.getString("media_url")
                        );
                        callback.onSuccess(message);
                    } catch (Exception e) {
                        callback.onError("Failed to parse server response");
                    }
                } else {
                    callback.onSuccess(null);
                }
            }
        }.execute();
    }
}