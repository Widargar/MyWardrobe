package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private String userId, fileName, currentPhotoPath, strUri, urll, name, surname, email ,
            phone, fullPhone, birthday, gender, user_email, date, default_img_name, default_default_img_name, default_default_image_url;
    private TextView profName, profEmail, profPhone, profBirthday, profGender, resendEmail, notVerified;
    private LinearLayout profileData, profileDataEdit;
    private ImageView profImgMale, profImgFemale, backProfile, profileImg, profileImgEdit;
    private TextInputEditText nameEditTxt, surnameEditTxt, emailEditTxt, phoneEditTxt, birthdayEditTxt;
    private CardView profImage, profileImageEdit;
    private RadioGroup profileGenderEdit;
    private RadioButton profileMale, profileFemale, ProfileGender;
    private Button editProfile, saveProfile, cancelProfile, camera_bttn, gallery_bttn, change_password;
    private Uri imageUri, photoUri;
    private File f;
    private Dialog choose_source;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog picker;
    private CountryCodePicker country;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference, imageReference, default_image_reference, existedImageReference;
    private StorageTask uploadTask;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private DocumentSnapshot document;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int SELECT_PICTURE_CODE = 101;
    private final static String TAG = "bilki: profile ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");

        userId = mAuth.getUid();

        editProfile = (Button) findViewById(R.id.edit_profile_bttn);
        saveProfile = (Button) findViewById(R.id.save_profile_bttn);
        cancelProfile = (Button) findViewById(R.id.cancel_profile_bttn);

        resendEmail = (TextView) findViewById(R.id.email_virif_resend);
        notVerified = (TextView) findViewById(R.id.not_verified);

        profileImg = (ImageView) findViewById(R.id.img_profile);
        profileData = (LinearLayout) findViewById(R.id.profile_data);
        profName = (TextView) findViewById(R.id.profile_name);
        profEmail = (TextView) findViewById(R.id.profile_email);
        profPhone = (TextView) findViewById(R.id.profile_phone);
        profGender = (TextView) findViewById(R.id.profile_gender);
        profBirthday = (TextView) findViewById(R.id.profile_birtday);
        profImage = (CardView) findViewById(R.id.img_profile_card);
        profImgMale = (ImageView) findViewById(R.id.profile_gender_male);
        profImgFemale = (ImageView) findViewById(R.id.profile_gender_female);

        profileImgEdit = (ImageView) findViewById(R.id.img_profile_edit);
        profileDataEdit = (LinearLayout) findViewById(R.id.profile_data_edit);
        profileImageEdit = (CardView) findViewById(R.id.img_profile_card_edit);
        nameEditTxt = (TextInputEditText) findViewById(R.id.edit_profile_name);
        surnameEditTxt = (TextInputEditText) findViewById(R.id.edit_profile_surname);
        emailEditTxt = (TextInputEditText) findViewById(R.id.edit_profile_email);
        phoneEditTxt = (TextInputEditText) findViewById(R.id.edit_profile_phone);
        country = (CountryCodePicker) findViewById(R.id.profile_country_picker);
        birthdayEditTxt = (TextInputEditText) findViewById(R.id.edit_profile_birthday);
        birthdayEditTxt.setInputType(InputType.TYPE_NULL);
        birthdayEditTxt.setShowSoftInputOnFocus(false);
        profileGenderEdit = (RadioGroup) findViewById(R.id.profile_radio_group_male_female);
        profileMale = (RadioButton) findViewById(R.id.profile_radio_male);
        profileFemale = (RadioButton) findViewById(R.id.profile_radio_female);

        setData();
        getUserEmail();
        userEmailVerification();

        change_password = (Button) findViewById(R.id.change_password_bttn);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Profile.this, ChangePassword.class);
                startActivity(i);
                finish();

            }
        });

        backProfile = (ImageView) findViewById(R.id.back_profile);
        backProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Profile.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                finish();

            }
        });

        FirebaseUser user = mAuth.getCurrentUser();

        if (!user.isEmailVerified()){

            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(Profile.this, "Verify your email!.", Toast.LENGTH_SHORT).show();

                }
            });

        }else {

            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    profileData.setVisibility(View.GONE);
                    profImage.setVisibility(View.GONE);
                    profileDataEdit.setVisibility(View.VISIBLE);
                    profileImageEdit.setVisibility(View.VISIBLE);

                    editProfile.setVisibility(View.GONE);
                    saveProfile.setVisibility(View.VISIBLE);
                    cancelProfile.setVisibility(View.VISIBLE);

                    setData();

                    birthdayEditTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            calendar = Calendar.getInstance();

                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            int month = calendar.get(Calendar.MONTH);
                            int year = calendar.get(Calendar.YEAR);

                            picker = new DatePickerDialog(Profile.this, android.R.style.Theme_Material_Dialog_NoActionBar,
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
                                    + ((mmonth < 10) ? "0" + mmonth  : mmonth) + "." + myear;
                            birthdayEditTxt.setText(date);

                        }
                    };

                    profileImgEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            chooseSource();

                        }
                    });

                }
            });

        }


        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileData.setVisibility(View.VISIBLE);
                profImage.setVisibility(View.VISIBLE);
                profileDataEdit.setVisibility(View.GONE);
                profileImageEdit.setVisibility(View.GONE);

                editProfile.setVisibility(View.VISIBLE);
                saveProfile.setVisibility(View.GONE);
                cancelProfile.setVisibility(View.GONE);

