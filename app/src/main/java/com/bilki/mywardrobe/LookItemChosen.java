package com.bilki.mywardrobe;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class LookItemChosen extends DialogFragment {

    private String lookId, itemLookId;
    private ImageView itemImage, itemColor;
    private TextView itemType, itemSize, itemSeason, itemName, itemDescription;
    private boolean edit;
    private View view;
    private Button deleteItemBttn;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private CollectionReference collectionReference, collectionReferenceImage;
    private DocumentSnapshot document;
    private DocumentReference documentReference, documentReferenceImage;
    static private final String TAG = "bilki: LookItemChosen: ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_look_item_chosen, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users/");
        documentReference = collectionReference.document(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/");
        context = getActivity();

        itemImage = (ImageView) view.findViewById(R.id.look_item_chosen_img);
        itemColor = (ImageView) view.findViewById(R.id.img_color_look) ;
        itemSize = (TextView) view.findViewById(R.id.img_size_look);
        itemType = (TextView) view.findViewById(R.id.img_type_look);
        itemSeason = (TextView) view.findViewById(R.id.img_season_look);
        itemName = (TextView) view.findViewById(R.id.look_item_chosen_name);
        itemDescription = (TextView) view.findViewById(R.id.look_item_chosen_description);
        deleteItemBttn = (Button) view.findViewById(R.id.look_item_chosen_delete);


        SelectedLooks selectedLooks = (SelectedLooks) getActivity();
        lookId = selectedLooks.getLookId();
        itemLookId = selectedLooks.getItemId();
        edit = selectedLooks.getEditBool();

        if(edit){

            deleteItemBttn.setVisibility(View.VISIBLE);

            deleteItemBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    documentReference.collection("looks/").document(lookId).collection("look_items/").document(itemLookId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Log.d(TAG, "Look item has been deleted");
                            getDialog().dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "Look item hasn't been deleted");

                        }
                    });

                }
            });

        }



        setData();

        return view;
    }

    private void setData(){

        documentReference.collection("looks/").document(lookId).collection("look_items/").document(itemLookId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.d(TAG, "Data set failed: " + error);
                    return;

                }

                documentReference.collection("looks/").document(lookId).collection("look_items/").document(itemLookId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){

                            if (value.exists()){

                                LookItems lookItems = value.toObject(LookItems.class);
                                String _itemName = lookItems.getClotheName();
                                String _itemImageUrl = lookItems.getClotheImageUrl();
                                Uri _itemImageUri = Uri.parse(_itemImageUrl);
                                String _itemSize = lookItems.getClotheSize();
                                String _itemColor = lookItems.getClotheColor();
                                String _itemType = lookItems.getClotheType();
                                String _itemSeason = lookItems.getClotheSeason();
                                String _itemDesc = lookItems.getClotheDescription();
                                String _itemImageName = lookItems.getClotheImageName();

                                Picasso.get().load(_itemImageUri).fit().centerInside().into(itemImage);
                                itemName.setText(_itemName);

                                if (_itemDesc == "" || _itemDesc == null || _itemDesc.isEmpty()) {

                                    itemDescription.setVisibility(View.GONE);
                                    Log.d(TAG, "Description is empty!");

                                } else {

                                    itemDescription.setVisibility(View.VISIBLE);
                                    itemDescription.setText(_itemDesc);
                                    Log.d(TAG, "Description is not empty!");

                                }

                                switch (_itemColor) {

                                    case "Black":
                                        itemColor.setImageDrawable(getResources().getDrawable(R.drawable.color_black_));
                                        break;

                                    case "White":
                                        itemColor.setImageDrawable(getResources().getDrawable(R.drawable.color_white_));
                                        break;
                                }

                                itemSize.setText(_itemSize);
                                itemType.setText(_itemType);
                                itemSeason.setText(_itemSeason);

                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "Look's clothe data get unsuccessful: " + e.getMessage());

                    }
                });

            }
        });

    }

}