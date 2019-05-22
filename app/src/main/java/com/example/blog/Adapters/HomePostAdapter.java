package com.example.blog.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.blog.Activities.PostDetailsActivity;
import com.example.blog.Fragments.FragmentProfile;
import com.example.blog.Models.Post;
import com.example.blog.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Nullable;


public class HomePostAdapter extends FirestoreRecyclerAdapter<Post, HomePostAdapter.PostHolder> {

    Context mContext ;
    public HomePostAdapter(@NonNull FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        mContext = context ;
    }

    @Override
    protected void onBindViewHolder(@NonNull final PostHolder holder, int i, @NonNull final Post post) {
        holder.postTitle.setText(post.getTitle());
        holder.userName.setText(post.getName());
        holder.postDate.setText(formatDate(post.getTimestamp()));



        Glide.with(mContext).load(post.getImageUri()).into(holder.postImg);
        Glide.with(mContext).load(post.getUserImg()).into(holder.postUser);

        addLike(post,holder.likeBtn);
        addLikeColor(post, holder.likeBtn);
        likeCount(post, holder.likeCount);
        CommentCount(post, holder.commentCount);

        holder.commentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCommentItems(post);

            }
        });

        holder.commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCommentItems(post);

            }
        });
        holder.postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCommentItems(post);
            }
        });
        holder.postUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("userImg", post.getUserImg());
                bundle.putString("userName", post.getName());

                FragmentProfile fragobj = new FragmentProfile();
                fragobj.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragobj);
                transaction.addToBackStack(null);

                transaction.commit();


            }

        });

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostHolder(v);
    }

    public class PostHolder extends RecyclerView.ViewHolder{
        TextView userName, postTitle, postDate, likeCount , commentCount ;
        ImageView postUser , postImg , likeBtn , commentIcon ;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.row_post_user_name);
            postTitle = itemView.findViewById(R.id.row_post_title);
            postDate = itemView.findViewById(R.id.row_post_date);
            postUser = itemView.findViewById(R.id.row_post_user);
            postImg = itemView.findViewById(R.id.row_post_image);
            likeBtn = itemView.findViewById(R.id.like_btn);
            likeCount = itemView.findViewById(R.id.like_count);
            commentIcon = itemView.findViewById(R.id.post_comment_icon);
            commentCount = itemView.findViewById(R.id.commentCount);
        }
    }

    public String formatDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        return dateFormat.format(date);
    }

    public String formatPostDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy");
        return format.format(date);
    }

    public void addLike(final Post blogPost, ImageView likeButton){
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference likeDocument = FirebaseFirestore.getInstance()
                        .collection("posts").document(blogPost.getId())
                        .collection("likes").document(FirebaseAuth.getInstance().getUid());

                likeDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("timestamp", FieldValue.serverTimestamp());
                            likeDocument.set(map);
                        }
                        else{
                            likeDocument.delete();
                        }
                    }
                });
            }


        });
    }

    public void addLikeColor(final Post blogPost, final ImageView likeButton){
        FirebaseFirestore.getInstance()
                .collection("posts").document(blogPost.getId())
                .collection("likes").document(FirebaseAuth.getInstance().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()){
                            likeButton.setImageResource(R.drawable.red_like_icon);
                        }
                        else{
                            likeButton.setImageResource(R.drawable.grey_like_icon);
                        }
                    }
                });
    }

    public void likeCount(final Post blogPost, final TextView likeCount){

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("posts").document(blogPost.getId())
                .collection("likes");


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    if(queryDocumentSnapshots.getDocuments().size() == 1){
                        likeCount.setText(queryDocumentSnapshots.getDocuments().size() + " like");

                    }
                    if(queryDocumentSnapshots.size() > 1)
                        likeCount.setText(queryDocumentSnapshots.getDocuments().size() + " likes");

                }
                if(queryDocumentSnapshots.isEmpty()){
                        likeCount.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void CommentCount(final Post blogPost, final TextView commentCount){

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("posts").document(blogPost.getId())
                .collection("comment");


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    if(queryDocumentSnapshots.getDocuments().size() == 1){
                        commentCount.setText(queryDocumentSnapshots.getDocuments().size() + " comment");

                    }
                    if(queryDocumentSnapshots.size() > 1)
                        commentCount.setText(queryDocumentSnapshots.getDocuments().size() + " Comments");

                }
                if(queryDocumentSnapshots.isEmpty()){
                    commentCount.setText(queryDocumentSnapshots.getDocuments().size() + " Comments");
                }
            }
        });
    }

    public void clickCommentItems(Post post){
        Intent openPostDetailActivity = new Intent(mContext, PostDetailsActivity.class);
        openPostDetailActivity.putExtra("title",post.getTitle() );
        openPostDetailActivity.putExtra("description", post.getDescription());
        openPostDetailActivity.putExtra("time", formatPostDate(post.getTimestamp()));
        openPostDetailActivity.putExtra("userImg",post.getUserImg());
        openPostDetailActivity.putExtra("postImg",post.getImageUri());
        openPostDetailActivity.putExtra("userName",post.getName());
        openPostDetailActivity.putExtra("userId",post.getId());
        mContext.startActivity(openPostDetailActivity);
    }


}
