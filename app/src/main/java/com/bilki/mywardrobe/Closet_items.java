package com.bilki.mywardrobe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class Closet_items extends AppCompatActivity {

    private int position;
    private Context context;
    private String textPosition, imageUrl, name;
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

        textPosition = intent.getStringExtra("closet");

        closetItemText = (TextView) findViewById(R.id.closet_items);
        closetItemText.setText(textPosition);

        closetRecycler();

//        switch (textPosition){
//
//            case "Shirt":
//
//
//        }



    }

    private void closetRecycler(){

        Query query = documentReference.collection("images/").whereEqualTo("type", textPosition).orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Upload> options = new FirestoreRecyclerOptions.Builder<Upload>()
                .setQuery(query, Upload.class)
                .build();

        adapter = new ClothesAdapter(context, options);
//        adapter.setHasStableIds(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
//        closetRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        closetRecycler.setLayoutManager(gridLayoutManager);
        closetRecycler.setHasFixedSize(false);
        closetRecycler.setAdapter(adapter);
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


}