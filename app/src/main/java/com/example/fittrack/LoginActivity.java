package com.example.fittrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText Email = findViewById(R.id.edit_text_firstname);
        EditText Password = findViewById(R.id.edit_text_password);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                LoginUser(email, password);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void LoginUser(String email, String password) {
        ParseUser.logInInBackground(email, password, (UserToLogin, e) -> {
            if (UserToLogin != null) {
                Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                if(e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    Toast.makeText(LoginActivity.this, "Invalid email or password entered", Toast.LENGTH_SHORT).show();
                    Log.d("message", "Invalid email or password");
                } else {
                ParseUser.logOut();
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("message", e.getMessage());
            }
        }
    });
}
}