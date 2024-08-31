package com.example.fittrack;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagingChatActivity extends AppCompatActivity {
    ImageButton btnSendMessage;
    EditText TypeBox;
    FirebaseUser mAuth;
    String currentUser, recipientUser, recipientName;
    TextView txtRecipientUser;
    MessagesRecyclerViewAdapter messagesAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DirectMessagingUtil MessagingUtil = new DirectMessagingUtil(db);
    ArrayList<MessageModel> messages = new ArrayList<>();
    String messageDocumentID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_chat);

        btnSendMessage = findViewById(R.id.imageButtonSendMessage);

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = mAuth.getUid();

        Bundle bundle = getIntent().getExtras();
        recipientName = (String) bundle.get("name");
        recipientUser = bundle.getString("UserID");

        txtRecipientUser = findViewById(R.id.txtChatUser);
        TypeBox = findViewById(R.id.editTypeMessage);

        txtRecipientUser.setText(recipientName);

        RecyclerView messagesRecyclerView = findViewById(R.id.MessagesRecyclerView);
        messagesAdapter = new MessagesRecyclerViewAdapter(this, messages, currentUser);
        messagesRecyclerView.setAdapter(messagesAdapter);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        MessagingUtil.retrieveChannelMessages(currentUser, recipientUser, new DirectMessagingUtil.ChannelMessagesCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> data) {
                for(Map<String, Object> message : data) {
                    MessageModel SingleMessage = new MessageModel(String.valueOf(message.get("sender")),
                            String.valueOf(message.get("recipient")),
                            String.valueOf(message.get("message-content")),
                            (Timestamp) (message.get("timestamp"))
                    );
                    messages.add(SingleMessage);
                }
                messagesAdapter.notifyDataSetChanged();
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = TypeBox.getText().toString().trim();
                if(message.isEmpty()) {
                    Toast.makeText(MessagingChatActivity.this,"Please enter a message", Toast.LENGTH_SHORT);
                } else {
                    sendDirectMessage(currentUser, recipientUser, message);
                    TypeBox.setText("");
                }
            }
        });

    }

    private void sendDirectMessage(String sender, String recipient, String message_content) {
        MessagingUtil.CreateDirectMessagingChannel(sender, recipient);
        MessagingUtil.AddMessage(sender, recipient, message_content);
    }
}