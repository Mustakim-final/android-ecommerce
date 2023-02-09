package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton,loginButton;
    private String prentDbName="Users";


    @Override
    protected void onStart() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton =findViewById(R.id.main_join_now_btn);
        loginButton=findViewById(R.id.main_login_btn);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

//        String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
//        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
//
//        if (UserPhoneKey!= "" && UserPasswordKey!=""){
//            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){
//                AllowAccess(UserPhoneKey,UserPasswordKey);
//            }
//        }
    }


//    private void AllowAccess(String userPhoneKey, String userPasswordKey) {
//        final DatabaseReference RootRef;
//        RootRef= FirebaseDatabase.getInstance().getReference();
//
//        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(prentDbName).child(userPhoneKey).exists()){
//
//                    Users userData=snapshot.child(prentDbName).child(userPhoneKey).getValue(Users.class);
//
//                    if (userData.getPhone().equals(userPhoneKey)){
//                        if (userData.getPassword().equals(userPasswordKey)){
//
//                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
//                            startActivity(intent);
//                        }else {
//
//                        }
//                    }
//
//                }else {
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}