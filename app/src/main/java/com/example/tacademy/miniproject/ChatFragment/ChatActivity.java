package com.example.tacademy.miniproject.ChatFragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;


import com.example.tacademy.miniproject.R;
import com.example.tacademy.miniproject.data.ChatContract;
import com.example.tacademy.miniproject.data.User;
import com.example.tacademy.miniproject.manager.DBManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.rv_list)
    RecyclerView listView;

    ChatAdapter mAdapter;

    @BindView(R.id.group_type)
    RadioGroup typeView;

    @BindView(R.id.edit_input)
    EditText inputView;

    public static final String EXTRA_USER = "user";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        user = (User)getIntent().getSerializableExtra(EXTRA_USER);

        mAdapter = new ChatAdapter();
        listView.setAdapter(mAdapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.btn_send)
    public void onSend(View view) {
        String message = inputView.getText().toString();
        int type = ChatContract.ChatMessage.TYPE_SEND;
        switch (typeView.getCheckedRadioButtonId()) {
            case R.id.radio_send :
                type = ChatContract.ChatMessage.TYPE_SEND;
                break;
            case R.id.radio_receive :
                type = ChatContract.ChatMessage.TYPE_RECEIVE;
                break;
        }
        DBManager.getInstance().addMessage(user, type, message);

        updateMessage();
    }

    private void updateMessage() {
        Cursor c = DBManager.getInstance().getChatMessage(user);
        mAdapter.changeCursor(c);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateMessage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.changeCursor(null);
    }
}
