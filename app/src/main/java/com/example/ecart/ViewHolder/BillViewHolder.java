package com.example.ecart.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecart.Interface.ItemClickListener;
import com.example.ecart.R;

import org.jetbrains.annotations.NotNull;

public class BillViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    public TextView txtOrderUserName, txtOrderPhoneNum, txtOrderTotalPrice, txtOrderAddress, txtOrderDateTime;
    public Button btnShowProducts;
    private ItemClickListener itemClickListener;

    public BillViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderUserName = itemView.findViewById(R.id.txtOrderUserName);
        txtOrderPhoneNum = itemView.findViewById(R.id.txtOrderPhoneNum);
        txtOrderTotalPrice = itemView.findViewById(R.id.txtOrderTotalPrice);
        txtOrderAddress = itemView.findViewById(R.id.txtOrderAddress);
        txtOrderDateTime = itemView.findViewById(R.id.txtOrderDateTime);
        btnShowProducts = itemView.findViewById(R.id.btnShowProducts);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
