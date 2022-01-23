package com.bilki.mywardrobe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Looks_fragment extends Fragment {

    private Context context;
    private View view;
    private RecyclerView looksRecycler;
    private Button addLookBttn;
    private LooksAdapter adapter;
    private ImageView backLooks;
    private ArrayList<Upload> uploads;
    private String user_email;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private CollectionReference collectionReference, collectionReferenceImages;
    private DocumentSnapshot document;
    private final static String TAG = "bilki: Looks ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_looks_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");

        context = container.getContext();
        looksRecycler = (RecyclerView) view.findViewById(R.id.looks_recycler);

        backLooks = (ImageView) view.findViewById(R.id.back_looks);
        addLookBttn = (Button) view.findViewById(R.id.add_looks_bttn);

        backLooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                getActivity().finish();

            }
        });

        getUserEmail();
        addLookBttn();
        looksRecycler();

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

    public void addLookBttn(){

        addLookBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), AddNewLook.class);
                startActivity(i);
                getActivity().finish();

            }
        });

    }



    private void looksRecycler() {

        Query query = documentReference.collection("looks/").orderBy("lookName", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Look> options = new FirestoreRecyclerOptions.Builder<Look>()
                .setQuery(query, Look.class)
                .build();
        adapter = new LooksAdapter(context, options);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        looksRecycler.setLayoutManager(gridLayoutManager);
        looksRecycler.setHasFixedSize(false);
        looksRecycler.setAdapter(adapter);
        Log.d(TAG, "looksRecycler: Adapter set");

    }

    private void loadrecyclerViewData() {


        String uploadId = firebaseFirestore.collection("images").document().getId();
        DocumentReference documentReference = firebaseFirestore.collection("images/").document(uploadId + "/");
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