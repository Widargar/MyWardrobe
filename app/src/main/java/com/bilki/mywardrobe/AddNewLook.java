package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNewLook extends AppCompatActivity {

    private Button nextStepOne;
    private String userId, user_email, fileName, currentPhotoPath, title, description, _fileName, _imageUrl, _title, _description;
    private TextInputEditText title_look_edit, description_look_edit;
    private TextInputLayout title_look, description_look;
    private TextView addImage;
    private Button gallery_bttn, camera_bttn;
    private ImageView look_picture, backLook;
    private Uri imageUri, photoUri, _imageUri;
    private File f;
    private Intent _intent;
    private Dialog choose_source;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference, imageReference, spaceReference;
    private StorageTask uploadTask;
    private DocumentSnapshot document;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int SELECT_PICTURE_CODE = 101;
    private static final String TAG = "bilki: AddNewLook:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_look);

        _intent = getIntent();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = mAuth.getUid();

        look_picture = (ImageView) findViewById(R.id.look_picture);
        nextStepOne = (Button) findViewById(R.id.next_step_1);
        title_look = (TextInputLayout) findViewById(R.id.look_title);
        description_look = (TextInputLayout) findViewById(R.id.look_description);
        title_look_edit = (TextInputEditText) findViewById(R.id.edit_look_title);
        description_look_edit = (TextInputEditText) findViewById(R.id.edit_look_description);
        addImage = (TextView) findViewById(R.id.add_look_image);
        backLook = (ImageView) findViewById(R.id.back_add_new_look);

        _title = _intent.getStringExtra("_title");
        _description = _intent.getStringExtra("_description");
        _fileName = _intent.getStringExtra("_imageName");
        _imageUrl = _intent.getStringExtra("_imageUrl");

        Log.d(TAG, "_description: " + _description);

        look_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseSource();

            }
        });

        backLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(AddNewLook.this, Closet.class);
                startActivity(i);
                finish();

            }
        });

        getUserEmail();
        nextStep_passData();

        if(_title != null){

            setData();

        }


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

    private void setData(){

        if(_description.isEmpty() ||_description == null){

            title_look_edit.setText(_title);
            _imageUri = Uri.parse(_imageUrl);
            look_picture.setImageURI(_imageUri);

        } else {

            title_look_edit.setText(_title);
            description_look_edit.setText(_description);
            _imageUri = Uri.parse(_imageUrl);
            look_picture.setImageURI(_imageUri);

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

                checkCameraHardware(AddNewLook.this);
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
            look_picture.setImageURI(imageUri);
            Log.d(TAG, "Absolute Url of the photo is " + Uri.fromFile(f));

            galleryAddPic();


        }

        if (requestCode == SELECT_PICTURE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            look_picture.setImageURI(imageUri);
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

    private boolean titleValidation() {

        title = title_look.getEditText().getText().toString().trim();

        if (title.isEmpty()) {

            title_look.setError("Enter a title!");
            return false;

        } else {

            title_look.setError(null);
            title_look.setErrorEnabled(false);
            return true;

        }

    }

    private void nextStep_passData(){

        nextStepOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!titleValidation()){

                    return;

                } else if (imageUri != null){

                    description = description_look.getEditText().getText().toString().trim();
                    addImage.setVisibility(View.GONE);
                    Intent i = new Intent(AddNewLook.this, AddLookItems.class);
                    i.putExtra("imageName", fileName);
                    i.putExtra("imageUrl", imageUri.toString());
                    i.putExtra("lookTitle", title);
                    i.putExtra("lookDescription", description);
                    startActivity(i);
                    finish();

                } else if (_imageUrl != null){

                    description = description_look.getEditText().getText().toString().trim();
                    addImage.setVisibility(View.GONE);
                    Intent i = new Intent(AddNewLook.this, AddLookItems.class);
                    i.putExtra("imageName", fileName);
                    i.putExtra("imageUrl", _imageUrl);
                    i.putExtra("lookTitle", title);
                    i.putExtra("lookDescription", description);
                    startActivity(i);
                    finish();

                } else {

                    addImage.setVisibility(View.VISIBLE);

                }



            }
        });

    }



}