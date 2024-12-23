package com.example.fittrack;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MessagingChatActivity extends AppCompatActivity {
    ImageButton btnSendMessage;
    EditText TypeBox;
    FirebaseUser mAuth;
    String currentUser, recipientUser, recipientName;
    TextView txtRecipientUser;
    MessagesRecyclerViewAdapter messagesAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DirectMessagingUtil MessagingUtil = new DirectMessagingUtil(db);
    String messageDocumentID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_chat);

        btnSendMessage = findViewById(R.id.btnSendPost);

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = mAuth.getUid();

        Bundle bundle = getIntent().getExtras();
        recipientName = (String) bundle.get("name");
        recipientUser = bundle.getString("UserID");
        messageDocumentID = DirectMessagingUtil.getDocumentID(currentUser, recipientUser);

        txtRecipientUser = findViewById(R.id.txtChatUser);
        TypeBox = findViewById(R.id.editPost);

        txtRecipientUser.setText(recipientName);

        Query query =  db.collection("DM")
                .document(messageDocumentID)
                .collection("Messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<MessageModel> options =
                new FirestoreRecyclerOptions.Builder<MessageModel>()
                        .setQuery(query, MessageModel.class)
                        .build();

        RecyclerView messagesRecyclerView = findViewById(R.id.MessagesRecyclerView);
        messagesAdapter = new MessagesRecyclerViewAdapter(options,this, currentUser);
        messagesRecyclerView.setAdapter(messagesAdapter);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    @Override
    protected void onStart() {
        super.onStart();
        if (messagesAdapter != null)
            messagesAdapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(messagesAdapter != null) {
            messagesAdapter.stopListening();
        }
    }

    private void sendDirectMessage(String sender, String recipient, String message_content) {
        MessagingUtil.CreateDirectMessagingChannel(sender, recipient);
        MessagingUtil.AddMessage(sender, recipient, message_content);
    }

}