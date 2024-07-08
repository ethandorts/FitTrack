package com.example.fittrack;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;


public class MenuActivity extends AppCompatActivity {
    Button btnRecordActivity;
    Button btnViewActivities;
    TextView txtWelcome;
    FirebaseFirestore db;
    private String UserID;

    private FirebaseUser mAuth;
    private String FullName;
    private String Activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        txtWelcome = findViewById(R.id.txtWelcome);
        btnRecordActivity = findViewById(R.id.btnRecordActivity);
        btnViewActivities = findViewById(R.id.btnViewActivities);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        UserID = mAuth.getUid();

        DocumentReference documentReference = db.collection("Users").document(UserID);
        documentReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                 Map<String, Object> data = documentSnapshot.getData();
                                 FullName = data.get("FirstName") + " " + data.get("Surname");
                                 txtWelcome.setText("Welcome " + FullName);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());
                    }
                });




        btnRecordActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnViewActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}