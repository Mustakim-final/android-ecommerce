package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView tShirts,sportsTshirts,femaleDress,sweaters;
    ImageView glasses,hatscaps,walletsBagPurses,shoes;
    ImageView hadphoneHandFree,laptops,watches,mobilePhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        tShirts=findViewById(R.id.t_shirts);
        sportsTshirts=findViewById(R.id.shortshirt);
        femaleDress=findViewById(R.id.female_dress);
        sweaters=findViewById(R.id.sweather);
        glasses=findViewById(R.id.glasses);
        hatscaps=findViewById(R.id.hats);
        walletsBagPurses=findViewById(R.id.purses_bags);
        shoes=findViewById(R.id.shoess);
        hadphoneHandFree=findViewById(R.id.headphoness);
        laptops=findViewById(R.id.laptops);
        watches=findViewById(R.id.watches);
        mobilePhone=findViewById(R.id.mobiles);

        tShirts.setOnClickListener(this);
        sportsTshirts.setOnClickListener(this);
        femaleDress.setOnClickListener(this);
        sweaters.setOnClickListener(this);
        glasses.setOnClickListener(this);
        hatscaps.setOnClickListener(this);
        walletsBagPurses.setOnClickListener(this);
        shoes.setOnClickListener(this);
        hadphoneHandFree.setOnClickListener(this);
        laptops.setOnClickListener(this);
        watches.setOnClickListener(this);
        mobilePhone.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.t_shirts){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","T-Shirts");
            startActivity(intent);
        }else if (v.getId()==R.id.shortshirt){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Sports T-Shirt");
            startActivity(intent);
        }else if (v.getId()==R.id.female_dress){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Female Dress");
            startActivity(intent);
        }else if (v.getId()==R.id.sweather){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Sweater");
            startActivity(intent);
        }else if (v.getId()==R.id.glasses){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Glasses");
            startActivity(intent);
        }else if (v.getId()==R.id.hats){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Hats");
            startActivity(intent);
        }else if (v.getId()==R.id.purses_bags){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Purses Bags");
            startActivity(intent);
        }else if (v.getId()==R.id.shoess){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Shoes");
            startActivity(intent);
        }else if (v.getId()==R.id.headphoness){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Head Phones");
            startActivity(intent);
        }else if (v.getId()==R.id.laptops){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Laptops");
            startActivity(intent);
        }else if (v.getId()==R.id.watches){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Watches");
            startActivity(intent);
        }else if (v.getId()==R.id.mobiles){
            Intent intent=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
            intent.putExtra("category","Mobiles");
            startActivity(intent);
        }
    }
}