package com.example.kITa;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lost_found_db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS messages (id INTEGER PRIMARY KEY AUTOINCREMENT, sender_id INTEGER, receiver_id INTEGER, message TEXT, is_admin_sender INTEGER, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, media_url TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    public long insertMessage(int senderId, int receiverId, String message, boolean isAdminSender, String mediaUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sender_id", senderId);
        values.put("receiver_id", receiverId);
        values.put("message", message);
        values.put("is_admin_sender", isAdminSender ? 1 : 0);
        values.put("media_url", mediaUrl);
        return db.insert("messages", null, values);
    }

    public Message getLatestAdminMessage() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("messages", null, "is_admin_sender = 1", null, null, null, "created_at DESC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            Message message = cursorToMessage(cursor);
            cursor.close();
            return message;
        }
        return null;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("messages", null, null, null, null, null, "created_at ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                messages.add(cursorToMessage(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return messages;
    }

    @SuppressLint("Range")
    private Message cursorToMessage(Cursor cursor) {
        return new Message(
                cursor.getInt(cursor.getColumnIndex("sender_id")),
                cursor.getInt(cursor.getColumnIndex("receiver_id")),
                cursor.getString(cursor.getColumnIndex("message")),
                new Date(cursor.getLong(cursor.getColumnIndex("created_at"))),
                cursor.getString(cursor.getColumnIndex("media_url"))
        );
    }

    public List<Message> getUserMessages(int userId) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                true, // distinct
                "messages",
                null,
                "sender_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "created_at ASC",
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                messages.add(cursorToMessage(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return messages;
    }
}
