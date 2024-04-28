package com.example.fittrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;

import java.util.Arrays;
import java.util.Collection;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;
import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText Email = findViewById(R.id.edit_text_firstname);
        EditText Password = findViewById(R.id.edit_text_password);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        Button btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        ParseFacebookUtils.initialize(this);

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

        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FacebookLogin();
            }
        });

        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    public void FacebookLogin() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait...");
        dialog.setMessage("Logging user in...");
        dialog.show();

        Collection <String> permissions = Arrays.asList("public_profile", "email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, (user, err) -> {
            dialog.dismiss();
            if(err != null) {
                Log.e("FacebookLogin", "done: ", err);
                Toast.makeText(this, err.getMessage(), Toast.LENGTH_LONG).show();
            } else if (user == null) {
                Toast.makeText(this, "The user cancelled the Facebook login.", Toast.LENGTH_LONG).show();
                Log.d("FacebookLoginExample", "Uh oh. The user cancelled the Facebook login.");
            } else if (user.isNew()) {
                Toast.makeText(this, "User signed up and logged in through Facebook.", Toast.LENGTH_LONG).show();
                Log.d("FacebookLoginExample", "User signed up and logged in through Facebook!");
                getUserDetailFromFB();
            } else {
                Toast.makeText(this, "User logged in through Facebook.", Toast.LENGTH_LONG).show();
                Log.d("FacebookLoginExample", "User logged in through Facebook!");
                showAlert("Oh, you!", "Welcome back!");
            }
        });
    }

    private void getUserDetailFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
            ParseUser user = ParseUser.getCurrentUser();
            try {
                if (object.has("name"))
                    user.setUsername(object.getString("name"));
                if (object.has("email"))
                    user.setEmail(object.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            user.saveInBackground(e -> {
                if (e == null) {
                    showAlert("First Time Login!", "Welcome!");
                } else
                    showAlert("Error", e.getMessage());
            });
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void GoogleLogin() {

    }
}