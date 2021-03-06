package com.diorsunion.dbtest.db.impl;

import com.diorsunion.dbtest.db.SqlBuilder;
import com.diorsunion.dbtest.util.ColumnObject;
import org.apache.ibatis.type.*;

import java.util.List;


/**
 * The Class OracleSqlBuilder.
 *
 * @author harley-dog
 */
public class OracleSqlBuilder implements SqlBuilder{

    @Override
    public String getCreateTable(String tableName, Class clazz) {List<ColumnObject> columnObjects = SqlBuilder.getColumnsByClass(clazz);
        final StringBuilder create_sql = new StringBuilder("create table "+tableName+"(");
        columnObjects.stream().forEach(columnObject -> {
            create_sql.append(columnObject.name);
            Class typeHanlerClazz = columnObject.typeHandler.getClass();
            if(IntegerTypeHandler.class.equals(typeHanlerClazz) || LongTypeHandler.class.equals(typeHanlerClazz) ){
                create_sql.append(" ").append("number");
            }else if(StringTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("varchar2(2048)");
            }else if(CharacterTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("char(255)");
            }else if(DateTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("datetime");
            }
            if(columnObject.increase){
                create_sql.append(" primary key IDENTITY,");
            }else{
                create_sql.append(",");
            }
        });
        create_sql.deleteCharAt(create_sql.length()-1);
        create_sql.append(")");
        return create_sql.toString();
    }

    /* (non-Javadoc)
         * @see com.taobao.obunit.db.SqlBuilder#getValue(com.taobao.obunit.ColumnType)
         */
	public String getValue(ColumnObject columnObject,int index){

		if(columnObject.value.startsWith("FUNC{") && columnObject.value.endsWith("}")){
			return (columnObject.value.substring(5, columnObject.value.length()-1));
		}
		

		if(columnObject.customs != null && columnObject.customs.length>0 && index<columnObject.customs.length){
			if(columnObject.valueType.isQuotation()){
				return "'"+columnObject.customs[index]+"'";
			}
			else{
				return columnObject.customs[index];
			}
		}
		switch (columnObject.valueType) {
		case SYSDATE:
			if (columnObject.value == null || columnObject.value.isEmpty()) {
				if(columnObject.increase){
					return "trunc(sysdate+"+index*columnObject.step+")";
				}
				else{
					return "trunc(sysdate)";
				}
			} else {
				if(columnObject.increase){
					return "to_date('" + columnObject.value + "','YYYY-MM-DD')+"+index*columnObject.step;
				}
				else{
					return "to_date('" + columnObject.value + "','YYYY-MM-DD')";
				}
			}
		case SYSTIME:
			if (columnObject.value == null || columnObject.value.isEmpty()) {
				if(columnObject.increase){
					return "sysdate+"+index*columnObject.step;
				}
				else{
					return "sysdate";
				}
			} else {
				if(columnObject.increase){
					return "to_date('" + columnObject.value + "','YYYY-MM-DD HH24:MI:SS')+"+index*columnObject.step;
				}
				else{
					return "to_date('" + columnObject.value + "','YYYY-MM-DD HH24:MI:SS')";
				}
			}

		default:
            return SqlBuilder.getDefaultValue(columnObject,index);
		}
	}
}
