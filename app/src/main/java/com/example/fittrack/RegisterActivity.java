package com.example.fittrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText firstnameTextBox = findViewById(R.id.edit_text_firstname);
        EditText surnameTextBox = findViewById(R.id.edit_text_surname);
        EditText dobTextBox = findViewById(R.id.edit_text_DOB);
        EditText emailTextBox = findViewById(R.id.edit_text_register_email);
        EditText passwordTextBox = findViewById(R.id.edit_text_register_password);
        EditText confirm_passwordTextBox = findViewById(R.id.edit_text_confirm_password);
        Button btnRegister = findViewById(R.id.btnRegisterUser);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String firstname = firstnameTextBox.getText().toString();
                //String surname = surnameTextBox.getText().toString();
                //String dob = dobTextBox.getText().toString();
                String email = emailTextBox.getText().toString();
                String password = passwordTextBox.getText().toString();
                String confirm_password = confirm_passwordTextBox.getText().toString();

                if(password.equals(confirm_password)) {
                    Log.d("message", "Password equals correct");
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this , "Account successfully created", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        Log.d("RegisterActivity", "User successfully created");
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Account Registration failed. Please try again", Toast.LENGTH_SHORT);
                    Log.d("message", "User registration failed");
                }
            }
        });
    }

    public void RegisterUser(String email, String password) {

    }
}