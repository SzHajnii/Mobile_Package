package com.example.mobilepackage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "LOG_TAG";
    EditText profileFullName, profileEmailAddress, profilePhone, profileAddress;
    ImageView profileImageView;
    Button saveProfileInfo, back;
    FirebaseAuth auth;
    FirebaseFirestore store;
    FirebaseUser user;
    StorageReference storageReference;
    String userId;

    TextView enamewrite, eemailWrite, ephoneWrite, eaddresswrite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent data = getIntent();
        final String fullName = data.getStringExtra("fullname");
        String email = data.getStringExtra("email");
        String phone = data.getStringExtra("phone");

        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        profileFullName = findViewById(R.id.profileFullName);
        profileEmailAddress = findViewById(R.id.profileEmailAddress);
        profilePhone = findViewById(R.id.profilePhone);
        profileAddress = findViewById(R.id.profilAddress);
        saveProfileInfo = findViewById(R.id.saveProfileInfo);
        profileImageView = findViewById(R.id.profileImage);
        back = findViewById(R.id.back);


        enamewrite = findViewById(R.id.namewrite);
        eemailWrite = findViewById(R.id.emailWrite);
        ephoneWrite = findViewById(R.id.phoneWrite);
        eaddresswrite = findViewById(R.id.addresswrite);

        userId = auth.getCurrentUser().getUid();

        StorageReference profileRef = storageReference.child("users/"+auth.getCurrentUser().getUid()+"/profile.jpg");
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        DocumentReference documentReference = store.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    ephoneWrite.setText(documentSnapshot.getString("phone"));
                    enamewrite.setText(documentSnapshot.getString("fName"));
                    eemailWrite.setText(documentSnapshot.getString("email"));
                    eaddresswrite.setText(documentSnapshot.getString("address"));
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

        saveProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileFullName.getText().toString().isEmpty() || profileEmailAddress.getText().toString().isEmpty() ||
                        profilePhone.getText().toString().isEmpty() || profileAddress.getText().toString().isEmpty() ){
                    Toast.makeText(ProfileActivity.this, "Egy vagy több mező üres", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String email = profileEmailAddress.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = store.collection("users").document(user.getUid());
                        Map<String,Object> edited = new HashMap<>();
                        edited.put("fName",profileFullName.getText().toString());
                        edited.put("email",email);
                        edited.put("phone",profilePhone.getText().toString());
                        edited.put("address",profileAddress.getText().toString());
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileActivity.this, "Profil frissítve", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),ProductsActivity.class));
                                finish();
                            }
                        });
                        Toast.makeText(ProfileActivity.this, "Email megváltoztatva.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ProductsActivity.class));
            }
        });
        profileEmailAddress.setText(email);
        profileFullName.setText(fullName);
        profilePhone.setText(phone);

        Log.d(TAG, "onCreate: " + fullName + " " + email + " " + phone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    imageUri);
                    profileImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        if(imageUri != null){
            final StorageReference fileRef = storageReference.child("users/"+auth.getCurrentUser().getUid()+"/profile.jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast
                            .makeText(ProfileActivity.this,
                                    "Failed " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
