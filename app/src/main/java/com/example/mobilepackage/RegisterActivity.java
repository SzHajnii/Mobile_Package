package com.example.mobilepackage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity  extends AppCompatActivity{
    public static final String LOG_TAG = "Tag";
    EditText efullName, eEmail, epwd, epasswordAgain,ephone, eaddress;
    Button eregisterBtn, ecancelBtn;
    FirebaseAuth auth;
    FirebaseFirestore store;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrate);

        efullName = findViewById(R.id.fullName);
        eEmail    = findViewById(R.id.Email);
        epwd      = findViewById(R.id.pwd);
        epasswordAgain = findViewById(R.id.passwordAgain);
        ephone    = findViewById(R.id.phone);
        eaddress  = findViewById(R.id.address);
        eregisterBtn = findViewById(R.id.registerBtn);
        ecancelBtn = findViewById(R.id.cancelBtn);

        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),ProductsActivity.class));
            finish();
        }
        eregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = eEmail.getText().toString();
                String password = epwd.getText().toString();
                String passwordAgain = epasswordAgain.getText().toString();
                String fullName = efullName.getText().toString();
                String address = eaddress.getText().toString();
                String phone = ephone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    eEmail.setError("Email cím kötelező!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    epwd.setError("Jelszó kötelző");
                    return;
                }

                if(password.length() < 6){
                    epwd.setError("A jelszó több mint 6 karakter hosszú");
                    return;
                }

                if(!password.equals(passwordAgain)){
                    epasswordAgain.setError("A két jelszónak meg kell egyeznie!");
                }

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser fuser = auth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = auth.getCurrentUser().getUid();
                            DocumentReference documentReference = store.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",fullName);
                            user.put("email",email);
                            user.put("phone",phone);
                            user.put("address",address);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(LOG_TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(LOG_TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),ProductsActivity.class));

                        }else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        ecancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


    }

}
