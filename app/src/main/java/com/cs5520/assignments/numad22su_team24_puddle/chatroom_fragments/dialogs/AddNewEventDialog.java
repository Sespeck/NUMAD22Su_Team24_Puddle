package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class AddNewEventDialog extends DialogFragment {
    private Button exitButton;
    private AppCompatActivity parent;
    private Toolbar toolbar;
    private String startingTime;
    private String endingTime;
    private String startingDate;
    private String endingDate;
    private EditText title;
    private Uri imageUri;
    private StorageReference storeRef;
    private DatabaseReference imgRef;




    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    uploadToFirebase(imageUri);
                }
            }
    );

    public AddNewEventDialog(AppCompatActivity parent){
        this.parent = parent;
    }


    /** the system calls this to get the dialogfragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_event_dialog, container, false);
        exitButton = view.findViewById(R.id.add_event_dialog_exit_button);
        title = view.findViewById(R.id.add_title_edit_text);
        toolbar = view.findViewById(R.id.add_event_toolbar);
        parent.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setTitle("Add New Event");
        toolbar.setNavigationOnClickListener(v -> {
            dismiss();
        });
        initializeAllTextViewOnClicks(view);
        exitButton.setOnClickListener(v -> dismiss());
        view.findViewById(R.id.add_event_save_button).setOnClickListener(v->{
            Bundle result = new Bundle();
            getParentFragmentManager().setFragmentResult("event_creation_result", result);

        });
        return view;
    }

    public void uploadToFirebase(Uri uri){
        StorageReference ref = storeRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        ref.putFile(uri).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri1) {
                String imgUrl = uri1.toString();
                imgRef.setValue(imgUrl);
            }
        })).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(muri));
    }



    private void initializeAllTextViewOnClicks(View view){
        TextView startingTimeView = view.findViewById(R.id.starting_time_text_view);
        TextView endingTimeView = view.findViewById(R.id.ending_time_text_view);
        TextView startingDateView = view.findViewById(R.id.starting_date_text_view);
        TextView endingDateView = view.findViewById(R.id.ending_date_text_view);
        String[] initalTime = DateTimeFormatUtil.formatPresetTime(java.time.LocalTime.now().toString());
        String date = DateTimeFormatUtil.formatEventDate(java.time.LocalDate.now().toString());
        startingTimeView.setText(initalTime[0]);
        endingTimeView.setText(initalTime[1]);
        startingDateView.setText(date);
        endingDateView.setText(date);
        startingDateView.setOnClickListener(this::showDatePickerDialog);
        endingDateView.setOnClickListener(this::showDatePickerDialog);
        startingTimeView.setOnClickListener(this::showTimePickerDialog);
        endingTimeView.setOnClickListener(this::showTimePickerDialog);
    }

    /** The system calls this only when creating the layout in a dialog. */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new EventCalendarPickerDialog(v, this);
        newFragment.show(getParentFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new EventTimePickerDialog(v, this);
        newFragment.show(getParentFragmentManager(), "timePicker");
    }

    public void acceptPickerStartTime(String startingTime){
        this.startingTime = startingTime;
    }

    public void acceptPickerEndingTime(String endingTime){
        this.endingTime = endingTime;
    }

    public void acceptPickerStartingDate(String startingDate){
        this.startingDate = startingDate;
    }

    public void acceptPickerEndingDate(String endingDate){
        this.endingDate = endingDate;
    }
}