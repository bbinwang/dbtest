package com.diorsunion.dbtest.db.impl;

import com.diorsunion.dbtest.ColumnObject;
import com.diorsunion.dbtest.db.SqlBuilder;
import org.apache.ibatis.type.*;

import java.util.List;


/**
 * The Class OracleSqlBuilder.
 *
 * @author 王尼玛
 */
public class OracleSqlBuilder implements SqlBuilder{

    @Override
    public String getCreateTable(String tableName, Class clazz) {List<ColumnObject> columnObjects = SqlBuilder.getColumnsByClass(clazz);
        final StringBuilder create_sql = new StringBuilder("create table "+tableName+"(");
        columnObjects.stream().forEach(columnObject -> {
            create_sql.append(columnObject.getName());
            Class typeHanlerClazz = columnObject.getTypeHandler().getClass();
            if(IntegerTypeHandler.class.equals(typeHanlerClazz) || LongTypeHandler.class.equals(typeHanlerClazz) ){
                create_sql.append(" ").append("number");
            }else if(StringTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("varchar2(2048)");
            }else if(CharacterTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("char(255)");
            }else if(DateTypeHandler.class.equals(typeHanlerClazz)){
                create_sql.append(" ").append("datetime");
            }
            if(columnObject.isIncrease()){
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
		//函数
		if(columnObject.getValue().startsWith("FUNC{") && columnObject.getValue().endsWith("}")){
			return (columnObject.getValue().substring(5, columnObject.getValue().length()-1));
		}
		
		//自定义数据
		if(columnObject.getCustoms() != null && columnObject.getCustoms().length>0 && index<columnObject.getCustoms().length){
			if(columnObject.getValueType().isQuotation()){
				return "'"+columnObject.getCustoms()[index]+"'";
			}
			else{
				return columnObject.getCustoms()[index];
			}
		}
		switch (columnObject.getValueType()) {
		case SYSDATE:
			if (columnObject.getValue() == null || columnObject.getValue().isEmpty()) {
				if(columnObject.isIncrease()){
					return "trunc(sysdate+"+index*columnObject.getStep()+")";
				}
				else{
					return "trunc(sysdate)";
				}
			} else {
				if(columnObject.isIncrease()){
					return "to_date('" + columnObject.getValue() + "','YYYY-MM-DD')+"+index*columnObject.getStep();
				}
				else{
					return "to_date('" + columnObject.getValue() + "','YYYY-MM-DD')";
				}
			}
		case SYSTIME:
			if (columnObject.getValue() == null || columnObject.getValue().isEmpty()) {
				if(columnObject.isIncrease()){
					return "sysdate+"+index*columnObject.getStep();
				}
				else{
					return "sysdate";
				}
			} else {
				if(columnObject.isIncrease()){
					return "to_date('" + columnObject.getValue() + "','YYYY-MM-DD HH24:MI:SS')+"+index*columnObject.getStep();
				}
				else{
					return "to_date('" + columnObject.getValue() + "','YYYY-MM-DD HH24:MI:SS')";
				}
			}

		default:
            return SqlBuilder.getDefaultValue(columnObject,index);
		}
	}
}
