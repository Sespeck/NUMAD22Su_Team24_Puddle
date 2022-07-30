package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.cs5520.assignments.numad22su_team24_puddle.Adapter.PuddleListAdapter;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
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

public class PuddleListActivity extends AppCompatActivity {

    // Widgets
    RecyclerView puddleListRecyclerView;
    PuddleListAdapter puddleListAdapter;
    ImageButton btn;

    // Firebase
    FirebaseUser current_user;
    DatabaseReference userRef;
    DatabaseReference imgRef;
    StorageReference storeRef;

    private HashMap<String, String> userDetails;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puddle_list);
        userDetails = new HashMap<>();

        // Api Calls
        fetchCurrentUserData();
        uploadImageToFb();

        // Initializing Widgets
        puddleListRecyclerView = findViewById(R.id.puddle_list_rv);
        btn = findViewById(R.id.profile_icon);

        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        btn.setImageURI(imageUri);

                        uploadToFirebase(imageUri);
                    }
                }
        );

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult.launch(gallery);
            }
        });

        // Initializing RecyclerView
        puddleListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        puddleListAdapter = new PuddleListAdapter(this, getPuddleList());
        puddleListRecyclerView.setAdapter(puddleListAdapter);
    }

    private List<List<Puddle>> getPuddleList() {
        List<List<Puddle>> puddlesList= new ArrayList<>();
        for(Category category: Category.values()) {
            List<Puddle> puddleArray = new ArrayList<>();
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddleArray.add(new Puddle("Number "+(category.id+1), "Number 1", BitmapFactory.decodeResource(this.getResources(), R.drawable.puddle)));
            puddlesList.add(puddleArray);
        }
        return puddlesList;
    }

    public void clickProfile(View view){
//        drawer.openDrawer(GravityCompat.START);
        Intent intent = new Intent(this, MyPuddles.class);
        startActivity(intent);
    }

    public void fetchCurrentUserData(){
        current_user = FirebaseDB.getCurrentUser();

        userRef = FirebaseDB.getDataReference(getString(R.string.users)).child(current_user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    userDetails.put(snap.getKey(), snap.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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