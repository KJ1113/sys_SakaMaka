package com.example.leehangeol.sakamaka.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.leehangeol.sakamaka.R;
import com.kakao.usermgmt.LoginButton;

public class FragmentLogin extends Fragment {

    LoginActivity activity;
    private LoginButton login;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (LoginActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        activity = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_login, container, false);

        return view;
    }
}
