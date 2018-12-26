package com.example.leehangeol.sakamaka.Login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.example.leehangeol.sakamaka.ARactivity;
import com.example.leehangeol.sakamaka.Main.MainActivity;
import com.example.leehangeol.sakamaka.R;
import com.kakao.auth.AccessTokenCallback;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private final int LOGIN = 1;
    private final int SELECT = 2;

    private String path;
    private String name;

    FragmentLogin fLogin;
    FragmentLoginSelect fSelect;

    private SessionCallback callback;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        callFragment(SELECT);
    }

    public void callFragment(int frament_no) {
        // 프래그먼트 사용을 위해
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (frament_no) {
            case 1:
                if(fLogin ==null) {
                    callback = new SessionCallback();
                    Session.getCurrentSession().addCallback(callback);
                    Session.getCurrentSession().checkAndImplicitOpen();

                    fLogin = new FragmentLogin();
                    transaction.replace(R.id.fragment_container, fLogin);
                    transaction.commit();
                }
                else {
                    transaction.replace(R.id.fragment_container, fLogin);
                    transaction.commit();
                }
                // '프래그먼트1' 호출
                break;

            case 2:
                if(fSelect ==null) {
                    fSelect = new FragmentLoginSelect();
                    transaction.replace(R.id.fragment_container, fSelect);
                    transaction.commit();
                }
                else {
                    transaction.replace(R.id.fragment_container, fSelect);
                    transaction.commit();
                }
                break;
        }
    }

    public void arView()
    {
        Intent arIntent = new Intent(this, ARactivity.class);
        startActivity(arIntent);
    }

    public void login()
    {
        Intent arIntent = new Intent(this, MainActivity.class);
        startActivity(arIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }

        private void requestMe() {
            List<String> keys = new ArrayList<>();
            keys.add("properties.nickname");
            keys.add("properties.profile_image");
            keys.add("kakao_account.email");

            UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }

                @Override
                public void onSuccess(MeV2Response response) {
                    name = response.getNickname();
                    path = response.getProfileImagePath();
                    final Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("img",path);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }


//    public void requestMe() {
//        //유저의 정보를 받아오는 함수
//        UserManagement.getInstance().requestMe(new MeResponseCallback() {
//            @Override
//            public void onFailure(ErrorResult errorResult) {
////                super.onFailure(errorResult);
//            }
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//
//            }
//            @Override
//            public void onNotSignedUp() {
//                //카카오톡 회원이 아닐시
//            }
//            @Override
//            public void onSuccess(UserProfile result) {
//                path = result.getNickname();
//                name = result.getProfileImagePath();
//            }
//        });
//    }


    private void handleScopeError(UserAccount account) {
        List<String> neededScopes = new ArrayList<>();
        if (account.needsScopeAccountEmail()) {
            neededScopes.add("account_email");
        }
        if (account.needsScopeGender()) {
            neededScopes.add("gender");
        }
        Session.getCurrentSession().updateScopes(this, neededScopes, new
                AccessTokenCallback() {
                    @Override
                    public void onAccessTokenReceived(AccessToken accessToken) {
                        // 유저에게 성공적으로 동의를 받음. 토큰을 재발급 받게 됨.
                    }

                    @Override
                    public void onAccessTokenFailure(ErrorResult errorResult) {
                        // 동의 얻기 실패
                    }
                });
    }






//    public void request(){
//
//        UserManagement.getInstance().requestMe(new MeResponseCallback() {
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//
//                Log.d("error", "Session Closed Error is " + errorResult.toString());
//
//            }
//
//            @Override
//            public void onNotSignedUp() {
//
//            }
//
//            @Override
//            public void onSuccess(UserProfile result) {
//
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.putExtra("name",result.getNickname());
//                intent.putExtra("img",result.getThumbnailImagePath());
//                startActivity(intent);
//            }
//        });
//    }
//
//    private class SessionCallback implements ISessionCallback{
//        @Override
//
//        public void onSessionOpened() {
//
//            request();
//
//        }
//
//        @Override
//        public void onSessionOpenFailed(KakaoException exception) {
//
//            Log.d("error", "Session Fail Error is " + exception.getMessage().toString());
//        }
//    }
}
