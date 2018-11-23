package example.com.demofon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    LinearLayout cont;
    ProgressBar p;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(StaticFields.PREFERENCE_NAME,MODE_PRIVATE);
        editor =sharedPreferences.edit();
        if(sharedPreferences.getString(StaticFields.PREFERENCE_TOKEN,"")!="") finish();
        cont = findViewById(R.id.main_cont);
        p = findViewById(R.id.progressBar);
    }

    private void showProgresBar(boolean b){
        if(b){
            cont.setVisibility(View.GONE);
            p.setVisibility(View.VISIBLE);
        }else{
            cont.setVisibility(View.VISIBLE);
            p.setVisibility(View.GONE);
        }
    }

    private void authorization(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(StaticFields.AUTH_URI+"?response_type=code&" +
                "client_id="+StaticFields.CLIEND_ID+
                "&redirect_uri="+StaticFields.REDIRECT_URI));
        startActivity(intent);
    }

    private void getToken(String code){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
                                      @Override
                                      public okhttp3.Response intercept(Chain chain) throws IOException {
                                          Request original = chain.request();

                                          Request request = original.newBuilder()
                                                  .header("User-Agent", "student-app")
                                                  .header("Content-Type", "application/json;charset=utf-8")
                                                  .method(original.method(), original.body())
                                                  .build();

                                          return chain.proceed(request);
                                      }
                                  });
        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://id.is74.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        IsApi api = retrofit.create(IsApi.class);
        showProgresBar(true);
        api.getToken(code,StaticFields.CLIEND_ID,StaticFields.SECRET,StaticFields.REDIRECT_URI,"authorization_code").enqueue(new Callback<TokenModel>() {
            @Override
            public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                if(response.isSuccessful()) {
                    editor.putString(StaticFields.PREFERENCE_TOKEN, response.body().access_token);
                    editor.putString(StaticFields.PREFERENCE_REFRESH_TOKEN, response.body().refresh_token);
                    editor.apply();
                    showProgresBar(false);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TokenModel> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Что-то пошло не так :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if(uri!=null && uri.toString().startsWith(StaticFields.REDIRECT_URI)){
            String code = uri.getQueryParameter("code");
            getToken(code);
        }
        showProgresBar(false);
    }

    public void onClickAuth(View view) {
        showProgresBar(true);
        authorization();
    }

    @Override
    public void onBackPressed() {

    }
}
