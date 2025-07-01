package com.example.nutritrack.fragmentsMainActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritrack.R;
import com.example.nutritrack.api.ApiClient;
import com.example.nutritrack.api.NutritionApi;
import com.example.nutritrack.api.NutritionRequest;
import com.example.nutritrack.api.NutritionResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private EditText editTextMealName;
    private Button btnCheckCalories;
    private TextView textViewCaloriesResult;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container,false);

        editTextMealName = view.findViewById(R.id.editTextMealName);
        btnCheckCalories = view.findViewById(R.id.buttonCheckCalories);
        textViewCaloriesResult = view.findViewById(R.id.textViewCaloriesResult);

        String userId = getArguments() != null ? getArguments().getString("userId") : "";
        Log.d("HomeFragment", "Received userId: " + userId);

        btnCheckCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meal = editTextMealName.getText().toString().trim();
                if (meal.isEmpty()){
                    Toast.makeText(getActivity(), "Please enter a meal name", Toast.LENGTH_SHORT).show();
                } else {
                    fetchCalories(meal);

                }
            }
        });

        return view;
    }

    private void fetchCalories(String mealQuery) {
        NutritionApi apiService = ApiClient.getClient().create(NutritionApi.class);
        NutritionRequest request = new NutritionRequest(mealQuery);
        Call<NutritionResponse> call = apiService.getNutrients(request);

        call.enqueue(new Callback<NutritionResponse>() {
            @Override
            public void onResponse(Call<NutritionResponse> call, Response<NutritionResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    float totalCalories = 0f;
                    StringBuilder result = new StringBuilder();
                    for(NutritionResponse.Food food : response.body().foods) {
                        totalCalories += food.nf_calories;
                        result.append(food.food_name)
                                .append(": ")
                                .append(food.nf_calories)
                                .append(" kcal per ")
                                .append(food.serving_qty)
                                .append(" ")
                                .append(food.serving_unit)
                                .append("\n");
                    }
                    result.append("Total calories: ");
                    result.append(totalCalories);
                    textViewCaloriesResult.setText(result.toString());
                } else {
                    textViewCaloriesResult.setText("No data found or error in response.");
                }
            }

            @Override
            public void onFailure(Call<NutritionResponse> call, Throwable t) {
                textViewCaloriesResult.setText("API call failed: " + t.getMessage());
            }
        });



    }
}