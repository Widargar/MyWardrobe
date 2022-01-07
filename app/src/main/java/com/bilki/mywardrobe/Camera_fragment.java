package com.bilki.mywardrobe;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Camera_fragment extends Fragment {

    private static final String RANDOM_GOOD_DEED_KEY = "key";
    private Context context;
    private View view;
    private ImageView takenPicture;
    private Button camera_bttn, gallery_bttn, add_bttn;
    private RadioGroup colorGroup, sizeGroup, typeGroup, seasonGroup;
    private RadioButton color_radio, size_radio, type_radio, season_radio;
    private TextInputLayout clothe_title, clothe_description;
    private TextInputEditText edit_clothe_title, edit_clothe_description;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int CAMERA_PERM_CODE = 101;
    private static final int SELECT_PICTURE_CODE = 101;
    public String currentPhotoPath, user_email, url, userId, title, description,
            image_name, fileName, strUri, color, size, type, season;
    private File f, ff;
    private ProgressBar progressBar;
    private Uri imageUri, downloadUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference, imageReference, spaceReference;
    private StorageTask uploadTask;
    private DocumentSnapshot document;
    private DocumentReference docRef;
    private UploadTask.TaskSnapshot taskSnapshot;
    private RecyclerView colorsRecycler, sizesRecycler, typesRecycler, seasonsRecycler;
    private RecyclerView.Adapter adapter1, adapter2, adapter3, adapter4;
    private final static String TAG = "bilki: Camera ";
    private int requestCode;
    private int resultCode;
    private Dialog choose_source;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_camera_fragment, container, false);


        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        add_bttn = (Button) view.findViewById(R.id.add_bttn);
        clothe_title = (TextInputLayout) view.findViewById(R.id.clothe_title);
        clothe_description = (TextInputLayout) view.findViewById(R.id.clothe_description);


        edit_clothe_title = (TextInputEditText) view.findViewById(R.id.edit_clothe_title);
        edit_clothe_description = (TextInputEditText) view.findViewById(R.id.edit_clothe_description);

        colorGroup = (RadioGroup) view.findViewById(R.id.color_radio_group);
        sizeGroup = (RadioGroup) view.findViewById(R.id.size_radio_group);
        typeGroup = (RadioGroup) view.findViewById(R.id.type_radio_group);
        seasonGroup = (RadioGroup) view.findViewById(R.id.season_radio_group);

        takenPicture = (ImageView) view.findViewById(R.id.taken_picture);
        context = container.getContext();

        colorsRecycler = (RecyclerView) view.findViewById(R.id.colors_recycler);
        sizesRecycler = (RecyclerView) view.findViewById(R.id.size_recycler);
        typesRecycler = (RecyclerView) view.findViewById(R.id.type_recycler);
        seasonsRecycler = (RecyclerView) view.findViewById(R.id.season_recycler);

        userId = mAuth.getUid();


        takenPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseSource();

            }
        });


        getUserEmail();
        addClothe();


        //askCameraPermissions();

        colorsRecycler();
        sizesRecycler();
        typesRecycler();
        seasonsRecycler();

        return view;
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

    public void chooseSource() {

        choose_source = new Dialog(getActivity());
        choose_source.requestWindowFeature(Window.FEATURE_NO_TITLE);
        choose_source.setContentView(R.layout.dialog_choose_source);
        choose_source.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        choose_source.show();

        camera_bttn = (Button) choose_source.findViewById(R.id.dialog_camera_button);
        gallery_bttn = (Button) choose_source.findViewById(R.id.dialog_gallery_button);

        camera_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCameraHardware(getActivity());
                takePicture();
                choose_source.dismiss();

            }
        });

        gallery_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeImageFromGallery();
                //Toast.makeText(getActivity(), "Gallery open", Toast.LENGTH_SHORT).show();
                choose_source.dismiss();

            }
        });


    }

    public void takeImageFromGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, SELECT_PICTURE_CODE);

    }

    //Camera --start--

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getActivity(), "There is camera on your phone", Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(getActivity(), "There is no camera on your phone", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /*private void askCameraPermissions(){

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);

        }else{

            takePicture();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                takePicture();

            }else{

                Toast.makeText(getActivity(), "Camera permission is required to take picture", Toast.LENGTH_SHORT);

            }

        }
    }*/

    public void takePicture() {


        Log.d(TAG, "Take picture activity");
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {

            File photoFile = null;

            try {

                photoFile = createPhotoFile();

            } catch (IOException e) {
                Log.d(TAG, "Photo file cannot be created");
                e.printStackTrace();
            }

            if (photoFile != null) {

                Log.d(TAG, "File != null");
                Uri photoUri = FileProvider.getUriForFile(getActivity(), "com.bilki.mywardrobe.fileprovider", photoFile);
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
//        f = new File(currentPhotoPath);
//        contentUri = Uri.fromFile(f);
        //uploadImageToFirebase(f.getName(), contentUri);

        return photoFile;

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
            takenPicture.setImageURI(imageUri);
            Log.d(TAG, "Absolute Url of the photo is " + Uri.fromFile(f));

            galleryAddPic();


        }/*else{

            Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();

        }*/

        if (requestCode == SELECT_PICTURE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
//            String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
//            String fileName = "myWardrobe_" + timeStamp + "." + getFileExt(imageUri);
            takenPicture.setImageURI(imageUri);
            Log.d(TAG, "Gallery url of the photo is " + fileName);
            //uploadImageToFirebase(fileName, contentUri);


        }/*else{

            Toast.makeText(getActivity(), "Picture wasn't chosen!", Toast.LENGTH_SHORT).show();

        }*/
    }

    private String getFileExt(Uri contentUri) {

        ContentResolver c = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));

    }

    //Add photo to gallery
    private void galleryAddPic() {

        Log.d(TAG, "Added to gallery");

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        f = new File(currentPhotoPath);
        imageUri = Uri.fromFile(f);
        mediaScanIntent.setData(imageUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }


    private void uploadImageToFirebase(String name, Uri contentUri) {

        if(imageUri != null){

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Adding...");
            progressDialog.show();

            imageReference = storageReference.child("images/" + userId + "/" + name);
            uploadTask = imageReference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {



//                            colorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                                @Override
//                                public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//                                    switch (checkedId){
//
//                                        case R.id.radio_bttn_black:
//                                            color;
//                                            break;
//                                        case R.id.radio_bttn_white:
//                                            color = "White";
//                                            break;
//
//                                    }
//
//                                }
//                            });

                            strUri = uri.toString();


                            // Color get
                            int colorId = colorGroup.getCheckedRadioButtonId();
                            color_radio = (RadioButton) view.findViewById(colorId);
                            color = color_radio.getTag().toString();

                            //Size get
                            int sizeId = sizeGroup.getCheckedRadioButtonId();
                            size_radio = (RadioButton) view.findViewById(sizeId);
                            size = size_radio.getTag().toString();

                            //Type get
                            int typeId = typeGroup.getCheckedRadioButtonId();
                            type_radio = (RadioButton) view.findViewById(typeId);
                            type = type_radio.getTag().toString();

                            //Season get
                            int seasonId = seasonGroup.getCheckedRadioButtonId();
                            season_radio = (RadioButton) view.findViewById(seasonId);
                            season = season_radio.getTag().toString();



                            title = clothe_title.getEditText().getText().toString().trim();
                            description = clothe_description.getEditText().getText().toString().trim();

                            Upload upload = new Upload(title, strUri, fileName, description, color, size, type, season);

                            Log.d(TAG, "Color: " + color);
                            Log.d(TAG, "FileName: " + fileName);

//                            Log.d(TAG, "URL: " + taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String uploadId = firebaseFirestore.collection("users/").document(user_email + "/").collection("images/").document().getId();
                            firebaseFirestore.collection("users/").document( user_email + "/").collection("images/").document(uploadId).set(upload);


                            Log.d(TAG, "Image Url " + uri.toString());
                            Log.d(TAG, "Uploaded");
                            progressDialog.dismiss();
//                            progressBar.setProgress(0);


                            Log.d(TAG, "strUri: " + strUri);
                            Log.d(TAG, "NAME: " + name);



                        }
                    });



//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            progressBar.setProgress(0);
//
//                        }
//                    }, 5000);

                    Toast.makeText(getActivity(), "Image is uploaded", Toast.LENGTH_SHORT).show();




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "Upload failed: " + e.getMessage());
                    Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_SHORT).show();

                }
            });
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//
//                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
//                    progressBar.setProgress((int) progress);
//
//                }
//            });

        }else{

            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();

        }



    }

    //Camera --end--

    private boolean titleValidation() {

        title = clothe_title.getEditText().getText().toString().trim();

        if (title.isEmpty()) {

            clothe_title.setError("Enter a title!");
            return false;

        } else {

            clothe_title.setError(null);
            clothe_title.setErrorEnabled(false);
            return true;

        }

    }



    private void addClothe() {


        add_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!titleValidation()) {

                    return;

                } else if (uploadTask != null && uploadTask.isInProgress()) {

                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();

                } else if(imageUri != null){

                    title = clothe_title.getEditText().getText().toString().trim();
                    description = clothe_description.getEditText().getText().toString().trim();
                    Log.d(TAG, "On add button click!");

                    imageUri = Uri.fromFile(f);

                    uploadImageToFirebase(fileName, imageUri);

//                    //StorageReference urlImage = storageReference.child("images/" + userId + "/" + fileName);
//                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//
//                            //downloadUri = uri;
//                            //strUri = downloadUri.toString();
//                            //strUri = taskSnapshot.getMetadata().getName();
//
//                            Log.d(TAG, "Download URL: " + uri);
//
//                        }
//                    });


//                ff = new File(currentPhotoPath);
//                contentUri = Uri.fromFile(ff);
//                uploadImageToFirebase(ff.getName(), contentUri);

                    Log.d(TAG, "IMAGE URL:" + strUri);



//                    DocumentReference documentReference = firebaseFirestore.collection("images").document();
////                    String imageId = firebaseFirestore.collection("images").document().getId();
//
//
//                    Map<String, Object> image = new HashMap<>();
//
//                    image.put("phone_image_url", imageUri.toString().trim());
//                    image.put("image_name", fileName);
//                    image.put("image_url", strUri);
//                    image.put("title", title);
//                    image.put("description", description);
//
//                    Log.d(TAG, "Adding image data");
//                    Log.d(TAG, "title: " + title);
//
//
//                    documentReference.set(image).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Log.d(TAG, "Data for image has been added");
//                        }
//                    })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                    Log.d(TAG, "Data for image hasn't been added. Smth gone wrong");
//
//                                }
//                            })
//                            .addOnCanceledListener(new OnCanceledListener() {
//                                @Override
//                                public void onCanceled() {
//
//                                    Log.d(TAG, "Data adding for image has been canceled");
//
//                                }
//                            });


                }
            }

        });

    }

    //Colors recycler
    private void colorsRecycler() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5);
        colorsRecycler.setLayoutManager(gridLayoutManager);
        colorsRecycler.setHasFixedSize(false);

        ArrayList<ColorsHelperClass> colorsLocations = new ArrayList<>();

        colorsLocations.add(new ColorsHelperClass(R.drawable.color_black));
        colorsLocations.add(new ColorsHelperClass(R.drawable.round_background));


        adapter1 = new ColorsAdapter(colorsLocations);
        colorsRecycler.setAdapter(adapter1);

    }

    //Sizes recycler
    private void sizesRecycler() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 6);
        sizesRecycler.setLayoutManager(gridLayoutManager);
        sizesRecycler.setHasFixedSize(true);

        ArrayList<SizeHelperClass> sizeLocations = new ArrayList<>();

        sizeLocations.add(new SizeHelperClass("xs"));
        sizeLocations.add(new SizeHelperClass("s"));
        sizeLocations.add(new SizeHelperClass("m"));
        sizeLocations.add(new SizeHelperClass("l"));
        sizeLocations.add(new SizeHelperClass("xl"));
        sizeLocations.add(new SizeHelperClass("xxl"));


        adapter2 = new SizeAdapter(sizeLocations);
        sizesRecycler.setAdapter(adapter2);

    }

    //Types recycler
    private void typesRecycler() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        typesRecycler.setLayoutManager(gridLayoutManager);
        typesRecycler.setHasFixedSize(true);

        ArrayList<TypeHelperClass> typeLocations = new ArrayList<>();

        typeLocations.add(new TypeHelperClass("Shirt"));
        typeLocations.add(new TypeHelperClass("T-shirt"));
        typeLocations.add(new TypeHelperClass("Sweater"));
        typeLocations.add(new TypeHelperClass("Jacket"));
        typeLocations.add(new TypeHelperClass("Pants"));
        typeLocations.add(new TypeHelperClass("Jeans"));
        typeLocations.add(new TypeHelperClass("Shorts"));
        typeLocations.add(new TypeHelperClass("Sneakers"));
        typeLocations.add(new TypeHelperClass("Dressed shoes"));
        typeLocations.add(new TypeHelperClass("Boots"));


        adapter3 = new TypeAdapter(typeLocations);
        typesRecycler.setAdapter(adapter3);

    }

    //Seasons recycler
    private void seasonsRecycler() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        seasonsRecycler.setLayoutManager(gridLayoutManager);
        seasonsRecycler.setHasFixedSize(true);

        ArrayList<TypeHelperClass> typeLocations = new ArrayList<>();

        typeLocations.add(new TypeHelperClass("Winter"));
        typeLocations.add(new TypeHelperClass("Spring"));
        typeLocations.add(new TypeHelperClass("Summer"));
        typeLocations.add(new TypeHelperClass("Autumn"));

        adapter4 = new TypeAdapter(typeLocations);
        seasonsRecycler.setAdapter(adapter4);

    }


}