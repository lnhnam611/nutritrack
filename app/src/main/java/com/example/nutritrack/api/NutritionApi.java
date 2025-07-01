package com.example.nutritrack.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NutritionApi {
    @Headers(
            {
                    "x-app-id: f944bb60",
                    "x-app-key: b56bca1e020b5a1943d9aada56f353e5",
                    "Content-Type: application/json"
            }
    )
    @POST("v2/natural/nutrients")
    Call<NutritionResponse> getNutrients(@Body NutritionRequest request);
}
