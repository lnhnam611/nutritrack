package com.example.nutritrack.fragmentsMainActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nutritrack.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);

        String userId = getArguments() != null ? getArguments().getString("userId") : "";
        Log.d("HistoryFragment", "Received userId: " + userId);

        // TODO: Use userId to load user-specific data

        return view;
    }
}