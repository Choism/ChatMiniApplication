package com.example.tacademy.miniproject.data;

import android.provider.BaseColumns;


public class    ChatContract {
    public interface ChatUser extends BaseColumns {
        public static final String TABLE = "chatuser";
        public static final String COLUMN_SERVER_ID = "sid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_LAST_MESSAGE_ID = "lastid";
    }

    public interface ChatMessage extends BaseColumns {
        public static final int TYPE_SEND = 0;
        public static final int TYPE_RECEIVE = 1;
        public static final int TYPE_DATE = 2;

        public static final String TABLE = "chatmessage";
        public static final String COLUMN_USER_ID = "uid";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_CREATED = "created";
    }
}
