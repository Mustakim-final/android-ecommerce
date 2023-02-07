package com.example.ecommerce.Listener;


import com.example.ecommerce.Model.CartModel;


import java.util.List;

public interface ICartListener {

    void onCartLoadSuccess(List<CartModel> cartModelList);
    void  onCartLoadFailed(String message);
}
