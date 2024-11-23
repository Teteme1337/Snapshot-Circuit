package com.mgkct.snapshotcircuit;

import java.util.List;

public class Component {
    private int id;
    private String title;
    private String component_photo;
    private String description;
    private String documentation_name;
    private int subtype_id;
    private Subtype subtype; // Объект подтипа
    private List<Property> component_properties;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getComponentPhoto() { return component_photo; }
    public String getDescription() { return description; }
    public String getDocumentationName() { return documentation_name; }
    public int getSubtypeId() { return subtype_id; }
    public Subtype getSubtype() { return subtype; } // Получить объект подтипа
    public List<Property> getComponentProperties() { return component_properties; }

    public static class Subtype {
        private String subtype_name; // Имя подтипа

        public String getSubtypeName() { return subtype_name; }
    }

    public static class Property {
        private String property_name;
        private String property_value;

        public String getPropertyName() { return property_name; }
        public String getPropertyValue() { return property_value; }
    }
}