package com.example.fittrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.parse.ParseUser;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText firstnameTextBox;
    private EditText surnameTextBox;
    private EditText dobTextBox;
    private EditText emailTextBox;
    private EditText passwordTextBox;
    private EditText confirm_passwordTextBox;
    private String uuid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstnameTextBox = findViewById(R.id.edit_text_firstname);
        surnameTextBox = findViewById(R.id.edit_text_surname);
        dobTextBox = findViewById(R.id.edit_text_DOB);
        emailTextBox = findViewById(R.id.edit_text_register_email);
        passwordTextBox = findViewById(R.id.edit_text_register_password);
        confirm_passwordTextBox = findViewById(R.id.edit_text_confirm_password);
        Button btnRegister = findViewById(R.id.btnRegisterUser);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });
    }

    public void RegisterUser() {
        String firstname = firstnameTextBox.getText().toString();
        String surname = surnameTextBox.getText().toString();
        String email = emailTextBox.getText().toString();
        String password = passwordTextBox.getText().toString();
        String confirm_password = confirm_passwordTextBox.getText().toString();
        String dob = dobTextBox.getText().toString();


        if(password.equals(confirm_password)) {
            Log.d("message", "Password equals correct");
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                uuid = user.getUid();

                                Map<String, Object> UserToInsert = new HashMap<>();
                                UserToInsert.put("FirstName", firstname);
                                UserToInsert.put("Surname", surname);
                                UserToInsert.put("DOB", StringtoTimeStamp(dob));

                                Log.d("Passed inserts", "Passed inserts");

                                db.collection("Users")
                                        .document(uuid)
                                        .set(UserToInsert)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void avoid) {
                                                Log.d("Document success", "User data added successfully to Firestore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Error adding document", e.getMessage());
                                            }
                                        });

                                Toast.makeText(RegisterActivity.this , "Account successfully created", Toast.LENGTH_SHORT).show();
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                },5000);
                                Log.d("RegisterActivity", "User successfully created");
                            }
                        }
                    });
        } else {
            Toast.makeText(RegisterActivity.this, "Account Registration failed. Please try again", Toast.LENGTH_SHORT);
            Log.d("message", "User registration failed");
        }
    }

    private Timestamp StringtoTimeStamp(String DateOfBirth) {
        Timestamp convertedDOB = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(DateOfBirth);
            convertedDOB = new Timestamp(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedDOB;
    }
}