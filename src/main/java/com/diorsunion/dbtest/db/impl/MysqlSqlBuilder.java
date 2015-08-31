package com.diorsunion.dbtest.db.impl;

import com.diorsunion.dbtest.ColumnObject;
import com.diorsunion.dbtest.db.SqlBuilder;
import com.diorsunion.dbtest.enums.ColumnType;
import org.apache.ibatis.type.*;

import java.util.List;


/**
 * The Class OracleSqlBuilder.
 *
 * @author 王尼玛
 */
public class MysqlSqlBuilder implements SqlBuilder{
	
	/* (non-Javadoc)
	 * @see com.taobao.obunit.db.SqlBuilder#getValue(com.taobao.obunit.ColumnType)
	 */
	public String getValue(ColumnObject columnObject,int index){
		//函数
		if(columnObject.getValue()!=null
				&& columnObject.getValue().startsWith("FUNC{")
				&& columnObject.getValue().endsWith("}")){
			return (columnObject.getValue().substring(5, columnObject.getValue().length()-1));
		}
		//自定义数据
		if(columnObject.getCustoms() != null && columnObject.getCustoms().length>0 && index<columnObject.getCustoms().length){
            ColumnType columnType = columnObject.getValueType();
            String value = columnObject.getCustoms()[index];
			if(columnType.isQuotation()){
				return "'"+value+"'";
			}else{
				return value;
			}	
		}
		
		switch (columnObject.getValueType()) {
		case SYSDATE:
			if (columnObject.getValue() == null || columnObject.getValue().isEmpty()) {
				if(columnObject.isIncrease()){
					return "current_date() + interval "+index*columnObject.getStep()+" day";
				}
				else{
					return "current_date()";
				}	
			} else {
				if(columnObject.isIncrease()){
					return "date_format('" + columnObject.getValue() + "','%Y-%m-%d') + interval "+index+" day";
				}
				else{
					return "date_format('" + columnObject.getValue() + "','%Y-%m-%d')";
				}
			}
		case SYSTIME:
			if (columnObject.getValue() == null || columnObject.getValue().isEmpty()) {
				if(columnObject.isIncrease()){
					return "now()+ interval "+index*columnObject.getStep()+" day";
				}
				else{
					return "now()";
				}
			} else {
				if(columnObject.isIncrease()){
					return "date_format('" + columnObject.getValue() + "','%Y-%m-%d %H:%i:%S') + interval "+index+" day";
				}
				else{
					return "date_format('" + columnObject.getValue() + "','%Y-%m-%d %H:%i:%S')";
				}	
			}
        case TIMESTAMP:
                return "CURRENT_TIMESTAMP";
		default:
            return SqlBuilder.getDefaultValue(columnObject,index);
		}
	}

    @Override
    public String getCreateTable(String tableName,Class clazz) {
        List<ColumnObject> columnObjects = SqlBuilder.getColumnsByClass(clazz);
        final StringBuilder create_sql = new StringBuilder("create table "+tableName+"(");
        columnObjects.stream().forEach(columnObject -> {
            create_sql.append(columnObject.getName());
            Class typeHanlerClazz = columnObject.getTypeHandler().getClass();
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
            if(columnObject.isIncrease()){
                create_sql.append(" primary key AUTO_INCREMENT,");
//                create_sql.append(" primary key IDENTITY,");
            }else{
                create_sql.append(",");
            }
        });
        create_sql.deleteCharAt(create_sql.length()-1);
        create_sql.append(")");
        return create_sql.toString();
    }
}