package com.example.jeremy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private CircleImageView setupImage;
    private Uri mainImageURI;
    private EditText setupName;
    private Button setupBtn;
    private ProgressBar setupProgress;
    //firebase magic
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar=findViewById(R.id.setupToolbar);
        //setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setting");

        setupImage=(CircleImageView) findViewById(R.id.setup_image);
        setupName=(EditText) findViewById(R.id.setup_name);
        setupBtn=(Button) findViewById(R.id.setup_btn);
        setupProgress=(ProgressBar) findViewById(R.id.setup_progress);

        //firebase magic
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();


        // onClick SAVE ACCOUNT SETTINGS
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = setupName.getText().toString();
                if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {
                    setupProgress.setVisibility(View.VISIBLE);
                    // add image to firebase storege and user_name to file storage
                    user_id = firebaseAuth.getCurrentUser().getUid();
                    //root firebase storage with profile_images folder will have image name with user with format .jpg
                    StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Task<Uri> download_uri = task.getResult().getMetadata().getReference().getDownloadUrl();
                                Toast.makeText(SetupActivity.this, "Image is uploaded " , Toast.LENGTH_LONG).show();
                            } else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "Image Error " + errorMessage, Toast.LENGTH_LONG).show();
                                setupProgress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(SetupActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                }else{
                    BringImagePicker();
                }
            }
        });
    }

    private void BringImagePicker() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //if match the requestCode, this will get URI Activity
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}