package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SelectedLooks extends AppCompatActivity {

    private String userId, _lookId, fileName, currentPhotoPath, strUri, urll, lookName, title, description, lookItemId;
    private File f;
    boolean edit = false;
    private Uri imageUri;
    private Dialog choose_source;
    private LookItemChosen dialog;
    private LookItemAdd _dialog;
    private ImageView backSelectedLooks, lookImage, lookImageEdit;
    private TextView lookTitle, lookDescription;
    private RecyclerView lookItemsRecycler;
    private Button lookEditBttn, lookSaveBttn, lookDeleteBttn, camera_bttn, gallery_bttn, lookItemAddBttn;
    private TextInputLayout lookTitleInput, lookDescriptionInput;
    private TextInputEditText lookTitleEdit, lookDescriptionEdit;
    private LinearLayout lookTagsLayout, lookTagsEditLayout;
    private LookItemsAdapter adapter;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference, imageReference, existedImageReference;
    private StorageTask uploadTask;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int SELECT_PICTURE_CODE = 101;
    private final static String TAG = "bilki: SelectedLooks ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_looks);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");
        userId = mAuth.getUid();

        Intent intent = getIntent();

        _lookId = intent.getStringExtra("lookId");


        backSelectedLooks = (ImageView) findViewById(R.id.back_selected_look);
        lookImage = (ImageView) findViewById(R.id.img_look);
        lookImageEdit = (ImageView) findViewById(R.id.img_look_edit);
        lookTitle = (TextView) findViewById(R.id.look_title);
        lookDescription = (TextView) findViewById(R.id.look_description);
        lookItemsRecycler = (RecyclerView) findViewById(R.id.added_look_items_recycler);
        lookTagsLayout = (LinearLayout) findViewById(R.id.look_tags_layout);
        lookTagsEditLayout = (LinearLayout) findViewById(R.id.look_tags_edit_layout);
        lookTitleInput = (TextInputLayout) findViewById(R.id.look_title_edit);
        lookDescriptionInput = (TextInputLayout) findViewById(R.id.look_description_edit);
        lookTitleEdit = (TextInputEditText) findViewById(R.id.edit_look_title);
        lookDescriptionEdit = (TextInputEditText) findViewById(R.id.edit_look_description);
        lookEditBttn = (Button) findViewById(R.id.look_edit_bttn);
        lookSaveBttn = (Button) findViewById(R.id.look_save_bttn);
        lookDeleteBttn = (Button) findViewById(R.id.look_delete_bttn);
        lookItemAddBttn = (Button) findViewById(R.id.look_item_add_bttn);

        backSelectedLooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SelectedLooks.this, Closet.class);
                i.putExtra("look_select", true);
                startActivity(i);
                overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                finish();

            }
        });

        lookImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseSource();

            }
        });

        lookEditBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lookTagsLayout.setVisibility(View.GONE);
                lookImage.setVisibility(View.GONE);
                lookImageEdit.setVisibility(View.VISIBLE);
                lookTagsEditLayout.setVisibility(View.VISIBLE);
                lookEditBttn.setVisibility(View.GONE);
                lookSaveBttn.setVisibility(View.VISIBLE);
                lookDeleteBttn.setVisibility(View.VISIBLE);

                edit = true;

                setData();
            }
        });

        lookSaveBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lookTagsLayout.setVisibility(View.VISIBLE);
                lookImage.setVisibility(View.VISIBLE);
                lookImageEdit.setVisibility(View.GONE);
                lookTagsEditLayout.setVisibility(View.GONE);
                lookEditBttn.setVisibility(View.VISIBLE);
                lookSaveBttn.setVisibility(View.GONE);
                lookDeleteBttn.setVisibility(View.GONE);

                edit = false;

                if (uploadTask != null && uploadTask.isInProgress()) {

                    Toast.makeText(SelectedLooks.this, "Upload in progress", Toast.LENGTH_SHORT).show();

                } else if (imageUri != null) {

                    uploadImageToFirebase(fileName, imageUri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {

                            editLook();
                            setData();
                            imageUri = null;
                            UIUtil.hideKeyboard(SelectedLooks.this);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "upload task wasn't successful: " + e.getMessage());

                        }
                    });

                } else if (imageUri == null) {

                    editLook();
                    setData();
                    UIUtil.hideKeyboard(SelectedLooks.this);

                }

            }
        });

        lookItemAddBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _dialog = new LookItemAdd();
                _dialog.show(getFragmentManager(), "dialog");

            }
        });

        lookDeleteBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                documentReference.collection("looks").document(_lookId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Look look = documentSnapshot.toObject(Look.class);
                        String imageUrl = look.getLookImageUrl();
                        deletePreviousImage(imageUrl);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "Look data get for image delete unsuccessful: " + e.getMessage());

                    }
                });

                documentReference.collection("looks").document(_lookId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Intent i = new Intent(SelectedLooks.this, Closet.class);
                        i.putExtra("look_select", true);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "Look delete wasn't successful: " + e.getMessage());

                    }
                });

            }
        });

        setData();
        lookClothesRecycler();

    }



    private void setData(){

        documentReference.collection("looks").document(_lookId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.d(TAG, "Data set failed: " + error);
                    return;

                }

                documentReference.collection("looks").document(_lookId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            if (value.exists()) {

                                Look look = value.toObject(Look.class);
                                String imageUrl = look.getLookImageUrl();
                                Uri lookImgUri = Uri.parse(imageUrl);
                                String lookName = look.getLookName();
                                String lookDesc = look.getLookDescription();

                                if(lookTagsLayout.getVisibility() == View.VISIBLE && lookImage.getVisibility() == View.VISIBLE){

                                    Picasso.get().load(lookImgUri).fit().centerInside().into(lookImage);
                                    lookTitle.setText(lookName);

                                    if(lookDesc == "" || lookDesc == null || lookDesc.isEmpty()){

                                        lookDescription.setVisibility(View.GONE);

                                    } else {

                                        lookDescription.setVisibility(View.VISIBLE);
                                        lookDescription.setText(lookDesc);

                                    }

                                } else {

                                    Picasso.get().load(lookImgUri).fit().centerInside().into(lookImageEdit);
                                    lookTitleEdit.setText(lookName);

                                    if (lookDesc == "" || lookDesc == null || lookDesc.isEmpty()) {

                                        Log.d(TAG, "Description is empty!");

                                    } else {

                                        lookDescriptionEdit.setText(lookDesc);
                                        Log.d(TAG, "Description is not empty!");

                                    }

                                }

                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "Data set failed: " + e.getMessage());

                    }
                });

            }
        });

    }

    private void lookClothesRecycler(){

        Query query = documentReference.collection("looks/").document(_lookId).collection("look_items/").orderBy("clotheName", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<LookItems> options = new FirestoreRecyclerOptions.Builder<LookItems>()
                .setQuery(query, LookItems.class)
                .build();
        adapter = new LookItemsAdapter(this, options);
        lookItemsRecycler.setHasFixedSize(false);
        lookItemsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lookItemsRecycler.setAdapter(adapter);

        adapter.setOnClotheClickListener(new LookItemsAdapter.OnClotheClickListener() {
            @Override
            public void onClotheClick(DocumentSnapshot documentSnapshot, int position) {

                LookItems lookItems = documentSnapshot.toObject(LookItems.class);
                lookItemId = documentSnapshot.getId();

                dialog = new LookItemChosen();
                dialog.show(getFragmentManager(), "dialog");

            }
        });

    }

    public String getLookId(){

        return _lookId;
    }

    public String getItemId(){

        return lookItemId;

    }

    public boolean getEditBool(){

        return edit;

    }


    private void chooseSource() {

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

                checkCameraHardware(SelectedLooks.this);
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
            lookImageEdit.setImageURI(imageUri);
            Log.d(TAG, "Absolute Url of the photo is " + Uri.fromFile(f));

            galleryAddPic();


        }

        if (requestCode == SELECT_PICTURE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            lookImageEdit.setImageURI(imageUri);
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

    private void uploadImageToFirebase(String name, Uri contentUri){

        if (imageUri != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading photo...");
            progressDialog.show();

            documentReference.collection("looks/").document(_lookId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    Look look = documentSnapshot.toObject(Look.class);
                    String lookImgUrl = look.getLookImageUrl();
                    String lookImgName = look.getLookImageName();
                    lookName = look.getLookName();

                    if (lookImgUrl != null) {

                        Uri imgUrl = Uri.parse(lookImgUrl);
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

            imageReference = storageReference.child("images/" + userId + "/" + "looks/" + name);
            uploadTask = imageReference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

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
                    Toast.makeText(SelectedLooks.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            Toast.makeText(SelectedLooks.this, "No file selected", Toast.LENGTH_SHORT).show();

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

    private void editLook(){

        documentReference.collection("looks/").document(_lookId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Look look = documentSnapshot.toObject(Look.class);
                String lookImageName = look.getLookImageName();

                if(fileName == null){

                    imageReference = storageReference.child("images/" + userId + "/" + "looks/" + lookImageName);
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Look lookk = documentSnapshot.toObject(Look.class);
                            Uri lookImgUri = Uri.parse(lookk.getLookImageUrl());
                            strUri = lookImgUri.toString();
                            existedImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);
                            urll = uri.toString().trim();

                            if (strUri == urll) {

                                Map<String, Object> look = new HashMap<>();
                                look.put("lookName", lookTitleEdit.getText().toString().trim());
                                look.put("lookDescription", lookDescriptionEdit.getText().toString().trim());
                                documentReference.collection("looks/").document(_lookId).update(look);

                            } else {

                                Map<String, Object> look = new HashMap<>();
                                look.put("lookName", lookTitleEdit.getText().toString().trim());
                                look.put("lookDescription", lookDescriptionEdit.getText().toString().trim());
                                look.put("lookImageUrl", urll);
                                look.put("lookImageName", lookImageName);
                                documentReference.collection("looks/").document(_lookId).update(look);

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "New url wasn't set: " + e.getMessage());

                        }
                    });

                } else {

                    imageReference = storageReference.child("images/" + userId + "/" + "looks/" + fileName);
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Look lookk = documentSnapshot.toObject(Look.class);
                            Uri lookImgUri = Uri.parse(lookk.getLookImageUrl());
                            strUri = lookImgUri.toString();
                            existedImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);
                            urll = uri.toString().trim();

                            if (strUri == urll) {

                                Map<String, Object> look = new HashMap<>();
                                look.put("lookName", lookTitleEdit.getText().toString().trim());
                                look.put("lookDescription", lookDescriptionEdit.getText().toString().trim());
                                documentReference.collection("looks/").document(_lookId).update(look);

                            } else {

                                Map<String, Object> look = new HashMap<>();
                                look.put("lookName", lookTitleEdit.getText().toString().trim());
                                look.put("lookDescription", lookDescriptionEdit.getText().toString().trim());
                                look.put("lookImageUrl", urll);
                                look.put("lookImageName", fileName);
                                documentReference.collection("looks/").document(_lookId).update(look);

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

                Log.d(TAG, "Getting data to edit look wasn't successful");

            }
        });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(SelectedLooks.this, Closet.class);
        i.putExtra("look_select", true);
        startActivity(i);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
        finish();

    }
}