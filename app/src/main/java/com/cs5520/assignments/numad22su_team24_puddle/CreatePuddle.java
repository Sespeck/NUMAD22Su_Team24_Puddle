package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class CreatePuddle extends AppCompatActivity {

    // Widgets
    TextInputEditText puddleName;
    TextInputEditText puddleBio;
    SwitchMaterial isPrivate;
    Slider range;
    Button createPuddle;
    LinearLayout addBanner;
    AutoCompleteTextView menu;

    Uri imgUri;
    String bannerUrl = "";

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    imgUri = data.getData();
                    uploadImageToStore(imgUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_puddle);

        // Initializing Widgets
        puddleName = findViewById(R.id.puddle_name_txt);
        puddleBio = findViewById(R.id.puddle_bio_txt);
        isPrivate = findViewById(R.id.private_pud);
        range = findViewById(R.id.range_filter);
        createPuddle = findViewById(R.id.create);
        addBanner = findViewById(R.id.add_banner);
        menu = findViewById(R.id.category_menu);

        String[] options = {"Select", "Music", "Sports", "Education"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.category_options, options);
        menu.setText(arrayAdapter.getItem(0).toString(), false);
        menu.setAdapter(arrayAdapter);

        addBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult.launch(gallery);
            }
        });

        createPuddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPuddleToFirebase();
            }
        });
    }

    public void uploadImageToStore(Uri uri){
        StorageReference ref = FirebaseDB.storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        bannerUrl = uri.toString();

                        if(bannerUrl != null || bannerUrl != ""){
//                            sendPuddleToFirebase(bannerUrl);
                            Toast.makeText(CreatePuddle.this, "Success!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreatePuddle.this, "Error sending image to Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//                Toast.makeText(PuddleListActivity.this, "Success!", Toast.LENGTH_SHORT).show();
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

    public void sendPuddleToFirebase(){
        DatabaseReference ref = FirebaseDB.getDataReference("Puddles");

        HashMap<String, Object> puddleMap = new HashMap<>();
        puddleMap.put("name", puddleName.getText().toString());
        puddleMap.put("bio", puddleBio.getText().toString());
        puddleMap.put("isPrivate", String.valueOf(isPrivate.isChecked()));
        puddleMap.put("bannerUrl", bannerUrl);
        puddleMap.put("range", String.valueOf(range.getValue()));
        puddleMap.put("category", menu.getText().toString());

        ref.push().setValue(puddleMap);
    }
}