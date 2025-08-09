package com.example.nutritrack.fragmentsMainActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.nutritrack.R;
import com.example.nutritrack.room.AppDatabase;
import com.example.nutritrack.room.dao.MealDao;
import com.example.nutritrack.room.entity.Meal;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    private BarChart barChart;
    private AppDatabase db;
    private int userId;
    String expectedCalories;

    private TextView textCurrentCalories, textExpectCalories;

    private Button btnAddMeals, btnRemoveMeals;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard,container,false);

        barChart = view.findViewById(R.id.barChart);
        textCurrentCalories = view.findViewById(R.id.textCurrentCalories);
        textExpectCalories = view.findViewById(R.id.textExpectCalories);
        db = AppDatabase.getInstance(requireContext());
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("NutriPrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("userId", -1);
        expectedCalories = sharedPref.getString("calories","0");
        //populateDummyMeals();
        loadCalorieData();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAddMeals = view.findViewById(R.id.btnAddMeals);
        btnRemoveMeals = view.findViewById(R.id.btnRemoveMeals);

        btnAddMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    populateDummyMeals();  // insert dummy meals
                    new Handler(Looper.getMainLooper()).post(() -> loadCalorieData());  // update chart
                });
            }
        });

        btnRemoveMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    db.mealDao().deleteAllMealsForUser(userId);  // delete all meals
                    new Handler(Looper.getMainLooper()).post(() -> loadCalorieData());  // update chart
                });
            }
        });

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

            // Get today's total calories
            String todayKey = sdf.format(new Date());
            double todayCalories = calorieMap.containsKey(todayKey) ? calorieMap.get(todayKey) : 0.0;

            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            int index = 0;
            for (Map.Entry<String, Double> entry : calorieMap.entrySet()) {
                entries.add(new BarEntry(index, entry.getValue().floatValue()));
                labels.add(entry.getKey());
                index++;
            }

            BarDataSet dataSet = new BarDataSet(entries, "Calories");
            dataSet.setDrawValues(false);
            BarData barData = new BarData(dataSet);

            double finalTodayCalories = todayCalories;
            new Handler(Looper.getMainLooper()).post(() -> {
                barChart.setData(barData);
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barChart.getXAxis().setDrawGridLines(false);
                barChart.getAxisLeft().setDrawGridLines(false);
                barChart.getAxisRight().setDrawGridLines(false);

                barChart.getDescription().setEnabled(false);
                barChart.getLegend().setEnabled(false);

                barChart.setDrawBorders(true);
                barChart.setDrawGridBackground(false);

                barChart.animateY(1000);
                barChart.invalidate();

                LimitLine goalLine = new LimitLine(Float.parseFloat(expectedCalories),
                        "Limit "+expectedCalories + " kCal");
                goalLine.setLineColor(Color.RED);
                goalLine.setLineWidth(2f);
                goalLine.setTextColor(Color.RED);
                goalLine.setTextSize(12f);
                YAxis leftAxis = barChart.getAxisLeft();
                //leftAxis.removeAllLimitLines(); // Optional: clear previous
                if (!expectedCalories.equals("0")) {
                    leftAxis.addLimitLine(goalLine);
                }
                //set min max for chart
                leftAxis.setAxisMinimum(0f);
                leftAxis.setAxisMaximum(5000f);
                barChart.getAxisRight().setAxisMaximum(0f);
                barChart.getAxisRight().setAxisMaximum(5000f);

                //show calories summary
                DecimalFormat decimalFormat = new DecimalFormat("0000.00");
                String formattedTodayCalories = decimalFormat.format(todayCalories);
                textExpectCalories.setText(expectedCalories);
                textCurrentCalories.setText(String.valueOf(formattedTodayCalories));

                float expected = Float.parseFloat(expectedCalories);
                int colorFrom = ContextCompat.getColor(requireContext(), android.R.color.black);
                int colorTo = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark);

                if (todayCalories > expected) {
                    // Animate to red
                    ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnim.setDuration(500); // 500ms duration
                    colorAnim.addUpdateListener(animator -> {
                        textCurrentCalories.setTextColor((int) animator.getAnimatedValue());
                    });
                    colorAnim.start();
                } else {
                    // Animate back to black
                    ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), textCurrentCalories.getCurrentTextColor(), colorFrom);
                    colorAnim.setDuration(500);
                    colorAnim.addUpdateListener(animator -> {
                        textCurrentCalories.setTextColor((int) animator.getAnimatedValue());
                    });
                    colorAnim.start();
                }

            });
        });
    }

    private void populateDummyMeals() {
        Executors.newSingleThreadExecutor().execute(() -> {

            String[] mealNames = {
                    "100g Grilled Chicken Breast",
                    "1 Slice of Avocado Toast",
                    "200g Spaghetti Bolognese",
                    "150g Beef Stir Fry",
                    "1 Bowl of Oatmeal with Berries",
                    "1 Turkey Sandwich",
                    "2-Egg Veggie Omelette",
                    "1 Glass of Protein Smoothie",
                    "1 Tuna Wrap",
                    "1 Bowl of Miso Soup with Tofu",
                    "3 Pancakes with Syrup",
                    "150g Baked Salmon with Quinoa",
                    "1 Chicken Caesar Wrap",
                    "1 Cup Greek Yogurt with Granola",
                    "2 Slices Peanut Butter Banana Toast",
                    "2 Shrimp Tacos",
                    "1 Veggie Burrito Bowl",
                    "1 Cup of Egg Fried Rice",
                    "100g Hummus with Pita",
                    "1 Bowl of Lentil Soup"
            };
            Random random = new Random();
            //String randomMealName = mealNames[random.nextInt(mealNames.length)];

            MealDao mealDao = db.mealDao();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();

            //mealDao.deleteAllMealsForUser(userId);

            for (int i = 0; i < 30; i++) {
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_YEAR, -i);
                Date mealDate = calendar.getTime();

                // Add 2 meals per day
                for (int j = 0; j < 2; j++) {
                    Meal meal = new Meal();
                    meal.userId = userId;
                    meal.foodName = mealNames[random.nextInt(mealNames.length)];;
                    meal.totalCalories = 300 + (j * random.nextInt(mealNames.length)); // 300, 400
                    meal.date = mealDate;
                    mealDao.insertMeal(meal);
                }
            }
        });
    }

}