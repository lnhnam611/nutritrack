package com.example.nutritrack.fragmentsMainActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nutritrack.R;


public class HomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container,false);

        String userId = getArguments() != null ? getArguments().getString("userId") : "";
        Log.d("HomeFragment", "Received userId: " + userId);

        // TODO: Use userId to load user-specific data

        return view;
    }
}