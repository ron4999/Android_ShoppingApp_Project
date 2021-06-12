package com.example.ecart.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecart.Interface.ItemClickListener;
import com.example.ecart.R;

import org.jetbrains.annotations.NotNull;

public class CartViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    public TextView txtCartPName, txtCartPPrice, txtCartPQuantity;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        txtCartPName = itemView.findViewById(R.id.txtCartPName);
        txtCartPPrice = itemView.findViewById(R.id.txtCartPPrice);
        txtCartPQuantity = itemView.findViewById(R.id.txtCartPQuantity);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
