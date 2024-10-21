package com.example.kITa;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
    private static final String SERVER_URL = "http://10.0.2.2/lost_found_db/";

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

    public static void uploadImage(int senderId, Uri imageUri, ApiCallback<Boolean> callback) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... voids) {
                try {
                    File imageFile = new File(imageUri.getPath());
                    URL url = new URL(SERVER_URL + "upload_image_message.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");

                    DataOutputStream request = new DataOutputStream(conn.getOutputStream());
                    request.writeBytes("--*****\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"sender_id\"\r\n\r\n" + senderId + "\r\n");
                    request.writeBytes("--*****\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + imageFile.getName() + "\"\r\n\r\n");

                    FileInputStream fileInputStream = new FileInputStream(imageFile);
                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        request.write(buffer, 0, bytesRead);
                    }
                    fileInputStream.close();
                    request.writeBytes("\r\n--*****--\r\n");
                    request.flush();
                    request.close();

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

    public static void getLatestAdminMessage(ApiCallback<Message> callback) {
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


    // ------- User to User Functions -------

    public static void sendMessageToUser(int senderId, int receiverId, String message, ApiCallback<Boolean> callback) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL(SERVER_URL + "send_message_to_user.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);

                    String postData = "sender_id=" + senderId + "&receiver_id=" + receiverId + "&message=" + message;
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

    public static void getUserConversations(int userId, ApiCallback<List<UserConversation>> callback) {
        new AsyncTask<Void, Void, List<UserConversation>>() {
            protected List<UserConversation> doInBackground(Void... voids) {
                try {
                    URL url = new URL(SERVER_URL + "get_user_conversations.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write("user_id=" + userId);
                    writer.flush();
                    writer.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    in.close();
                    conn.disconnect();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    List<UserConversation> conversations = new ArrayList<>();
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray jsonConversations = jsonResponse.getJSONArray("conversations");
                        for (int i = 0; i < jsonConversations.length(); i++) {
                            JSONObject jsonConversation = jsonConversations.getJSONObject(i);
                            int senderId = jsonConversation.getInt("sender_id");
                            int receiverId = jsonConversation.getInt("receiver_id");
                            String message = jsonConversation.getString("message");
                            String createdAt = jsonConversation.getString("created_at");

                            conversations.add(new UserConversation(senderId, receiverId, message, createdAt));
                        }
                    }

                    return conversations;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<UserConversation> conversations) {
                if (conversations != null) {
                    callback.onSuccess(conversations);
                } else {
                    callback.onError("Failed to load conversations");
                }
            }
        }.execute();
    }

    public static void searchUsers(String query, ApiCallback<List<User>> callback) {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                try {
                    URL url = new URL(SERVER_URL + "search_users.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write("query=" + query);
                    writer.flush();
                    writer.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    in.close();
                    conn.disconnect();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    List<User> users = new ArrayList<>();
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray jsonUsers = jsonResponse.getJSONArray("users");
                        for (int i = 0; i < jsonUsers.length(); i++) {
                            JSONObject jsonUser = jsonUsers.getJSONObject(i);
                            int id = jsonUser.getInt("id");
                            String Fname = jsonUser.getString("Fname");
                            String Lname = jsonUser.getString("Lname");

                            users.add(new User(id, Fname, Lname));
                        }
                    }

                    return users;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<User> users) {
                if (users != null) {
                    callback.onSuccess(users);
                } else {
                    callback.onError("Failed to fetch users");
                }
            }
        }.execute();
    }
}
