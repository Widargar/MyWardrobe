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
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class Closet_items extends AppCompatActivity{

    private int position;
    private Context context;
    private String textPosition, imageUrl, name, imgType, img_type, imgTypeCheck,
            imgTypeShirt, imgTypeTShirt, imgTypeSweater, imgTypeHoodie, imgTypeJacket,
            imgTypePants, imgTypeJeans, imgTypeShorts,
            imgTypeSneaker, imgTypeDressedShoes, imgTypeBoots, search;
    private TextInputLayout searchInput;
    private TextInputEditText searchEdit;
    private TextView closetItemText;
    private ImageView backClosetItems;
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
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");

        context = Closet_items.this;

        searchInput = (TextInputLayout) findViewById(R.id.search_input);
        searchEdit = (TextInputEditText) findViewById(R.id.edit_search);
//        search = searchInput.getEditText().getText().toString().trim();
        search = "Black";

        Intent intent = getIntent();

        backClosetItems = (ImageView) findViewById(R.id.back_closet_items);

        closetRecycler = (RecyclerView) findViewById(R.id.closet_recycler);

        imgTypeCheck = intent.getStringExtra("imgType");

        if (imgTypeCheck == null || imgTypeCheck == ""){

            imgType = intent.getStringExtra("imgType_delete");

        }else {

            imgType = intent.getStringExtra("imgType");

        }

        textPosition = intent.getStringExtra("closet");

        imgTypeShirt = intent.getStringExtra("Shirt");
        imgTypeTShirt = intent.getStringExtra("T-shirt");
        imgTypeSweater = intent.getStringExtra("Sweater");
        imgTypeHoodie = intent.getStringExtra("Hoodie");
        imgTypeJacket = intent.getStringExtra("Jacket");

        imgTypePants = intent.getStringExtra("Pants");
        imgTypeJeans = intent.getStringExtra("Jeans");
        imgTypeShorts = intent.getStringExtra("Shorts");

        imgTypeSneaker = intent.getStringExtra("Sneakers");
        imgTypeDressedShoes = intent.getStringExtra("Dressed shoes");
        imgTypeBoots = intent.getStringExtra("Boots");




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

        backClosetItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgTypeShirt = null;
                imgTypeTShirt = null;
                imgTypeSweater = null;
                imgTypeHoodie = null;
                imgTypeJacket = null;
                imgTypePants = null;
                imgTypeJeans = null;
                imgTypeShorts = null;
                imgTypeSneaker = null;
                imgTypeBoots = null;
                imgTypeDressedShoes = null;

                Intent i = new Intent(Closet_items.this, Closet.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                finish();

            }
        });



    }

    private void closetRecycler(){

        if (imgTypeShirt != null && imgTypeTShirt!= null && imgTypeSweater!= null && imgTypeHoodie != null && imgTypeJacket != null){

            Query query = documentReference.collection("clothes/").whereIn("type", Arrays.asList(imgTypeShirt, imgTypeTShirt, imgTypeSweater, imgTypeJacket)).orderBy("name", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<Upload> options = new FirestoreRecyclerOptions.Builder<Upload>()
                    .setQuery(query, Upload.class)
                    .build();
            adapter = new ClothesAdapter(context, options);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            closetRecycler.setLayoutManager(gridLayoutManager);
            closetRecycler.setHasFixedSize(false);
            closetRecycler.setAdapter(adapter);

            adapter.setOnItemClickListener(new ClothesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                    Upload upload = documentSnapshot.toObject(Upload.class);
                    String imgId = documentSnapshot.getId();

                    Intent intent = new Intent(context, SelectedClothe.class);
                    intent.putExtra("imgId", imgId);
                    startActivity(intent);
                    finish();


                }
            });
            Log.d(TAG, "closetsRecycler: Adapter set");

        } else if (imgTypePants != null && imgTypeJeans != null && imgTypeShorts != null){

            Query query = documentReference.collection("clothes/").whereIn("type", Arrays.asList(imgTypePants, imgTypeJeans, imgTypeShorts)).orderBy("name", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<Upload> options = new FirestoreRecyclerOptions.Builder<Upload>()
                    .setQuery(query, Upload.class)
                    .build();
            adapter = new ClothesAdapter(context, options);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            closetRecycler.setLayoutManager(gridLayoutManager);
            closetRecycler.setHasFixedSize(false);
            closetRecycler.setAdapter(adapter);

            adapter.setOnItemClickListener(new ClothesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                    Upload upload = documentSnapshot.toObject(Upload.class);
                    String imgId = documentSnapshot.getId();

                    Intent intent = new Intent(context, SelectedClothe.class);
                    intent.putExtra("imgId", imgId);
                    startActivity(intent);
                    finish();


                }
            });
            Log.d(TAG, "closetsRecycler: Adapter set");

        } else if (imgTypeBoots != null && imgTypeSneaker != null && imgTypeDressedShoes != null){

            Query query = documentReference.collection("clothes/").whereIn("type", Arrays.asList(imgTypeBoots, imgTypeSneaker, imgTypeDressedShoes)).orderBy("name", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<Upload> options = new FirestoreRecyclerOptions.Builder<Upload>()
                    .setQuery(query, Upload.class)
                    .build();
            adapter = new ClothesAdapter(context, options);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            closetRecycler.setLayoutManager(gridLayoutManager);
            closetRecycler.setHasFixedSize(false);
            closetRecycler.setAdapter(adapter);

            adapter.setOnItemClickListener(new ClothesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                    Upload upload = documentSnapshot.toObject(Upload.class);
                    String imgId = documentSnapshot.getId();

                    Intent intent = new Intent(context, SelectedClothe.class);
                    intent.putExtra("imgId", imgId);
                    startActivity(intent);
                    finish();


                }
            });
            Log.d(TAG, "closetsRecycler: Adapter set");

        } else {
//            Query query = documentReference.collection("clothes/").orderBy("name", Query.Direction.DESCENDING).startAt(search).endAt(search + "\uf8ff");
            Query query = documentReference.collection("clothes/").whereEqualTo("type", img_type).orderBy("name", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<Upload> options = new FirestoreRecyclerOptions.Builder<Upload>()
                    .setQuery(query, Upload.class)
                    .build();
            adapter = new ClothesAdapter(context, options);
//        adapter.setHasStableIds(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
//        closetRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        int initialSize = options.getSnapshots().size();

            closetRecycler.setLayoutManager(gridLayoutManager);
            closetRecycler.setHasFixedSize(false);
            closetRecycler.setAdapter(adapter);
//        adapter.notifyItemRangeChanged(initialSize - 1, options.getSnapshots().size());
//        adapter.notifyItemRangeInserted(initialSize, options.getSnapshots().size() - 1);


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

        }




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

        imgTypeShirt = null;
        imgTypeTShirt = null;
        imgTypeSweater = null;
        imgTypeJacket = null;
        imgTypePants = null;
        imgTypeJeans = null;
        imgTypeShorts = null;
        imgTypeSneaker = null;
        imgTypeBoots = null;
        imgTypeDressedShoes = null;

        Intent i = new Intent(Closet_items.this, Closet.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        finish();

    }
}