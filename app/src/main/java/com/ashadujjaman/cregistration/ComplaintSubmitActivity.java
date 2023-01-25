package com.ashadujjaman.cregistration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;


public class ComplaintSubmitActivity extends AppCompatActivity {

    EditText complainTitleEt, complainDescEt;
    ImageView complainImageIv;
    Button submitBtn;

    String complainTitle, complainDesc;
    boolean valid = true;

    //Permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    //Image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;
    //aRRAYS OF PERMISSION
    private String[] cameraPermissions; //camera and storage
    private String[] storagePermissions; //only storage

    //Variables will contain data to save
    private Uri imageUri = null;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_submit);

        complainTitleEt = findViewById(R.id.complainTitleEt);
        complainDescEt = findViewById(R.id.complainDescEt);
        complainImageIv = findViewById(R.id.complainImageIv);
        submitBtn = findViewById(R.id.submit_btn);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        complainImageIv.setOnClickListener(v -> {
            //show image pick Dialog
            showImagePickDialog();
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(complainTitleEt);
                checkField(complainDescEt);

                complainTitle = complainTitleEt.getText().toString().trim();
                complainDesc = complainDescEt.getText().toString().trim();

                if (valid){
                    inputData();
                }
            }
        });

    }

    private void inputData() {
        progressDialog.setMessage("Submitting....");
        progressDialog.show();

        //current time to millis
        String timestamp = ""+System.currentTimeMillis();

        //post with out image
        if (imageUri == null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("complainId", ""+timestamp);
            hashMap.put("complainTitle", complainTitle);
            hashMap.put("complainDesc", complainDesc);
            hashMap.put("complainImageIv", "");
            hashMap.put("complainAction", "pending");
            hashMap.put("userId", firebaseUser.getUid());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Complain");
            ref.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(unused -> {
                        //successfully
                        progressDialog.dismiss();
                        Toast.makeText(ComplaintSubmitActivity.this, "Complain submit successful", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ComplaintSubmitActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }
        else {
            //file path and name
            String filePathName = "Complain/" + "Complain_"+ firebaseUser.getUid()+ "_"+ timestamp +".jpg";

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathName);
            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        //image is uploaded to firebase storage, get uri
                        Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String downloadUri = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("complainId", ""+timestamp);
                            hashMap.put("complainTitle", complainTitle);
                            hashMap.put("complainDesc", complainDesc);
                            hashMap.put("complainImageIv", downloadUri);
                            hashMap.put("complainAction", "pending");
                            hashMap.put("userId", firebaseUser.getUid());

                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Complain");
                            ref1.child(timestamp).setValue(hashMap)
                                    .addOnSuccessListener(unused -> {
                                        //successfully
                                        progressDialog.dismiss();
                                        Toast.makeText(ComplaintSubmitActivity.this, "Complain submit successful", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(ComplaintSubmitActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ComplaintSubmitActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showImagePickDialog() {
        //options to display in dialog
        String[] options = {"Camera", "Gallery"};
        //show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //title
        builder.setTitle("Choose Image From");
        //set items/options
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //handle clicks
                if (i == 0) {
                    //camera clickde
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        //permission already granted
                        pickFromCamera();
                    }
                } else if (i == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        //permission already granted
                        pickFromGallery();
                    }
                }
            }
        });
        //create show dialog
        builder.create().show();
    }

    private void pickFromGallery() {
        //Intent to pick image from gallery , the image will be returned in onActivityResult method
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //Intent to pick image from camera, the image will be returned in onActivityResult method
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image description");
        //put image uri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to open camera for image
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enable or not
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission() {
        //request the storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission() {
        //check if camera permissions is enable or not
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        //request the camera permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    public void checkField(EditText textField){
        if (textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //result of permission allowed/denied
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    //if allowed returns true otherwise false
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        //both permission allowed
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Camera & Storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    //if allowed returns true otherwise false
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        //storage permission allow
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //image picked from camera or gallery will be received here

        if (resultCode == RESULT_OK) {
            //image is picked
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //picked from gallery
                //crop image
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //picked from camera
                //crop image
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                //croped image received
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK){
                    Uri resultUri = result.getUri();
                    imageUri = resultUri;
                    //set image
                    complainImageIv.setImageURI(resultUri);
                }
                else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    //error
                    Exception error = result.getError();
                    Toast.makeText(this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}