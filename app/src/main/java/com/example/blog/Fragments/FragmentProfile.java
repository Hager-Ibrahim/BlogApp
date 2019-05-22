package com.example.blog.Fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blog.Adapters.ProfilePostAdapter;
import com.example.blog.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentProfile extends Fragment {
    View view ;
    CircleImageView userImg ;
    TextView userName ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile,container, false);
        userImg = view.findViewById(R.id.profile_user_img);
        userName = view.findViewById(R.id.profile_user_name);


        addProfileInfo();

       return view;
    }

    public void addProfileInfo(){



        if( getArguments()== null){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Glide.with(this).load(user.getPhotoUrl()).into(userImg);
            userName.setText(user.getDisplayName());
            query(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
        if(getArguments() != null){
            userName.setText((String)getArguments().get("userName"));
            Glide.with(this).load((String)getArguments().get("userImg")).into(userImg);
            query((String)getArguments().get("userName"));
        }

    }


    public void query( String name){
        final ArrayList<String> firebaseArray = new ArrayList<>();
        final GridView androidGridView=view.findViewById(R.id.gridView);
        FirebaseFirestore.getInstance().collection("posts")
                .whereEqualTo("name", name)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for( DocumentSnapshot documentSnapshot:list){
                    String s = (String) documentSnapshot.get("imageUri");
                    firebaseArray.add(s);

                }
                ProfilePostAdapter adapterViewAndroid = new ProfilePostAdapter(
                        getContext(), firebaseArray);
                androidGridView.setAdapter(adapterViewAndroid);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();


    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }







}
