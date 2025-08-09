package com.example.nutritrack.fragmentsMainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Context;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.nutritrack.LoginPage;
import com.example.nutritrack.R;
import com.example.nutritrack.room.AppDatabase;
import com.example.nutritrack.room.entity.User;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    Button logoutButton;
    private EditText name, email, weight, height, age, expectedCalories;
    private Spinner gender;
    private Button buttonEdit, buttonSave;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.editTextProfileName);
        email = view.findViewById(R.id.editTextProfileEmail);
        gender = view.findViewById(R.id.spinnerProfileGender);
        weight = view.findViewById(R.id.editTextProfileWeight);
        height = view.findViewById(R.id.editTextProfileHeight);
        age = view.findViewById(R.id.editTextProfileAge);
        expectedCalories = view.findViewById(R.id.editTextExpectCalories);
        buttonEdit = view.findViewById(R.id.buttonEditProfile);
        buttonSave = view.findViewById(R.id.buttonSaveProfile);
        logoutButton = view.findViewById(R.id.buttonLogout);

        setFieldsEnabled(false);
        buttonSave.setVisibility(View.GONE);
        //gender.setVisibility(View.GONE);

        // Setup gender spinner options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);

        // Load user data from Room
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("NutriPrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("userId", -1);
        String expectedCaolories = sharedPref.getString("calories","0");
        if (!expectedCaolories.equals("0")) {
            expectedCalories.setText(expectedCaolories);
        } else {
            expectedCalories.setText("");
        }


        if (userId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(requireContext());

        new Thread(() -> {
            User user = db.userDao().getUserById(userId);
            if (user != null) {
                requireActivity().runOnUiThread(() -> {
                    name.setText(user.name);
                    email.setText(user.email);

                    String genderVal = user.gender != null ? user.gender : "N/A";
                    int genderIndex = adapter.getPosition(genderVal);
                    gender.setSelection(genderIndex >= 0 ? genderIndex : 0);

                    weight.setText(user.weight != 0 ? String.valueOf(user.weight) : "N/A");
                    height.setText(user.height != 0 ? String.valueOf(user.height) : "N/A");
                    age.setText(user.age != 0 ? String.valueOf(user.age) : "N/A");
                });
            }
        }).start();

        buttonEdit.setOnClickListener(v -> {
            setFieldsEnabled(true);
            buttonSave.setVisibility(View.VISIBLE);
        });

        buttonSave.setOnClickListener(v -> {
            setFieldsEnabled(false);
            buttonSave.setVisibility(View.GONE);
            new Thread(() -> {
                User user = db.userDao().getUserById(userId);

                String newName = name.getText().toString().trim();
                String newGender = gender.getSelectedItem().toString();
                String newWeightStr = weight.getText().toString().trim();
                String newHeightStr = height.getText().toString().trim();
                String newAgeStr = age.getText().toString().trim();
                String expecteCalories = expectedCalories.getText().toString().trim();
                sharedPref.edit().putString("calories", expecteCalories).apply();

                double newWeight = newWeightStr.isEmpty() ? 0 : Double.parseDouble(newWeightStr);
                double newHeight = newHeightStr.isEmpty() ? 0 : Double.parseDouble(newHeightStr);
                int newAge = newAgeStr.isEmpty() ? 0 : Integer.parseInt(newAgeStr);

                if (user != null) {
                    user.setName(newName);
                    user.setGender(newGender.isEmpty() ? null : newGender);
                    user.setWeight(newWeight);
                    user.setHeight(newHeight);
                    user.setAge(newAge);

                    db.userDao().updateUser(user);

                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();

        });

        logoutButton.setOnClickListener(v -> {
            sharedPref.edit().remove("userId").apply();
            Intent intent = new Intent(getActivity(), LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void setFieldsEnabled(boolean enabled) {
        name.setEnabled(enabled);
        gender.setEnabled(enabled);
        weight.setEnabled(enabled);
        height.setEnabled(enabled);
        age.setEnabled(enabled);
        expectedCalories.setEnabled(enabled);
    }
}