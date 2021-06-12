package com.example.ecart.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ecart.Interface.ItemClickListener;
import com.example.ecart.R;

public class ProductViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    public TextView txtProductName, txtProductDes, txtProductPrice;
    public ImageView imgProductImg;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.txtProductName);
        txtProductDes = itemView.findViewById(R.id.txtProductDes);
        imgProductImg = itemView.findViewById(R.id.imgProductImg);
        txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
