package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ecommerce.Adapter.MyCartAdapter;
import com.example.ecommerce.EventBus.MyUpdateCartEvent;
import com.example.ecommerce.Listener.ICartListener;
import com.example.ecommerce.Model.CartModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ICartListener {


    RecyclerView recyclerCart;
    RelativeLayout mainLayout;
    NotificationBadge badge;
    ImageView btnBack;
    TextView textTotal,total;
    ExtendedFloatingActionButton fl;

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
        loadCartFormFirebase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerCart=findViewById(R.id.recycler_Cart);
        btnBack=findViewById(R.id.back);
        textTotal=findViewById(R.id.textTotal);
        total=findViewById(R.id.total);
        fl=findViewById(R.id.checkOutBtn);
        badge=findViewById(R.id.badge);

        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartActivity.this,CheckOutActivity.class);
                intent.putExtra("total",total.getText().toString());
                startActivity(intent);
            }
        });

        init1();
        loadCartFormFirebase();
    }

    private void loadCartFormFirebase() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String myId=firebaseUser.getUid();

        List<CartModel> cartModelList=new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(myId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                CartModel cartModel=dataSnapshot.getValue(CartModel.class);
                                cartModelList.add(cartModel);
                            }

                            iCartListener.onCartLoadSuccess(cartModelList);
                        }else {
                            iCartListener.onCartLoadFailed("Cart Empty");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    private void init1() {
        iCartListener=this;
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));
        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        double sum=0;
        for (CartModel cartModel:cartModelList){
            sum+=cartModel.getTotalPrice();
        }

        int qn=0;
        for (CartModel cartModel:cartModelList){
            qn+=cartModel.getQuantity();
        }
        textTotal.setText(new StringBuilder("টাকা ").append(sum));
        total.setText(String.valueOf(sum));
        badge.setNumber(qn);

        MyCartAdapter myCartAdapter=new MyCartAdapter(this,cartModelList);
        recyclerCart.setAdapter(myCartAdapter);
    }

    @Override
    public void onCartLoadFailed(String message) {

    }
}