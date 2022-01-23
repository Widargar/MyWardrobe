package com.bilki.mywardrobe;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedClothe extends AppCompatActivity {

    private String _name, _description, _color, _type, _size, _season, _id, __id, id, fileName, currentPhotoPath,
            userId, strUri, strImageUri, urll ;
    public String type, size, season;
    private File f;
    private Uri imageUri, _url;
    private Dialog choose_source;
    private TextView img_title, img_description, img_type, img_season, img_size,
            img_type_edit, img_season_edit, img_size_edit;
    private String[] typesArray, sizesArray, seasonsArray;
    private List<String> types, sizes, seasons;
    private TextInputLayout title_input, description_input;
    private TextInputEditText title_edit, description_edit;
    private Button edit_bttn, save_bttn, delete_bttn, camera_bttn, gallery_bttn;
    private LinearLayout img_desc, tags_layout, tags_layout_edit;
    private ImageView img_color, img, img_edit, img_color_edit, backSelectedClothe;
    private Spinner typeSpinner, sizeSpinner, seasonSpinner;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference, imageReference, existedImageReference;
    private StorageTask uploadTask;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private DocumentSnapshot documentSnapshot;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int SELECT_PICTURE_CODE = 101;
    private final static String TAG = "bilki: selected_clothe ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_clothe);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");

        userId = mAuth.getUid();

        Intent intent = getIntent();

//        _url = intent.getStringExtra("imgUrl").trim();
//        _url = intent.getParcelableExtra("imgUrl");
//        _name = intent.getStringExtra("imgName").trim();
//        _description = intent.getStringExtra("imgDescription").trim();
//        _color = intent.getStringExtra("imgColor").trim();
//        _size = intent.getStringExtra("imgSize").trim();
//        _type = intent.getStringExtra("imgType").trim();
//        _season = intent.getStringExtra("imgSeason").trim();


        _id = intent.getStringExtra("imgId").trim();

        backSelectedClothe = (ImageView) findViewById(R.id.back_selected_clothe);

        backSelectedClothe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SelectedClothe.this, Closet_items.class);
                i.putExtra("imgType", img_type.getText().toString().trim());
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                finish();

            }
        });

//        Log.d(TAG, "Description: " + _description);


        img = (ImageView) findViewById(R.id.img_clothe);
        img_title = (TextView) findViewById(R.id.img_title);
        img_description = (TextView) findViewById(R.id.img_description);
        img_color = (ImageView) findViewById(R.id.img_color);
        img_size = (TextView) findViewById(R.id.img_size);
        img_type = (TextView) findViewById(R.id.img_type);
        img_season = (TextView) findViewById(R.id.img_season);
        img_edit = (ImageView) findViewById(R.id.img_clothe_edit);
        img_color_edit = (ImageView) findViewById(R.id.img_color_edit);
//        img_type_edit = (TextView) findViewById(R.id.img_type_edit);
//        img_season_edit = (TextView) findViewById(R.id.img_season_edit);
//        img_size_edit = (TextView) findViewById(R.id.img_size_edit);


        img_desc = (LinearLayout) findViewById(R.id.img_description_layout);
        tags_layout = (LinearLayout) findViewById(R.id.tags_layout);
        tags_layout_edit = (LinearLayout) findViewById(R.id.tags_layout_edit);

        title_input = (TextInputLayout) findViewById(R.id.img_title_edit);
        description_input = (TextInputLayout) findViewById(R.id.img_description_edit);

        title_edit = (TextInputEditText) findViewById(R.id.edit_clothe_title);
        description_edit = (TextInputEditText) findViewById(R.id.edit_clothe_description);

        edit_bttn = (Button) findViewById(R.id.edit_bttn);
        save_bttn = (Button) findViewById(R.id.save_bttn);
        delete_bttn = (Button) findViewById(R.id.delete_bttn);



        setData();

        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseSource();

            }
        });

