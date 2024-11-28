package com.example.kITa;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiClient {

    // URL of your server
    private static final String SERVER_URL = "https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/";

    // ------- User to Admin Functions -------

    public static void sendMessageToAdmins(int senderId, String message, ApiCallback<Boolean> callback) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL(SERVER_URL + "send_message_to_admin.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);

                    String postData = "sender_id=" + senderId + "&message=" + message;
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(postData);
                    writer.flush();
                    writer.close();

                    int responseCode = conn.getResponseCode();
                    return responseCode == HttpURLConnection.HTTP_OK;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            protected void onPostExecute(Boolean success) {
                callback.onSuccess(success);
            }
        }.execute();
    }

    public static void getUserMessages(int userId, ApiCallback<List<Message>> callback) {
        new AsyncTask<Void, Void, List<Message>>() {
            protected List<Message> doInBackground(Void... voids) {
                try {
                    URL url = new URL(SERVER_URL + "get_user_messages.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);

                    String postData = "user_id=" + userId;
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(postData);
                    writer.flush();
                    writer.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    in.close();
                    conn.disconnect();

                    JSONObject jsonResponse = new JSONObject(content.toString());
                    List<Message> messages = new ArrayList<>();

                    if (jsonResponse.getBoolean("success")) {
                        JSONArray jsonMessages = jsonResponse.getJSONArray("messages");
                        for (int i = 0; i < jsonMessages.length(); i++) {
                            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
                            String text = jsonMessage.getString("message");
                            String createdAt = jsonMessage.getString("created_at");
                            String mediaUrl = jsonMessage.optString("media_url", null);

                            messages.add(new Message(userId, 1, text, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createdAt), mediaUrl));
                        }
                    }
                    return messages;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(List<Message> messages) {
                if (messages != null) {
                    callback.onSuccess(messages);
                } else {
                    callback.onError("Failed to load messages");
                }
            }
        }.execute();
    }

    public static void getLatestAdminMessage(int currentUserId, ApiCallback<Message> callback) {
        new AsyncTask<Void, Void, Message>() {
            protected Message doInBackground(Void... voids) {
                try {
                    URL url = new URL(SERVER_URL + "get_latest_admin_message.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    in.close();
                    conn.disconnect();

                    JSONObject jsonResponse = new JSONObject(content.toString());
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray messages = jsonResponse.getJSONArray("messages");
                        if (messages.length() > 0) {
                            JSONObject latestMessage = messages.getJSONObject(0);
                            int senderId = latestMessage.getInt("sender_id");
                            int receiverId = latestMessage.getInt("receiver_id");
                            String text = latestMessage.getString("message");
                            String createdAt = latestMessage.getString("created_at");

                            return new Message(senderId, receiverId, text, new Date(), createdAt);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Message message) {
                if (message != null) {
                    callback.onSuccess(message);
                } else {
                    callback.onError("Failed to fetch the latest message.");
                }
            }
        }.execute();
    }
}
