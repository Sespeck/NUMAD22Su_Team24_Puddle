package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.dialogs;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddNewEventDialog extends DialogFragment {
    private Button exitButton;
    private AppCompatActivity parent;
    private Toolbar toolbar;
    private RelativeLayout upload;
    private String startingTime;
    private String endingTime;
    private String startingDate;
    private String endingDate;
    private TextInputLayout title;
    private Uri imageUri;
    private ImageView banner;
    private StorageReference storeRef;
    private DatabaseReference imgRef;
    private TextView startingDateTextView;




    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    imageUri = data.getData();
                    Glide.with(getContext()).load(imageUri).into(banner);
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
        upload = view.findViewById(R.id.add_banner);
        exitButton = view.findViewById(R.id.add_event_dialog_exit_button);
        title = view.findViewById(R.id.add_title_edit_text);
        banner = view.findViewById(R.id.selected_pud_img);
        startingDateTextView = view.findViewById(R.id.starting_date_text_view);
        toolbar = view.findViewById(R.id.add_event_toolbar);
        parent.setSupportActionBar(toolbar);
        initializeToolbar();
        initializeAllTextViewOnClicks(view);
        exitButton.setOnClickListener(v -> dismiss());
        upload.setOnClickListener(v -> {
            Intent gallery = new Intent();
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            gallery.setType("image/*");
            startActivityForResult.launch(gallery);
        });
        view.findViewById(R.id.add_event_save_button).setOnClickListener(v->{
            Bundle result = new Bundle();
            getParentFragmentManager().setFragmentResult("event_creation_result", result);

        });
        return view;
    }

    private void initializeToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setTitle("Add New Event");
        toolbar.setNavigationOnClickListener(v -> {
            dismiss();
        });
    }

    public void uploadToFirebase(Uri uri){
        StorageReference ref = storeRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        ref.putFile(uri).addOnSuccessListener(taskSnapshot
                -> ref.getDownloadUrl().addOnSuccessListener(uri1 -> {
            String imgUrl = uri1.toString();
            imgRef.setValue(imgUrl);
        })).addOnProgressListener(snapshot -> {

        }).addOnFailureListener(e -> {

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
        startingDate = java.time.LocalDate.now().toString();
        endingDate = java.time.LocalDate.now().toString();
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

    public boolean balanceStartPickerDates(String date) throws ParseException {
        Date newDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Date startingDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.startingDate);
        if (newDate != null && newDate.before(startingDate)) {
            Log.d("date",date);
            this.startingDate = date;
            startingDateTextView.setText(DateTimeFormatUtil.formatEventDate(date));
            return true;
        }
        return false;
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