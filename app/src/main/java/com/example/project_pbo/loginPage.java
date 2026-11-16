package com.example.project_pbo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class loginPage extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToRegister = findViewById(R.id.tvToRegister);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validasi via OOP Model Pengguna (Polimorfisme Akun -> Pengguna) dengan Dependency Injection
            Akun akun = new Pengguna("U01", user, pass, user, user + "@mail.com", new DeezerApiService());
            if (akun.login()) {
                Toast.makeText(this, "Login successful: " + user, Toast.LENGTH_SHORT).show();

                // Pindah ke Halaman Utama Musik
                Intent intent = new Intent(loginPage.this, homePage.class);
                intent.putExtra("USERNAME", user);
                intent.putExtra("EMAIL", user + "@mail.com");
                startActivity(intent);
                finish();
            }
        });

        tvToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, registerPage.class));
            finish();
        });
    }
}

