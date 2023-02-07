package com.example.ecommerce.Listener;


import com.example.ecommerce.Model.Product;

import java.util.List;

public interface IProductListener {
    void onProductLoadSuccess(List<Product> productList);
    void  onProductLoadFailed(String message);
}
