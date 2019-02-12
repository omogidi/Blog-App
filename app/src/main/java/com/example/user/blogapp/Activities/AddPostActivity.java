package com.example.user.blogapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.user.blogapp.Model.Blog;
import com.example.user.blogapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity
{
    private ImageButton mPostImage;
    private EditText mPostTitle, mPostDesc;
    private Button mSubmitButton;

    private DatabaseReference mPostReference;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog progressDialog;
    private static final int GALLERY_CODE = 1;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        mPostReference = FirebaseDatabase.getInstance().getReference().child("MBlog");

        mPostImage = findViewById(R.id.add_imageButton);
        mPostTitle = findViewById(R.id.titleEdt);
        mPostDesc = findViewById(R.id.descriptionEdt);
        mSubmitButton = findViewById(R.id.submitPostBtn);

        mPostImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Posting to database
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);

        }
    }

    private void startPosting()
    {
        progressDialog.setMessage("Posting to blog");
        progressDialog.show();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUri != null) {
            //start Uploading...
            //mImageUri.getLastPathSegment == /image/myphoto.jpeg"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
            StorageReference filepath = mStorage.child(sdf.format(new Date()));

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost = mPostReference.push();

                    //New Way
                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("title", titleVal);
                    dataToSave.put("desc", descVal);
                    dataToSave.put("image", downloadUrl.toString());
                    dataToSave.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userid", mUser.getUid());

                    newPost.setValue(dataToSave);

                    //oldway

//                    newPost.child("title").setValue(titleVal);
//                    newPost.child("desc").setValue(descVal);
//                    newPost.child("image").setValue(downloadUrl.toString());
//                    newPost.child("timestamp").setValue(java.lang.System.currentTimeMillis());

                    progressDialog.dismiss();
                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));

                }
            });






//This is for only text and when you are not putting image or audio inside the database

//            Blog blog = new Blog("Title", "Description", "imageurl", "timestamp", "userid");
//            mPostReference.setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>()
//            {
//                @Override
//                public void onSuccess(Void aVoid)
//                {
//                    Toast.makeText(getApplicationContext(), "Item Added", Toast.LENGTH_LONG).show();
//                    progressDialog.dismiss();
//                }
//            });

        }
    }
}
