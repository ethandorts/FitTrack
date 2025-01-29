package com.example.fittrack;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class RegisterPIFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText firstnameTextBox;
    private EditText surnameTextBox;
    private EditText dobTextBox;
    private EditText weightTextBox;
    private EditText passwordTextBox;
    private EditText heightTextBox;
    private EditText goalTextBox;
    private Button btnNext;

    private String uuid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstnameTextBox = view.findViewById(R.id.edit_text_firstname);
        surnameTextBox = view.findViewById(R.id.edit_text_surname);
        dobTextBox = view.findViewById(R.id.edit_text_DOB);
        heightTextBox = view.findViewById(R.id.edit_text_height);
        weightTextBox = view.findViewById(R.id.edit_text_weight);
        goalTextBox = view.findViewById(R.id.edit_text_fitness_goal);
        btnNext = view.findViewById(R.id.btnRegister);

        dobTextBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
                                dobTextBox.setText(selectedDate);
                            }
                        },
                        calendar.get(java.util.Calendar.YEAR),
                        calendar.get(java.util.Calendar.MONTH),
                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String FirstName = firstnameTextBox.getText().toString().trim();
                String Surname = surnameTextBox.getText().toString().trim();
                String orgWeight = weightTextBox.getText().toString().trim();
                String orgHeight = heightTextBox.getText().toString().trim();
                String Goal = goalTextBox.getText().toString().trim();
                String DOB = dobTextBox.getText().toString().trim();

                if (FirstName.isEmpty() || Surname.isEmpty() || Goal.isEmpty() || DOB.isEmpty() ||
                        orgWeight.isEmpty() || orgHeight.isEmpty()) {
                    Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int weight, height;
                try {
                    weight = Integer.parseInt(orgWeight);
                    height = Integer.parseInt(orgHeight);

                    if (weight < 20 || weight > 300) {
                        Toast.makeText(requireContext(), "Weight must be between 20 and 300 kg!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (height < 50 || height > 250) {
                        Toast.makeText(requireContext(), "Height must be between 50 and 250 cm!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Please enter valid numbers for weight and height!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!DOB.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    Toast.makeText(requireContext(), "DOB must be in format DD/MM/YYYY!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("FirstName", FirstName);
                bundle.putString("Surname", Surname);
                bundle.putInt("Weight", weight);
                bundle.putInt("Height", height);
                bundle.putString("Goal", Goal);
                bundle.putString("DOB", DOB);

                CompleteRegisterFragment fragment = new CompleteRegisterFragment();
                fragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameRegister, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
