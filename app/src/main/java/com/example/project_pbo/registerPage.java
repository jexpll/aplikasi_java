package com.example.project_pbo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class registerPage extends AppCompatActivity {
    EditText username, email, password;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        username = findViewById(R.id.etUsername);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        register = findViewById(R.id.btnRegister);

        register.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String mail = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Registration successful!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}