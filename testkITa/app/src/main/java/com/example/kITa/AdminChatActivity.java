package com.example.kITa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class AdminChatActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String BASE_URL = "http://10.0.2.2/lost_found_db/";

    private ImageButton sendMessage, uploadImg;
    private EditText messageEditText;
    private RecyclerView chatRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chatadmin);

        initializeViews();
        setClickListeners();
        loadMessages();
    }

    private void initializeViews() {
        sendMessage = findViewById(R.id.sendMessage);
        uploadImg = findViewById(R.id.uploadImg);
        messageEditText = findViewById(R.id.message);
        chatRecyclerView = findViewById(R.id.chatAdminRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setClickListeners() {
        sendMessage.setOnClickListener(v -> sendMessageToAdmin());
        uploadImg.setOnClickListener(v -> openImageChooser());
    }

    private void loadMessages() {
        int userId = UserSession.getInstance().getId();
        ApiClient.getMessages(userId, new ApiCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                messages = result;
                messageAdapter = new MessageAdapter(messages, userId);
                chatRecyclerView.setAdapter(messageAdapter);
                chatRecyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onError(String error) {
                Log.e("AdminChatActivity", "Error sending message: " + error);
                Toast.makeText(AdminChatActivity.this, "Error sending message: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToAdmin() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            int userId = UserSession.getInstance().getId();
            Message message = new Message(0, userId, 0, messageText, false, new Date(), null);
            ApiClient.createMessage(message, new ApiCallback<Integer>() {
                @Override
                public void onSuccess(Integer messageId) {
                    message.setId(messageId);
                    messages.add(message);
                    messageAdapter.notifyItemInserted(messages.size() - 1);
                    chatRecyclerView.scrollToPosition(messages.size() - 1);
                    messageEditText.setText("");
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AdminChatActivity.this, "Error sending message: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void logServerResponse(String response) {
        Log.d("AdminChatActivity", "Server response: " + response);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String imageUrl = uploadImageToServer(imageUri);
            if (imageUrl != null) {
                int userId = UserSession.getInstance().getId();
                Message message = new Message(0, userId, 0, "Image", false, new Date(), imageUrl);
                ApiClient.createMessage(message, new ApiCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer messageId) {
                        message.setId(messageId);
                        messages.add(message);
                        messageAdapter.notifyItemInserted(messages.size() - 1);
                        chatRecyclerView.scrollToPosition(messages.size() - 1);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminChatActivity.this, "Error sending image: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private String uploadImageToServer(Uri imageUri) {
        final String[] imageUrl = {null};
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
                    URL url = new URL(BASE_URL + "upload_image.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    writer.write("--" + boundary + "\r\n");
                    writer.write("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"\r\n");
                    writer.write("Content-Type: image/jpeg\r\n\r\n");
                    writer.flush();

                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = imageStream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    imageStream.close();

                    writer.write("\r\n");
                    writer.write("--" + boundary + "--\r\n");
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        br.close();
                        logServerResponse(response.toString());
                        return response.toString();
                    } else {
                        logServerResponse("Error response code: " + responseCode);
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logServerResponse("Exception: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                ApiCallback<Integer> callback = null;
                if (result != null) {
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        if (jsonResult.getBoolean("success")) {
                            callback.onSuccess(jsonResult.getInt("message_id"));
                        } else {
                            callback.onError(jsonResult.getString("error"));
                        }
                    } catch (Exception e) {
                        callback.onError("Failed to parse server response: " + e.getMessage() + "\nResponse: " + result);
                    }
                } else {
                    callback.onError("Network error");
                }
            }
        }.execute();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return imageUrl[0];
    }
}
