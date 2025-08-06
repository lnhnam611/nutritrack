package com.example.nutritrack.fragmentsMainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nutritrack.R;
import com.example.nutritrack.room.AppDatabase;
import com.example.nutritrack.room.entity.Meal;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    private BarChart barChart;
    private AppDatabase db;
    private int userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard,container,false);

        barChart = view.findViewById(R.id.barChart);
        db = AppDatabase.getInstance(requireContext());
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("NutriPrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("userId", -1);
        loadCalorieData();

        return view;
    }

    private void loadCalorieData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Meal> allMeals = db.mealDao().getMealsByUserId(userId);

            // Map date -> totalCalories
            Map<String, Double> calorieMap = new TreeMap<>();

            // Last 30 days
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -29); // start date
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());

            for (int i = 0; i < 30; i++) {
                calorieMap.put(sdf.format(calendar.getTime()), 0.0);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            for (Meal meal : allMeals) {
                String mealDate = sdf.format(meal.date);
                if (calorieMap.containsKey(mealDate)) {
                    double current = calorieMap.get(mealDate);
                    calorieMap.put(mealDate, current + meal.totalCalories);
                }
            }

            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            int index = 0;
            for (Map.Entry<String, Double> entry : calorieMap.entrySet()) {
                entries.add(new BarEntry(index, entry.getValue().floatValue()));
                labels.add(entry.getKey());
                index++;
            }

            BarDataSet dataSet = new BarDataSet(entries, "Calories");
            BarData barData = new BarData(dataSet);

            new Handler(Looper.getMainLooper()).post(() -> {
                barChart.setData(barData);
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barChart.getXAxis().setDrawGridLines(false);
                barChart.getAxisLeft().setDrawGridLines(false);
                barChart.getAxisRight().setDrawGridLines(false);

                barChart.getDescription().setEnabled(false);
                barChart.getLegend().setEnabled(true);

                barChart.setDrawBorders(true);     // ❌ No borders around chart
                barChart.setDrawGridBackground(false); // ❌ No background grid

                barChart.animateY(1000);
                barChart.invalidate();

                LimitLine goalLine = new LimitLine(300f, "Goal (2000 kcal)");
                goalLine.setLineColor(Color.RED);
                goalLine.setLineWidth(2f);
                goalLine.setTextColor(Color.RED);
                goalLine.setTextSize(12f);

                YAxis leftAxis = barChart.getAxisLeft();
                leftAxis.removeAllLimitLines(); // Optional: clear previous
                leftAxis.addLimitLine(goalLine);
            });
        });
    }
}