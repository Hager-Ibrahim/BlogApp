package com.example.blog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.blog.Adapters.CommentAdapter;
import com.example.blog.Models.Comment;
import com.example.blog.Models.Post;
import com.example.blog.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailsActivity extends AppCompatActivity {

    CircleImageView userImg , currentUserImg  ;
    ImageView postImg ;
    TextView postTime, postTitle, postDesc ;
    FirebaseAuth mAuth ;
    CommentAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        addPostFields();
        addComment();
        queryComments();
    }

    public void queryComments(){

        CollectionReference collectionReference =FirebaseFirestore.getInstance().collection("posts")
                .document(getIntent().getExtras().getString("userId"))
                .collection("comment");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<Comment> list = queryDocumentSnapshots.toObjects(Comment.class);
                    recyclerView = findViewById(R.id.post_details_recycler);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    adapter = new CommentAdapter(getBaseContext(),list);
                    recyclerView.setAdapter(adapter);
                }
            }
        });

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<Comment> list = queryDocumentSnapshots.toObjects(Comment.class);
                recyclerView = findViewById(R.id.post_details_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                adapter = new CommentAdapter(getBaseContext(),list);
                recyclerView.setAdapter(adapter);
            }
        });



    }

    public void addComment(){
        Button addBtn = findViewById(R.id.detail_post_btn);
        final EditText commentEditText = findViewById(R.id.detail_post_comment);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentEditText.getText().toString();
                FirebaseFirestore.getInstance().collection("posts")
                        .document(getIntent().getExtras().getString("userId"))
                        .collection("comment").document()
                        .set(new Comment(comment,
                                Calendar.getInstance().getTime()
                                ,FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                                ,FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()
                        ))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                commentEditText.getText().clear();

                            }
                        });

            }
        });
    }

    public void addPostFields(){
        postImg = findViewById(R.id.detail_post_img);
        currentUserImg = findViewById(R.id.detail_post_current_user);
        userImg = findViewById(R.id.detail_post_user_img);
        postTitle = findViewById(R.id.detail_post_title);
        postDesc = findViewById(R.id.detail_post_desc);
        postTime = findViewById(R.id.detail_post_time);
        mAuth= FirebaseAuth.getInstance();

        String time = getIntent().getExtras().getString("time") +
                " | By "
                + getIntent().getExtras().getString("userName");
        postTitle.setText(getIntent().getExtras().getString("title"));
        postDesc.setText(getIntent().getExtras().getString("description"));
        postTime.setText(time);

        Glide.with(this).load(getIntent().getExtras().getString("postImg")).into(postImg);
        Glide.with(this).load(getIntent().getExtras().getString("userImg")).into(userImg);
        Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(currentUserImg);
    }
}
