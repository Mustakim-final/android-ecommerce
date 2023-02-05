package com.example.ecommerce;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    String categoryName,saveCurrentDate,saveCurrentTime;
    Button AddNewProductButton;
    ImageView InputProductImage;
    EditText InputProductName,InputProductDescription,InputProductPrice;

    private static final int IMAGE_REQUEST=1;
    private static final int CAMERA_CODE=200;
    private static final int GALLERY_CODE=300;

    StorageReference storageReference;
    StorageTask storageTask;
    FirebaseStorage firebaseStorage;

    private Uri imageUri=null;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");

        categoryName=getIntent().getExtras().get("category").toString();

        AddNewProductButton=findViewById(R.id.add_new_product);
        InputProductName=findViewById(R.id.product_name);
        InputProductImage=findViewById(R.id.select_product_image);
        InputProductDescription=findViewById(R.id.product_description);
        InputProductPrice=findViewById(R.id.product_price);


        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallary();
            }
        });


        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName=InputProductName.getText().toString().trim();
                String productDescription=InputProductDescription.getText().toString().trim();
                String productPrice=InputProductPrice.getText().toString();

                if (imageUri==null){
                    Toast.makeText(AdminAddNewProductActivity.this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(productName)){
                    Toast.makeText(AdminAddNewProductActivity.this, "Please write product name...", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(productDescription)){
                    Toast.makeText(AdminAddNewProductActivity.this, "Please write product description...", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(productPrice)){
                    Toast.makeText(AdminAddNewProductActivity.this, "Please write product price...", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.show();
                    submitData(imageUri,productName,productDescription,productPrice,categoryName);
                }
            }
        });

    }



    private void openGallary() {
        Intent intent=new Intent();
        intent.setType(("image/*"));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK &&data!=null && data.getData()!=null ) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(InputProductImage);
        }
    }

    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }


    private void submitData(Uri imageUri, String productName, String productDescription, String productPrice,String categoryName) {
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM ,dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentDate=currentTime.format(calendar.getTime());


        storageReference = FirebaseStorage.getInstance().getReference("product");
        StorageReference sreference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        sreference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUri = uri.toString();
                        progressDialog.show();

                        reference= FirebaseDatabase.getInstance().getReference();
                        String productRandomKey=reference.push().getKey();

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("pid",productRandomKey);
                        hashMap.put("date",saveCurrentDate);
                        hashMap.put("time",saveCurrentTime);
                        hashMap.put("description",productDescription);
                        hashMap.put("image",imageUri);
                        hashMap.put("category",categoryName);
                        hashMap.put("price",productPrice);
                        hashMap.put("pname",productName);
                        reference.child("product").push().setValue(hashMap);

                        progressDialog.dismiss();


                    }
                });
            }
        });
    }
}