//        img_title.setText(_name);
//
//        if(_description == "" || _description == null || _description.isEmpty()){
//
//            img_desc.setVisibility(View.GONE);
//            Log.d(TAG, "Description is empty!");
//
//        } else {
//
//            img_desc.setVisibility(View.VISIBLE);
//            img_description.setText(_description);
//            Log.d(TAG, "Description is not empty!");
//
//        }

//        switch (_color){
//
//            case "Black":
////                img_color.setBackground(getResources().getDrawable(R.drawable.color_black));
//                img_color.setImageDrawable(getResources().getDrawable(R.drawable.color_black));
////                img_color.setText(_color);
//                break;
//
//            case "White":
////                img_color.setBackground(getResources().getDrawable(R.drawable.round_background));
//                img_color.setImageDrawable(getResources().getDrawable(R.drawable.round_background));
////                img_color.setText(_color);
//        }


//        img_type.setText(_type);
//        img_size.setText(_size);
//        img_season.setText(_season);
//        Picasso.get().load(_url).fit().centerInside().into(img);
//        img.setImageURI(_url);


        edit_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tags_layout.setVisibility(View.GONE);
                tags_layout_edit.setVisibility(View.VISIBLE);
                edit_bttn.setVisibility(View.GONE);
                save_bttn.setVisibility(View.VISIBLE);
                delete_bttn.setVisibility(View.VISIBLE);
                img.setVisibility(View.GONE);
                img_edit.setVisibility(View.VISIBLE);

//                title_edit.setText(_name);
//
//                if (_description == "" || _description == null || _description.isEmpty()) {
//
//                    Log.d(TAG, "Edit description is empty!");
//
//                } else {
//
//                    description_edit.setText(_description);
//                    Log.d(TAG, "Edit description is not empty!");
//
//                }

                setData();

            }
        });

        save_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save_bttn.setVisibility(View.GONE);
                delete_bttn.setVisibility(View.GONE);
                tags_layout.setVisibility(View.VISIBLE);
                tags_layout_edit.setVisibility(View.GONE);
                edit_bttn.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                img_edit.setVisibility(View.GONE);


//                imageUri = Uri.fromFile(f);
                if (uploadTask != null && uploadTask.isInProgress()) {

                    Toast.makeText(SelectedClothe.this, "Upload in progress", Toast.LENGTH_SHORT).show();

                } else if (imageUri != null) {

                    uploadImageToFirebase(fileName, imageUri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {

                                editClothe();
                                setData();

//                            Intent i = new Intent(SelectedClothe.this, SelectedClothe.class);
//                            startActivity(i);
//                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "upload task wasn't successful: " + e.getMessage());

                        }
                    });

                } else if (imageUri == null) {

//                    if (documentSnapshot.exists()){

                        editClothe();
                        setData();

//                    }


//                    Intent i = new Intent(SelectedClothe.this, SelectedClothe.class);
//                    startActivity(i);
//                    finish();

                }


//                } else if(uploadTask.isComplete()){
//
//
//
//                }


//                Intent intent = new Intent(SelectedClothe.this, Closet_items.class);
//                startActivity(intent);
//                finish();

//                recreate();

