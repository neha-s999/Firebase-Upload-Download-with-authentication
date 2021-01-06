package com.app.firebase_upload_and_download;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.firebase_upload_and_download.Helpers.CoreHelper;
import com.app.firebase_upload_and_download.Helpers.CustomModel;
import com.app.firebase_upload_and_download.Helpers.FilesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UploadActivity extends AppCompatActivity {

    private static final int READ_PERMISSION_CODE = 1;
    private static final int PICK_FILE_REQUEST_CODE = 2;
    ImageView no_files;
    FloatingActionButton btnPickFiles, btnUploadFiles;
    RecyclerView recyclerView;
    List<CustomModel> FilesList;
    List<String> savedFilesUri;
    FilesAdapter adapter;
    CoreHelper coreHelper;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    CollectionReference reference;
    int counter;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();


        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        reference = firestore.collection("Uploads");

        savedFilesUri = new ArrayList<>();

        no_files = findViewById(R.id.no_image);
        btnPickFiles = findViewById(R.id.ChooseFile);
        btnUploadFiles = findViewById(R.id.UploadFile);
        FilesList = new ArrayList<>();
        coreHelper = new CoreHelper(this);

        //displaying list of files
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new FilesAdapter(this, FilesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() != 0) {
                    no_files.setVisibility(View.GONE);
                } else {
                    no_files.setVisibility(View.VISIBLE);
                }
            }
        });
        //selecting files to upload
        btnPickFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPermissionAndPickFile();
            }
        });
        btnUploadFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFiles(view);
            }
        });


    }


    private void uploadFiles(View view) {
        if (FilesList.size() != 0) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploaded 0/" + FilesList.size());
            progressDialog.setCanceledOnTouchOutside(false); //Remove this line if you want your user to be able to cancel upload
            progressDialog.setCancelable(false);    //Remove this line if you want your user to be able to cancel upload
            progressDialog.show();
            final StorageReference storageReference = storage.getReference();
            for (int i = 0; i < FilesList.size(); i++) {
                final int finalI = i;
                storageReference.child("uploads/" + userID + "/").child(FilesList.get(i).getFileName()).putFile(FilesList.get(i).getFileURI()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            storageReference.child("uploads/" + userID + "/").child(FilesList.get(finalI).getFileName()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    counter++;
                                    progressDialog.setMessage("Uploaded " + counter + "/" + FilesList.size());
                                    if (task.isSuccessful()) {
                                        savedFilesUri.add(task.getResult().toString());
                                    } else {
                                        storageReference.child("uploads/" + userID + "/").child(FilesList.get(finalI).getFileName()).delete();
                                        Toast.makeText(UploadActivity.this, "Couldn't save " + FilesList.get(finalI).getFileName(), Toast.LENGTH_SHORT).show();
                                    }
                                    if (counter == FilesList.size()) {
                                        saveFileDataToFirestore(progressDialog);
                                    }
                                }
                            });
                        } else {
                            progressDialog.setMessage("Uploaded " + counter + "/" + FilesList.size());
                            counter++;
                            Toast.makeText(UploadActivity.this, "Couldn't upload " + FilesList.get(finalI).getFileName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            coreHelper.createSnackBar(view, "Please add some files first.", "", null, Snackbar.LENGTH_SHORT);
        }
    }

    private void saveFileDataToFirestore(final ProgressDialog progressDialog) {
        progressDialog.setMessage("Saving uploaded images...");
        Map<String, String> dataMap = new HashMap<>();
        for (int i = 0; i < savedFilesUri.size(); i++) {
            //saving the files on firestore
            dataMap.put("link", savedFilesUri.get(i));
            dataMap.put("name", FilesList.get(i).getFileName());

        }
        reference.add(dataMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.dismiss();
                coreHelper.createAlert("Success", "Files uploaded and saved successfully!", "OK", "", null, null, null);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                coreHelper.createAlert("Error", "Files uploaded but we couldn't save them to database.", "OK", "", null, null, null);
                Log.e("MainActivity:SaveData", e.getMessage());
            }
        });
    }

    // permissions to access files stored on ypur device
    private void verifyPermissionAndPickFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                pickFile();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
            }
        } else {
            pickFile();
        }
    }


    // picking files of multiple types
    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("files/*");   // Change the file type based on application requirement
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFile();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FILE_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri uri = clipData.getItemAt(i).getUri();
                            FilesList.add(new CustomModel(coreHelper.getFileNameFromUri(uri), uri));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Uri uri = data.getData();
                        FilesList.add(new CustomModel(coreHelper.getFileNameFromUri(uri), uri));
                        adapter.notifyDataSetChanged();
                    }
                }
        }
    }


}