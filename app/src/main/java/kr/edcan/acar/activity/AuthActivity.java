package kr.edcan.acar.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import kr.edcan.acar.utils.DataManager;
import kr.edcan.acar.models.FacebookUser;
import kr.edcan.acar.utils.NetworkHelper;
import kr.edcan.acar.utils.NetworkInterface;
import kr.edcan.acar.R;
import kr.edcan.acar.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    ImageView logo;
    LoginButton fbLogin;
    CallbackManager manager;
    NetworkInterface service;
    Call<FacebookUser> loginByFacebook;
    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        dataManager = new DataManager();
        dataManager.initializeManager(getApplicationContext());
        initialize();
        validateUserToken();
    }

    private void animate() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -500.0f);
                animation.setDuration(1000);
                animation.setFillAfter(true);
                logo.startAnimation(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fbLogin.setVisibility(View.VISIBLE);
                        Animation fadeIn = AnimationUtils.loadAnimation(AuthActivity.this, R.anim.fade_in);
                        fbLogin.setAnimation(fadeIn);
                    }
                }, 500);
            }
        }, 1000);
    }

    private void validateUserToken() {
        Pair<Boolean, User> userPair = dataManager.getActiveUser();
        if (!userPair.first) {
            setFacebook();
        } else {
            new LoadFacebookInfo().execute(dataManager.getFacebookUserCredential());
        }
    }

    private void initialize() {
        manager = CallbackManager.Factory.create();
        fbLogin = (LoginButton) findViewById(R.id.auth_facebookbutton);
        logo = (ImageView) findViewById(R.id.main_logo);
        service = NetworkHelper.getNetworkInstance();
    }

    private void setFacebook() {
        animate();
        LoginManager.getInstance().registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                new LoadFacebookInfo().execute(loginResult.getAccessToken().getToken());
                Log.e("asdf_tokenfb", loginResult.getAccessToken().getToken());
                dataManager.saveUserCredential(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(AuthActivity.this, "로그인 인증 중에 문제가 발생했습니다.\n서비스 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        manager.onActivityResult(requestCode, resultCode, data);
    }

    class LoadFacebookInfo extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            loginByFacebook = service.userLogin(strings[0]);
            loginByFacebook.enqueue(new Callback<FacebookUser>() {
                @Override
                public void onResponse(Call<FacebookUser> call, Response<FacebookUser> response) {
                    switch (response.code()) {
                        case 200:
                            final FacebookUser facebookUser = response.body();
                            dataManager.saveFacebookUserInfo(facebookUser);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                    Toast.makeText(AuthActivity.this, facebookUser.content.name + " 님 안녕하세요!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }, 500);
                            break;
                        case 401:
                            Toast.makeText(AuthActivity.this, "세션이 만료되었습니다.\n다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<FacebookUser> call, Throwable t) {
                    Log.e("asdf", t.getMessage());
                    Toast.makeText(AuthActivity.this, "로그인 인증 중에 문제가 발생했습니다.\n서비스 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}