//                Intent refresh = new Intent(SelectedClothe.this, SelectedClothe.class);
//                startActivity(refresh);
//                finish();

            }
        });

        delete_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteClothe();

            }
        });


    }

    private boolean titleValidation() {

        String val = title_input.getEditText().getText().toString().trim();
        String val_code = "[a-zA-Z ]+";

        if (val.isEmpty()) {

            title_input.setError("Title cannot be empty!");
            return false;

        } else if (val.length() > 25) {

            title_input.setError("Title is too long!");
            return false;

        } else if (!val.matches(val_code)) {

            title_input.setError("Invalid title!");
            return false;

        } else {

            title_input.setError(null);
            title_input.setErrorEnabled(false);
            return true;

        }
    }


    private void setData() {

        documentReference.collection("images/").document(_id).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.d(TAG, "Data set failed: " + error);
                    return;

                }

                documentReference.collection("images/").document(_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                if (value.exists()) {

//                        title_edit.setText(documentSnapshot.getString("name"));
//                        description_edit.setText(documentSnapshot.getString("description"));



                                    Upload upload = value.toObject(Upload.class);
                                    Uri imgUrl = Uri.parse(upload.getImageUrl());
                                    String imgName = upload.getName();
                                    String imgDescription = upload.getDescription();
                                    String imgColor = upload.getColor();
                                    String imgSize = upload.getSize();
                                    String imgType = upload.getType();
                                    String imgSeason = upload.getSeason();

//                        int visibility = img_title.getVisibility();
//                        Log.d(TAG, "Visibility: " + visibility);



                                    if (tags_layout.getVisibility() == View.VISIBLE && img.getVisibility() == View.VISIBLE) {

                                        Picasso.get().load(imgUrl).fit().centerInside().into(img);
                                        img_title.setText(imgName);

                                        if (imgDescription == "" || imgDescription == null || imgDescription.isEmpty()) {

                                            img_desc.setVisibility(View.GONE);
                                            Log.d(TAG, "Description is empty!");

                                        } else {

                                            img_desc.setVisibility(View.VISIBLE);
                                            img_description.setText(imgDescription);
                                            Log.d(TAG, "Description is not empty!");

                                        }

                                        switch (imgColor) {

                                            case "Black":
//                img_color.setBackground(getResources().getDrawable(R.drawable.color_black));
                                                img_color.setImageDrawable(getResources().getDrawable(R.drawable.color_black_));
//                img_color.setText(_color);
                                                break;

                                            case "White":
//                img_color.setBackground(getResources().getDrawable(R.drawable.round_background));
                                                img_color.setImageDrawable(getResources().getDrawable(R.drawable.color_white_));
//                img_color.setText(_color);
                                        }

                                        img_type.setText(imgType);
                                        img_size.setText(imgSize);
                                        img_season.setText(imgSeason);


                                    } else {

                                        Picasso.get().load(imgUrl).fit().centerInside().into(img_edit);

                                        title_edit.setText(imgName);

                                        if (imgDescription == "" || imgDescription == null || imgDescription.isEmpty()) {

                                            Log.d(TAG, "Description is empty!");

                                        } else {

                                            description_edit.setText(imgDescription);
                                            Log.d(TAG, "Description is not empty!");

                                        }

                                        switch (imgColor) {

                                            case "Black":
                                                img_color_edit.setImageDrawable(getResources().getDrawable(R.drawable.color_black_));
                                                break;

                                            case "White":
                                                img_color_edit.setImageDrawable(getResources().getDrawable(R.drawable.color_white_));
                                        }

//                                    img_type_edit.setText(imgType);
//                                    img_size_edit.setText(imgSize);
//                                    img_season_edit.setText(imgSeason);

                                        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
                                        sizeSpinner = (Spinner) findViewById(R.id.size_spinner);
                                        seasonSpinner = (Spinner) findViewById(R.id.season_spinner);

                                        typesArray = getResources().getStringArray(R.array.types);
                                        sizesArray = getResources().getStringArray(R.array.sizes);
                                        seasonsArray = getResources().getStringArray(R.array.seasons);

                                        types = new ArrayList<String>(Arrays.asList(typesArray));
                                        types.add(0, imgType);
                                        sizes =  new ArrayList<String>(Arrays.asList(sizesArray));
                                        sizes.add(0, imgSize);
                                        seasons = new ArrayList<String>(Arrays.asList(seasonsArray));
                                        seasons.add(0, imgSeason);

                                        ArrayAdapter<String> typeAdapter = new ArrayAdapter(SelectedClothe.this, R.layout.spinner_item, types);
                                        typeAdapter.setDropDownViewResource(R.layout.spinner_item_list);

                                        ArrayAdapter<String> sizeAdapter = new ArrayAdapter(SelectedClothe.this, R.layout.spinner_item, sizes);
                                        sizeAdapter.setDropDownViewResource(R.layout.spinner_item_list);

                                        ArrayAdapter<String> seasonAdapter = new ArrayAdapter(SelectedClothe.this, R.layout.spinner_item, seasons);
                                        seasonAdapter.setDropDownViewResource(R.layout.spinner_item_list);

                                        typeSpinner.setAdapter(typeAdapter);
                                        type = types.get(0);
                                        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                if (parent.getItemAtPosition(position).equals(imgType)){

                                                    switch (imgType){

                                                        case "Shirt":
                                                            types.remove(1);
                                                            break;
                                                        case "T-shirt":
                                                            types.remove(2);
                                                            break;
                                                        case "Sweater":
                                                            types.remove(3);
                                                            break;
                                                        case "Jacket":
                                                            types.remove(4);
                                                            break;
                                                        case "Jeans":
                                                            types.remove(5);
                                                            break;
                                                        case "Sneakers":
                                                            types.remove(6);
                                                            break;
                                                        case "Dressed shoes":
                                                            types.remove(7);
                                                            break;
                                                        case "Boots":
                                                            types.remove(8);
                                                            break;

                                                    }

                                                }else {

                                                    type = parent.getItemAtPosition(position).toString();
                                                    Log.d(TAG, "Type onItemSelected: " + type);

                                                }

                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {//

                                            }
                                        });

                                        sizeSpinner.setAdapter(sizeAdapter);
                                        size = sizes.get(0);
                                        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                if (parent.getItemAtPosition(position).equals(imgSize)){

                                                    switch (imgSize){

                                                        case "XS":
                                                            sizes.remove(1);
                                                            break;
                                                        case "S":
                                                            sizes.remove(2);
                                                            break;
                                                        case "M":
                                                            sizes.remove(3);
                                                            break;
                                                        case "L":
                                                            sizes.remove(4);
                                                            break;
                                                        case "XL":
                                                            sizes.remove(5);
                                                            break;
                                                        case "XXL":
                                                            sizes.remove(6);
                                                            break;

                                                    }

                                                }else {

                                                    size = parent.getItemAtPosition(position).toString();
                                                    Log.d(TAG, "Size onItemSelected: " + type);

                                                }

                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });

                                        seasonSpinner.setAdapter(seasonAdapter);
                                        season = seasons.get(0);
                                        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                if (parent.getItemAtPosition(position).equals(imgSeason)){

                                                    switch (imgSeason){

                                                        case "Winter":
                                                            seasons.remove(1);
                                                            break;
                                                        case "Spring":
                                                            seasons.remove(2);
                                                            break;
                                                        case "Summer":
                                                            seasons.remove(3);
                                                            break;
                                                        case "Autumn":
                                                            seasons.remove(4);
                                                            break;

                                                    }

                                                }else {

                                                    season = parent.getItemAtPosition(position).toString();
                                                    Log.d(TAG, "Season onItemSelected: " + type);

                                                }

                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });


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

