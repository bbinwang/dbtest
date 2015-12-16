package com.diorsunion.dbtest.db;

import com.diorsunion.dbtest.annotation.DataSet;
import com.diorsunion.dbtest.enums.ColumnType;
import com.diorsunion.dbtest.util.ColumnObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.ibatis.type.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    /* (non-Javadoc)
     * @see com.taobao.obunit.db.SqlBuilder#getValue(com.taobao.obunit.ColumnType, java.lang.String, int)
	 */
    static String getDefaultValue(ColumnObject columnObject, int index) {
        switch (columnObject.valueType) {
            case NULL:
                return "null";
            case BLANK:
                return "''";
            case VARCHAR:
                if (columnObject.increase) {
                    if (columnObject.columnSize == 0) {
                        return "'" + columnObject.value + (index * columnObject.step + 1) + "'";
                    }
                    int endIndex = columnObject.columnSize - (int) Math.log10(index * columnObject.step + 1) - 1;
                    return "'" + columnObject.value.substring(0, columnObject.value.length() > endIndex ? endIndex : columnObject.value.length()) + (index * columnObject.step + 1) + "'";
                } else {
                    if (columnObject.columnSize == 0) {
                        return "'" + columnObject.value + "'";
                    }
                    return "'" + columnObject.value.substring(0, columnObject.value.length() > columnObject.columnSize ? columnObject.columnSize : columnObject.value.length()) + "'";
                }
            case NUMBER:
                if (columnObject.increase) {
                    return (new BigDecimal(columnObject.value).add(BigDecimal.valueOf(index * columnObject.step))).toString();
                } else {
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
    static List<ColumnObject> getColumnsByClass(Class entityClass) {
        List<ColumnObject> list = Lists.newArrayList();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //静态变量不要
            if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) == java.lang.reflect.Modifier.STATIC) {
                continue;
            }
            JoinColumns join_key_annotaion = field.getAnnotation(JoinColumns.class);//
            //不在基本变量里的，并且不是Entity类型的不要
            if (join_key_annotaion == null && !classSet.contains(field.getType())) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);

            ColumnObject columnObject = new ColumnObject();
            columnObject.increase = generatedValue != null;

            if (column != null) {
                ColumnObject c = columnObject.clone();

                //强行设置列名
                c.name = column.name();
                c.typeHandler = registry.getTypeHandler(field.getType());
                c.valueType = getColumnType(c.typeHandler.getClass());

                if (c.valueType == ColumnType.VARCHAR) {
                    c.value = columnObject.name;
                } else if (c.valueType == ColumnType.NUMBER) {
                    if (c.increase)
                        c.value = String.valueOf(1L);
                    else
                        c.value = String.valueOf(1L);
                }
                list.add(c);
            } else if (join_key_annotaion != null) {
                Class colunm_class = field.getType();
                Field[] field_fields = colunm_class.getDeclaredFields();
                Map<String, Field> fieldMap = Maps.newHashMap();
                for (Field field_field : field_fields) {
                    field_field.setAccessible(true);
                    fieldMap.put(field_field.getName(), field_field);
                }
                JoinColumn[] joinColumns = join_key_annotaion.value();
                for (JoinColumn joinColumn : joinColumns) {
                    String joinColumnName = joinColumn.name();
                    Field field_field = fieldMap.get(joinColumnName);
                    ColumnObject c = columnObject.clone();
                    //默认列名,且为外键
                    c.name = field.getName() + "_" + field_field.getName();
                    final Class field_field_type = field_field.getType();
                    c.typeHandler = registry.getTypeHandler(field_field_type);
                    c.valueType = getColumnType(c.typeHandler.getClass());

                    if (c.valueType == ColumnType.VARCHAR) {
                        c.value = c.name;
                    } else if (c.valueType == ColumnType.NUMBER) {
                        if (c.increase)
                            c.value = String.valueOf(1L);
                        else
                            c.value = String.valueOf(1L);
                    }
                    list.add(c);
                }
            } else {

                ColumnObject c = columnObject.clone();

                //默认列名
                String fieldName = field.getName();
                c.name = convertEntityLabelToDBLabel(fieldName);
                c.typeHandler = registry.getTypeHandler(field.getType());
                c.valueType = getColumnType(c.typeHandler.getClass());

                if (c.valueType == ColumnType.VARCHAR) {
                    c.value = c.name;
                } else if (c.valueType == ColumnType.NUMBER) {
                    if (c.increase)
                        c.value = String.valueOf(1L);
                    else
                        c.value = String.valueOf(1L);
                }
                list.add(c);
            }
        }
        return list;
    }

    static ColumnType getColumnType(Class type) {
        if (IntegerTypeHandler.class.equals(type)) {
            return ColumnType.NUMBER;
        } else if (ShortTypeHandler.class.equals(type)) {
            return ColumnType.NUMBER;
        } else if (StringTypeHandler.class.equals(type)) {
            return ColumnType.VARCHAR;
        } else if (CharacterTypeHandler.class.equals(type)) {
            return ColumnType.VARCHAR;
        } else if (LongTypeHandler.class.equals(type)) {
            return ColumnType.NUMBER;
        } else if (DateTypeHandler.class.equals(type)) {
            return ColumnType.SYSDATE;
        } else if (DoubleTypeHandler.class.equals(type)) {
            return ColumnType.NUMBER;
        }
        return ColumnType.VARCHAR;
    }

    /**
     * hello_world -> helloWorld
     * @param dbLabel hello_world
     * @return helloWorld
     */
    static String convertDBLabelToEntityLabel(String dbLabel) {
        Pattern p = Pattern.compile("_[a-z]|_[A-Z]");
        Matcher m = p.matcher(dbLabel);
        while (m.find()) {
            String s = m.group();
            if (s.length() >= 2) {
                dbLabel = dbLabel.replaceFirst(s, s.substring(1, 2).toUpperCase());
            }
        }
        return dbLabel.intern();
    }

    /**
     * helloWorld -> hello_orld
     *
     * @param entityLabel helloWorld
     * @return hello_orld
     */
    static String convertEntityLabelToDBLabel(String entityLabel) {
        entityLabel = entityLabel.replaceFirst(entityLabel.substring(0, 1), entityLabel.substring(0, 1).toLowerCase());
        Pattern p = Pattern.compile("[A-Z]");
        Matcher m = p.matcher(entityLabel);
        while(m.find()){
            String s = m.group();
            if (s.length() >= 1) {
                entityLabel = entityLabel.replaceFirst(s, "_" + s.toLowerCase());
            }
        }
        return entityLabel.intern();
    }

    static String getTableName(DataSet dataSet) {
        String tableName = dataSet.tableName().intern();
        if (!StringUtils.isEmpty(tableName)) {
            return tableName;
        }
        Entity entity = dataSet.entityClass().getAnnotation(Entity.class);
        if (entity != null && entity.name() != null && !"".equals(entity.name())) {
            tableName = entity.name();
        } else {
            tableName = dataSet.entityClass().getSimpleName();
        }
        tableName = SqlBuilder.convertEntityLabelToDBLabel(tableName);
        return tableName;
    }

    /**
     * Gets the value.
     *
     * @param tableName 表名
     * @param clazz 表所对应的类名
     * @return the value
     */
    String getCreateTable(String tableName, Class clazz);

    String getValue(ColumnObject columnObject, int index);
}
