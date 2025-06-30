package com.example.nutritrack;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nutritrack.fragmentsLoginPage.ForgotPassFragment;
import com.example.nutritrack.fragmentsLoginPage.FragmentNavigationListener;
import com.example.nutritrack.fragmentsLoginPage.LoginFragment;
import com.example.nutritrack.fragmentsLoginPage.RegisterFragment;

public class LoginPage extends AppCompatActivity implements FragmentNavigationListener {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fragmentManager = getSupportFragmentManager();

        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.loginFragmentContainer,loginFragment,"fragLogin");
        transaction.commit();



    }

    @Override
    public void navigateToLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginFragmentContainer, new LoginFragment(), "fragLogin")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void navigateToRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginFragmentContainer, new RegisterFragment(), "fragRegister")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void navigateToForgotPassword() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginFragmentContainer,new ForgotPassFragment(),"fragForgotPass")
                .addToBackStack(null)
                .commit();

    }
}