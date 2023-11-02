package com.example.coenelec390.ui.item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coenelec390.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
    private Context context;
    private List<Item> items;

    public ItemAdapter(Context context, List<Item> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
        Item item = items.get(position);
        String url = item.getImageURL();
        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ivProduct);
        holder.tvId.setText("Id: " + item.getId());
        holder.tvDescription.setText(item.getDescription());
        holder.tvStock.setText(item.getStock());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvId, tvDescription, tvStock;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = (ImageView) itemView.findViewById(R.id.ivProduct);
            tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvStock = (TextView) itemView.findViewById(R.id.tvStock);
        }
    }
}
