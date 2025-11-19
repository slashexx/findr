package com.shruti.lofo.ui.Lost;

import android.content.Context;
import com.bumptech.glide.Glide;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shruti.lofo.R;

import java.util.List;

public class LostItemsAdapter extends RecyclerView.Adapter<LostItemsAdapter.ItemViewHolder> {

    Context context;
    private List<LostItems> items;
    private boolean showDeleteButton;

    public LostItemsAdapter(Context context, List<LostItems> items, boolean showDeleteButton) {
        this.context = context;
        this.items = items;
        this.showDeleteButton = showDeleteButton;
    }

    public void setItems(List<LostItems> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_item_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        LostItems item = items.get(position);

        if (item.getImageURI() != null && !item.getImageURI().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageURI())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.baseline_image_search_24)
                    .into(holder.itemImageView);
        }

        holder.itemNameTextView.setText(item.getItemName());
        holder.ownerNameTextView.setText(item.getOwnerName());
        holder.description.setText(item.getDescription());
        holder.location.setText(item.getLocation());
        holder.date.setText(item.getDateLost());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LostDetails.class);
            intent.putExtra("itemId", item.getItemName());
            context.startActivity(intent);
        });

        if (showDeleteButton) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                 // Handle delete
                 // For now, just show a toast as LocalStore delete implementation would require a callback
                 Toast.makeText(context, "Delete not implemented locally yet", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView ownerNameTextView;
        TextView description;
        TextView location;
        TextView date;
        ImageButton deleteButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            ownerNameTextView = itemView.findViewById(R.id.ownerNameTextView);
            description = itemView.findViewById(R.id.item_description);
            location = itemView.findViewById((R.id.location));
            date = itemView.findViewById(R.id.dateLost);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
