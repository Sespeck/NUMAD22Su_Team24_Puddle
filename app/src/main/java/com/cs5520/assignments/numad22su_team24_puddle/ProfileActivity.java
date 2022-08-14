package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.Model.ApiLoaderBar;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.LocationPermissionActivity;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.MessageNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    ShapeableImageView profileIcon, cameraBtn;
    TextInputEditText displayET, bioET, phoneNumberET;
    Button saveBtn;
    Uri imageUri = null;
    String dpUrl = "";
    File photoFile;

    DatabaseReference imgRef;
    StorageReference storeRef;
    Handler apiHandler = new Handler();
    final ApiLoaderBar apiBar = new ApiLoaderBar(ProfileActivity.this);
    private ArrayList<ValueEventListener> valueEventListeners = new ArrayList<>();
    private ArrayList<DatabaseReference> references = new ArrayList<>();
    private MessageNotification notification;
    private ValueEventListener valueEventListener;
    private DatabaseReference userRef;

    public static final int CAMERA_REQUEST_CODE = 102;

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    Log.d("here", imageUri.toString());
                    profileIcon.setImageURI(imageUri);
                }
            }
    );

    ActivityResultLauncher<Intent> startActivityForResultCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Uri uri = Uri.fromFile(photoFile);
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview
                    imageUri = uri;
                    profileIcon.setImageURI(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgRef = FirebaseDB.getDataReference("images");
        storeRef = FirebaseDB.storageRef;
        Util.isPuddleListForeground = true;

        profileIcon = findViewById(R.id.profile_user_icon);
        displayET = findViewById(R.id.profile_display_name_et);
        bioET = findViewById(R.id.profile_description_et);
        phoneNumberET = findViewById(R.id.profile_phone_number_et);
        phoneNumberET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        saveBtn = findViewById(R.id.profile_save_btn);
        cameraBtn = findViewById(R.id.profile_camera_icn);

        saveBtn.setOnClickListener(v -> saveBtnClick());
        profileIcon.setOnClickListener(v -> setProfileImage());
        cameraBtn.setOnClickListener(v -> clickCameraBtn());
        notification = new MessageNotification(this);
        if (!Util.listener.isRegistered()){
            Util.listener.registerListener(notification);
        }
        fillDetails();

    }

    private void clickCameraBtn() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String photoFileName = "photo.jpg";
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(ProfileActivity.this, "com.cs5520.assignments.numad22su_team24_puddle", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        startActivityForResultCamera.launch(intent);
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Puddle");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("Puddle", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void setProfileImage() {
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult.launch(gallery);
    }

    private void fillDetails() {
        displayET.setText(FirebaseDB.getLocalUser().getDisplay_name());
        bioET.setText(FirebaseDB.getLocalUser().getBio());
        phoneNumberET.setText(FirebaseDB.getLocalUser().getPhone_number());

        if (!FirebaseDB.getLocalUser().getProfile_icon().equals("")) {
            dpUrl = FirebaseDB.getLocalUser().getProfile_icon();
            Glide.with(ProfileActivity.this).load(dpUrl).into(profileIcon);
        }
    }

    private void saveBtnClick() {
        apiBar.showDialog();
        if (imageUri != null) {
            uploadImageToStore(imageUri);
        } else {
            uploadDataToFB();
        }
    }

    public void uploadDataToFB() {
        DatabaseReference ref = FirebaseDB.getDataReference("Users").child(FirebaseDB.getLocalUser().getId());
        HashMap<String, Object> hashMap = FirebaseDB.getLocalUser().getUserMap();

        hashMap.put("display_name", displayET.getText().toString());
        hashMap.put("bio", bioET.getText().toString());
        hashMap.put("phone_number", phoneNumberET.getText().toString());
        hashMap.put("profile_icon", dpUrl);

        ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                apiHandler.post(() -> {
                    apiBar.dismissBar();
                });

                if (task.isSuccessful()) {
                    FirebaseDB.getLocalUser().setDisplay_name(displayET.getText().toString());
                    FirebaseDB.getLocalUser().setBio(bioET.getText().toString());
                    FirebaseDB.getLocalUser().setPhone_number(phoneNumberET.getText().toString());
                    FirebaseDB.getLocalUser().setProfile_icon(dpUrl);
                    Intent intent = new Intent(ProfileActivity.this, PuddleListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Error registering user!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile_menu_logout) {
            FirebaseDB.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadImageToStore(Uri uri) {
        StorageReference ref = FirebaseDB.storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dpUrl = uri.toString();

                                if (dpUrl != null || dpUrl != "") {
                                    uploadDataToFB();
                                } else {
                                    apiHandler.post(() -> {
                                        apiBar.dismissBar();
                                    });
                                    Toast.makeText(ProfileActivity.this, "Error sending image to Store", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).start();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                apiBar.dismissBar();
                Toast.makeText(ProfileActivity.this, "Failed to upload DP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getFileExtension(Uri muri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(muri));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera access is not granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}