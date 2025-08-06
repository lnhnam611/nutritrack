package com.example.nutritrack.fragmentsLoginPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nutritrack.MainActivity;
import com.example.nutritrack.R;
import com.example.nutritrack.room.AppDatabase;
import com.example.nutritrack.room.entity.User;

public class LoginFragment extends Fragment {

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, btnRegister, btnForgot;
    private AppDatabase appDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login,container,false);

        btnRegister = view.findViewById(R.id.buttonRegister);
        btnForgot = view.findViewById(R.id.buttonForgotPassword);
        btnLogin = view.findViewById(R.id.buttonLogin);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        appDatabase = AppDatabase.getInstance(requireContext());

        btnRegister.setOnClickListener(v -> {
            ((FragmentNavigationListener) requireActivity()).navigateToRegister();
        });

        btnForgot.setOnClickListener(v -> {
            ((FragmentNavigationListener) requireActivity()).navigateToForgotPassword();
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Run DB query in background thread
                new Thread(() -> {
                    User user = appDatabase.userDao().getUserByEmail(email);
                    if (user != null && user.password.equals(password)) {
                        // Success: save userId and navigate
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();

                            // Save userId to SharedPreferences
                            SharedPreferences sharedPref = requireActivity().getSharedPreferences("NutriPrefs", Context.MODE_PRIVATE);
                            sharedPref.edit().putInt("userId", user.id).apply();

                            // Navigate to MainActivity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("userId", user.id);
                            startActivity(intent);

                            requireActivity().finish();
                        });
                    } else {
                        // Failed login
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        });

        return view;
    }
}
