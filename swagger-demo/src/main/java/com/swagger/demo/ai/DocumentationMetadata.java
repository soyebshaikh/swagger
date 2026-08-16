package com.swagger.demo.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentationMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    private String summary;
    private String description;
    private List<String> tags = new ArrayList<>();
    private Map<String, String> parameterDescriptions = new HashMap<>();
    private Map<String, String> fieldDescriptions = new HashMap<>();
    private Map<String, Object> exampleValues = new HashMap<>();

    public DocumentationMetadata() {}

    public DocumentationMetadata(String summary, String description) {
        this.summary = summary;
        this.description = description;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Map<String, String> getParameterDescriptions() { return parameterDescriptions; }
    public void setParameterDescriptions(Map<String, String> parameterDescriptions) { this.parameterDescriptions = parameterDescriptions; }

    public Map<String, String> getFieldDescriptions() { return fieldDescriptions; }
    public void setFieldDescriptions(Map<String, String> fieldDescriptions) { this.fieldDescriptions = fieldDescriptions; }

    public Map<String, Object> getExampleValues() { return exampleValues; }
    public void setExampleValues(Map<String, Object> exampleValues) { this.exampleValues = exampleValues; }

    @Override
    public String toString() {
        return "DocumentationMetadata{" +
                "summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                '}';
    }
}
