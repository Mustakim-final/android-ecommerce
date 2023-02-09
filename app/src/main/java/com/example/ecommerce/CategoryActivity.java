package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.ecommerce.Adapter.ProductAdapter;
import com.example.ecommerce.EventBus.MyUpdateCartEvent;
import com.example.ecommerce.Listener.ICartListener;
import com.example.ecommerce.Listener.IProductListener;
import com.example.ecommerce.Model.CartModel;
import com.example.ecommerce.Model.Product;
import com.example.ecommerce.Util.SpaceItemDecoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements IProductListener, ICartListener {
    Toolbar toolbar;
    Intent intent;
    RecyclerView recyclerViewCategory;
    List<Product> productList;
    DatabaseReference reference;

    ProductAdapter productAdapter;

    SearchView search;


    NotificationBadge badge;
    FrameLayout btnCart;

    IProductListener iProductListener;
    ICartListener iCartListener;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class)){
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)

    public void onUpdateCart(MyUpdateCartEvent event){
        countCartItem();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        intent=getIntent();
        String category=intent.getStringExtra("category");

        recyclerViewCategory=findViewById(R.id.categoryRecycler);
        badge=findViewById(R.id.badge);
        btnCart=findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


        initial();
        LoadDrinkFormFirebase(category);
        countCartItem();
    }
    private void LoadDrinkFormFirebase(String category) {
        List<Product> productList=new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("product")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                Product product=dataSnapshot.getValue(Product.class);
                                product.setKey(dataSnapshot.getKey());
                                if (product.getCategory().equals(category)){
                                    productList.add(product);
                                }

                            }
                            iProductListener.onProductLoadSuccess(productList);
                        }else {
                            iProductListener.onProductLoadFailed("Not product find");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void initial() {
        iProductListener=this;
        iCartListener=this;

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerViewCategory.setLayoutManager(gridLayoutManager);
        recyclerViewCategory.addItemDecoration(new SpaceItemDecoration());
    }


    @Override
    public void onProductLoadSuccess(List<Product> productList) {
        ProductAdapter productAdapter=new ProductAdapter(CategoryActivity.this,productList,iCartListener);
        recyclerViewCategory.setAdapter(productAdapter);
    }

    @Override
    public void onProductLoadFailed(String message) {

    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int sum=0;
        for (CartModel cartModel:cartModelList)
            sum+=cartModel.getQuantity();
        badge.setNumber(sum);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Toast.makeText(CategoryActivity.this,message,Toast.LENGTH_SHORT).show();
    }

    private void countCartItem() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String myid=firebaseUser.getUid();
        List<CartModel> cartModelList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Cart").child(myid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            CartModel cartModel=dataSnapshot.getValue(CartModel.class);
                            cartModel.setKey(dataSnapshot.getKey());
                            cartModelList.add(cartModel);
                        }
                        iCartListener.onCartLoadSuccess(cartModelList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}