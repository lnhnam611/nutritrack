package com.example.nutritrack.api;

import java.util.List;

public class NutritionResponse {

    public List<Food> foods;
    public static class Food {
        public String food_name;
        public float nf_calories;
        public float serving_qty;
        public String serving_unit;
    }

}
