package com.example.babyneeds.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babyneeds.R;
import com.example.babyneeds.data.DatabaseHandler;
import com.example.babyneeds.model.Item;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    public RecyclerViewAdapter(Context context , List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_row , parent , false);
        return new ViewHolder(view , context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position); //Object Item
        holder.itemName.setText(MessageFormat.format("Activity: {0}", item.getItemName()));
        holder.itemColor.setText(MessageFormat.format("Status: {0}", item.getItemColor()));
        holder.quantity.setText(MessageFormat.format("Time: {0}", String.valueOf(item.getItemQuantity())));
        holder.size.setText(MessageFormat.format("Duration: {0}", String.valueOf(item.getItemSize())));
        holder.dateAdded.setText(MessageFormat.format("Added on: {0}", item.getDateItemAdded()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName;
        public TextView itemColor;
        public TextView quantity;
        public TextView size;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;
        public int id;
        public ViewHolder(@NonNull View itemView,Context ctx)
        {
            super(itemView);
            context = ctx;
            itemName = itemView.findViewById(R.id.item_name);
            itemColor = itemView.findViewById(R.id.item_color);
            quantity = itemView.findViewById(R.id.item_quantity);
            size = itemView.findViewById(R.id.item_size);
            dateAdded = itemView.findViewById(R.id.item_date);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Item item= itemList.get(position);
            switch (view.getId()){
                case R.id.editButton:
                    //edit
                    editItem(item);
                    break;
                case R.id.deleteButton:
                    //Delete function invoked
                    deleteItem(item.getId());
                    break;
            }
        }

        private void editItem(final Item newItem) {
            Button saveButton;
            final EditText babyItem;
            final EditText itemQuantity;
            final EditText itemColor;
            final EditText itemSize;
            TextView title;
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup,null);
            babyItem = view.findViewById(R.id.baby_items);
            itemQuantity= view.findViewById(R.id.itemQuantity);
            itemColor = view.findViewById(R.id.itemColor);
            itemSize = view.findViewById(R.id.itemSize);
            saveButton = view.findViewById(R.id.saveButton);
            saveButton.setText(R.string.update_txt);
            title = view.findViewById(R.id.title);
            title.setText(R.string.edit_title);
            babyItem.setText(newItem.getItemName());
            itemQuantity.setText(String.valueOf(newItem.getItemQuantity()));
            itemColor.setText(newItem.getItemColor());
            itemSize.setText(String.valueOf(newItem.getItemSize()));
            builder.setView(view);
            dialog = builder.create();
            dialog.show();
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //update item
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);
                    newItem.setItemName(babyItem.getText().toString());
                    newItem.setItemQuantity(itemQuantity.getText().toString());
                    newItem.setItemColor(itemColor.getText().toString());
                    newItem.setItemSize(itemSize.getText().toString());
                    if (!babyItem.getText().toString().isEmpty()
                    &&!itemQuantity.getText().toString().isEmpty()
                    &&!itemColor.getText().toString().isEmpty()
                    &&!itemSize.getText().toString().isEmpty()){
                        databaseHandler.updateItem(newItem);
                        notifyItemChanged(getAdapterPosition(),newItem);
                    }else {
                        Snackbar.make(view,"Fields Empty!",Snackbar.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });
        }

        public void deleteItem(final int id){
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_popup,null);
            Button noButton = view.findViewById(R.id.conf_no_button);
            Button yesButton = view.findViewById(R.id.conf_yes_button);
            builder.setView(view);
            dialog = builder.create();
            dialog.show();
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });
        }
    }
}
