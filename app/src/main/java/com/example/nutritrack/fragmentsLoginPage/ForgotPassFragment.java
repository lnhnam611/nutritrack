package com.example.nutritrack.fragmentsLoginPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nutritrack.R;
import com.example.nutritrack.room.AppDatabase;
import com.example.nutritrack.room.dao.UserDao;
import com.example.nutritrack.room.entity.User;

import java.util.concurrent.Executors;

public class ForgotPassFragment extends Fragment {

    private EditText editTextEmail, editTextAnswer, editTextNewPassword;
    private Spinner spinnerQuestions;
    private Button btnResetPassword, btnConfirmAnswer, btnSetNewPassword, btnBackToLogin;
    private TextView textViewShowQuestionAndAnswer, textViewShowNewPassword;
    private AppDatabase db;
    private UserDao userDao;
    private User currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forgotpassword,container,false);

        // Initialize views
        editTextEmail = view.findViewById(R.id.editTextForgotEmail);
        editTextAnswer = view.findViewById(R.id.editTextSecretAnswer);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        spinnerQuestions = view.findViewById(R.id.spinnerSecretQuestion);

        textViewShowNewPassword = view.findViewById(R.id.textViewShowNewPassword);
        textViewShowQuestionAndAnswer = view.findViewById(R.id.textViewShowQuestionAndAnswer);

        btnResetPassword = view.findViewById(R.id.buttonResetPassword);
        btnConfirmAnswer = view.findViewById(R.id.buttonConfirmAnswer);
        btnSetNewPassword = view.findViewById(R.id.buttonConfirmNewPassword);
        btnBackToLogin = view.findViewById(R.id.buttonBackToLogin);

        db = AppDatabase.getInstance(requireContext());
        userDao = db.userDao();

        //set up spinner
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


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,  // custom layout
                secretQuestions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuestions.setAdapter(adapter);

        // Hide extra steps initially
        editTextAnswer.setVisibility(View.GONE);
        spinnerQuestions.setVisibility(View.GONE);
        btnConfirmAnswer.setVisibility(View.GONE);
        editTextNewPassword.setVisibility(View.GONE);
        btnSetNewPassword.setVisibility(View.GONE);
        textViewShowQuestionAndAnswer.setVisibility(View.GONE);
        textViewShowNewPassword.setVisibility(View.GONE);

        btnResetPassword.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();

            Executors.newSingleThreadExecutor().execute(() -> {
                currentUser = userDao.getUserByEmail(email);

                requireActivity().runOnUiThread(() -> {
                    if (currentUser != null) {
                        // Setup spinner
                        spinnerQuestions.setVisibility(View.VISIBLE);
                        editTextAnswer.setVisibility(View.VISIBLE);
                        btnConfirmAnswer.setVisibility(View.VISIBLE);
                        textViewShowQuestionAndAnswer.setVisibility(View.VISIBLE);
                        btnResetPassword.setVisibility(View.GONE);
                        editTextEmail.setEnabled(false);

                        int position = adapter.getPosition(currentUser.getSecretQuestion());
                        spinnerQuestions.setSelection(position);

                    } else {
                        Toast.makeText(getContext(), "Email not found.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        btnConfirmAnswer.setOnClickListener(v -> {
            String selectedQuestion = spinnerQuestions.getSelectedItem().toString();
            String answer = editTextAnswer.getText().toString().trim();

            if (currentUser != null &&
                    selectedQuestion.equals(currentUser.getSecretQuestion()) &&
                    answer.equalsIgnoreCase(currentUser.getSecretAnswer())) {

                editTextNewPassword.setVisibility(View.VISIBLE);
                btnSetNewPassword.setVisibility(View.VISIBLE);
                textViewShowNewPassword.setVisibility(View.VISIBLE);
                btnConfirmAnswer.setVisibility(View.GONE);
                spinnerQuestions.setEnabled(false);
                editTextAnswer.setEnabled(false);

            } else {
                Toast.makeText(getContext(), "Incorrect answer or question mismatch.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSetNewPassword.setOnClickListener(v -> {
            String newPassword = editTextNewPassword.getText().toString().trim();

            if (!newPassword.isEmpty()) {
                currentUser.setPassword(newPassword);

                Executors.newSingleThreadExecutor().execute(() -> {
                    userDao.updateUser(currentUser);

                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Password updated successfully.", Toast.LENGTH_SHORT).show();
                        ((FragmentNavigationListener) requireActivity()).navigateToLogin();
                    });
                });
            } else {
                Toast.makeText(getContext(), "Please enter a new password.", Toast.LENGTH_SHORT).show();
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
