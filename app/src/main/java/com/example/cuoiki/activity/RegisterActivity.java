package com.example.cuoiki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cuoiki.database.DatabaseHelper;
import com.example.cuoiki.databinding.ActivityRegisterBinding;
import com.example.cuoiki.model.Account;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.getWritableDatabase();

        binding.btnRegister.setOnClickListener(v -> registerAccount());

        binding.txtLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });
    }

    private void registerAccount() {

        String fullName = getText(binding.editFullName);
        String username = getText(binding.editUsername);
        String password = getText(binding.editPassword);
        String email = getText(binding.editEmail);

        if (!validateInput(fullName, username, password, email)) {
            return;
        }

        if (databaseHelper.isUsernameExists(username)) {
            binding.editUsername.setError("Tên đăng nhập đã tồn tại");
            binding.editUsername.requestFocus();
            return;
        }

        if (databaseHelper.isEmailExists(email)) {
            binding.editEmail.setError("Email đã được sử dụng");
            binding.editEmail.requestFocus();
            return;
        }

        Account account = new Account(
                username,
                password,
                fullName,
                email
        );

        boolean inserted = databaseHelper.insertAccount(account);

        if (inserted) {
            Toast.makeText(
                    this,
                    "Đăng ký tài khoản thành công",
                    Toast.LENGTH_SHORT
            ).show();

            clearForm();
        } else {
            Toast.makeText(
                    this,
                    "Đăng ký thất bại, vui lòng thử lại",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private boolean validateInput(
            String fullName,
            String username,
            String password,
            String email
    ) {

        clearErrors();

        if (TextUtils.isEmpty(fullName)) {
            binding.editFullName.setError("Vui lòng nhập họ và tên");
            binding.editFullName.requestFocus();
            return false;
        }

        if (fullName.length() < 2) {
            binding.editFullName.setError("Họ và tên phải có ít nhất 2 ký tự");
            binding.editFullName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(username)) {
            binding.editUsername.setError("Vui lòng nhập tên đăng nhập");
            binding.editUsername.requestFocus();
            return false;
        }

        if (username.length() < 4) {
            binding.editUsername.setError(
                    "Tên đăng nhập phải có ít nhất 4 ký tự"
            );
            binding.editUsername.requestFocus();
            return false;
        }

        if (username.contains(" ")) {
            binding.editUsername.setError(
                    "Tên đăng nhập không được chứa khoảng trắng"
            );
            binding.editUsername.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.editPassword.setError("Vui lòng nhập mật khẩu");
            binding.editPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            binding.editPassword.setError(
                    "Mật khẩu phải có ít nhất 6 ký tự"
            );
            binding.editPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            binding.editEmail.setError("Vui lòng nhập email");
            binding.editEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editEmail.setError("Email không đúng định dạng");
            binding.editEmail.requestFocus();
            return false;
        }

        return true;
    }

    private String getText(
            com.google.android.material.textfield.TextInputEditText editText
    ) {
        return editText.getText() == null
                ? ""
                : editText.getText().toString().trim();
    }

    private void clearErrors() {
        binding.editFullName.setError(null);
        binding.editUsername.setError(null);
        binding.editPassword.setError(null);
        binding.editEmail.setError(null);
    }

    private void clearForm() {
        binding.editFullName.setText("");
        binding.editUsername.setText("");
        binding.editPassword.setText("");
        binding.editEmail.setText("");

        binding.editFullName.requestFocus();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        binding = null;
        super.onDestroy();
    }
}