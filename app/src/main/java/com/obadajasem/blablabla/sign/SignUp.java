package com.obadajasem.blablabla.sign;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.obadajasem.blablabla.MainActivity;
import com.obadajasem.blablabla.R;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "Signup";
    Button signup_in;
    EditText emailet,passet,usernameet;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signup_in=findViewById(R.id.signup_up);
        emailet=findViewById(R.id.signup_email);
        passet=findViewById(R.id.signup_password);
        usernameet=findViewById(R.id.signup_username);

        mAuth = FirebaseAuth.getInstance();


        signup_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailet.getText().toString();
                String password = passet.getText().toString();
                if(email.equals("") || password.equals("")){
                    Toast.makeText(SignUp.this, "Both Email and password Required", Toast.LENGTH_SHORT).show();
                }else if (!email.contains("@") && email.length()<5){
                    Toast.makeText(SignUp.this, "Please Enter a Vaild Email", Toast.LENGTH_SHORT).show();
                }
                else {
                    createAcoount(email, password);

                }
            }
        });

    }
    public void createAcoount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    addUsernameTOUser(task.getResult().getUser());
                    Toast.makeText(SignUp.this, "done."+user.getDisplayName(),Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SignUp.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    Log.d(TAG, "createUserWithEmail:success");

                    // updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUp.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });
    }

    private void addUsernameTOUser(FirebaseUser user) {
        String username=usernameet.getText().toString();
        UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: update successful");
                }
            }
        });


    }
}

