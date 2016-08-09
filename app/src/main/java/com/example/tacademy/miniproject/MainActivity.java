package com.example.tacademy.miniproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tacademy.miniproject.data.NetworkResult;
import com.example.tacademy.miniproject.data.User;
import com.example.tacademy.miniproject.manager.NetworkManager;
import com.example.tacademy.miniproject.manager.NetworkRequest;
import com.example.tacademy.miniproject.request.FriendListRequest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//로그인창 만들고 회원관리창만들고
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.listView)
    ListView listView;

    ArrayAdapter<User> mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        mAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);

        FriendListRequest request = new FriendListRequest(this);
        //
        NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<List<User>>>() {
            @Override   //성공할시
            public void onSuccess(NetworkRequest<NetworkResult<List<User>>> request, NetworkResult<List<User>> result) {
                List<User> users = result.getResult();
                mAdapter.addAll(users);
            }

            @Override   //실패할시
            public void onFail(NetworkRequest<NetworkResult<List<User>>> request, int errorCode, String errorMessage, Throwable e) {

            }
        });
    }
}