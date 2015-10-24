package com.diorsunion.dbtest.db;

import com.diorsunion.dbtest.annotation.DataSet;
import com.diorsunion.dbtest.enums.ColumnType;
import com.diorsunion.dbtest.util.ColumnObject;
import com.google.common.collect.Sets;
import org.apache.ibatis.type.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author harley-dog
 */
public interface SqlBuilder {

    Set<Class> classSet = Sets.newHashSet(String.class,Integer.class,int.class,Long.class,long.class,Short.class,Date.class,double.class);
    TypeHandlerRegistry registry = new TypeHandlerRegistry();

    /**
     * Gets the value.
     *
     * @param tableName 表名
     * @param clazz 表所对应的类名
     * @return the value
     */
    String getCreateTable(String tableName,Class clazz);

    String getValue(ColumnObject columnObject,int index);

    /* (non-Javadoc)
	 * @see com.taobao.obunit.db.SqlBuilder#getValue(com.taobao.obunit.ColumnType, java.lang.String, int)
	 */
    static String getDefaultValue(ColumnObject columnObject,int index){
        switch (columnObject.valueType) {
            case NULL:
                return "null";
            case BLANK:
                return "''";
            case VARCHAR:
                if(columnObject.increase){
                    if(columnObject.columnSize == 0){
                        return "'"+columnObject.value+(index*columnObject.step+1)+"'";
                    }
                    int endIndex = columnObject.columnSize - (int) Math.log10(index*columnObject.step+1) -1;
                    return "'"+columnObject.value.substring(0, columnObject.value.length() > endIndex ? endIndex : columnObject.value.length())+(index*columnObject.step+1)+"'";
                }
                else{
                    if(columnObject.columnSize == 0){
                        return "'"+columnObject.value+"'";
                    }
                    return "'"+columnObject.value.substring(0, columnObject.value.length() > columnObject.columnSize ? columnObject.columnSize : columnObject.value.length())+"'";
                }
            case NUMBER:
                if(columnObject.increase){
                    return (new BigDecimal(columnObject.value).add(BigDecimal.valueOf(index*columnObject.step))).toString();
                }
                else{
                    return columnObject.value;
                }
            case SYSDATE:
                return "sysdate";
            case SYSTIME:
                return "trunc(sysdate)";
            case FUNCTION:
                return columnObject.value;
            default:
                return "null";
        }
    }

    /***
     * 根据实体类型返回列集合
     * ColumnObject:包括name和type两个重要属性，用于在创建表结构的时候生成表字段名称和表字段类型
     */
    static List<ColumnObject> getColumnsByClass(Class entityClass){
        List<ColumnObject> list = new ArrayList<ColumnObject>();
        Field[] fields = entityClass.getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);
            //静态变量不要
            if((field.getModifiers() & java.lang.reflect.Modifier.STATIC) == java.lang.reflect.Modifier.STATIC){
                continue;
            }
            Class fieldType = field.getType();
            Annotation annotaion = fieldType.getAnnotation(Entity.class);
            //不在基本变量里的，不要
            if(annotaion==null && !classSet.contains(field.getType())){
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            ColumnObject columnObject = new ColumnObject();

            if(column!=null){
                columnObject.name = column.name();
            }else if(annotaion==null){
                String fieldName = field.getName();
                columnObject.name = convertEntityLabelToDBLabel(fieldName);
            }else{
                String fieldName = field.getName()+"_id";
                columnObject.name = fieldName;
            }
            TypeHandler typeHandler = registry.getTypeHandler(field.getType());
            columnObject.typeHandler=typeHandler;
            Class typeHanlerClazz = typeHandler.getClass();
            if(annotaion!=null){
                Field[] field_fields = fieldType.getFields();
                for(Field field_field:field_fields){
                    Annotation id_annctation = field_field.getAnnotation(Id.class);
                    if(id_annctation!=null){
                        columnObject.valueType = getColumnType(field_field.getType());
                    }
                }
            }else{
                columnObject.valueType = getColumnType(typeHanlerClazz);
            }
            if(generatedValue==null){
                columnObject.increase = false;
            }else{
                columnObject.increase = true;
            }
            if (columnObject.valueType == ColumnType.VARCHAR) {
                columnObject.value = columnObject.name;
            } else if (columnObject.valueType == ColumnType.NUMBER) {
                if(columnObject.increase)
                    columnObject.value = String.valueOf(1L);
                else
                    columnObject.value = String.valueOf(1L);
            }
            list.add(columnObject);
        }
        return list;
    }

    static ColumnType getColumnType(Class type){
        if(IntegerTypeHandler.class.equals(type)){
            return ColumnType.NUMBER;
        }else if(ShortTypeHandler.class.equals(type)){
            return ColumnType.NUMBER;
        }else if(StringTypeHandler.class.equals(type)){
            return ColumnType.VARCHAR;
        }else if(CharacterTypeHandler.class.equals(type)){
            return ColumnType.VARCHAR;
        }else if(LongTypeHandler.class.equals(type)){
            return ColumnType.NUMBER;
        }else if(DateTypeHandler.class.equals(type)){
            return ColumnType.SYSDATE;
        }else if(DoubleTypeHandler.class.equals(type)){
            return ColumnType.NUMBER;
        }
        return ColumnType.VARCHAR;
    }


    /**
     * hello_world -> helloWorld
     * @param dbLabel
     */
    static String convertDBLabelToEntityLabel(String dbLabel){
        Pattern p= Pattern.compile("_[a-z]|_[A-Z]");
        Matcher m=p.matcher(dbLabel);
        while(m.find()){
            String s = m.group();
            if(s.length()>=2){
                dbLabel = dbLabel.replaceFirst(s,s.substring(1,2).toUpperCase());
            }
        }
        return dbLabel.intern();
    }

    /**
     * helloWorld -> hello_orld
     * @param entityLabel
     */
    static String convertEntityLabelToDBLabel(String entityLabel){
        entityLabel = entityLabel.replaceFirst(entityLabel.substring(0,1),entityLabel.substring(0,1).toLowerCase());
        Pattern p= Pattern.compile("[A-Z]");
        Matcher m=p.matcher(entityLabel);
        while(m.find()){
            String s = m.group();
            if(s.length()>=1){
                entityLabel = entityLabel.replaceFirst(s, "_" + s.toLowerCase());
            }
        }
        return entityLabel.intern();
    }

    static String getTableName(DataSet dataSet){
        String tableName = dataSet.tableName().intern();
        if(tableName!=""){
            return tableName;
        }
        Entity entity = dataSet.entityClass().getAnnotation(Entity.class);
        if (entity != null && entity.name()!=null && !"".equals(entity.name())){
            tableName = entity.name();
        }else{
            tableName = dataSet.entityClass().getSimpleName();
        }
        tableName = SqlBuilder.convertEntityLabelToDBLabel(tableName);
        return tableName;
    }
}
