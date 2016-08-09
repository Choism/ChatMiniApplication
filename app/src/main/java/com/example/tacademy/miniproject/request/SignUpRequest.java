package com.example.tacademy.miniproject.request;

import android.content.Context;


import com.example.tacademy.miniproject.data.NetworkResult;
import com.example.tacademy.miniproject.data.NetworkResultTemp;
import com.example.tacademy.miniproject.data.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2016-08-09.
 */
public class SignUpRequest extends AbstractRequest<NetworkResult<User>> {
    Request request;
    public SignUpRequest(Context context, String username, String password,String email, String regId) { //AbstrackRequst에 세팅해둔 부분
        HttpUrl.Builder builder = getBaseUrlBuilder(); // 뒤에  요청 URl 뒤 세그먼트 부분 ex) 호스트 뒤 부분
        builder.addPathSegment("signup");  //.build 해야 getBaseUrlBuilder+addPathSegment을 .Build 써줘야 붙여진다.
        //URL 합치는 곳 scheme(http://) + host(dongjatestweb.appspot.com) + segment(/singin)


        RequestBody body = new FormBody.Builder()  //서버에 저장된 key: value 을 세팅
                .add("username",username)
                .add("password",password)
                .add("email", email)
                .add("registrationId", regId)
                .build();
        //post부분 : 서버랑 데이터를 어덯게 주고 받을지 세팅하는 하는 곳

        request = new Request.Builder()
                .url(builder.build())
                .post(body)
                .tag(context)
                .build();
    }   //여기서 최종적으로 URI + BODY  가 만들어진다.

    @Override//다른곳에서 Request를 요청 한 곳으로 request 값을 리턴함
    public Request getRequest() {
        return request;
    }

    @Override//요청한게 성공인지 실패인지 확인하는 곳?
    protected NetworkResult<User> parse(ResponseBody body) throws IOException {
        String text = body.string();
        Gson gson = new Gson();
        NetworkResultTemp temp = gson.fromJson(text, NetworkResultTemp.class);
        if (temp.getCode() == 1) {
            Type type = new TypeToken<NetworkResult<User>>(){}.getType();
            NetworkResult<User> result = gson.fromJson(text, type);
            return result;
        } else {
            Type type = new TypeToken<NetworkResult<String>>(){}.getType();
            NetworkResult<String> result = gson.fromJson(text, type);
            throw new IOException(result.getResult());
        }
    }
}
