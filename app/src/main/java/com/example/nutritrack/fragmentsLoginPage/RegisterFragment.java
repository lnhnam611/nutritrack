package com.example.nutritrack.fragmentsLoginPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nutritrack.R;
import com.example.nutritrack.room.AppDatabase;
import com.example.nutritrack.room.dao.UserDao;
import com.example.nutritrack.room.entity.User;

import java.util.Arrays;

public class RegisterFragment extends Fragment {

    Button btnBackToLogin, btnRegister;
    Spinner spinner;
    EditText editTextName, editTextEmail, editTextPassword, editTextSecretAnswer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registration,container,false);
        btnBackToLogin = view.findViewById(R.id.buttonBackToLogin);
        btnRegister = view.findViewById(R.id.buttonRegister);
        spinner = view.findViewById(R.id.spinnerSecretQuestion);
        editTextName = view.findViewById(R.id.editTextName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextSecretAnswer = view.findViewById(R.id.editTextSecretAnswer);

        String[] secretQuestions = {
                "Select a secret question...",
                "What is your favorite color?",
                "What is your pet’s name?",
                "What is your mother’s maiden name?",
                "What city were you born in?",
                "What is your favorite food?",
                "What was your childhood nickname?",
                "What is the name of your first school?",
                "What is your favorite book?",
                "What is your best friend’s name?",
                "What was the make of your first car?"
        };

        spinner = view.findViewById(R.id.spinnerSecretQuestion);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,  // custom layout
                secretQuestions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString();
                String secretQuestion = spinner.getSelectedItem().toString();
                String secretAnswer = editTextSecretAnswer.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || secretAnswer.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedIndex = spinner.getSelectedItemPosition();
                if (selectedIndex == 0) {
                    Toast.makeText(getContext(), "Please select a secret question", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Insert into Room (on background thread)
                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
                    UserDao userDao = db.userDao();
                    // Check if email already exists
                    User existingUser = userDao.getUserByEmail(email);

                    if (existingUser != null) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Email already registered", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        User newUser = new User(name, email, password, secretQuestion, secretAnswer,0,0,0);
                        userDao.insertUser(newUser);

                        Log.d("DB", "User inserted: " + newUser.email);

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                            // Navigate back to login fragment
                            ((FragmentNavigationListener) requireActivity()).navigateToLogin();
                        });
                    }
                }).start();

            }
        });

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentNavigationListener) requireActivity()).navigateToLogin();
            }
        });

        return view;
    }
}
