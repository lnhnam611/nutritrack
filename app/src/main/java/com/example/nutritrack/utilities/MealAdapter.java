package com.example.nutritrack.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritrack.R;
import com.example.nutritrack.room.entity.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private ArrayList<Meal> mealList;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Meal meal);
    }

    public MealAdapter(ArrayList<Meal> mealList, OnDeleteClickListener listener) {
        this.mealList = mealList;
        this.deleteClickListener = listener;
    }


    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.meal_item, viewGroup, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {

        Meal meal = mealList.get(position);

        holder.textMealName.setText(meal.foodName);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        holder.textMealDate.setText(dateFormat.format(meal.date));

        holder.textMealCalories.setText(String.format(Locale.getDefault(), "%.0f kcal", meal.totalCalories));

        holder.imageDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(meal);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }


    public static class MealViewHolder extends  RecyclerView.ViewHolder{
        TextView textMealName, textMealDate, textMealCalories;
        ImageView imageDelete;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            textMealName = itemView.findViewById(R.id.textMealName);
            textMealDate = itemView.findViewById(R.id.textMealDate);
            textMealCalories = itemView.findViewById(R.id.textMealCalories);
            imageDelete = itemView.findViewById(R.id.imageDeleteMeal);
        }
    }

    public void updateMeals(ArrayList<Meal> updatedList) {
        this.mealList = updatedList;
        notifyDataSetChanged();
    }


}
