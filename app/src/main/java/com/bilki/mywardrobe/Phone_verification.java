package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Phone_verification extends AppCompatActivity {

    Button back_registration, verify_bttn, resend_button;
    PinView ed_code;
    TextView desc, verif_code_error;
    String _phone, _name, _surname, _email, _birthday, _password, _gender;

    String number = "+4888888888";
    String vercode = "123456";

    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataref;
    private FirebaseFirestore mFirebaseFirestore;
    private String mVerificationId;
    public String userID;
    private final static String TAG = "bilki: Phone_verif ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        verif_code_error = (TextView) findViewById(R.id.verification_code_error);
        ed_code = (PinView) findViewById(R.id.code);
        //_userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        _phone = intent.getStringExtra("phone").trim();
        _name = intent.getStringExtra("name").trim();
        _surname = intent.getStringExtra("surname").trim();
        _email = intent.getStringExtra("email").trim();
        _birthday = intent.getStringExtra("birthday").trim();
        _password = intent.getStringExtra("password").trim();
        _gender = intent.getStringExtra("gender").trim();

        desc = findViewById(R.id.verification_description);
        String phone_verify_desc_string = getString(R.string.phone_verif_sent);
        desc.setText(phone_verify_desc_string + " " + _phone);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                String code = credential.getSmsCode();
                if (code != null) {
                    verifyCode(code);
                }

                signInTheUserByCredentials(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        sendVerificationCode(_phone);

        verify_bttn = (Button) findViewById(R.id.verify);
        verify_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(Phone_verification.this, SignIn.class);
                String code = ed_code.getText().toString();
                if (code.isEmpty()) {

                    Log.d(TAG, "Wrong code!");
                    ed_code.requestFocus();
                    return;
                }
                verifyCode(code);
                //startActivity(i);
                //finish();
            }
        });

        resend_button = (Button) findViewById(R.id.resend_code);
        resend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resendVerificationCode(_phone, mResendToken);

            }
        });

        back_registration = (Button) findViewById(R.id.back_2);
        back_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Phone_verification.this, SignUp.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                finish();
            }
        });

    }

    private void verifyCode(String codeByUser) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeByUser);
        signInTheUserByCredentials(credential);

    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(Phone_verification.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Log.d(TAG, "SignInUser:" + "Your Account has been created successfully!");

                            mAuth.getCurrentUser().linkWithCredential(credential);
                            //Perform Your required action here to either let the user sign In or do something required
                            //addNewUser();
                            Intent intent = new Intent(getApplicationContext(), SignIn.class);
                            startActivity(intent);

                        } else {
                            Log.d(TAG, "SignInUser:" + task.getException().toString());
                        }
                    }
                });
    }


    /*private void addNewUser(){

        mFirebaseDatabase = FirebaseDatabase.getInstance("https://mywardrobe-92dcd-default-rtdb.europe-west1.firebasedatabase.app/");
        mDataref = mFirebaseDatabase.getReference("Users");

        UserHelperClass addNewUser = new UserHelperClass(_name, _surname, _email, _birthday, _gender, _phone, _password);

        mDataref.child(_phone).setValue(addNewUser);

        Log.d(TAG, "User is added");
        mAuth.signOut();

        }*/




    private void sendVerificationCode(String _phone) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(_phone)                      // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
                        .setActivity(this)                          // Activity (for callback binding)
                        .setCallbacks(mCallbacks)                  // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void resendVerificationCode(String _phone,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(_phone)                     // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
                        .setActivity(this)                          // Activity (for callback binding)
                        .setCallbacks(mCallbacks)                   // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)              // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(Phone_verification.this, SignUp.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        finish();

    }
}