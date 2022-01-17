package com.bilki.mywardrobe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class Closet_fragment extends Fragment implements ClosetAdapter.ClosetViewHolder.OnItemListener {

    private Context context;
    private View view;
    private RecyclerView topsRecycler, bottomsRecycler, shoesRecycler, accessoriesRecycler;
    private ClosetAdapter adapter;
    private ImageView backCloset;
    private String _uri, imageUrl, type;
    private Bundle result;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private CollectionReference collectionReference, collectionReferenceImage;
    private DocumentSnapshot document;
    private DocumentReference documentReference, documentReferenceImage;
    ArrayList<FeaturedHelperClass> closetLocations = new ArrayList<>();
    private final static String TAG = "bilki: Closet ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_closet_fragment, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");

        context = container.getContext();
        topsRecycler = (RecyclerView) view.findViewById(R.id.tops_recycler);
        bottomsRecycler = (RecyclerView) view.findViewById(R.id.bottoms_recycler);
        shoesRecycler = (RecyclerView) view.findViewById(R.id.shoes_recycler);

        backCloset = (ImageView) view.findViewById(R.id.back_closet);

        backCloset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                getActivity().finish();

            }
        });

        topsRecycler();
//        bottomsRecycler();
//        shoesRecycler();
        return view;
    }

    private void topsRecycler(){

//        Query query = documentReference.collection("images/").whereIn("type", Arrays.asList("Sneakers", "Shirt")).orderBy("type", Query.Direction.DESCENDING).limit(1).;
//        FirestoreRecyclerOptions<Upload> options = new FirestoreRecyclerOptions.Builder<Upload>()
//                .setQuery(query, Upload.class)
//                .build();
//
//        adapter = new ClosetAdapter(context, options);
//        topsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        topsRecycler.setHasFixedSize(false);
//        topsRecycler.setAdapter(adapter);
//        Log.d(TAG, "topsRecycler: Adapter set");


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
//                        type = document.getString("type");
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

        topsRecycler.setHasFixedSize(true);
        topsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));



        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Shirt"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "T-shirt"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Sweater"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Jacket"));

        adapter = new ClosetAdapter(closetLocations, this);
        topsRecycler.setAdapter(adapter);




    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }

    private void bottomsRecycler(){

        bottomsRecycler.setHasFixedSize(true);
        bottomsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeaturedHelperClass> closetLocations = new ArrayList<>();

        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Pants"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Jeans"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Shorts"));

        adapter = new ClosetAdapter(closetLocations, this);
        bottomsRecycler.setAdapter(adapter);

    }

    private void shoesRecycler(){

        shoesRecycler.setHasFixedSize(true);
        shoesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeaturedHelperClass> closetLocations = new ArrayList<>();

        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Sneakers"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Dressed shoes"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Boots"));

        adapter = new ClosetAdapter(closetLocations, this);
        shoesRecycler.setAdapter(adapter);

    }

    @Override
    public void onItemClick(int position) {

        Log.d(TAG, "onItemClick: Clicked: " + position);

        Intent intent = new Intent(getActivity(), Closet_items.class);


        switch (position){

            case 0:
                intent.putExtra("closet", "Shirt");
                break;
            case 1:
                intent.putExtra("closet", "T-shirt");
                break;
            case 2:
                intent.putExtra("closet", "Sweater");
                break;
            case 3:
                intent.putExtra("closet", "Jacket");
                break;

        }

        startActivity(intent);

    }

}