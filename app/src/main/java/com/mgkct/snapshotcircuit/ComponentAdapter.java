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
import java.util.ArrayList;
import java.util.List;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ViewHolder> {

    private List<Component> components;
    private List<Component> originalComponents; // Для хранения оригинального списка
    private Context context;
    private OnItemClickListener onItemClickListener;

    // Интерфейс для обработки кликов
    public interface OnItemClickListener {
        void onItemClick(int componentId);
    }

    // Конструктор, принимающий слушателя
    public ComponentAdapter(Context context, List<Component> components, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.components = components;
        this.originalComponents = new ArrayList<>(components); // Сохраняем оригинальный список для фильтрации
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Component component = components.get(position);
        holder.title.setText(component.getTitle());
        holder.subtype.setText(component.getSubtype().getSubtypeName());

        // Установка отступов для первого элемента
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (position == 0) {
            layoutParams.topMargin = this.context.getResources().getDimensionPixelSize(R.dimen.last_item_margin);
        } else {
            layoutParams.topMargin = 0;
        }
        holder.itemView.setLayoutParams(layoutParams);

        // Загрузка изображения через Glide
        Glide.with(holder.photo.getContext())
                .load(component.getComponentPhoto())
                .centerInside()
                .into(holder.photo);

        // Обработчик клика по элементу
        holder.itemView.setOnClickListener(v -> {
            // Вызываем слушателя и передаем ID компонента
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(component.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    // Метод для обновления списка компонентов
    public void updateList(List<Component> newList) {
        components = newList;
        notifyDataSetChanged();
    }

    // Метод для фильтрации списка по запросу
    public void filterList(String query) {
        List<Component> filteredList = new ArrayList<>();
        for (Component component : originalComponents) {
            // Фильтруем по названию компонента (сравниваем без учета регистра)
            if (component.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(component);
            }
        }
        updateList(filteredList); // Обновляем список в адаптере
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtype;
        ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtype = itemView.findViewById(R.id.subtype);
            photo = itemView.findViewById(R.id.photo);
        }
    }
}