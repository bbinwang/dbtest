package com.diorsunion.dbtest.db.impl;

import com.diorsunion.dbtest.db.SqlBuilder;
import com.diorsunion.dbtest.util.ColumnObject;
import org.apache.ibatis.type.*;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * The Class OracleSqlBuilder.
 *
 * @author harley-dog
 */
public class HSQLSqlBuilder implements SqlBuilder {

    @NotNull
    public String getValue(ColumnObject columnObject, int index) {

        SimpleDateFormat format_simple = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format_full = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //函数
        if (columnObject.value != null
                && columnObject.value.startsWith("FUNC{")
                && columnObject.value.endsWith("}")) {
            return (columnObject.value.substring(5, columnObject.value.length() - 1));
        }
        String return_value = null;
        //自定义数据
        if (columnObject.customs != null && columnObject.customs.length > 0 && index < columnObject.customs.length) {
//            ColumnType columnType = columnObject.valueType;
//            String value = columnObject.customs[index];
            return_value = columnObject.customs[index];
//            if (columnType.isQuotation()) {
//                return "'" + value + "'";
//            } else {
//                return value;
//            }
        } else {
            return_value = columnObject.value;
        }
        switch (columnObject.valueType) {
            case SYSDATE:
            case SYSTIME:
            case TIMESTAMP:
                if (return_value == null) {
                    return "now()";
                } else if (return_value.length() == 10) {
                    try {
                        Date date = format_simple.parse(return_value);
                        return "\'" + format_full.format(date) + "\'";
                    } catch (ParseException e) {
                        return "now()";
                    }
                } else if (return_value.length() == 19) {
                    return "\'" + return_value + "\'";
                } else {
                    return "now()";
                }
            default:
                return SqlBuilder.getDefaultValue(columnObject, index);
        }
    }

    @Override
    public String getCreateTable(String tableName, Class clazz) {
        List<ColumnObject> columnObjects = SqlBuilder.getColumnsByClass(clazz);
        final StringBuilder create_sql = new StringBuilder("create table " + tableName + "(");
        columnObjects.stream().forEach(columnObject -> {
            create_sql.append(columnObject.name);
            Class typeHanlerClazz = columnObject.typeHandler.getClass();
            if (IntegerTypeHandler.class.equals(typeHanlerClazz)) {
                create_sql.append(" ").append("int");
            } else if (ShortTypeHandler.class.equals(typeHanlerClazz)) {
                create_sql.append(" ").append("smallint");
            } else if (StringTypeHandler.class.equals(typeHanlerClazz)) {
                create_sql.append(" ").append("varchar(2048)");
            } else if (CharacterTypeHandler.class.equals(typeHanlerClazz)) {
                create_sql.append(" ").append("char(255)");
            } else if (LongTypeHandler.class.equals(typeHanlerClazz)) {
                create_sql.append(" ").append("bigint");
            } else if (DateTypeHandler.class.equals(typeHanlerClazz)) {
                create_sql.append(" ").append("datetime");
            } else if (DoubleTypeHandler.class.equals(typeHanlerClazz)) {
                create_sql.append(" ").append("double");
            }
            if (columnObject.increase) {
                create_sql.append(" PRIMARY KEY NOT NULL IDENTITY,");
            } else {
                create_sql.append(",");
            }
        });
        create_sql.deleteCharAt(create_sql.length() - 1);
        create_sql.append(")");
        return create_sql.toString();
    }
}