package com.example.cuoiki.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuoiki.database.DatabaseHelper;

import com.example.cuoiki.R;
import com.example.cuoiki.model.Category;

import java.util.ArrayList;

public class CategoryAdapter
        extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final ArrayList<Category> categoryList;
    private final OnCategoryActionListener listener;
    public interface OnCategoryActionListener {

        void onEdit(Category category);

        void onDelete(Category category);
    }

    public CategoryAdapter(
            ArrayList<Category> categoryList,
            OnCategoryActionListener listener
    ) {
        this.categoryList = categoryList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_category,
                        parent,
                        false
                );

        return new CategoryViewHolder(view);
    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }
    @Override
    public void onBindViewHolder(
            @NonNull CategoryViewHolder holder,
            int position
    ) {

        Category category = categoryList.get(position);


        holder.txtCategoryName.setText(
                category.getName()
        );

        if (category.getType().equals(DatabaseHelper.TYPE_INCOME)) {

            holder.txtCategoryType.setText(
                    "Thu nhập"
            );

        } else {

            holder.txtCategoryType.setText(
                    "Chi tiêu"
            );
        }
        holder.btnEditCategory.setOnClickListener(v ->
                listener.onEdit(category)
        );

        holder.btnDeleteCategory.setOnClickListener(v ->
                listener.onDelete(category)
        );
    }
    public static class CategoryViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtCategoryName;
        TextView txtCategoryType;

        ImageButton btnEditCategory;
        ImageButton btnDeleteCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCategoryName =
                    itemView.findViewById(R.id.txtCategoryName);

            txtCategoryType =
                    itemView.findViewById(R.id.txtCategoryType);

            btnEditCategory =
                    itemView.findViewById(R.id.btnEditCategory);

            btnDeleteCategory =
                    itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
