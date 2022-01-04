package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SelectedClothe extends AppCompatActivity {

    private String _name, _description, _color, _type, _size, _season, _id;
    private Uri _url;
    private TextView img_title, img_description, img_type, img_season, img_size,
    img_type_edit, img_season_edit, img_size_edit;
    private TextInputLayout title_input, description_input;
    private TextInputEditText title_edit, description_edit;
    private Button edit_bttn, save_bttn, delete_bttn;
    private LinearLayout img_desc, tags_layout, tags_layout_edit;
    private ImageView img_color, img,  img_color_edit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private DocumentSnapshot documentSnapshot;
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

//        Log.d(TAG, "Description: " + _description);


        img = (ImageView) findViewById(R.id.img_clothe);
        img_title = (TextView) findViewById(R.id.img_title);
        img_description = (TextView) findViewById(R.id.img_description);
        img_color = (ImageView) findViewById(R.id.img_color);
        img_size = (TextView) findViewById(R.id.img_size);
        img_type = (TextView) findViewById(R.id.img_type);
        img_season = (TextView) findViewById(R.id.img_season);
        img_color_edit = (ImageView) findViewById(R.id.img_color_edit);
        img_type_edit = (TextView) findViewById(R.id.img_type_edit);
        img_season_edit = (TextView) findViewById(R.id.img_season_edit);
        img_size_edit = (TextView) findViewById(R.id.img_size_edit);


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

                setData();
                editClothe();


//                Intent intent = new Intent(SelectedClothe.this, Closet_items.class);
//                startActivity(intent);
//                finish();

//                recreate();

//                Intent refresh = new Intent(SelectedClothe.this, SelectedClothe.class);
//                startActivity(refresh);
//                finish();


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

    private void editClothe() {

        Map<String, Object> clothe = new HashMap<>();
        clothe.put("name", title_input.getEditText().getText().toString().trim());
        clothe.put("description", description_input.getEditText().getText().toString().trim());

        documentReference.collection("images/").document(_id).update(clothe);
    }

    private void setData() {

        documentReference.collection("images/").document(_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()) {

//                        title_edit.setText(documentSnapshot.getString("name"));
//                        description_edit.setText(documentSnapshot.getString("description"));

                        Upload upload = documentSnapshot.toObject(Upload.class);
                        Uri imgUrl = Uri.parse(upload.getImageUrl());
                        String imgName = upload.getName();
                        String imgDescription = upload.getDescription();
                        String imgColor = upload.getColor();
                        String imgSize = upload.getSize();
                        String imgType = upload.getType();
                        String imgSeason = upload.getSeason();

//                        int visibility = img_title.getVisibility();
//                        Log.d(TAG, "Visibility: " + visibility);

                        if (tags_layout.getVisibility() == View.VISIBLE){

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
                                    img_color.setImageDrawable(getResources().getDrawable(R.drawable.color_black));
//                img_color.setText(_color);
                                    break;

                                case "White":
//                img_color.setBackground(getResources().getDrawable(R.drawable.round_background));
                                    img_color.setImageDrawable(getResources().getDrawable(R.drawable.round_background));
//                img_color.setText(_color);
                            }

                            img_type.setText(imgType);
                            img_size.setText(imgSize);
                            img_season.setText(imgSeason);
                            Picasso.get().load(imgUrl).fit().centerInside().into(img);

                        }else {

                            title_edit.setText(imgName);

                            if (imgDescription == "" || imgDescription == null || imgDescription.isEmpty()) {

                                Log.d(TAG, "Description is empty!");

                            } else {

                                description_edit.setText(imgDescription);
                                Log.d(TAG, "Description is not empty!");

                            }

                            switch (imgColor) {

                                case "Black":
                                    img_color_edit.setImageDrawable(getResources().getDrawable(R.drawable.color_black));
                                    break;

                                case "White":
                                    img_color_edit.setImageDrawable(getResources().getDrawable(R.drawable.round_background));
                            }

                            img_type_edit.setText(imgType);
                            img_size_edit.setText(imgSize);
                            img_season_edit.setText(imgSeason);



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


}