//        documentReference.collection("images/").document(_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if (task.isSuccessful()) {
//
//                    documentSnapshot = task.getResult();
//
//                    if (documentSnapshot.exists()) {
//
////                        title_edit.setText(documentSnapshot.getString("name"));
////                        description_edit.setText(documentSnapshot.getString("description"));
//
//                        Upload upload = documentSnapshot.toObject(Upload.class);
//                        Uri imgUrl = Uri.parse(upload.getImageUrl());
//                        String imgName = upload.getName();
//                        String imgDescription = upload.getDescription();
//                        String imgColor = upload.getColor();
//                        String imgSize = upload.getSize();
//                        String imgType = upload.getType();
//                        String imgSeason = upload.getSeason();
//
////                        int visibility = img_title.getVisibility();
////                        Log.d(TAG, "Visibility: " + visibility);
//
//
//
//                        if (tags_layout.getVisibility() == View.VISIBLE && img.getVisibility() == View.VISIBLE) {
//
//                            Picasso.get().load(imgUrl).fit().centerInside().into(img);
//                            img_title.setText(imgName);
//
//                            if (imgDescription == "" || imgDescription == null || imgDescription.isEmpty()) {
//
//                                img_desc.setVisibility(View.GONE);
//                                Log.d(TAG, "Description is empty!");
//
//                            } else {
//
//                                img_desc.setVisibility(View.VISIBLE);
//                                img_description.setText(imgDescription);
//                                Log.d(TAG, "Description is not empty!");
//
//                            }
//
//                            switch (imgColor) {
//
//                                case "Black":
////                img_color.setBackground(getResources().getDrawable(R.drawable.color_black));
//                                    img_color.setImageDrawable(getResources().getDrawable(R.drawable.color_black));
////                img_color.setText(_color);
//                                    break;
//
//                                case "White":
////                img_color.setBackground(getResources().getDrawable(R.drawable.round_background));
//                                    img_color.setImageDrawable(getResources().getDrawable(R.drawable.round_background));
////                img_color.setText(_color);
//                            }
//
//                            img_type.setText(imgType);
//                            img_size.setText(imgSize);
//                            img_season.setText(imgSeason);
//
//
//                        } else {
//
//                            Picasso.get().load(imgUrl).fit().centerInside().into(img_edit);
//
//                            title_edit.setText(imgName);
//
//                            if (imgDescription == "" || imgDescription == null || imgDescription.isEmpty()) {
//
//                                Log.d(TAG, "Description is empty!");
//
//                            } else {
//
//                                description_edit.setText(imgDescription);
//                                Log.d(TAG, "Description is not empty!");
//
//                            }
//
//                            switch (imgColor) {
//
//                                case "Black":
//                                    img_color_edit.setImageDrawable(getResources().getDrawable(R.drawable.color_black));
//                                    break;
//
//                                case "White":
//                                    img_color_edit.setImageDrawable(getResources().getDrawable(R.drawable.round_background));
//                            }
//
////                            img_type_edit.setText(imgType);
//                            img_size_edit.setText(imgSize);
//                            img_season_edit.setText(imgSeason);
//
//
//                            typeSpinner = (Spinner) findViewById(R.id.type_spinner);
//                            typesArray = getResources().getStringArray(R.array.types);
//                            types = new ArrayList<String>(Arrays.asList(typesArray));
//                            types.add(0, imgType);
//                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(SelectedClothe.this, R.layout.spinner_item, types);
//                            arrayAdapter.setDropDownViewResource(R.layout.spinner_item_list);
//                            typeSpinner.setAdapter(arrayAdapter);
//                            typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                @Override
//                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                                    if (parent.getItemAtPosition(position).equals(imgType)){
//
//                                        switch (imgType){
//
//                                            case "Shirt":
//                                                types.remove(1);
//                                                break;
//                                            case "T-shirt":
//                                                types.remove(2);
//                                                break;
//                                            case "Sweater":
//                                                types.remove(3);
//                                                break;
//                                            case "Jacket":
//                                                types.remove(4);
//                                                break;
//                                            case "Jeans":
//                                                types.remove(5);
//                                                break;
//                                            case "Sneakers":
//                                                types.remove(6);
//                                                break;
//                                            case "Dressed shoes":
//                                                types.remove(7);
//                                                break;
//                                            case "Boots":
//                                                types.remove(8);
//                                                break;
//
//                                        }
//
//                                    }else {
//
//                                        String item = parent.getItemAtPosition(position).toString();
//                                        Toast.makeText(SelectedClothe.this, "Selected type: " + item, Toast.LENGTH_SHORT).show();
//
//                                    }
//
//                                }
//
//                                @Override
//                                public void onNothingSelected(AdapterView<?> parent) {
//
//                                }
//                            });
//
//
//
//
//                        }
//
//
//                    } else {
//
//                        Log.d(TAG, "Document doesn't exist");
//
//                    }
//
//                } else {
//
//                    Log.d(TAG, "Failed with: " + task.getException());
//
//                }
//
//            }
//        });

    }

    public void chooseSource() {

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

                checkCameraHardware(SelectedClothe.this);
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
            Log.d(TAG, "checkCameraHardware: There is camera on your phone!");
            return true;
        } else {
            Log.d(TAG, "checkCameraHardware: There is no camera on your phone!");
            return false;
        }
    }

    public void takeImageFromGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/");
        startActivityForResult(gallery, SELECT_PICTURE_CODE);
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        fileName = "myWardrobe_" + timeStamp + ".";


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
                Uri photoUri = FileProvider.getUriForFile(this, "com.bilki.mywardrobe.fileprovider", photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                //getActivity().startActivityFromFragment(Camera_fragment.this, takePicture, REQUEST_IMAGE_CAPTURE);
                startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);

            }

        }

    }

    private File createPhotoFile() throws IOException {

        Log.d(TAG, "File created");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        fileName = "myWardrobe_" + timeStamp + ".";
        //File storageDirectory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(fileName, ".jpg", storageDirectory);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = photoFile.getAbsolutePath();
        f = new File(currentPhotoPath);
        imageUri = Uri.fromFile(f);

//        f = new File(currentPhotoPath);
//        contentUri = Uri.fromFile(f);
        //uploadImageToFirebase(f.getName(), contentUri);

        return photoFile;

    }

    private String getFileExt(Uri contentUri) {

        ContentResolver c = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "On result");

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            /*Bitmap photoBitmap = BitmapFactory.decodeFile(currentPhotoPath);

            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            takenPicture.setImageBitmap(photoBitmap);*/

            f = new File(currentPhotoPath);
            imageUri = Uri.fromFile(f);
            img_edit.setImageURI(imageUri);
            Log.d(TAG, "Absolute Url of the photo is " + Uri.fromFile(f));

            galleryAddPic();


        }/*else{

            Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();

        }*/

        if (requestCode == SELECT_PICTURE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
//            String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
//            String fileName = "myWardrobe_" + timeStamp + "." + getFileExt(imageUri);
            img_edit.setImageURI(imageUri);
            Log.d(TAG, "Gallery url of the photo is " + fileName);
            //uploadImageToFirebase(fileName, contentUri);


        }/*else{

            Toast.makeText(getActivity(), "Picture wasn't chosen!", Toast.LENGTH_SHORT).show();

        }*/
    }

    private void galleryAddPic() {

        Log.d(TAG, "Added to gallery");

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        f = new File(currentPhotoPath);
        imageUri = Uri.fromFile(f);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {

        if (imageUri != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Adding...");
            progressDialog.show();

//            deletePreviousImage();


            documentReference.collection("images/").document(_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    Upload upload = documentSnapshot.toObject(Upload.class);
                    Uri imgUrl = Uri.parse(upload.getImageUrl());
                    strUri = imgUrl.toString().trim();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "Getting data to delete previous image wasn't successful");

                }
            });

            imageReference = storageReference.child("images/" + userId + "/" + "clothes/" + name);
            uploadTask = imageReference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Log.d(TAG, "Image Url " + uri.toString());
                            Log.d(TAG, "Uploaded");
                            Log.d(TAG, "Image name: " + name);
                            Log.d(TAG, "File name:  " + fileName);

                            deletePreviousImage(strUri);

                            progressDialog.dismiss();

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
                    Toast.makeText(SelectedClothe.this, "Upload failed", Toast.LENGTH_SHORT).show();

                }
            });

        } else {

            Toast.makeText(SelectedClothe.this, "No file selected", Toast.LENGTH_SHORT).show();

        }


    }

    private void deletePreviousImage(String strUri) {

//        Upload upload = documentSnapshot.toObject(Upload.class);
//        Uri imgUrl = Uri.parse(upload.getImageUrl());
//        strUri = imgUrl.toString().trim();
        existedImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);

