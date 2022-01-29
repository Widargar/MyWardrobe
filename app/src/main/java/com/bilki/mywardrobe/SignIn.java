package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity {

    private long pressedTime;
    private Button reg_redir_bttn, pass_forget_bttn, sign_in_bttn;
    private TextInputLayout email, password;
    private FirebaseAuth mAuth;
    private noInternetConnection dialog;
    private Dialog internetReconnect;
    private FirebaseFirestore mFirebaseFirestore;
    private final static String TAG = "bilki: SignIn ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        email = (TextInputLayout) findViewById(R.id.email_edit_in);
        password = (TextInputLayout) findViewById(R.id.password_edit_in);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            Log.d(TAG, "Logged in using: " + mAuth.getCurrentUser().getEmail());
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }

        mFirebaseFirestore = FirebaseFirestore.getInstance();

        /*Intent intent = getIntent();
        _email = intent.getStringExtra("email").trim();
        _password = intent.getStringExtra("password").trim();*/

        //On click methode to go to SignUp activity

        reg_redir_bttn = (Button) findViewById(R.id.reg_redir_bttn);
        reg_redir_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this , SignUp.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
                finish();
            }
        });

        //On click methode to go to Forget_password activity

        pass_forget_bttn = (Button) findViewById(R.id.password_forget);
        pass_forget_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this , Forget_password.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
                finish();
            }
        });

        sign_in_bttn = (Button) findViewById(R.id.sign_in_bttn);
        sign_in_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckInternet checkInternet = new CheckInternet();
                if(!checkInternet.isConnected(SignIn.this)){

                    dialog = new noInternetConnection();
                    dialog.show(getFragmentManager(), "internetConnectionDialog");

                }
                signInUser();

            }
        });



    }

    /*public void SignInUser(View view){

        //email and password validation
        if(!validateFields()){

            return;

        }

        //get data
        String _email = email.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();
        //String _userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(_email);


        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    email.setError(null);
                    email.setErrorEnabled(false);

                    String systemPasssword = snapshot.child(_email).child("password").getValue(String.class);
                    if(systemPasssword.equals(_password)){

                        password.setError(null);
                        password.setErrorEnabled(false);

                        String _name = snapshot.child(_email).child("name").getValue(String.class);
                        String _surname = snapshot.child(_email).child("surname").getValue(String.class);
                        String _phone = snapshot.child(_email).child("phone").getValue(String.class);
                        String _birthday = snapshot.child(_email).child("birthday").getValue(String.class);


                    }else {

                        Toast.makeText(SignIn.this, "Invalid password doesn't exist!", Toast.LENGTH_SHORT).show();

                    }

                }else {

                    Toast.makeText(SignIn.this, "Invalid email doesn't exist!", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(SignIn.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }*/

    public void signInUser(){

        //email and password validation
        if(!validateFields()){

            return;

        }

        mAuth.signInWithEmailAndPassword(email.getEditText().getText().toString().trim(), password.getEditText().getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Log.d(TAG,"Authorization successful");
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Log.d(TAG,"Authorization failed. Issue - "+task.getException().getMessage());
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "E-mail or password is incorrect or \n account does not exist", Toast.LENGTH_SHORT);
                    toast.show();
                    if (mAuth.getCurrentUser() != null) {
                        Log.d(TAG, "Logged in using : " + mAuth.getCurrentUser().getEmail());
                    }
                }

            }
        });

    }



    private boolean validateFields(){

        String _email = email.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();
        String val_code = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        if (_email.isEmpty()) {

            email.setError("Enter your email!");
            email.requestFocus();
            return false;

        } else if (!_email.matches(val_code)){

            email.setError("Invalid email!");
            email.requestFocus();
            return false;

        } else if (_email.isEmpty() && _password.isEmpty()) {

            password.setError("Enter your password!");
            password.requestFocus();
            email.setError("Enter your email!");
            return false;
        }  else if (!_email.matches(val_code) && _password.isEmpty()) {

            password.setError("Enter your password!");
            password.requestFocus();
            email.setError("Invalid email!");
            return false;

        } else {

            password.setError(null);
            password.setErrorEnabled(false);
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }

    }

    // Rewrited OnBackPressed methode to exit from application
    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Please, press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

}