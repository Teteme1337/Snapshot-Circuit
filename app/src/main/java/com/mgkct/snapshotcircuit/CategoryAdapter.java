package com.mgkct.snapshotcircuit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryItem> categoryItemList;
    private Context context;
    private OnItemClickListener listener; // Листенер для кликов

    // Интерфейс для обработки кликов
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Метод для установки слушателя
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public CategoryAdapter(Context context, List<CategoryItem> categoryItemList) {
        this.context = context;
        this.categoryItemList = categoryItemList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view, listener); // передаем listener
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem item = categoryItemList.get(position);
        holder.titleTextView.setText(item.getTitle());

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();

        if (position == 0) {
            layoutParams.topMargin = context.getResources().getDimensionPixelSize(R.dimen.last_item_margin);
        } else {
            layoutParams.topMargin = 0;
        }

        holder.itemView.setLayoutParams(layoutParams);

        // Используем Glide для загрузки изображения по URL или ресурсу
        Glide.with(context)
                .load(item.getImg())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView imageView;

        public CategoryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.image);

            // Устанавливаем клик-листенер на itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position); // передаем позицию
                        }
                    }
                }
            });
        }
    }
}
