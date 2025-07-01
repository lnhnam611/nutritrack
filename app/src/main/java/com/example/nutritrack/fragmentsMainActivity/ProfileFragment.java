package com.example.nutritrack.fragmentsMainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Context;
import android.widget.EditText;


import com.example.nutritrack.LoginPage;
import com.example.nutritrack.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    Button logoutButton;
    private EditText name, email, gender, weight, height, age;
    private Button buttonEdit, buttonSave;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);


        name = view.findViewById(R.id.editTextProfileName);
        email = view.findViewById(R.id.editTextProfileEmail);
        gender = view.findViewById(R.id.editTextProfileGender);
        weight = view.findViewById(R.id.editTextProfileWeight);
        height = view.findViewById(R.id.editTextProfileHeight);
        age = view.findViewById(R.id.editTextProfileAge);

        buttonEdit = view.findViewById(R.id.buttonEditProfile);
        buttonSave = view.findViewById(R.id.buttonSaveProfile);
        logoutButton = view.findViewById(R.id.buttonLogout);

        String userId = getArguments() != null ? getArguments().getString("userId") : "";
        Log.d("ProfileFragment", "Received userId: " + userId);


        buttonEdit.setOnClickListener(v -> {
            // Enable fields
            setFieldsEnabled(true);
            buttonSave.setVisibility(View.VISIBLE);
        });

        



        logoutButton.setOnClickListener(v -> {

            SharedPreferences sharedPref = requireActivity().getSharedPreferences("NutriPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("userId"); // or editor.clear() if you want to remove all
            editor.apply();

            // Navigate back to login
            Intent intent = new Intent(getActivity(), LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

        return view;
    }
}