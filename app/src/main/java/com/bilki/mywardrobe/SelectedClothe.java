package com.bilki.mywardrobe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SelectedClothe extends AppCompatActivity {

    private String  _name, _description, _color, _type, _size, _season;
    private Uri _url;
    private TextView img_title, img_description, img_color, img_type, img_season, img_size;
    private ImageView img;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
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
        _url = intent.getParcelableExtra("imgUrl");
        _name = intent.getStringExtra("imgName").trim();
        _description = intent.getStringExtra("imgDescription").trim();
        _color = intent.getStringExtra("imgColor").trim();
        _size = intent.getStringExtra("imgSize").trim();
        _type = intent.getStringExtra("imgType").trim();
        _season = intent.getStringExtra("imgSeason").trim();


        img = (ImageView) findViewById(R.id.img_clothe);
        img_title = (TextView) findViewById(R.id.img_title);
        img_description = (TextView) findViewById(R.id.img_description);
        img_color = (TextView) findViewById(R.id.img_color);
        img_size = (TextView) findViewById(R.id.img_size);
        img_type = (TextView) findViewById(R.id.img_type);
        img_season = (TextView) findViewById(R.id.img_season);

        img_title.setText(_name);
        img_description.setText(_description);
        img_color.setText(_color);
        img_type.setText(_type);
        img_size.setText(_size);
        img_season.setText(_season);
        Picasso.get().load(_url).fit().centerInside().into(img);
//        img.setImageURI(_url);

    }

}