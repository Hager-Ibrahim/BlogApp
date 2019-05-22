package com.example.blog.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.blog.Adapters.HomePostAdapter;
import com.example.blog.Models.Post;
import com.example.blog.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FragmentHome extends Fragment  {

    FirebaseFirestore db ;
    FloatingActionButton homeFloatingActionBar;
    Dialog addPostDialog ;
    View view ;
    ImageView postImg,createBtn ;
    EditText postTitleEditText, postDescEditText ;
    ProgressBar postProgress;
    String title, desc ;
    FirebaseAuth mAuth ;
    ProgressBar mProgressBar ;
    Uri pickedImgUri ;
    HomePostAdapter postAdapter ;
    Bitmap bitmap2;
    RecyclerView recyclerView ;
    byte[] byt ;
    static int PERMISSION_REQUEST_CODE = 1 ;
    static int OPEN_GALLERY_REQUEST_CODE = 2 ;
    private static final String DEFAULT_PATTERN = "%d%%";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mProgressBar = view.findViewById(R.id.home_progressbar);
        recyclerView = view.findViewById(R.id.recycler);
        homeFloatingActionBar = view.findViewById(R.id.home_floating_action_bar);



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && homeFloatingActionBar.isShown())
                    homeFloatingActionBar.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    homeFloatingActionBar.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        queryData();
        createDialog();
        showAddPostDialog();
        clickOnPostImg();
        clickAddPost();
        return view;

    }



    public void queryData(){

        CollectionReference ref = db.collection("posts");
        Query query = ref.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query,Post.class)
                        .build();
        postAdapter = new HomePostAdapter(options, getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postAdapter);

    }




    public void createDialog(){
        // Create an dialog to add a new post
        addPostDialog = new Dialog(getActivity());
        addPostDialog.setContentView(R.layout.add_post_dialog);
        addPostDialog.getWindow().setLayout(androidx.appcompat.widget.Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        addPostDialog.getWindow().getAttributes().gravity= Gravity.TOP ;
        addPostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showAddPostDialog(){
        homeFloatingActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show dialog
                addPostDialog.show();
            }
        });
    }

    public void clickOnPostImg(){
        postImg = addPostDialog.findViewById(R.id.pop_up_img);
        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22){
                    checkAndRequestPermission();
                }
                else {
                    openGallery();
                }
            }
        });

    }

    public void checkAndRequestPermission(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                new AlertDialog.Builder(getActivity())
                        .setTitle("Permission Needed")
                        .setMessage("Permission needed to be able to choose an image")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSION_REQUEST_CODE);
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        }
        else{
            openGallery();
        }
    }

    public void openGallery(){
        Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openGalleryIntent.setType("image/*");
        startActivityForResult(openGalleryIntent, OPEN_GALLERY_REQUEST_CODE);
    }


    public void clickAddPost(){

        postTitleEditText = addPostDialog.findViewById(R.id.pop_up_title);
        postDescEditText = addPostDialog.findViewById(R.id.pop_up_des);
        createBtn = addPostDialog.findViewById(R.id.add_btn);
        postProgress =addPostDialog.findViewById(R.id.pop_up_progress);


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postProgress.setVisibility(View.VISIBLE);
                createBtn.setVisibility(View.INVISIBLE);

                title = postTitleEditText.getText().toString().trim();
                desc = postDescEditText.getText().toString().trim();

                if(!title.isEmpty() && !desc.isEmpty() && pickedImgUri != null){
                    uploadPhoto();
                }
                if(title.isEmpty() || desc.isEmpty()){
                    showMessage("Please fill in all fields");
                    postProgress.setVisibility(View.INVISIBLE);
                    createBtn.setVisibility(View.VISIBLE);
                }
                if(pickedImgUri == null && !title.isEmpty() && !desc.isEmpty()){
                    showMessage("please add an image");
                    postProgress.setVisibility(View.INVISIBLE);
                    createBtn.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public void uploadPhoto(){

        StorageReference storageReference  = FirebaseStorage.getInstance().getReference();
        final StorageReference imgPath = storageReference.child("blog_images");

        imgPath.putBytes(byt).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        final String id = db.collection("posts").document().getId();
                        FirebaseFirestore.getInstance().collection("posts")
                                .document(id)
                                .set(new Post(title,
                                        desc,
                                        uri.toString(),
                                        Calendar.getInstance().getTime()
                                        ,id
                                        , mAuth.getCurrentUser().getDisplayName()
                                        , mAuth.getCurrentUser().getPhotoUrl().toString()
                                )).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addPostDialog.dismiss();
                                showMessage("done");
                                clearDialogData();

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Adding post failed");
                        postProgress.setVisibility(View.INVISIBLE);
                        createBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

    }


    public void clearDialogData(){
        postImg.setImageBitmap(null);
        postTitleEditText.getText().clear();
        postDescEditText.getText().clear();
        postProgress.setVisibility(View.INVISIBLE);
        createBtn.setVisibility(View.VISIBLE);
    }


    public void showMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == OPEN_GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            addPostDialog.findViewById(R.id.add_photo_word).setVisibility(View.INVISIBLE);
            addPostDialog.findViewById(R.id.add_img_icon).setVisibility(View.INVISIBLE);
            pickedImgUri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pickedImgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }


            ByteArrayOutputStream bytearrayoutputstream =  new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,bytearrayoutputstream);

            byt = bytearrayoutputstream.toByteArray();

            bitmap2 = BitmapFactory.decodeByteArray(byt,0,byt.length);

            postImg.setImageBitmap(bitmap2);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }
}
