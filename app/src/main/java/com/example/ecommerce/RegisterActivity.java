package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Button createAccountButton;
    EditText inputName,inputEmail,inputPassword;
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton=findViewById(R.id.register_btn);
        inputName=findViewById(R.id.register_username_input);
        inputEmail=findViewById(R.id.register_email_input);
        inputPassword=findViewById(R.id.register_password_input);

        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=inputName.getText().toString().trim();
                String gmail=inputEmail.getText().toString().trim();
                String password=inputPassword.getText().toString().trim();

                if (username.isEmpty()){
                    inputName.setError("Enter User Name!!");
                    inputName.requestFocus();
                }else if (gmail.isEmpty()){
                    inputEmail.setError("Enter Gmail !!");
                    inputEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(gmail).matches()){
                    inputEmail.setError("Enter Valid gmail !!");
                    inputEmail.requestFocus();
                }else if (password.isEmpty()){
                    inputPassword.setError("Enter Password !!");
                    inputPassword.requestFocus();
                }else if (password.length()<6){
                    inputPassword.setError("Enter 6 digit password !!");
                    inputPassword.requestFocus();
                }else {
                    progressDialog.show();
                    validatePhoneNumber(username,gmail,password);
                }
            }

        });

        
    }



    private void validatePhoneNumber(String username, String gmail, String password) {


        firebaseAuth.createUserWithEmailAndPassword(gmail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                    String userID=firebaseUser.getUid();

                    reference= FirebaseDatabase.getInstance().getReference("Users").child(userID);

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("name",username);
                    hashMap.put("gmail",gmail);
                    hashMap.put("id",userID);
                    hashMap.put("imageUrl","imageUrl");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Account create successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                                startActivity(intent);
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Network error:please try again after sometime...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(RegisterActivity.this, "This"+gmail+"already exists", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again another email", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }


}