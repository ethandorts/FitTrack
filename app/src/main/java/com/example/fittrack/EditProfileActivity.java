package com.example.fittrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView txtName;
    private ImageView imgProfile;
    private EditText editWeight, editHeight, editCalories, editActivitiesNo;
    private String originalWeight, originalHeight, originalCalories, originalActivitiesNo;
    private Button btnSaveDetails;
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgProfile = findViewById(R.id.editProfileImage);
        txtName = findViewById(R.id.txtProfileName);
        editWeight = findViewById(R.id.editProfileWeight);
        editHeight = findViewById(R.id.editProfileHeight);
        editCalories = findViewById(R.id.editProfileCalories);
        editActivitiesNo = findViewById(R.id.editActivitiesNo);
        btnSaveDetails = findViewById(R.id.btnSaveFitnessDetails);

        DatabaseUtil.retrieveProfilePicture(UserID + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                if (PicturePath != null) {
                    Glide.with(EditProfileActivity.this)
                            .load(PicturePath)
                            .into(imgProfile);
                } else {
                    Log.e("No profile picture found", "No profile picture found.");
                    imgProfile.setImageResource(R.drawable.profile);
                }
            }
        });

        DatabaseUtil.retrieveUserName(UserID, new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
                originalWeight = String.valueOf(weight);
                originalHeight = String.valueOf(height);
                originalCalories = String.valueOf(dailyCalorieGoal);
                originalActivitiesNo = String.valueOf(activityFrequency);

                txtName.setText(FullName);
                editWeight.setText(String.valueOf(weight));
                editHeight.setText(String.valueOf(height));
                editCalories.setText(String.valueOf(dailyCalorieGoal));
                editActivitiesNo.setText(String.valueOf(activityFrequency));
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        editWeight.addTextChangedListener(textWatcher);
        editHeight.addTextChangedListener(textWatcher);
        editCalories.addTextChangedListener(textWatcher);
        editActivitiesNo.addTextChangedListener(textWatcher);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int weight = 0;
                int height = 0;
                int activityFrequency = 0;
                int calories = 0;

                try {
                    weight = Integer.parseInt(editWeight.getText().toString().trim());
                    height = Integer.parseInt(editHeight.getText().toString().trim());
                    activityFrequency = Integer.parseInt(editActivitiesNo.getText().toString().trim());
                    calories = Integer.parseInt(editCalories.getText().toString().trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (TextUtils.isEmpty(editActivitiesNo.getText().toString().trim())) {
                    editActivitiesNo.setError("No value for activities per week entered!");
                    return;
                }

                if (TextUtils.isEmpty(editHeight.getText().toString().trim())) {
                    editHeight.setError("No value for height!");
                }

                if (TextUtils.isEmpty(editWeight.getText().toString().trim())) {
                    editWeight.setError("No value for weight!");
                    return;
                }

                if (TextUtils.isEmpty(editCalories.getText().toString().trim())) {
                    editCalories.setError("No value for calories!");
                    return;
                }

                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("Weight", weight);
                updatedData.put("Height", height);
                updatedData.put("ActivityFrequency", activityFrequency);
                updatedData.put("DailyCalorieGoal", calories);

                db.collection("Users").document(UserID)
                        .update(updatedData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfileActivity.this, "Error updating user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void isChanged() {
        boolean weightChanged = !editWeight.getText().toString().trim().equals(originalWeight);
        boolean heightChanged = !editHeight.getText().toString().trim().equals(originalHeight);
        boolean caloriesChanged = !editCalories.getText().toString().trim().equals(originalCalories);
        boolean activitiesChanged = !editActivitiesNo.getText().toString().trim().equals(originalActivitiesNo);

        if (weightChanged || heightChanged || caloriesChanged || activitiesChanged) {
            btnSaveDetails.setVisibility(View.VISIBLE);
        } else {
            btnSaveDetails.setVisibility(View.GONE);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
            uploadImage();
        }
    }

    private void uploadImage() {
        if(imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile-images/" + UserID +".jpeg");

            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Profile Picture Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Profile Picture Uploaded Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Profile Picture Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Profile Picture Uploaded Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}