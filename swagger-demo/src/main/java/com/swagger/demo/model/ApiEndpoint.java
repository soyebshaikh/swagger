package com.swagger.demo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiEndpoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private String controllerClass;
    private String controllerPackage;
    private String methodName;
    private String httpMethod;
    private String path;
    private List<ApiParameter> parameters = new ArrayList<>();
    private String requestType;
    private String responseType;

    public ApiEndpoint() {}

    public ApiEndpoint(String controllerClass, String controllerPackage, String methodName, String httpMethod, String path, String requestType, String responseType) {
        this.controllerClass = controllerClass;
        this.controllerPackage = controllerPackage;
        this.methodName = methodName;
        this.httpMethod = httpMethod;
        this.path = path;
        this.requestType = requestType;
        this.responseType = responseType;
    }

    public String getControllerClass() { return controllerClass; }
    public void setControllerClass(String controllerClass) { this.controllerClass = controllerClass; }

    public String getControllerPackage() { return controllerPackage; }
    public void setControllerPackage(String controllerPackage) { this.controllerPackage = controllerPackage; }

    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public List<ApiParameter> getParameters() { return parameters; }
    public void setParameters(List<ApiParameter> parameters) { this.parameters = parameters; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getResponseType() { return responseType; }
    public void setResponseType(String responseType) { this.responseType = responseType; }

    @Override
    public String toString() {
        return String.format("%-6s %-32s -> %s.%s()", httpMethod, path, controllerClass, methodName);
    }

    public static class ApiParameter implements Serializable {
        private String name;
        private String type;
        private String annotationType; // PathVariable, RequestParam, RequestBody, QueryParam, PathParam
        private boolean required;

        public ApiParameter() {}

        public ApiParameter(String name, String type, String annotationType, boolean required) {
            this.name = name;
            this.type = type;
            this.annotationType = annotationType;
            this.required = required;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getAnnotationType() { return annotationType; }
        public void setAnnotationType(String annotationType) { this.annotationType = annotationType; }

        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }

        @Override
        public String toString() {
            return name + " (" + type + ", @" + annotationType + ")";
        }
    }
}
