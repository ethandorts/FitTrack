package com.example.fittrack;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MessagingChatActivity extends AppCompatActivity {
    TextView txtRecipientUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_chat);

        Bundle bundle = getIntent().getExtras();
        txtRecipientUser = findViewById(R.id.txtChatUser);
        txtRecipientUser.setText((String) bundle.get("name"));
    }
}