package example.com.demofon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.auth0.android.jwt.JWT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvRelays;
    Button btn;
    ProgressBar p;

    Retrofit retrofit;
    OkHttpClient client;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(StaticFields.PREFERENCE_NAME,MODE_PRIVATE);
        editor =sharedPreferences.edit();
        if(sharedPreferences.getString(StaticFields.PREFERENCE_TOKEN,"")=="") startAuth();

    }

    private void startAuth() {
        Intent authIntent = new Intent(this,LoginActivity.class);
        authIntent.putExtra("refresh_token",false);
        startActivity(authIntent);
    }

    private void startRefreshToken() {
        Intent authIntent = new Intent(this,LoginActivity.class);
        authIntent.putExtra("refresh_token",true);
        startActivity(authIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getSharedPreferences(StaticFields.PREFERENCE_NAME,MODE_PRIVATE).getString(StaticFields.PREFERENCE_TOKEN,"")!=""){
            initViews();
            initRetrofit();
            requestRelaysList();
        }
    }

    private void initViews(){
        rvRelays = findViewById(R.id.rv);
        btn = findViewById(R.id.btn_logout);
        p = findViewById(R.id.progressBar2);
    }

    private void initRetrofit(){
        String token = sharedPreferences.getString(StaticFields.PREFERENCE_TOKEN,"");
        final JWT jwt = new JWT(token);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("User-Agent", "student-app")
                        .header("Content-Type", "application/json;charset=utf-8")
                        .header("Authorization", "Bearer "+jwt.getId())
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        client = httpClient.build();
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.is74.ru")
                .client(client)
                .build();
    }

    private void requestRelaysList(){
        final IsApi api = retrofit.create(IsApi.class);
        showProgressBar(true);
        api.getRelaysList().enqueue(new Callback<List<RelayModel>>() {
            @Override
            public void onResponse(Call<List<RelayModel>> call, Response<List<RelayModel>> response) {
                if (response.isSuccessful()) {
                    rvRelays.setAdapter(new RelayAdapter(getBaseContext(), response.body(), api));

                }
                if (response.code()==401) startRefreshToken();
                showProgressBar(false);
            }


            @Override
            public void onFailure(Call<List<RelayModel>> call, Throwable t) {
                showProgressBar(false);
            }
        });

    }

    private void showProgressBar(boolean b){
        if(b){
            rvRelays.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);
            p.setVisibility(View.VISIBLE);
        }else{
            rvRelays.setVisibility(View.VISIBLE);
            btn.setVisibility(View.VISIBLE);
            p.setVisibility(View.GONE);
        }
    }



    public void onClickLogout(View view) {
        startAuth();
    }
}
