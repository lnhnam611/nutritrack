package com.example.nutritrack.fragmentsMainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nutritrack.R;
import com.example.nutritrack.room.AppDatabase;
import com.example.nutritrack.room.dao.MealDao;
import com.example.nutritrack.room.entity.Meal;
import com.example.nutritrack.utilities.MealAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private MealAdapter adapter;
    private MealDao mealDao;
    private int userId = -1; // Get this from SharedPreferences or arguments
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewMeals);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = AppDatabase.getInstance(requireContext());
        mealDao = db.mealDao();

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("NutriPrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("userId", -1);

        Log.d("HistoryFragment", "Received userId: " + userId);

        loadMeals();

        adapter = new MealAdapter(new ArrayList<>(), meal -> {
            // Delete clicked
            new Thread(() -> {
                mealDao.deleteMeal(meal);
                loadMeals(); // Refresh
            }).start();
        });

        recyclerView.setAdapter(adapter);
    }

    private void loadMeals() {
        new Thread(() -> {
            List<Meal> meals = mealDao.getMealsByUserId(userId);
            requireActivity().runOnUiThread(() -> {
                adapter.updateMeals(new ArrayList<>(meals));
            });
        }).start();
    }
}