package com.example.project_pbo;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class registerPage extends AppCompatActivity {
    EditText username, email, password;
    Button register;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        username = findViewById(R.id.etUsername);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        register = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        register.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String mail = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Buat objek model Pengguna (OOP Enkapsulasi & Abstraksi) dengan Dependency Injection
                Pengguna penggunaBaru = new Pengguna("U-" + System.currentTimeMillis(), user, pass, user, mail, new DeezerApiService());

                Toast.makeText(this, "Registration successful!",
                        Toast.LENGTH_SHORT).show();

                // Pindah ke loginPage setelah registrasi sukses
                Intent intent = new Intent(registerPage.this, loginPage.class);
                startActivity(intent);
                finish();
            }
        });

        // Klik "Log in" pindah ke loginPage
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(registerPage.this, loginPage.class);
            startActivity(intent);
            finish();
        });
    }
}
