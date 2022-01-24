package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AddLookItems extends AppCompatActivity {

    private RecyclerView added_items_recycler;
    public ArrayList<LookItems> lookItems;
    public AddLookItemsAdapter adapter;
    private LookItemsChoose dialog;
    private String imageName, imageUrl, userId, strUri, user_email, title, description;
    private ImageView backItems;
    private Uri imageUri;
    private Button addLookItem_bttn, saveLook_bttn;
    private FrameLayout lookItemsFrame;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference, imageReference, spaceReference;
    private StorageTask uploadTask;
    private DocumentSnapshot document;
    private final static String TAG = "bilki: AddLookItems:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_look_items);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = mAuth.getUid();

        Intent intent = getIntent();

        addLookItem_bttn = (Button) findViewById(R.id.add_looks_item_bttn);
        saveLook_bttn = (Button) findViewById(R.id.save_look_bttn);
        backItems = (ImageView) findViewById(R.id.back_add_look_items);
        imageName = intent.getStringExtra("imageName");
        imageUrl = intent.getStringExtra("imageUrl");
        imageUri = Uri.parse(imageUrl);
        title = intent.getStringExtra("lookTitle");
        description = intent.getStringExtra("lookDescription");
        added_items_recycler = (RecyclerView) findViewById(R.id.look_items_recycler);
        lookItems = new ArrayList<LookItems>();



        backItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(AddLookItems.this, AddNewLook.class);
                i.putExtra("_imageName", imageName);
                i.putExtra("_imageUrl", imageUrl);
                i.putExtra("_title", title);
                i.putExtra("_description", description);
                startActivity(i);
                finish();

            }
        });

        addedItemsRecycler();
        addLookItem();
        getUserEmail();
        saveLook();

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

    private void addLookItem(){

        addLookItem_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new LookItemsChoose();
                dialog.show(getFragmentManager(), "dialog");
//                lookItemsFrame.setVisibility(View.VISIBLE);
//                getSupportFragmentManager().beginTransaction().add(R.id.look_items_fragment, new LookItemsChoose()).commit();

            }
        });

    }

    private void saveLook(){

        saveLook_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadTask != null && uploadTask.isInProgress()){

                    Toast.makeText(AddLookItems.this, "Upload in progress", Toast.LENGTH_SHORT).show();

                } else if (imageUri != null){

                    uploadImageToFirebase(imageName, imageUri);
                    uploadTask.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            task.addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {

                                    Intent i = new Intent(AddLookItems.this, Closet.class);
                                    i.putExtra("look", true);
                                    startActivity(i);
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG, "Upload task has failed");

                                };
                            });


                        }
                    });

                }

            }

        });

    }

    private void addedItemsRecycler(){

        added_items_recycler.setHasFixedSize(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        added_items_recycler.setLayoutManager(gridLayoutManager);
        adapter = new AddLookItemsAdapter(this, lookItems);
        added_items_recycler.setAdapter(adapter);

    }

    private void uploadImageToFirebase(String name, Uri contentUri){

        if(imageUri != null){

            final ProgressDialog progressDialog = new ProgressDialog(AddLookItems.this);
            progressDialog.setTitle("Uploading photo...");
            progressDialog.setOwnerActivity(AddLookItems.this);
            progressDialog.show();

            imageReference = storageReference.child("images/" + userId + "/" + "looks/" + name);
            uploadTask = imageReference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            strUri = uri.toString().trim();

//                            for (int i = 0; i < lookItems.size(); i++){
//
//                                String name = "item" + i;
//                                Uri imageUrl = Uri.parse(lookItems.get(i).getImageUrl());
//                                storageReference.child("images/" + userId + "/" + "looks/" + title + "/" + "look_items/" + name).putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                                        Log.d(TAG, "Items were uploaded!");
//
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//
//                                        Log.d(TAG, "Items weren't uploaded");
//
//                                    }
//                                });
//
//                            }

                            Look look = new Look(title, description, imageName, strUri);

                            String looksId = firebaseFirestore.collection("users/").document(user_email + "/").collection("looks/").document().getId();
                            firebaseFirestore.collection("users/").document( user_email + "/").collection("looks/").document(looksId).set(look).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    for (int i = 0; i < lookItems.size(); i++){

                                        String imageUrl = lookItems.get(i).getClotheImageUrl();
                                        String name = lookItems.get(i).getClotheName();
                                        String imageName = lookItems.get(i).getClotheImageName();
                                        String description = lookItems.get(i).getClotheDescription();
                                        String type = lookItems.get(i).getClotheType();
                                        String color = lookItems.get(i).getClotheColor();
                                        String size = lookItems.get(i).getClotheSize();
                                        String season = lookItems.get(i).getClotheSeason();

                                        LookItems lookItems = new LookItems(name, imageUrl, imageName, description, color, size, type, season);

                                        String itemsId = firebaseFirestore.collection("users/").document( user_email + "/").collection("looks/").document(looksId).collection("look_items").document().getId();
                                        firebaseFirestore.collection("users/").document( user_email + "/").collection("looks/").document(looksId).collection("look_items").document(itemsId).set(lookItems).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Log.d(TAG, "Look items has been added!");

                                                Activity activity = progressDialog.getOwnerActivity();
                                                if( activity!= null && !activity.isFinishing()){

                                                    progressDialog.dismiss();

                                                }

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Log.d(TAG, "Look items has not been added!");


                                            }
                                        });

                                    }

                                    Log.d(TAG, "Look metadata has been added");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG, "Look metadata hasn't been added");


                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "Look image url hasn't been get");


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "Upload image look is not successful");

                }
            });



        }

    }
}