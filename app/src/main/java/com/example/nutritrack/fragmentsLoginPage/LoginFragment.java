package com.example.nutritrack.fragmentsLoginPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Context;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nutritrack.MainActivity;
import com.example.nutritrack.R;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login,container,false);

        Button btnRegister = view.findViewById(R.id.buttonRegister);
        Button btnForgot = view.findViewById(R.id.buttonForgotPassword);
        Button btnLogin = view.findViewById(R.id.buttonLogin);


        btnRegister.setOnClickListener(v -> {
            ((FragmentNavigationListener) requireActivity()).navigateToRegister();
        });

        btnForgot.setOnClickListener(v -> {
            ((FragmentNavigationListener) requireActivity()).navigateToForgotPassword();
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assume login is successful here (you can add real logic later)
                String userId = "user123";  // You can dynamically get this after login success

                SharedPreferences sharedPref = requireActivity().getSharedPreferences("NutriPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userId", userId);
                editor.apply();

                // Prepare intent with bundle data
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                // Optionally finish LoginPage activity so user can't go back to login
                getActivity().finish();
            }
        });

        return view;
    }
}
