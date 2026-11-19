package com.example.cuoiki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.cuoiki.utils.SessionManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cuoiki.database.DatabaseHelper;
import com.example.cuoiki.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {

            Intent intent = new Intent(
                    LoginActivity.this,
                    HomeActivity.class
            );

            startActivity(intent);
            finish();

            return;
        }

        binding.btnLogin.setOnClickListener(v -> login());

        binding.txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
            finish();
        });
    }

    private void login() {

        String username = binding.editUsername.getText() == null
                ? ""
                : binding.editUsername.getText().toString().trim();

        String password = binding.editPassword.getText() == null
                ? ""
                : binding.editPassword.getText().toString().trim();

        binding.editUsername.setError(null);
        binding.editPassword.setError(null);

        if (username.isEmpty()) {
            binding.editUsername.setError("Vui lòng nhập tên đăng nhập");
            binding.editUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.editPassword.setError("Vui lòng nhập mật khẩu");
            binding.editPassword.requestFocus();
            return;
        }

        boolean success = databaseHelper.checkLogin(
                username,
                password
        );

        if (success) {
            sessionManager.saveLogin(username);
            Toast.makeText(
                    this,
                    "Đăng nhập thành công",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    LoginActivity.this,
                    HomeActivity.class
            );

            startActivity(intent);
            finish();

        } else {
            Toast.makeText(
                    this,
                    "Sai tài khoản hoặc mật khẩu",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        binding = null;
        super.onDestroy();
    }
}