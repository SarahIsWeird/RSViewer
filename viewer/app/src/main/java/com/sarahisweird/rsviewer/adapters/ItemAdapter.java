package com.sarahisweird.rsviewer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sarahisweird.rsviewer.Item;
import com.sarahisweird.rsviewer.R;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter {
    Context context;
    public List<Item> items;
    public List<Item> displayedItems;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView count;
        TextView mod;

        public ItemViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
            mod = itemView.findViewById(R.id.mod);
        }
    }

    public ItemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;

        this.displayedItems = new ArrayList<>();

        this.displayedItems.addAll(this.items);
    }

    public void filterItems(String filterString) {
        this.displayedItems = new ArrayList<>();

        if (filterString.trim().equals("")) {
            this.displayedItems.addAll(this.items);

            this.notifyDataSetChanged();

            return;
        }

        for (Item item : this.items) {
            if (item.label.toLowerCase().contains(filterString.toLowerCase())) {
                this.displayedItems.add(item);
            }
        }

        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_layout, parent, false);

        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder vh = (ItemViewHolder) holder;
        vh.name.setText(displayedItems.get(position).label);
        vh.count.setText(String.valueOf(displayedItems.get(position).size));
        vh.mod.setText("(" + displayedItems.get(position).name.split(":")[0] + ")");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = displayedItems.get(position);

                String text = item.name;

                if (item.damage > 0)
                    text += "\n" + String.format(context.getString(R.string.damage_value), item.damage);

                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return displayedItems != null ? displayedItems.size() : 0;
    }
}
