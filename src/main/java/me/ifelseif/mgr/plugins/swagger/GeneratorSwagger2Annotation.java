package me.ifelseif.mgr.plugins.swagger;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.*;

public class GeneratorSwagger2Annotation extends PluginAdapter {

    private Map<String, List<String>> swaggerHiddenFieldMap = new HashMap<String, List<String>>();

    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        String swaggerHiddenField = properties.getProperty("swaggerHiddenField");
        if (swaggerHiddenField != null) {
            String[] tableField = swaggerHiddenField.split(",");
            for (String s : tableField) {
                String[] field = s.split("\\.");
                List<String> fieldList = swaggerHiddenFieldMap.get(field[0]);
                if (fieldList == null) {
                    fieldList = new ArrayList<String>();
                }
                if (!fieldList.contains(field[1])) {
                    fieldList.add(field[1]);
                }
                swaggerHiddenFieldMap.put(field[0], fieldList);
            }
        }
        super.setProperties(properties);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String classAnnotation = "@ApiModel()";
        if (!topLevelClass.getAnnotations().contains(classAnnotation)) {
            topLevelClass.addAnnotation(classAnnotation);
        }

        topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
        topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
        topLevelClass.addImportedType("javax.validation.constraints.NotNull");
        topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonIgnore");


        String value;
        if (introspectedColumn.getRemarks() == null || introspectedColumn.getRemarks().equals("")) {
            value = introspectedColumn.getJavaProperty();
        } else {
            value = introspectedColumn.getRemarks();
        }
        boolean hidden = false;
        List<String> hiddenField = swaggerHiddenFieldMap.get(introspectedTable.getTableConfiguration().getTableName());
        if (hiddenField != null && hiddenField.contains(introspectedColumn.getActualColumnName())) {
            hidden = true;
        }
        if (hidden) {
            field.addAnnotation("@JsonIgnore");
            field.addAnnotation("@ApiModelProperty(value=\"" + value + "\", hidden = " + hidden + ")");
        } else {
            field.addAnnotation("@ApiModelProperty(value=\"" + value + "\")");
        }

        //判断是否not null
        if (hidden && !introspectedColumn.isNullable() && !introspectedColumn.getJavaProperty().equals("id")) {
            field.addAnnotation("@NotNull(message = \"" + introspectedColumn.getJavaProperty() + "不能为空" + "\")");
        }

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }
}
