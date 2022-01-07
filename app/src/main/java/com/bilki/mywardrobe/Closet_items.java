package com.bilki.mywardrobe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.Query;

public class Closet_items extends AppCompatActivity{

    private int position;
    private Context context;
    private String textPosition, imageUrl, name, imgType, img_type;
    private TextView closetItemText;
    private ClothesAdapter adapter;
    private RecyclerView closetRecycler;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private CollectionReference collectionReference, collectionReferenceImage;
    private DocumentSnapshot document;
    private DocumentReference documentReference, documentReferenceImage;
    private final static String TAG = "bilki: closet_items ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closte_items);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");

        context = Closet_items.this;

        Intent intent = getIntent();

        closetRecycler = (RecyclerView) findViewById(R.id.closet_recycler);


        imgType = intent.getStringExtra("imgType");
        textPosition = intent.getStringExtra("closet");


        closetItemText = (TextView) findViewById(R.id.closet_items);

        if(textPosition == null || textPosition == ""){


            closetItemText.setText(imgType);
            img_type = imgType;


        }else{


            closetItemText.setText(textPosition);
            img_type = textPosition;

        }



        closetRecycler();

//        switch (textPosition){
//
//            case "Shirt":
//
//
//        }



    }

    private void closetRecycler(){



        Query query = documentReference.collection("images/").whereEqualTo("type", img_type).orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Upload> options = new FirestoreRecyclerOptions.Builder<Upload>()
                .setQuery(query, Upload.class)
                .build();
        adapter = new ClothesAdapter(context, options);

        adapter.setHasStableIds(true);
//        adapter.setHasStableIds(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
//        closetRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        closetRecycler.setLayoutManager(gridLayoutManager);
        closetRecycler.setHasFixedSize(false);
        closetRecycler.setAdapter(adapter);


//        long id = adapter.getItemId(2);
//
//        if(id != RecyclerView.NO_ID){
//
//            adapter.notifyDataSetChanged();
//
//        }else{
//
//
//
//        }

//        adapter.getItemCount();
//
//        long id = adapter.getItemId(2);
//
//        if (id == -1){
//
//
//
//        }

        adapter.setOnItemClickListener(new ClothesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                Upload upload = documentSnapshot.toObject(Upload.class);
                String imgId = documentSnapshot.getId();

//                String path = documentSnapshot.getReference().getPath();
//
//                Uri imgUrl = Uri.parse(upload.getImageUrl());
//                String imgName = upload.getName();
//                String imgDescription = upload.getDescription();
//                String imgColor = upload.getColor();
//                String imgSize = upload.getSize();
//                String imgType = upload.getType();
//                String imgSeason = upload.getSeason();

                Intent intent = new Intent(context, SelectedClothe.class);
//                intent.putExtra("imgUrl", imgUrl);
//                intent.putExtra("imgName", imgName);
//                intent.putExtra("imgDescription", imgDescription);
//                intent.putExtra("imgColor", imgColor);
//                intent.putExtra("imgSize", imgSize);
//                intent.putExtra("imgType", imgType);
//                intent.putExtra("imgSeason", imgSeason);
                intent.putExtra("imgId", imgId);
                startActivity(intent);
                finish();


            }
        });
        Log.d(TAG, "closetsRecycler: Adapter set");


//        collectionReferenceImage = documentReference.collection("images/");
//        documentReferenceImage = collectionReferenceImage.document();
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if (task.isSuccessful()) {
//                    document = task.getResult();
//
//                    if(document.exists()){
//
//                        imageUrl = document.getString("imageUrl");
//                        name = document.getString("name");
//
//                    }else {
//
//                        Log.d(TAG, "No such document");
//
//                    }
//                } else {
//
//                    Log.d(TAG, "get failed with ", task.getException());
//
//                }
//
//            }
//        });
//
//        closetRecycler.setAdapter(adapter);


    }
//
//    @Override
//    public void onClotheClick(int position) {
//
//        Log.d(TAG, "onClotheClick: Clicked: " + position);
//
//        Intent intent = new Intent(Closet_items.this, MainActivity.class);
//        startActivity(intent);
//
//    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(Closet_items.this, Closet.class);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        startActivity(i);
        finish();
    }
}