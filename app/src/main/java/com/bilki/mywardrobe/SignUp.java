package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private TextInputEditText displayDate2, ed_name, ed_surname, ed_email, ed_phone, ed_password, ed_repeat_password;
    private TextInputLayout birthday, name, surname, email, phone, password, repeat_password;
    private CountryCodePicker country;
    private RadioGroup male_female_group;
    private RadioButton male, female, male_female_radio_bttn;
    private Button back_sign_up, register_bttn;
    boolean email_bool;
    private String date, userId, str_birthday_pass, str_gender_pass, str_full_number_pass,
            str_name_pass, str_email_pass, str_password_pass, str_surname_pass, str_profile_image_name, str_profile_image_url;
    private TextView choose_gender;
    public SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String preference_name = "Name";
    public static final String preference_surname = "Surname";
    public static final String preference_email = "Email";
    public static final String preference_phone = "Phone";
    public static final String preference_password = "Password";
    public static final String preference_repeat_password = "Repeat password";
    public static final String preference_birthday = "Birthday";
    public static final String preference_gender = "Gender";
    private String text_name, text_surname, text_email, text_phone, text_password, text_repeat_password, text_birthday, text_gender;
    public Calendar calendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    public DatePickerDialog picker;
    private StorageReference mStorageReference, mImageReference;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mAuth;
    private StorageTask uploadTask;
    private DocumentSnapshot mDocumentSnaphot;
    private DocumentReference mDocumentReference;
    private final static String TAG = "bilki: Sign_up ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        userId = mAuth.getUid();


        name = (TextInputLayout) findViewById(R.id.sign_up_name);
        surname = (TextInputLayout) findViewById(R.id.sign_up_surname);
        email = (TextInputLayout) findViewById(R.id.sign_up_email);
        phone = (TextInputLayout) findViewById(R.id.sign_up_number);
        password = (TextInputLayout) findViewById(R.id.sign_up_password);
        repeat_password = (TextInputLayout) findViewById(R.id.sign_up_repeat_password);
        birthday = (TextInputLayout) findViewById(R.id.sign_up_birth);
        choose_gender = (TextView) findViewById(R.id.gender_error);

        country = (CountryCodePicker) findViewById(R.id.country_picker);

        male_female_group = (RadioGroup) findViewById(R.id.radio_group_male_female);
        male = (RadioButton) findViewById(R.id.radio_male);
        female = (RadioButton) findViewById(R.id.radio_female);


        ed_name = (TextInputEditText) findViewById(R.id.edit_sign_up_name);
        ed_surname = (TextInputEditText) findViewById(R.id.edit_sign_up_surname);
        ed_email = (TextInputEditText) findViewById(R.id.edit_sign_up_email);
        ed_phone = (TextInputEditText) findViewById(R.id.edit_sign_up_number);
        ed_password = (TextInputEditText) findViewById(R.id.edit_sign_up_password);
        ed_repeat_password = (TextInputEditText) findViewById(R.id.edit_sign_up_repeat_password);
        displayDate2 = (TextInputEditText) findViewById(R.id.edit_sign_up_birthday);

        displayDate2.setInputType(InputType.TYPE_NULL);
        displayDate2.setShowSoftInputOnFocus(false);

        displayDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(SignUp.this, android.R.style.Theme_Material_Dialog_NoActionBar,
                        dateSetListener, year, month, day);
                picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                picker.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int myear, int mmonth, int mday) {
                mmonth = mmonth + 1;
                date = ((mday < 10) ? "0" + mday : mday) + "."
                        + ((mmonth < 10) ? "0" + mmonth : mmonth) + "." + myear;
                displayDate2.setText(date);

            }
        };

        register_bttn = (Button) findViewById(R.id.register);
        register_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!nameValidation() | !surnameValidation() |
                        !genderValidation() | !emailValidation() |
                        !ageValidation() | !passwordValidation() |
                        !repeatPasswordValidation() | !phoneValidation()) {

                    return;
                }

                str_name_pass = name.getEditText().getText().toString().trim();
                str_surname_pass = surname.getEditText().getText().toString().trim();
                str_email_pass = email.getEditText().getText().toString().trim();
                String str_phone_pass = phone.getEditText().getText().toString().trim();
                str_full_number_pass = "+" + country.getFullNumber() + str_phone_pass;
                str_password_pass = password.getEditText().getText().toString().trim();

                male_female_radio_bttn = findViewById(male_female_group.getCheckedRadioButtonId());
                str_gender_pass = male_female_radio_bttn.getText().toString().trim();

                int day = picker.getDatePicker().getDayOfMonth();
                int month = picker.getDatePicker().getMonth();
                int year = picker.getDatePicker().getYear();
                month = month + 1;
                str_birthday_pass = ((day < 10) ? "0" + day : day) + "."
                        + ((month < 10) ? "0" + month  : month) + "." + year;
                //String str_birthday_pass = birthday.getEditText().getText().toString().trim();



                /*
                int day = picker.getDatePicker().getDayOfMonth();
                int month = picker.getDatePicker().getMonth();
                int year = picker.getDatePicker().getMonth();
                */


                //saveData();

                //Intent i = new Intent(SignUp.this, Phone_verification.class);

                /*i.putExtra("name", str_name_pass);
                i.putExtra("surname", str_surname_pass);
                i.putExtra("email", str_email_pass);
                i.putExtra("birthday", str_birthday_pass);
                i.putExtra("password", str_password_pass);
                i.putExtra("phone", str_full_number_pass);
                i.putExtra("gender", str_gender_pass);*/


                /*startActivity(i);
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
                finish();*/
                addNewUser();
                Intent i = new Intent(SignUp.this, SignIn.class);
                startActivity(i);
                finish();
            }
        });

        //loadData();
        //updateData();
        //clearData();


    }

    public void saveData(){



        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String str_name = name.getEditText().getText().toString().trim();
        String str_surname = surname.getEditText().getText().toString().trim();
        String str_email = email.getEditText().getText().toString().trim();
        String str_phone = phone.getEditText().getText().toString().trim();
        String str_birthday = birthday.getEditText().getText().toString().trim();
        String str_password = password.getEditText().getText().toString().trim();
        String str_repeat_password = repeat_password.getEditText().getText().toString().trim();

        //Save data from radio group

        int gender_id = male_female_group.getCheckedRadioButtonId();
        male_female_radio_bttn = (RadioButton)male_female_group.findViewById(gender_id);
        String str_gender = male_female_radio_bttn.getText().toString().trim();


        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(preference_name, str_name);
        editor.putString(preference_surname, str_surname);
        editor.putString(preference_email, str_email);
        editor.putString(preference_phone, str_phone);
        editor.putString(preference_birthday, str_birthday);
        editor.putString(preference_password, str_password);
        editor.putString(preference_repeat_password, str_repeat_password);
        editor.putString(preference_gender, str_gender);

        editor.apply();

    }

    public void loadData(){

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        text_name = sharedpreferences.getString(preference_name, "");
        text_surname = sharedpreferences.getString(preference_surname, "");
        text_email = sharedpreferences.getString(preference_email, "");
        text_phone = sharedpreferences.getString(preference_phone, "");
        text_birthday = sharedpreferences.getString(preference_birthday, "");
        text_password = sharedpreferences.getString(preference_password, "");
        text_repeat_password = sharedpreferences.getString(preference_repeat_password, "");
        text_gender = sharedpreferences.getString(preference_gender, "");


    }

    public void updateData(){

        ed_name.setText(text_name);
        ed_surname.setText(text_surname);
        ed_email.setText(text_email);
        ed_phone.setText(text_phone);
        ed_password.setText(text_password);
        ed_repeat_password.setText(text_repeat_password);
        displayDate2.setText(text_birthday);
        if (text_gender.equals("Male")){

            male.setChecked(true);

        }else if (text_gender.equals("Female")){

            female.setChecked(true);

        } else {

            male.setChecked(false);
            female.setChecked(false);

        }


    }

    public void clearData(){

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        sharedpreferences.edit().clear().commit();
    }

    //Validation

    private boolean nameValidation() {

        String val = name.getEditText().getText().toString().trim();
        String val_code = "[a-zA-Z ]+";

        if (val.isEmpty()) {

            name.setError("Enter your first name!");
            return false;

        } else if (val.length() > 20) {

            name.setError("Name is too long!");
            return false;

        } else if (!val.matches(val_code)) {

            name.setError("Invalid name!");
            return false;

        } else {

            name.setError(null);
            name.setErrorEnabled(false);
            return true;

        }
    }

    private boolean surnameValidation() {

        String val = surname.getEditText().getText().toString().trim();
        String val_code = "[a-zA-Z ]+";

        if (val.isEmpty()) {

            surname.setError("Enter your surname!");
            return false;

        } else if (val.length() > 20) {

            surname.setError("Surname is too long!");
            return false;

        } else if (!val.matches(val_code)) {
            surname.setError("Invalid surname!");
            return false;
        } else {
            surname.setError(null);
            surname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean emailValidation() {

        String val = email.getEditText().getText().toString().trim();
        String val_code = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; //"[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+";
//        mAuth.fetchSignInMethodsForEmail(val).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
//            @Override
//            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
//
//                boolean emailExists = task.getResult().getSignInMethods().isEmpty();
//
//                if(emailExists){
//
//                    email_bool = emailExists;
//                    Log.d(TAG, "Email is not in use!");
//
//                } else{
//
//                    email_bool = emailExists;
//                    Log.d(TAG, "Email is already used!");
//
//                }
//
//            }
//        });


        if (val.isEmpty()) {

            email.setError("Enter your email!");
            return false;

        } else if (!val.matches(val_code)) {

            email.setError("Invalid email!");
            return false;

        }
//        else if (email_bool = true){
//
//            email.setError("Email is already used!");
//            return false;
//
//        }

        else {

            email.setError(null);
            email.setErrorEnabled(false);
            return true;

        }
    }

    private boolean phoneValidation() {

        String val = phone.getEditText().getText().toString().trim();

        if (val.isEmpty()){

            phone.setError("Enter your phone number!");
            return false;

        } else if (val.length() < 9){

            phone.setError("Invalid phone number length");
            return false;

        } else if (val.length() > 9){

            phone.setError("Invalid phone number length");
            return false;

        } else {

            return true;

        }

    }

    private boolean passwordValidation() {

        String val = password.getEditText().getText().toString().trim();
        String val_code = "^([a-zA-Z0-9]{5,15})$"; //"^" + "(?=.*[a-zA-Z])" + "$";
//        String val_code_number = "^" + "(?=.*[0-9])" + "$";
//        String val_code_upper = "^" + "(?=.*[A-Z])" + "$";
//        String val_code_white = "^" + "(?=S+$)" + "$";
//        String val_code_count = "^" + ".{5,}" + "$";

        if (val.isEmpty()) {

            password.setError("Enter your password!");
            return false;

        } else if (!val.matches(val_code)) {

            password.setError("Invalid password!");
            return false;

        }

//        else if (!val.matches(val_code_upper)) {
//
//            password.setError("At least 1 upper case!");
//            return false;
//
//        } else if (!val.matches(val_code_white)) {
//
//            password.setError("No white space!");
//            return false;
//
//        } else if (!val.matches(val_code_count)) {
//
//            password.setError("At least 5 characters!");
//            return false;
//
//        } else if (!val.matches(val_code_number)) {
//
//            password.setError("At least 1 number!");
//            return false;
//
//        }

        else {

            password.setError(null);
            password.setErrorEnabled(false);
            return true;

        }
    }

    private boolean repeatPasswordValidation() {

        String val = repeat_password.getEditText().getText().toString().trim();

        if (val.isEmpty()){

            repeat_password.setError("Repeat password correctly!");
            return false;

        }else {

            return true;

        }

    }

    private boolean genderValidation() {
        if (male_female_group.getCheckedRadioButtonId() == -1) {

            choose_gender.setVisibility(View.VISIBLE);
            return false;

        } else {

            return true;

        }
    }

    private boolean ageValidation() {

        //String val = birthday.getEditText().getText().toString().trim();
        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge=0;
        if(picker!=null) {

            userAge = picker.getDatePicker().getYear();

        }else {

            birthday.setError("Choose your birthday!");
            return false;

        }
        int validAge = currYear - userAge;

        if (validAge < 16) {

            birthday.setError("You are too yong to use this application");
            return false;

        }else {

            return true;

        }

    }

    private void addNewUser() {

        mAuth.createUserWithEmailAndPassword(str_email_pass, str_password_pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseUser verifUser = mAuth.getCurrentUser();
                        verifUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(SignUp.this, "Verification email has been sent.", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d(TAG, "Verification email wasn't sent " + e.getMessage());

                            }
                        });

                        //UserHelperClass addNewUser = new UserHelperClass(_name, _surname, _email, _birthday, _gender, _phone, _password);

                        userId =mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = mFirebaseFirestore.collection("users").document(str_email_pass);//userID);

                        mImageReference = mStorageReference.child("images/" + "default_image/" + "default_profile_photo.png");
                        mImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                str_profile_image_url = uri.toString().trim();
                                str_profile_image_name = "default_profile_photo.png";

                                UserHelperClass user = new UserHelperClass(str_name_pass, str_surname_pass, str_email_pass, str_birthday_pass, str_gender_pass, str_full_number_pass, str_password_pass, str_profile_image_url, str_profile_image_name);
//                        Map<String, Object> user = new HashMap<>();
//                        user.put("id", userID);
//                        user.put("name", str_name_pass);
//                        user.put("surname", str_surname_pass);
//                        user.put("email", str_email_pass);
//                        user.put("birthday", str_birthday_pass);
//                        user.put("gender", str_gender_pass);
//                        user.put("phone", str_full_number_pass);
//                        user.put("password", str_password_pass);
                                Log.d(TAG, "Adding users data");

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Data has been added");
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Log.d(TAG, "Data hasn't been added. Smth gone wrong");

                                            }
                                        })
                                        .addOnCanceledListener(new OnCanceledListener() {
                                            @Override
                                            public void onCanceled() {

                                                Log.d(TAG, "Data adding has been canceled");

                                            }
                                        });


                            }
                        });

                            }
                        });






        mAuth.signOut();

    }

//    private void uploadImageToFirebase(String name, Uri contentUri){
//
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Creating new user...");
//        progressDialog.show();
//
//        mImageReference = mStorageReference.child("images/" + userId + "/" + "profile_image/" + name);
//        uploadTask = mImageReference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                Log.d(TAG, "Default profile image has been uploaded");
//                progressDialog.dismiss();
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                Log.d(TAG, "Default profile image has not been uploaded: " + e.getMessage());
//                Toast.makeText(SignUp.this, "User's default image has not been uploaded!", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//
//
//    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(SignUp.this, SignIn.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        finish();

    }

}