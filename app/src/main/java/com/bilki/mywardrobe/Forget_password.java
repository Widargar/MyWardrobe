package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

public class Forget_password extends AppCompatActivity {

    private Button back, send;
    private TextInputLayout emailInput;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        emailInput = (TextInputLayout) findViewById(R.id.user_email_password);

        back = (Button) findViewById(R.id.back_3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Forget_password.this, SignIn.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                finish();
            }
        });

        send = (Button) findViewById(R.id.send_reset_email);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailInput.getEditText().getText().toString().trim();

                if (email.isEmpty()) {

                    emailInput.setError("Please enter your account email, to reset password!");

                } else {

                    emailInput.setError(null);
                    emailInput.setErrorEnabled(false);
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(Forget_password.this, "Email sent successfully to reset your password!", Toast.LENGTH_SHORT).show();

                            }

                            Intent intent = new Intent(Forget_password.this, SignIn.class);
                            startActivity(intent);
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Forget_password.this, "Email hasn't been sent!" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }


        });


    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(Forget_password.this, SignIn.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        finish();

    }
}