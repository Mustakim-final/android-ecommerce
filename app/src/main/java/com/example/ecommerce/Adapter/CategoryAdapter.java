package com.example.ecommerce.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Model.Product;
import com.example.ecommerce.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyHodlder>{

    Context context;
    String[] title;
    int[] image;

    public CategoryAdapter(Context context, String[] title, int[] image) {
        this.context = context;
        this.title = title;
        this.image = image;
    }

    @NonNull
    @Override
    public MyHodlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.category_item,parent,false);
        return new MyHodlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHodlder holder, int position) {
        holder.imageView.setImageResource(image[position]);
        holder.categoryText.setText(title[position]);
    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    public class MyHodlder extends RecyclerView.ViewHolder{
        TextView categoryText;
        ImageView imageView;
        public MyHodlder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.categoryImage);
            categoryText=itemView.findViewById(R.id.categoryName);
        }
    }
}
