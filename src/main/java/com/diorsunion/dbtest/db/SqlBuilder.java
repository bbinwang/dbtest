package com.diorsunion.dbtest.db;

import com.google.common.collect.Sets;
import com.diorsunion.dbtest.ColumnObject;
import com.diorsunion.dbtest.annotation.DataSet;
import com.diorsunion.dbtest.enums.ColumnType;
import org.apache.ibatis.type.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author 王尼玛
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
        switch (columnObject.getValueType()) {
            case NULL:
                return "null";
            case BLANK:
                return "''";
            case VARCHAR:
                if(columnObject.isIncrease()){
                    if(columnObject.getColumnSize() == 0){
                        return "'"+columnObject.getValue()+(index*columnObject.getStep()+1)+"'";
                    }
                    int endIndex = columnObject.getColumnSize() - (int) Math.log10(index*columnObject.getStep()+1) -1;
                    return "'"+columnObject.getValue().substring(0,columnObject.getValue().length()>endIndex?endIndex:columnObject.getValue().length())+(index*columnObject.getStep()+1)+"'";
                }
                else{
                    if(columnObject.getColumnSize() == 0){
                        return "'"+columnObject.getValue()+"'";
                    }
                    return "'"+columnObject.getValue().substring(0,columnObject.getValue().length()>columnObject.getColumnSize()?columnObject.getColumnSize():columnObject.getValue().length())+"'";
                }
            case NUMBER:
                if(columnObject.isIncrease()){
                    return (new BigDecimal(columnObject.getValue()).add(BigDecimal.valueOf(index*columnObject.getStep()))).toString();
                }
                else{
                    return columnObject.getValue();
                }
            case SYSDATE:
                return "sysdate";
            case SYSTIME:
                return "trunc(sysdate)";
            case FUNCTION:
                return columnObject.getValue();
            default:
                return "null";
        }
    }

    static List<ColumnObject> getColumnsByClass(Class entityClass){
        List<ColumnObject> list = new ArrayList<ColumnObject>();
        Field[] fields = entityClass.getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);
            //静态变量不要
            if((field.getModifiers() & java.lang.reflect.Modifier.STATIC) == java.lang.reflect.Modifier.STATIC){
                continue;
            }
            //不在基本变量里的，不要
            if(!classSet.contains(field.getType())){
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            ColumnObject columnObject = new ColumnObject();

            if(column!=null){
                columnObject.setName(column.name());
            }else {
                String fieldName = field.getName();
                columnObject.setName(convertEntityLabelToDBLabel(fieldName));
            }
            TypeHandler typeHandler = registry.getTypeHandler(field.getType());
            columnObject.setTypeHandler(typeHandler);
            Class typeHanlerClazz = typeHandler.getClass();
            if(IntegerTypeHandler.class.equals(typeHanlerClazz)){
                columnObject.setValueType(ColumnType.NUMBER);
            }else if(ShortTypeHandler.class.equals(typeHanlerClazz)){
                columnObject.setValueType(ColumnType.NUMBER);
            }else if(StringTypeHandler.class.equals(typeHanlerClazz)){
                columnObject.setValueType(ColumnType.VARCHAR);
            }else if(CharacterTypeHandler.class.equals(typeHanlerClazz)){
                columnObject.setValueType(ColumnType.VARCHAR);
            }else if(LongTypeHandler.class.equals(typeHanlerClazz)){
                columnObject.setValueType(ColumnType.NUMBER);
            }else if(DateTypeHandler.class.equals(typeHanlerClazz)){
                columnObject.setValueType(ColumnType.SYSDATE);
            }else if(DoubleTypeHandler.class.equals(typeHanlerClazz)){
                columnObject.setValueType(ColumnType.NUMBER);
            }
            if(generatedValue==null){
                columnObject.setIncrease(false);
            }else{
                columnObject.setIncrease(true);
            }
            if (columnObject.getValueType() == ColumnType.VARCHAR) {
                columnObject.setValue(columnObject.getName());
            } else if (columnObject.getValueType() == ColumnType.NUMBER) {
                if(columnObject.isIncrease())
                    columnObject.setValue(String.valueOf(1L));
                else
                    columnObject.setValue(String.valueOf(1L));
            }
            list.add(columnObject);
        }
        return list;
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
        Entity entity = dataSet.entityClass().getAnnotation(Entity.class);
        String tableName = null;
        if (entity != null && entity.name()!=null && !"".equals(entity.name())){
            tableName = entity.name();
        }else{
            tableName = dataSet.entityClass().getSimpleName();
        }
        tableName = SqlBuilder.convertEntityLabelToDBLabel(tableName);
        return tableName;
    }
}