//                closeKeyboard();
                UIUtil.hideKeyboard(Profile.this);


                if (uploadTask != null && uploadTask.isInProgress()){

                }else if (imageUri != null){

                    uploadImageToFirebase(fileName, imageUri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {

                            updateData();
                            setData();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "upload task wasn't successful: " + e.getMessage());

                        }
                    });

                } else if (imageUri == null){

                    updateData();
                    setData();

                }

            }
        });

        cancelProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileData.setVisibility(View.VISIBLE);
                profImage.setVisibility(View.VISIBLE);
                profileDataEdit.setVisibility(View.GONE);
                profileImageEdit.setVisibility(View.GONE);

                editProfile.setVisibility(View.VISIBLE);
                saveProfile.setVisibility(View.GONE);
                cancelProfile.setVisibility(View.GONE);

            }
        });

    }

    public void getUserEmail() {

        firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    document = task.getResult();

                    if (document.exists()) {

                        user_email = document.getString("email");
                        Log.d(TAG, "Email taken " + user_email);

                    } else {

                        Log.d(TAG, "No such document");

                    }

                } else {

                    Log.d(TAG, "Get failed with ", task.getException());

                }

            }
        });

    }

    private void userEmailVerification(){

        FirebaseUser user = mAuth.getCurrentUser();

        if (!user.isEmailVerified()){

            resendEmail.setVisibility(View.VISIBLE);
            notVerified.setVisibility(View.VISIBLE);

            resendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseUser verifUser = mAuth.getCurrentUser();
                    verifUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(Profile.this, "Verification email has been sent.", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "Verification email wasn't sent " + e.getMessage());

                        }
                    });

                }
            });

        }

    }

    private void chooseSource(){

        choose_source = new Dialog(this);
        choose_source.requestWindowFeature(Window.FEATURE_NO_TITLE);
        choose_source.setContentView(R.layout.dialog_choose_source);
        choose_source.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        choose_source.show();

        camera_bttn = (Button) choose_source.findViewById(R.id.dialog_camera_button);
        gallery_bttn = (Button) choose_source.findViewById(R.id.dialog_gallery_button);

        camera_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCameraHardware(Profile.this);
                takePicture();
                choose_source.dismiss();

            }
        });

        gallery_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeImageFromGallery();
                choose_source.dismiss();

            }
        });

    }

    private void setData() {

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.d(TAG, "Data set failed: " + error);
                    return;

                }

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {


                            if (value.exists()) {

                                UserHelperClass user = value.toObject(UserHelperClass.class);
                                String profileName = user.getName();
                                String profileSurname = user.getSurname();
                                String profileEmail = user.getEmail();
                                String profilePhone = user.getPhone();
                                String phoneSpread = profilePhone.substring(profilePhone.length() - 9);
                                String profileBirthday = user.getBirthday();
                                String profileGender = user.getGender();
                                String profileImageName = user.getImageName();
                                String profileImageUrl = user.getImageUrl();
                                Uri imgUrl = Uri.parse(profileImageUrl);


                                if (profileData.getVisibility() == View.VISIBLE && profImage.getVisibility() == View.VISIBLE) {

                                    Picasso.get().load(imgUrl).into(profileImg);
                                    profName.setText(profileName + " " + profileSurname);
                                    profEmail.setText(profileEmail);
                                    profPhone.setText(profilePhone);
                                    profGender.setText(profileGender);
                                    profBirthday.setText(profileBirthday);

                                    switch (profileGender) {

                                        case "Male":
                                            profImgMale.setVisibility(View.VISIBLE);
                                            profImgFemale.setVisibility(View.GONE);
                                            break;
                                        case "Female":
                                            profImgMale.setVisibility(View.GONE);
                                            profImgFemale.setVisibility(View.VISIBLE);
                                            break;

                                    }

                                } else {

                                    Picasso.get().load(imgUrl).into(profileImgEdit);
                                    nameEditTxt.setText(profileName);
                                    surnameEditTxt.setText(profileSurname);
                                    emailEditTxt.setText(profileEmail);
                                    phoneEditTxt.setText(phoneSpread);
                                    birthdayEditTxt.setText(profileBirthday);

                                    switch (profileGender) {

                                        case "Male":
                                            profileMale.setChecked(true);
                                            break;
                                        case "Female":
                                            profileFemale.setChecked(true);
                                            break;

                                    }


                                }

                            } else {

                                Log.d(TAG, "Document doesn't exist");

                            }

                        } else {

                            Log.d(TAG, "Failed with: " + task.getException());

                        }

                    }
                });

            }
        });

    }

    private void updateData(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.show();

        name = nameEditTxt.getText().toString().trim();
        surname = surnameEditTxt.getText().toString().trim();
        email = emailEditTxt.getText().toString().trim();
        profGender = (RadioButton) findViewById(profileGenderEdit.getCheckedRadioButtonId());
        gender = profGender.getText().toString().trim();

        if(picker != null){

            int day = picker.getDatePicker().getDayOfMonth();
            int month = picker.getDatePicker().getMonth();
            int year = picker.getDatePicker().getYear();
            month = month + 1;
            birthday = ((day < 10) ? "0" + day : day) + "."
                    + ((month < 10) ? "0" + month  : month) + "." + year;

        }else{

            birthday = birthdayEditTxt.getText().toString().trim();

        }

        phone = phoneEditTxt.getText().toString().trim();
        fullPhone = "+" + country.getFullNumber() + phone;


        firebaseFirestore.collection("users/").document(user_email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                UserHelperClass user = documentSnapshot.toObject(UserHelperClass.class);
                String imageName = user.getImageName();
                Log.d(TAG, "Get image name to update data: " + imageName);
                Log.d(TAG, "File name to update data: " + fileName);

                if(imageName != null && fileName == null){


//                    UserHelperClass userr = new UserHelperClass(name, surname, email, birthday, gender, fullPhone);
                    Map<String, Object> userr = new HashMap<>();
                    userr.put("name", name);
                    userr.put("surname", surname);
                    userr.put("birthday", birthday);
                    userr.put("gender", gender);
                    userr.put("phone", fullPhone);
                    userr.put("email", email);

                    firebaseFirestore.collection("users/").document(user_email).update(userr).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Log.d(TAG, "User has been updated without image");
                            progressDialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "User hasn't been updated without image: " + e.getMessage());

                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            Log.d(TAG, "User update without image has been canceled");

                        }
                    });

                } else if (fileName == null){

                    imageReference = storageReference.child("images/" + userId + "/" + "profile_image/" + imageName);
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            UserHelperClass user = documentSnapshot.toObject(UserHelperClass.class);
                            Uri imgUrl = Uri.parse(user.getImageUrl());
                            strUri = imgUrl.toString().trim();
                            existedImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);
                            urll = uri.toString().trim();

                            if(strUri == urll){

                                Map<String, Object> userr = new HashMap<>();
                                userr.put("name", name);
                                userr.put("surname", surname);
                                userr.put("birthday", birthday);
                                userr.put("gender", gender);
                                userr.put("phone", fullPhone);
                                userr.put("email", email);

                                firebaseFirestore.collection("users/").document(user_email).update(userr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Log.d(TAG, "User has been updated without image (imageName, strUri == urll)");
                                        progressDialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.d(TAG, "User hasn't been updated without image (imageName, strUri == urll):" + e.getMessage());

                                    }
                                }).addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {

                                        Log.d(TAG, "User update without image has been canceled (imageName, strUri == urll)");

                                    }
                                });

                            }else {

                                Map<String, Object> userr = new HashMap<>();
                                userr.put("name", name);
                                userr.put("surname", surname);
                                userr.put("birthday", birthday);
                                userr.put("gender", gender);
                                userr.put("phone", fullPhone);
                                userr.put("email", email);
                                userr.put("imageName", imageName);
                                userr.put("imageUrl", urll);

                                firebaseFirestore.collection("users/").document(user_email).update(userr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Log.d(TAG, "User has been updated with image (imageName, strUri != urll)");
                                        progressDialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.d(TAG, "User hasn't been updated with image (imageName, strUri != urll): " + e.getMessage());

                                    }
                                }).addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {

                                        Log.d(TAG, "User update with image has been  (imageName, strUri != urll)");

                                    }
                                });

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "Url of the image by imageName wasn't get; " + e.getMessage());

                        }
                    });

                }else {

                    imageReference = storageReference.child("images/" + userId + "/" + "profile_image/" + fileName);
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            UserHelperClass user = documentSnapshot.toObject(UserHelperClass.class);
                            Uri imgUrl = Uri.parse(user.getImageUrl());
                            strUri = imgUrl.toString().trim();
                            existedImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);
                            urll = uri.toString().trim();

                            if(strUri == urll){

                                Map<String, Object> userr = new HashMap<>();
                                userr.put("name", name);
                                userr.put("surname", surname);
                                userr.put("birthday", birthday);
                                userr.put("gender", gender);
                                userr.put("phone", fullPhone);
                                userr.put("email", email);

                                firebaseFirestore.collection("users/").document(user_email).update(userr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Log.d(TAG, "User has been updated without image (fileName, strUri == urll)");
                                        progressDialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.d(TAG, "User hasn't been updated without image (fileName, strUri == urll):" + e.getMessage());

                                    }
                                }).addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {

                                        Log.d(TAG, "User update without image has been canceled (fileName, strUri == urll)");

                                    }
                                });

                            }else {

                                Map<String, Object> userr = new HashMap<>();
                                userr.put("name", name);
                                userr.put("surname", surname);
                                userr.put("birthday", birthday);
                                userr.put("gender", gender);
                                userr.put("phone", fullPhone);
                                userr.put("email", email);
                                userr.put("imageName", fileName);
                                userr.put("imageUrl", urll);

                                firebaseFirestore.collection("users/").document(user_email).update(userr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Log.d(TAG, "User has been updated with image (fileName, strUri != urll)");
                                        progressDialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.d(TAG, "User hasn't been updated with image (fileName, strUri != urll): " + e.getMessage());

                                    }
                                }).addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {

                                        Log.d(TAG, "User update with image has been  (fileName, strUri != urll)");

                                    }
                                });

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "Url of the image by fileName wasn't get; " + e.getMessage());

                        }
                    });

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "Edit profile(), imageName get wasn't successful");

            }
        });

    }

    private void deleteData(){



    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "There is camera on your phone", Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, "There is no camera on your phone", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void takePicture() {

        Log.d(TAG, "Take picture activity");
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePicture.resolveActivity(this.getPackageManager()) != null) {

            File photoFile = null;

            try {

                photoFile = createPhotoFile();

            } catch (IOException e) {
                Log.d(TAG, "Photo file cannot be created");
                e.printStackTrace();
            }

            if (photoFile != null) {

                Log.d(TAG, "File != null");
                photoUri = FileProvider.getUriForFile(this, "com.bilki.mywardrobe.fileprovider", photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);

            }

        }

    }

    private File createPhotoFile() throws IOException {

        Log.d(TAG, "File created");

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        fileName = "myWardrobe_" + timeStamp + ".";
//                + getFileExt(photoUri);
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(fileName, ".jpg", storageDirectory);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = photoFile.getAbsolutePath();

        return photoFile;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "On result");

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            f = new File(currentPhotoPath);
            imageUri = Uri.fromFile(f);
            profileImgEdit.setImageURI(imageUri);
            Log.d(TAG, "Absolute Url of the photo is " + Uri.fromFile(f));

            galleryAddPic();


        }

        if (requestCode == SELECT_PICTURE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            profileImgEdit.setImageURI(imageUri);
            Log.d(TAG, "Gallery url of the photo is " + imageUri);

        }

    }

    private void galleryAddPic() {

        Log.d(TAG, "Added to gallery");

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        f = new File(currentPhotoPath);
        imageUri = Uri.fromFile(f);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void takeImageFromGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/");
        startActivityForResult(gallery, SELECT_PICTURE_CODE);
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        fileName = "myWardrobe_" + timeStamp;


    }

    private void uploadImageToFirebase(String name, Uri contentUri) {

        if (imageUri != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploadin photo...");
            progressDialog.show();

            firebaseFirestore.collection("users/").document(user_email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    UserHelperClass user = documentSnapshot.toObject(UserHelperClass.class);
                    String ImageUrl = user.getImageUrl();
                    default_img_name = user.getImageName();
                    default_default_img_name = "default_profile_photo.png";

                    if (ImageUrl != null) {

                        Uri imgUrl = Uri.parse(ImageUrl);
                        strUri = imgUrl.toString().trim();

                        Log.d(TAG, "strUri:" + strUri);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "Getting data to delete previous image wasn't successful");

                }
            });

            imageReference = storageReference.child("images/" + userId + "/" + "profile_image/" + name);
            uploadTask = imageReference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Log.d(TAG, "Default image name: " + default_img_name);

                            default_image_reference = storageReference.child("images/" + "default_image/" + default_default_img_name);
                            default_image_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    default_default_image_url = uri.toString().trim();
                                    Log.d(TAG, "default_default_image_url: " + default_default_image_url);

                                    if (!default_default_image_url.equals(strUri) || !default_img_name.equals(default_default_img_name)) {

                                        deletePreviousImage(strUri);

                                    }
                                    progressDialog.dismiss();

                                }
                            });






                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "Url of the previous photo wasn't get " + e.getMessage());


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "Upload failed: " + e.getMessage());
                    Toast.makeText(Profile.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            Toast.makeText(Profile.this, "No file selected", Toast.LENGTH_SHORT).show();

        }

    }

    private void deletePreviousImage(String strUri) {

        existedImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);
        existedImageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d(TAG, "Previous image deleted!");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "Image wasn't deleted: " + e.getMessage());

            }
        });

    }

    private void closeKeyboard(){

        View view = this.getCurrentFocus();
        if (view != null){

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (profileDataEdit.getVisibility() == View.VISIBLE && profileImageEdit.getVisibility() == View.VISIBLE){

            profileData.setVisibility(View.VISIBLE);
            profImage.setVisibility(View.VISIBLE);
            profileDataEdit.setVisibility(View.GONE);
            profileImageEdit.setVisibility(View.GONE);

            editProfile.setVisibility(View.VISIBLE);
            saveProfile.setVisibility(View.GONE);
            cancelProfile.setVisibility(View.GONE);

        }else {

            Intent i = new Intent(Profile.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
            finish();
        }

    }


}