package com.example.dinesh.eventadminapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class NewEvent extends AppCompatActivity {


    EditText from, till, event_name_et, des, location;
    Calendar myCalendar, myCalendar1;
    Button image_upload, save;
    private static final int RESULT_LOAD_IMAGE = 1;
    ProgressDialog dialog;
    Toolbar toolbar;
    private StorageReference mStorage;
    Intent myIntent;
    Bitmap thumb_bitmap;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mImageDatabase;

    byte[] thumb_byte;


    private RecyclerView mUploadList;
    private List<String> fileNameList;
    int totalItemsSelected;

    private UploadListAdapter uploadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("New Event");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorage = FirebaseStorage.getInstance().getReference();

        from = (EditText) findViewById(R.id.from);
        till = (EditText) findViewById(R.id.till);
        location = (EditText) findViewById(R.id.location);
        event_name_et = (EditText) findViewById(R.id.event_name);
        des = (EditText) findViewById(R.id.des);
        image_upload = (Button) findViewById(R.id.image_upload);
        save = (Button) findViewById(R.id.create_event);
        mUploadList = (RecyclerView) findViewById(R.id.upload_list);



        from.setFocusable(false);
        till.setFocusable(false);

        fileNameList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileNameList);

        //RecyclerView

        mUploadList.setLayoutManager(new LinearLayoutManager(this));
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);


        myCalendar = Calendar.getInstance();
        myCalendar1 = Calendar.getInstance();


        final DatePickerDialog.OnDateSetListener date = new
                DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }

                };

        final DatePickerDialog.OnDateSetListener date1 = new
                DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar1.set(Calendar.YEAR, year);
                        myCalendar1.set(Calendar.MONTH, monthOfYear);
                        myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel1();
                    }

                };

        from.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(NewEvent.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return false;
            }
        });

        till.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(NewEvent.this, date1, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar1.get(Calendar.DAY_OF_MONTH)).show();
                }
                return false;
            }
        });


        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (event_name_et.getText().toString().isEmpty()) {
                    event_name_et.setError("Event Name Should not be blank");
                } else if (location.getText().toString().isEmpty()) {
                    location.setError("Event location Should not be blank");
                } else if (from.getText().toString().isEmpty()) {
                    from.setError("from date Should not be blank");
                } else if (till.getText().toString().isEmpty()) {

                    till.setError("till date Should not be blank");
                } else if (des.getText().toString().isEmpty()) {

                    till.setError("Description Should not be blank");
                } else {


                    dialog = ProgressDialog.show(NewEvent.this, "",
                            "Creating Event. Please wait...", true);

                    final String name = event_name_et.getText().toString();

                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Events").child(name);


                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("event_name", event_name_et.getText().toString());
                    userMap.put("des", des.getText().toString());
                    userMap.put("from", from.getText().toString());
                    userMap.put("till", till.getText().toString());
                    userMap.put("location", location.getText().toString());
                    userMap.put("Images", "default");


                    mUserDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull final Task<Void> task) {


                            if (totalItemsSelected >= 1) {


                                for (int i = 0; i < totalItemsSelected; i++) {

                                    Uri fileUri = myIntent.getClipData().getItemAt(i).getUri();

                                    String fileName = getFileName(fileUri);

                                    uploadListAdapter.notifyDataSetChanged();


                                    Uri file = myIntent.getClipData().getItemAt(i).getUri();

                                    StorageReference riversRef = mStorage.child("Events").child(name).child(fileName);

                                    riversRef.putFile(file)
                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                    mImageDatabase = FirebaseDatabase.getInstance().getReference().child("Events").child(name);


                                                    String download_url = task.getResult().getDownloadUrl().toString();
                                                    Map update_hashmap = new HashMap<>();
                                                    update_hashmap.put("Images", download_url);

                                                    mImageDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {

                                                            }

                                                        }
                                                    });


                                                    Toast.makeText(NewEvent.this, "Event Created Successfully.", Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                    finish();

                                                } /*else {

                                                        Toast.makeText(NewEvent.this, "Something went wrong! Try Again.", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }*/

                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {

                                                    Toast.makeText(NewEvent.this, "Something went wrong! Try again.", Toast.LENGTH_SHORT).show();

                                                    dialog.dismiss();
                                                }
                                            });


                                }


                            } else {

                                Uri uri = myIntent.getData();

                                String fileName = getFileName(uri);

                                uploadListAdapter.notifyDataSetChanged();

                                StorageReference riversRef = mStorage.child("Events").child(name).child(fileName);

                                riversRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                                        if (task.isSuccessful()) {

                                            mImageDatabase = FirebaseDatabase.getInstance().getReference().child("Events").child(name);

                                            String download_url = task.getResult().getDownloadUrl().toString();
                                            Map update_hashmap = new HashMap<>();
                                            update_hashmap.put("Images", download_url);

                                            mImageDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(NewEvent.this, "Event Created Successfully.", Toast.LENGTH_LONG).show();

                                                        dialog.dismiss();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(NewEvent.this, "Something went wrong! Try Again.", Toast.LENGTH_SHORT).show();

                                                        dialog.dismiss();
                                                    }

                                                }
                                            });

                                        } else {
                                            Toast.makeText(NewEvent.this, "Something went wrong! Try Again.", Toast.LENGTH_SHORT).show();

                                            dialog.dismiss();
                                        }

                                    }
                                });


                            }
                        }
                    });


                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {

                myIntent = data;

                totalItemsSelected = data.getClipData().getItemCount();

                for (int i = 0; i < totalItemsSelected; i++) {

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                    String fileName = getFileName(fileUri);

                    fileNameList.add(fileName);
                    uploadListAdapter.notifyDataSetChanged();
                }


            } else if (data.getData() != null) {

                myIntent = data;

                //Toast.makeText(NewEvent.this, "Selected Single File", Toast.LENGTH_SHORT).show();

                Uri resultUri = data.getData();


                String fileName = getFileName(resultUri);

                fileNameList.add(fileName);
                uploadListAdapter.notifyDataSetChanged();

            }

        }

    }

    private void updateLabel() {

        String myFormat = "dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        from.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel1() {

        String myFormat = "dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        till.setText(sdf.format(myCalendar1.getTime()));
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
