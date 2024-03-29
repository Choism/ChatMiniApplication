package com.example.tacademy.miniproject.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.tacademy.miniproject.MyApplication;
import com.example.tacademy.miniproject.data.ChatContract;
import com.example.tacademy.miniproject.data.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-08-11.
 */
public class DBManager extends SQLiteOpenHelper {
    private static DBManager instance;
    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    private static final String DB_NAME = "chat_db";
    private static final int DB_VERSION = 1;

    private DBManager() {
        super(MyApplication.getContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + ChatContract.ChatUser.TABLE + "(" +
                ChatContract.ChatUser._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChatContract.ChatUser.COLUMN_SERVER_ID + " INTEGER," +
                ChatContract.ChatUser.COLUMN_NAME + " TEXT," +
                ChatContract.ChatUser.COLUMN_EMAIL + " TEXT NOT NULL," +
                ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID + " INTEGER);";
        db.execSQL(sql);

        sql = "CREATE TABLE " + ChatContract.ChatMessage.TABLE + "(" +
                ChatContract.ChatMessage._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChatContract.ChatMessage.COLUMN_USER_ID + " INTEGER," +
                ChatContract.ChatMessage.COLUMN_TYPE + " INTEGER," +
                ChatContract.ChatMessage.COLUMN_MESSAGE + " TEXT," +
                ChatContract.ChatMessage.COLUMN_CREATED + " INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long getUserId(long serverId) {
        String selection = ChatContract.ChatUser.COLUMN_SERVER_ID + " = ?";
        String[] args = {""+serverId};
        String[] columns = {ChatContract.ChatUser._ID};
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(ChatContract.ChatUser.TABLE, columns, selection, args, null, null, null);
        try {
            if (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndex(ChatContract.ChatUser._ID));
                return id;
            }
        } finally {
            c.close();
        }
        return -1;
    }

    ContentValues values = new ContentValues();
    public long addUser(User user) {
        if (getUserId(user.getId()) != -1) {
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_SERVER_ID, user.getId());
            values.put(ChatContract.ChatUser.COLUMN_NAME, user.getUserName());
            values.put(ChatContract.ChatUser.COLUMN_EMAIL, user.getEmail());
            return db.insert(ChatContract.ChatUser.TABLE, null, values);
        }
        throw new IllegalArgumentException("aleady user added");
    }

    Map<Long, Long> resolveUserId = new HashMap<>();
    public long addMessage(User user, int type, String message) {
        Long uid = resolveUserId.get(user.getId());
        if (uid == null) {
            long id = getUserId(user.getId());
            if (id == -1) {
                id = addUser(user);
            }
            resolveUserId.put(user.getId(), id);
            uid = id;
        }

        SQLiteDatabase db = getWritableDatabase();
        values.clear();
        values.put(ChatContract.ChatMessage.COLUMN_USER_ID, (long)uid);
        values.put(ChatContract.ChatMessage.COLUMN_TYPE, type);
        values.put(ChatContract.ChatMessage.COLUMN_MESSAGE, message);
        long current = System.currentTimeMillis();
        values.put(ChatContract.ChatMessage.COLUMN_CREATED, current);
        try {
            db.beginTransaction();
            long mid = db.insert(ChatContract.ChatMessage.TABLE, null, values);

            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID, mid);
            String selection = ChatContract.ChatUser._ID + " = ?";
            String[] args = {"" + uid};
            db.update(ChatContract.ChatUser.TABLE, values, selection, args);
            db.setTransactionSuccessful();
            return mid;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getChatUser() {
        String table = ChatContract.ChatUser.TABLE + " INNER JOIN " +
                ChatContract.ChatMessage.TABLE + " ON " +
                ChatContract.ChatUser.TABLE + "." + ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID + " = " +
                ChatContract.ChatMessage.TABLE + "." + ChatContract.ChatMessage._ID;
        String[] columns = {ChatContract.ChatUser.TABLE + "." + ChatContract.ChatUser._ID,
                ChatContract.ChatUser.COLUMN_SERVER_ID,
                ChatContract.ChatUser.COLUMN_EMAIL,
                ChatContract.ChatUser.COLUMN_NAME,
                ChatContract.ChatMessage.COLUMN_MESSAGE};
        String sort = ChatContract.ChatUser.COLUMN_NAME + " COLLATE LOCALIZED ASC";
        SQLiteDatabase db = getReadableDatabase();
        return db.query(table, columns, null, null, null, null, sort);
    }

    public Cursor getChatMessage(User user) {
        long userid = -1;
        Long uid = resolveUserId.get(user.getId());
        if (uid == null) {
            long id = getUserId(user.getId());
            if (id != -1) {
                resolveUserId.put(user.getId(), id);
                userid = id;
            }
        } else {
            userid = uid;
        }

        String[] columns = {ChatContract.ChatMessage._ID,
                ChatContract.ChatMessage.COLUMN_TYPE,
                ChatContract.ChatMessage.COLUMN_MESSAGE,
                ChatContract.ChatMessage.COLUMN_CREATED};
        String selection = ChatContract.ChatMessage.COLUMN_USER_ID + " = ? ";
        String[] args = {"" + userid};
        String sort = ChatContract.ChatMessage.COLUMN_CREATED + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ChatContract.ChatMessage.TABLE, columns, selection, args, null, null, sort);
    }

}
