package com.diorsunion.dbtest.db.impl;

import com.diorsunion.dbtest.db.SqlBuilder;
import com.diorsunion.dbtest.enums.ColumnType;
import com.diorsunion.dbtest.util.ColumnObject;
import org.apache.ibatis.type.*;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * The Class OracleSqlBuilder.
 *
 * @author harley-dog
 */
public class HSQLSqlBuilder implements SqlBuilder{

    @NotNull
    public String getValue(ColumnObject columnObject, int index) {
        //函数
        if(columnObject.value!=null
				&& columnObject.value.startsWith("FUNC{")
				&& columnObject.value.endsWith("}")){
			return (columnObject.value.substring(5, columnObject.value.length() - 1));
		}
		//自定义数据
		if(columnObject.customs != null && columnObject.customs.length>0 && index<columnObject.customs.length){
            ColumnType columnType = columnObject.valueType;
            String value = columnObject.customs[index];
			if(columnType.isQuotation()){
				return "'"+value+"'";
			}else{
				return value;
			}	
		}
		switch (columnObject.valueType) {
		case SYSDATE:
            return "now()";
		case SYSTIME:
            return "now()";
        case TIMESTAMP:
                return "now()";
		default:
			return SqlBuilder.getDefaultValue(columnObject, index);
		}
	}

    @Override
    public String getCreateTable(String tableName,Class clazz) {
        List<ColumnObject> columnObjects = SqlBuilder.getColumnsByClass(clazz);
        final StringBuilder create_sql = new StringBuilder("create table "+tableName+"(");
        columnObjects.stream().forEach(columnObject -> {
            create_sql.append(columnObject.name);
            Class typeHanlerClazz = columnObject.typeHandler.getClass();
            if(IntegerTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("int");
            }else if(ShortTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("smallint");
            }else if(StringTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("varchar(2048)");
            }else if(CharacterTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("char(255)");
            }else if(LongTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("bigint");
            }else if(DateTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("datetime");
            }else if(DoubleTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("double");
            }
            if(columnObject.increase){
                create_sql.append(" PRIMARY KEY NOT NULL IDENTITY,");
            }else{
                create_sql.append(",");
            }
        });
        create_sql.deleteCharAt(create_sql.length()-1);
        create_sql.append(")");
        return create_sql.toString();
    }
}