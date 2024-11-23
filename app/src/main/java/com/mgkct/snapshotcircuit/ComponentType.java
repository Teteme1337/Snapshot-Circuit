package com.mgkct.snapshotcircuit;

import java.util.List;

public class ComponentType {

    private int id;
    private String type_name;
    private String type_description;
    private String type_image;

    private List<ComponentSubtype> subtypes;

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public String getType_name() {
        return type_name;
    }

    public String getType_description() {
        return type_description;
    }

    public String getType_image() {
        return type_image;
    }

    public List<ComponentSubtype> getSubtypes() {
        return subtypes;
    }

    public static class ComponentSubtype {
        private int id;
        private String subtype_name;

        // Геттеры и сеттеры
        public int getSubtype_id() {
            return id;
        }

        public String getSubtype_name() {
            return subtype_name;
        }
    }
}