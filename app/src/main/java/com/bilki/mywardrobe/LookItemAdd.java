package com.bilki.mywardrobe;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LookItemAdd extends DialogFragment {

    private View view;
    private String lookId;
    private static final String TAG = "bilki: LookItemAdd: ";
    private Context context;
    private RecyclerView addLookItemRecycler;
    private ClothesAdapter _adapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private CollectionReference collectionReference, collectionReferenceImage;
    private DocumentSnapshot document;
    private DocumentReference documentReference, documentReferenceImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_look_item_add, container, false);


        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");
        context = getActivity();

        addLookItemRecycler = (RecyclerView) view.findViewById(R.id.look_items_recycler_add);

        SelectedLooks selectedLooks = (SelectedLooks) getActivity();
        lookId = selectedLooks.getLookId();

        addLookItem();

        return view;
    }

    private void addLookItem(){

        Query query = documentReference.collection("clothes/").orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Upload> options = new FirestoreRecyclerOptions.Builder<Upload>()
                .setQuery(query, Upload.class)
                .build();
        _adapter = new ClothesAdapter(context, options);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        addLookItemRecycler.setLayoutManager(gridLayoutManager);
        addLookItemRecycler.setHasFixedSize(false);
        addLookItemRecycler.setAdapter(_adapter);

        _adapter.setOnItemClickListener(new ClothesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                Upload upload = documentSnapshot.toObject(Upload.class);
                String imgId = documentSnapshot.getId();
                String imgUrl = upload.getImageUrl();
                String name = upload.getName();
                String description = upload.getDescription();
                String type = upload.getType();
                String color  = upload.getColor();
                String size = upload.getSize();
                String season = upload.getSeason();
                String imageName = upload.getImageName();
                getDialog().dismiss();

                LookItems lookItem = new LookItems(name, imgUrl, imageName, description, color, size, type, season);

                documentReference.collection("looks/").document(lookId).collection("look_items/").add(lookItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {


                        Log.d(TAG, "New look item has been added!");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "New look items hasn't been added!");

                    }
                });

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        _adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        _adapter.stopListening();
    }



}