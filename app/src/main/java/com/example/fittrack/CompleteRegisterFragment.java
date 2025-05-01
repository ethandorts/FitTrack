package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CompleteRegisterFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editEmail, editPassword, editPassword2;
    private String FirstName, Surname, DOB, Goal;
    private int Weight, Height;
    private Button btnRegister;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        Bundle bundle = getArguments();
        if(bundle != null) {
            FirstName = (String) bundle.get("FirstName");
            Surname = (String) bundle.get("Surname");
            DOB = (String) bundle.get("DOB");
            Goal = (String) bundle.get("Goal");
            Weight = (int) bundle.get("Weight");
            Height = (int) bundle.get("Height");
        }

        editEmail = view.findViewById(R.id.edit_text_email);
        editPassword = view.findViewById(R.id.edit_text_password);
        editPassword2 = view.findViewById(R.id.edit_text_reenter_password);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_complete_form, container, false);
    }

    public void RegisterUser() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String confirm_password = editPassword2.getText().toString();

        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if(email.matches(emailPattern) && password.equals(confirm_password)) {
            Log.d("message", "Password equals correct");
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                String uuid = user.getUid();

                                Map<String, Object> UserToInsert = new HashMap<>();
                                UserToInsert.put("UserID", uuid);
                                UserToInsert.put("FirstName", FirstName);
                                UserToInsert.put("Surname", Surname);
                                UserToInsert.put("FullName", FirstName + " " + Surname);
                                UserToInsert.put("DOB", StringtoTimeStamp(DOB));
                                UserToInsert.put("Weight", Weight);
                                UserToInsert.put("Height", Height);
                                UserToInsert.put("FitnessGoal", Goal);
                                UserToInsert.put("ActivityFrequency", 2);
                                UserToInsert.put("DailyCalorieGoal", 2000);
                                UserToInsert.put("Level", "Beginner");


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

                                Toast.makeText(getContext() , "Account successfully created", Toast.LENGTH_SHORT).show();
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                },5000);
                                Log.d("RegisterActivity", "User successfully created");
                            }
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "Account Registration failed. Please try again", Toast.LENGTH_SHORT).show();
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
