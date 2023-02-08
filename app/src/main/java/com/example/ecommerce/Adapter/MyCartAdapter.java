package com.example.ecommerce.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.EventBus.MyUpdateCartEvent;
import com.example.ecommerce.Model.CartModel;
import com.example.ecommerce.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyHolder>{
    Context context;
    List<CartModel> cartModelList;
    FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    String myId=firebaseUser.getUid();

    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.cart_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        CartModel cartModel=cartModelList.get(position);
        Glide.with(context).load(cartModel.getImage()).into(holder.imageView);
        holder.textName.setText(cartModel.getName());
        holder.textPrice.setText(cartModel.getPrice()+" টাকা");
        holder.textQuantity.setText(new StringBuilder().append(cartModelList.get(position).getQuantity()));

        holder.minusBtn.setOnClickListener(view -> {
            minusCartItem(holder,cartModelList.get(position));
        });

        holder.plusBtn.setOnClickListener(view -> {
            plusCartItem(holder,cartModelList.get(position));
        });

        holder.deleteBtn.setOnClickListener(v->{
            AlertDialog dialog;
            dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Do you really want to delete item")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        notifyItemRemoved(position);
                        deleteFormFireBase(cartModelList.get(position));
                        dialogInterface.dismiss();
                    }).create();
            dialog.show();
        });
    }

    private void deleteFormFireBase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(myId)
                .child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid-> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    private void plusCartItem(MyHolder holder, CartModel cartModel) {
        cartModel.setQuantity(cartModel.getQuantity()+1);
        cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

        holder.textQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
        updateFirebase(cartModel);
    }

    private void minusCartItem(MyHolder holder, CartModel cartModel) {
        if (cartModel.getQuantity()>1){
            cartModel.setQuantity(cartModel.getQuantity()-1);
            cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

            holder.textQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
            updateFirebase(cartModel);
        }
    }

    private void updateFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(myId)
                .child(cartModel.getKey())
                .setValue(cartModel)
                .addOnSuccessListener(aVoid-> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView minusBtn,plusBtn,deleteBtn,imageView;
        TextView textName,textPrice,textQuantity;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            minusBtn=itemView.findViewById(R.id.btnMinus);
            plusBtn=itemView.findViewById(R.id.btnPlus);
            deleteBtn=itemView.findViewById(R.id.btnDelete);
            imageView=itemView.findViewById(R.id.imageView);
            textName=itemView.findViewById(R.id.textName);
            textPrice=itemView.findViewById(R.id.textPrice);
            textQuantity=itemView.findViewById(R.id.textQuantity);
        }
    }
}
