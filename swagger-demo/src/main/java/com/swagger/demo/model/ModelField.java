package com.swagger.demo.model;

import java.io.Serializable;

public class ModelField implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String type;
    private String genericType;
    private boolean collection;
    private boolean map;
    private boolean nestedModel;
    private boolean nullable;
    private boolean hasGetter;
    private boolean hasSetter;

    public ModelField() {}

    public ModelField(String name, String type, String genericType, boolean collection, boolean map, boolean nestedModel, boolean nullable, boolean hasGetter, boolean hasSetter) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.collection = collection;
        this.map = map;
        this.nestedModel = nestedModel;
        this.nullable = nullable;
        this.hasGetter = hasGetter;
        this.hasSetter = hasSetter;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getGenericType() { return genericType; }
    public void setGenericType(String genericType) { this.genericType = genericType; }

    public boolean isCollection() { return collection; }
    public void setCollection(boolean collection) { this.collection = collection; }

    public boolean isMap() { return map; }
    public void setMap(boolean map) { this.map = map; }

    public boolean isNestedModel() { return nestedModel; }
    public void setNestedModel(boolean nestedModel) { this.nestedModel = nestedModel; }

    public boolean isNullable() { return nullable; }
    public void setNullable(boolean nullable) { this.nullable = nullable; }

    public boolean isHasGetter() { return hasGetter; }
    public void setHasGetter(boolean hasGetter) { this.hasGetter = hasGetter; }

    public boolean isHasSetter() { return hasSetter; }
    public void setHasSetter(boolean hasSetter) { this.hasSetter = hasSetter; }

    @Override
    public String toString() {
        return "ModelField{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", genericType='" + genericType + '\'' +
                ", collection=" + collection +
                ", nestedModel=" + nestedModel +
                ", nullable=" + nullable +
                ", getter=" + hasGetter +
                ", setter=" + hasSetter +
                '}';
    }
}
