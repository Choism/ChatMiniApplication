package com.example.tacademy.miniproject.request;

import com.example.tacademy.miniproject.manager.NetworkRequest;

import okhttp3.HttpUrl;


// 자신의 서버에 요청을 할 때 HttpURl 미리 초기값을 세팅 ? 다른곳에서 리케스트 하면 여기있는걸 불러와 바로 사용할 수 있다.
// request 에 역할 == 서버에 요청할 때 URL/세그먼트 에 따라 요청 되는게 다른데 이것을 각 URL과 body를(데이터를) 어떻게 받을지 세팅해주는 곳.




public abstract class AbstractRequest<T> extends NetworkRequest<T> {

    protected HttpUrl.Builder getBaseUrlBuilder() {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("https");
        builder.host("dongjatestweb.appspot.com");
        return builder;
    }
}
