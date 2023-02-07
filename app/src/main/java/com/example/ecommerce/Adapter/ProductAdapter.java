package com.example.ecommerce.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.EventBus.MyUpdateCartEvent;
import com.example.ecommerce.Listener.ICartListener;
import com.example.ecommerce.Listener.IRecyclerListener;
import com.example.ecommerce.Model.CartModel;
import com.example.ecommerce.Model.Product;
import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyHolder>{
    Context context;
    List<Product> productList;
    ICartListener iCartListener;

    private String prentDbName="Users";

    public ProductAdapter(Context context, List<Product> productList, ICartListener iCartListener) {
        this.context = context;
        this.productList = productList;
        this.iCartListener = iCartListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.product_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Product product=productList.get(position);

        if (product.getImage().equals("")){
            holder.productImage.setImageResource(R.drawable.cart);
        }else {
            Glide.with(context).load(product.getImage()).into(holder.productImage);
        }

        holder.productNameText.setText(product.getPname());
        holder.productDescriptionText.setText(product.getDescription());
        holder.productPriceText.setText(product.getPrice());

        holder.setiRecyclerListener(new IRecyclerListener() {
            @Override
            public void OnRecyclerCLick(View view, int position) {
                addCart(productList.get(position));
            }
        });
    }

    private void addCart(Product product) {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String myid=firebaseUser.getUid();
        DatabaseReference userCart= FirebaseDatabase.getInstance().getReference("Cart").child(myid);
        userCart.child(product.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            CartModel cartModel=snapshot.getValue(CartModel.class);
                            Map<String,Object> updateData=new HashMap<>();
                            updateData.put("quantity",cartModel.getQuantity());
                            updateData.put("totalPrice",cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));
                            userCart.child(product.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            iCartListener.onCartLoadFailed("Already Add");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            iCartListener.onCartLoadFailed(e.getMessage());
                                        }
                                    });
                        }else {

                            CartModel cartModel=new CartModel();
                            cartModel.setName(product.getPname());
                            cartModel.setImage(product.getImage());
                            cartModel.setKey(product.getKey());
                            cartModel.setPrice(product.getPrice());
                            cartModel.setQuantity(1);
                            cartModel.setTotalPrice(Float.parseFloat(product.getPrice()));

                            userCart.child(product.getKey())
                                    .setValue(cartModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            iCartListener.onCartLoadFailed("Add to Cart Success");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            iCartListener.onCartLoadFailed(e.getMessage());
                                        }
                                    });
                        }

                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartListener.onCartLoadFailed(error.getMessage());
                    }
                });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView productImage;
        TextView productNameText,productPriceText,productDescriptionText;

        IRecyclerListener iRecyclerListener;

        public void setiRecyclerListener(IRecyclerListener iRecyclerListener) {
            this.iRecyclerListener = iRecyclerListener;
        }

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.productImage_ID);
            productNameText=itemView.findViewById(R.id.productName_ID);
            productPriceText=itemView.findViewById(R.id.productPrize_ID);
            productDescriptionText=itemView.findViewById(R.id.product_description_ID);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerListener.OnRecyclerCLick(v,getAdapterPosition());
        }
    }
}
