package com.mgkct.snapshotcircuit;

public class CategoryItem {

    private String title;
    private String img;

    // Конструктор
    public CategoryItem(String title, String img) {
        this.title = title;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }
}
