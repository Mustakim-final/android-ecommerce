package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Adapter.CategoryAdapter;
import com.example.ecommerce.Adapter.ProductAdapter;
import com.example.ecommerce.EventBus.MyUpdateCartEvent;
import com.example.ecommerce.Listener.ICartListener;
import com.example.ecommerce.Listener.IProductListener;
import com.example.ecommerce.Model.CartModel;
import com.example.ecommerce.Model.Product;
import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.Util.SpaceItemDecoration;
import com.google.android.material.navigation.NavigationView;
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

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IProductListener, ICartListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

    RecyclerView categoryRecycler,productRecycler;

    int[] categoryImage={
            R.drawable.tshirts,R.drawable.sports,R.drawable.female_dresses,R.drawable.sweather,
            R.drawable.glasses,R.drawable.hats,R.drawable.purses_bags,R.drawable.shoess,
            R.drawable.headphoness,R.drawable.laptops,R.drawable.watches,R.drawable.mobiles
    };

    String[] category;
    CategoryAdapter categoryAdapter;

    IProductListener iProductListener;
    ICartListener iCartListener;
    NotificationBadge badge;
    FrameLayout btnCart;

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

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar=findViewById(R.id.toolbar_ID);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category=getResources().getStringArray(R.array.category);

        categoryRecycler=findViewById(R.id.productCategoryRecycler_ID);

        categoryAdapter=new CategoryAdapter(HomeActivity.this,category,categoryImage);

        categoryRecycler.setHasFixedSize(true);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.HORIZONTAL,false));
        categoryRecycler.setAdapter(categoryAdapter);

        drawerLayout=findViewById(R.id.drawer_ID);



        toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.navigation_ID);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        TextView userNameText=headerView.findViewById(R.id.user_profile_name);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
                userNameText.setText(users.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnCart=findViewById(R.id.btnCart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

        productRecycler=findViewById(R.id.productRecycler_ID);
        badge=findViewById(R.id.badge);
        initial();
        LoadProductFormFirebase();
        countCartItem();

    }

    private void LoadProductFormFirebase() {
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
                                productList.add(product);
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
        productRecycler.setLayoutManager(gridLayoutManager);
        productRecycler.addItemDecoration(new SpaceItemDecoration());
    }


    @Override
    public void onProductLoadSuccess(List<Product> productList) {
        ProductAdapter productAdapter=new ProductAdapter(HomeActivity.this,productList,iCartListener);
        productRecycler.setAdapter(productAdapter);
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
        Toast.makeText(HomeActivity.this,message,Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.nav_logout){
           FirebaseAuth.getInstance().signOut();
           finish();
        }
        return false;
    }


}