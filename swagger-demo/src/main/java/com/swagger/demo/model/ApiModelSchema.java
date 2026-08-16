package com.swagger.demo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApiModelSchema implements Serializable {
    private static final long serialVersionUID = 1L;

    private String className;
    private String packageName;
    private List<ModelField> fields = new ArrayList<>();

    public ApiModelSchema() {}

    public ApiModelSchema(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
    }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public List<ModelField> getFields() { return fields; }
    public void setFields(List<ModelField> fields) { this.fields = fields; }

    public Optional<ModelField> getField(String fieldName) {
        return fields.stream().filter(f -> f.getName().equals(fieldName)).findFirst();
    }

    @Override
    public String toString() {
        return "ApiModelSchema{" +
                "className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", fieldsCount=" + fields.size() +
                '}';
    }
}
