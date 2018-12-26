package com.example.leehangeol.sakamaka.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.leehangeol.sakamaka.R;

public class FragmentLoginSelect extends Fragment {

    LoginActivity activity;

    private Button register;
    private Button noregister;

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
        View view = inflater.inflate(R.layout.fragment_select, container, false);

        register = (Button) view.findViewById(R.id.register);
        noregister = (Button) view.findViewById(R.id.noregister);

        register.setOnClickListener(new View.OnClickListener() {
            // 요청을 보내야 하는데 메인 액티비티에 다가 메소드를 하나 만들어야 한다.
            @Override
            public void onClick(View v) {
                activity.callFragment(1);
            }
        });

        noregister.setOnClickListener(new View.OnClickListener() {
            // 요청을 보내야 하는데 메인 액티비티에 다가 메소드를 하나 만들어야 한다.
            @Override
            public void onClick(View v) {
                activity.arView();
            }
        });

        return view;
    }

}