//        imageReference = storageReference.child("images/" + userId + "/" + );

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

    private void editClothe() {

        documentReference.collection("images/").document(_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Upload upload = documentSnapshot.toObject(Upload.class);
                String imageName = upload.getImageName();
//        Uri imgUrl = Uri.parse(upload.getImageUrl());
//        strUri = imgUrl.toString().trim();
//        existedImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);
//        String name = existedImageReference.getName();
//        strImageUri = imageUri.toString().trim();

                Log.d(TAG, "editClothe: fileName: " + fileName);
                Log.d(TAG, "editClothe: imageName: " + imageName);
                if(fileName == null){

                    imageReference = storageReference.child("images/" + userId + "/" + imageName);
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Upload upload = documentSnapshot.toObject(Upload.class);
                            Uri imgUrl = Uri.parse(upload.getImageUrl());
                            strUri = imgUrl.toString().trim();
                            existedImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);
                            urll = uri.toString().trim();
                            Log.d(TAG, "Uri: " + urll);
                            Log.d(TAG, "StrUri: " + strUri);

                            if (strUri == urll) {

                                Map<String, Object> clothe = new HashMap<>();
                                clothe.put("name", title_input.getEditText().getText().toString().trim());
                                clothe.put("description", description_input.getEditText().getText().toString().trim());
                                clothe.put("type", type);
                                clothe.put("size", size);
                                clothe.put("season", season);
                                Log.d(TAG, "Check Type: " + type);
                                documentReference.collection("images/").document(_id).update(clothe);

                            } else {

                                Map<String, Object> clothe = new HashMap<>();
                                clothe.put("name", title_input.getEditText().getText().toString().trim());
                                clothe.put("description", description_input.getEditText().getText().toString().trim());
                                clothe.put("imageUrl", urll);
                                clothe.put("imageName", imageName);
                                clothe.put("type", type);
                                clothe.put("size", size);
                                clothe.put("season", season);
                                Log.d(TAG, "Check Type: " + type);
                                documentReference.collection("images/").document(_id).update(clothe);

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "New url wasn't set: " + e.getMessage());

                        }
                    });

                }else {

                    imageReference = storageReference.child("images/" + userId + "/" + fileName);
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Upload upload = documentSnapshot.toObject(Upload.class);
                            Uri imgUrl = Uri.parse(upload.getImageUrl());
                            strUri = imgUrl.toString().trim();
                            existedImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);
                            urll = uri.toString().trim();
                            Log.d(TAG, "Uri: " + urll);
                            Log.d(TAG, "StrUri: " + strUri);

                            if (strUri == urll) {

                                Map<String, Object> clothe = new HashMap<>();
                                clothe.put("name", title_input.getEditText().getText().toString().trim());
                                clothe.put("description", description_input.getEditText().getText().toString().trim());
                                clothe.put("type", type);
                                clothe.put("size", size);
                                clothe.put("season", season);
                                Log.d(TAG, "Check Type: " + type);
                                documentReference.collection("images/").document(_id).update(clothe);

                            } else {

                                Map<String, Object> clothe = new HashMap<>();
                                clothe.put("name", title_input.getEditText().getText().toString().trim());
                                clothe.put("description", description_input.getEditText().getText().toString().trim());
                                clothe.put("imageUrl", urll);
                                clothe.put("imageName", fileName);
                                clothe.put("type", type);
                                clothe.put("size", size);
                                clothe.put("season", season);
                                Log.d(TAG, "Check Type: " + type);
                                documentReference.collection("images/").document(_id).update(clothe);

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "New url wasn't set: " + e.getMessage());

                        }
                    });

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "Getting data to edit clothe wasn't successful");

            }
        });



    }

    private void deleteClothe(){

        documentReference.collection("images/").document(_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Upload upload = documentSnapshot.toObject(Upload.class);
                Uri imgUrl = Uri.parse(upload.getImageUrl());
                strUri = imgUrl.toString().trim();

            }
        });

        documentReference.collection("images/").document(_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Log.d(TAG, "Image document deleted");
                    deletePreviousImage(strUri);
                    Log.d(TAG, "Image is deleted");
                    Intent intent = new Intent(SelectedClothe.this, Closet_items.class);
                    intent.putExtra("imgType_delete", img_type.getText().toString().trim());
                    overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                    startActivity(intent);
                    finish();


                }else{

                    Log.d(TAG, "Image document isn't deleted");

                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(SelectedClothe.this, Closet_items.class);
        i.putExtra("imgType", img_type.getText().toString().trim());
        startActivity(i);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        finish();

    }
}