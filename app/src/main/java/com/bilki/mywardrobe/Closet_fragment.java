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
import android.widget.TextView;

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

public class Closet_fragment extends Fragment implements ClosetAdapter.OnItemListener {

    private Context context;
    private View view;
    private RecyclerView topsRecycler, bottomsRecycler, shoesRecycler, accessoriesRecycler;
    private ClosetAdapter adapter1, adapter2, adapter3;
    private TextView topsShowAll, bottomsShowAll, shoesShowAll;
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
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");

        context = container.getContext();
        topsRecycler = (RecyclerView) view.findViewById(R.id.tops_recycler);
        bottomsRecycler = (RecyclerView) view.findViewById(R.id.bottoms_recycler);
        shoesRecycler = (RecyclerView) view.findViewById(R.id.shoes_recycler);

        topsShowAll = (TextView) view.findViewById(R.id.tops_show_all);
        bottomsShowAll = (TextView) view.findViewById(R.id.bottoms_show_all);
        shoesShowAll = (TextView) view.findViewById(R.id.shoes_show_all);

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

        topsShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Closet_items.class);
                intent.putExtra("Shirt", "Shirt");
                intent.putExtra("T-shirt", "T-shirt");
                intent.putExtra("Sweater", "Sweater");
                intent.putExtra("Jacket", "Jacket");
                startActivity(intent);
                getActivity().finish();


            }
        });

        bottomsShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Closet_items.class);
                intent.putExtra("Pants", "Pants");
                intent.putExtra("Jeans", "Jeans");
                intent.putExtra("Shorts", "Shorts");
                startActivity(intent);
                getActivity().finish();

            }
        });

        shoesShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Closet_items.class);
                intent.putExtra("Sneakers", "Sneakers");
                intent.putExtra("Dressed shoes", "Dressed shoes");
                intent.putExtra("Boots", "Boots");
                startActivity(intent);
                getActivity().finish();

            }
        });



        topsRecycler();
        bottomsRecycler();
        shoesRecycler();
        return view;
    }

    private void topsRecycler() {

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

        closetLocations.add(new FeaturedHelperClass(R.drawable.shirt, "Shirt"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.t_shirt, "T-shirt"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.sweater, "Sweater"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.hoodie_, "Hoodie"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.outwear, "Jacket"));

        adapter1 = new ClosetAdapter(getActivity(), closetLocations, this::onItemClick, "topsAdapter");
        topsRecycler.setAdapter(adapter1);


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

    private void bottomsRecycler() {

        bottomsRecycler.setHasFixedSize(true);
        bottomsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeaturedHelperClass> closetLocations = new ArrayList<>();

        closetLocations.add(new FeaturedHelperClass(R.drawable.pant, "Pants"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.pants, "Jeans"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.shorts, "Shorts"));

        adapter2 = new ClosetAdapter(getActivity(), closetLocations, this::onItemClick, "bottomsAdapter");
        bottomsRecycler.setAdapter(adapter2);

    }

    private void shoesRecycler() {

        shoesRecycler.setHasFixedSize(true);
        shoesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeaturedHelperClass> closetLocations = new ArrayList<>();

        closetLocations.add(new FeaturedHelperClass(R.drawable.shoe, "Sneakers"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.dressed_shoes, "Dressed shoes"));
        closetLocations.add(new FeaturedHelperClass(R.drawable.boots_, "Boots"));

        adapter3 = new ClosetAdapter(getActivity(), closetLocations, this::onItemClick, "shoesAdapter");
        shoesRecycler.setAdapter(adapter3);

    }

//    @Override
//    public void onItemClick(int position) {
//
//        Log.d(TAG, "onItemClick: Clicked: " + position);
//
//        Intent intent = new Intent(getActivity(), Closet_items.class);
//
//
//        switch (position){
//
//            case 0:
//                intent.putExtra("closet", "Shirt");
//                break;
//            case 1:
//                intent.putExtra("closet", "T-shirt");
//                break;
//            case 2:
//                intent.putExtra("closet", "Sweater");
//                break;
//            case 3:
//                intent.putExtra("closet", "Jacket");
//                break;
//
//        }
//
//        startActivity(intent);
//
//    }


    @Override
    public void onItemClick(int position, String tag) {

        switch (tag){

            case "topsAdapter":
                Intent intent1 = new Intent(getActivity(), Closet_items.class);

                switch (position){

                    case 0:
                        intent1.putExtra("closet", "Shirt");
                        break;
                    case 1:
                        intent1.putExtra("closet", "T-shirt");
                        break;
                    case 2:
                        intent1.putExtra("closet", "Sweater");
                        break;
                    case 3:
                        intent1.putExtra("closet", "Hoodie");
                        break;
                    case 4:
                        intent1.putExtra("closet", "Jacket");
                        break;

                }

                startActivity(intent1);

                break;
            case "bottomsAdapter":

                Intent intent2 = new Intent(getActivity(), Closet_items.class);


                switch (position){

                    case 0:
                        intent2.putExtra("closet", "Pants");
                        break;
                    case 1:
                        intent2.putExtra("closet", "Jeans");
                        break;
                    case 2:
                        intent2.putExtra("closet", "Shorts");
                        break;

                }

                startActivity(intent2);

                break;
            case "shoesAdapter":

                Intent intent3 = new Intent(getActivity(), Closet_items.class);


                switch (position){

                    case 0:
                        intent3.putExtra("closet", "Sneakers");
                        break;
                    case 1:
                        intent3.putExtra("closet", "Dressed shoes");
                        break;
                    case 2:
                        intent3.putExtra("closet", "Boots");
                        break;

                }

                startActivity(intent3);

                break;

        }

    }
}