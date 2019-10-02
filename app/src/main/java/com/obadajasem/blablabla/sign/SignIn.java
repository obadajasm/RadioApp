package com.obadajasem.blablabla.sign;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.obadajasem.blablabla.MainActivity;
import com.obadajasem.blablabla.R;
import com.obadajasem.blablabla.RestPasswordActivity;

public class SignIn extends AppCompatActivity {
    public static final String TAG = "Signin";
    TextView signuptv,restpasstv,gomaintv;
    Button signinbtn;
    EditText email0, pass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        gomaintv=findViewById(R.id.gomain);
        signinbtn = findViewById(R.id.btn_login);
        signuptv = findViewById(R.id.createone);
        email0 = findViewById(R.id.input_email);
        pass = findViewById(R.id.input_password);
        restpasstv=findViewById(R.id.link_forgotpassword);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email0.getText().toString();
                String password = pass.getText().toString();
                if (email.equals("") || password.equals("")) {
                    Toast.makeText(SignIn.this, "Both Email and password Required", Toast.LENGTH_SHORT).show();
                } else if (!email.contains("@") || email.length() < 5) {
                    Toast.makeText(SignIn.this, "Please Enter a Vaild Email", Toast.LENGTH_SHORT).show();
                } else {
                    signin(email, password);

                    getuserinfo();
                }
            }
        });

        signuptv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(SignIn.this,SignUp.class);
               startActivity(i);
           }
       });

        gomaintv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
       restpasstv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(SignIn.this, RestPasswordActivity.class);
               startActivity(i);
               finish();
           }
       });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
        } else {
            Toast.makeText(SignIn.this, "Signin in with " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(SignIn.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        //  updateUI(currentUser);
    }


    public void signin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent i = new Intent(SignIn.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    // updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(SignIn.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }

                // ...
            }
        });
        ;

    }

    public void getuserinfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String displayName = user.getDisplayName();
            Toast.makeText(SignIn.this, name + email, Toast.LENGTH_SHORT).show();
        }
    }
}
