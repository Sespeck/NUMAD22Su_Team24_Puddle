package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs5520.assignments.numad22su_team24_puddle.Adapter.MyPuddlesAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.Adapter.PuddleListAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PuddleListActivity extends AppCompatActivity implements View.OnClickListener {

    // Widgets
    RecyclerView puddleListRecyclerView;
    PuddleListAdapter puddleListAdapter;
    ShapeableImageView profileIcon;
    ImageView createIcon;
    ImageView navigationIcon;
    ImageView filterIcon;
    Button nearMeBtn, myPuddlesBtn;

    // Firebase
    FirebaseUser current_user;
    DatabaseReference userRef;
    DatabaseReference imgRef;
    StorageReference storeRef;

    private HashMap<String, String> userDetails;
    private Uri imageUri;

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    profileIcon.setImageURI(imageUri);

                    uploadToFirebase(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puddle_list);
        userDetails = new HashMap<>();

        profileIcon = findViewById(R.id.puddle_list_header_profile_icon);
        profileIcon.setOnClickListener(this);
        nearMeBtn = findViewById(R.id.header_near_me_btn);
        nearMeBtn.setOnClickListener(this);
        myPuddlesBtn = findViewById(R.id.header_my_puddles_btn);
        myPuddlesBtn.setOnClickListener(this);
        createIcon = findViewById(R.id.header_create_puddle_icon);
        createIcon.setOnClickListener(this);
        filterIcon = findViewById(R.id.header_filter_icon);
        filterIcon.setOnClickListener(this);
        navigationIcon = findViewById(R.id.header_navigation_icon);
        navigationIcon.setOnClickListener(this);

        // Api Calls
        fetchCurrentUserData();
        uploadImageToFb();

        // Initializing Widgets
        puddleListRecyclerView = findViewById(R.id.puddle_list_rv);


        updateRecyclerView(myPuddlesBtn);
    }

    private List<List<Puddle>> getPuddleList() {
        List<List<Puddle>> puddlesList = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle);
        for (Category category : Category.values()) {
            List<Puddle> puddleArray = new ArrayList<>();
            puddleArray.add(new Puddle("Number " + (category.id + 1), "Number " + (category.id + 1), bitmap));
            puddleArray.add(new Puddle("Number " + (category.id + 1), "Number " + (category.id + 1), bitmap));
            puddleArray.add(new Puddle("Number " + (category.id + 1), "Number " + (category.id + 1), bitmap));
            puddleArray.add(new Puddle("Number " + (category.id + 1), "Number " + (category.id + 1), bitmap));
            puddleArray.add(new Puddle("Number " + (category.id + 1), "Number " + (category.id + 1), bitmap));
            puddleArray.add(new Puddle("Number " + (category.id + 1), "Number " + (category.id + 1), bitmap));
            puddleArray.add(new Puddle("Number " + (category.id + 1), "Number " + (category.id + 1), bitmap));
            puddleArray.add(new Puddle("Number " + (category.id + 1), "Number " + (category.id + 1), bitmap));
            puddleArray.add(new Puddle("Number " + (category.id + 1), "Number " + (category.id + 1), bitmap));
            puddlesList.add(puddleArray);
        }
        return puddlesList;
    }

    public void fetchCurrentUserData() {
        current_user = FirebaseDB.getCurrentUser();

        userRef = FirebaseDB.getDataReference(getString(R.string.users)).child(current_user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    userDetails.put(snap.getKey(), snap.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view.equals(profileIcon)) {
            Intent gallery = new Intent();
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            gallery.setType("image/*");
            startActivityForResult.launch(gallery);
        } else if (view.equals(nearMeBtn) || view.equals(myPuddlesBtn)) {
            updateRecyclerView(view);
        } else if (view.equals(createIcon)) {

        } else if (view.equals(navigationIcon)) {

        } else if (view.equals(filterIcon)) {

        }
    }

    private void updateRecyclerView(View view) {
        // Initializing RecyclerView
        if (view.equals(nearMeBtn)) {
            puddleListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            puddleListRecyclerView.setAdapter(new PuddleListAdapter(this, getPuddleList()));
            nearMeBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
            myPuddlesBtn.setBackgroundColor(getResources().getColor(R.color.purple_200));
        } else {
            puddleListRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            puddleListRecyclerView.setAdapter(new MyPuddlesAdapter(this));
            nearMeBtn.setBackgroundColor(getResources().getColor(R.color.purple_200));
            myPuddlesBtn.setBackgroundColor(getResources().getColor(R.color.purple_700));
        }

    }

    // Testing Firestore + Firebase DB
    public void uploadImageToFb(){
        imgRef = FirebaseDB.getDataReference("images");
        imgRef.setValue("url");

        storeRef = FirebaseDB.storageRef;
    }

    public void uploadToFirebase(Uri uri){
        StorageReference ref = storeRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imgUrl = uri.toString();
                        imgRef.setValue(imgUrl);
                    }
                });
                Toast.makeText(PuddleListActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public String getFileExtension(Uri muri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(muri));
    }
}