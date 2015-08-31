package com.diorsunion.dbtest.util;

import com.diorsunion.dbtest.ColumnObject;
import com.diorsunion.dbtest.annotation.DBTestConfig;
import com.diorsunion.dbtest.enums.ColumnType;

import java.util.HashMap;
import java.util.Map;

public class DBDefault {
	
	private static Map<String,ColumnType> map = new HashMap<String,ColumnType>();
	
	static{
		//mysql
		map.put("BIT", ColumnType.NUMBER);
		map.put("TINYINT", ColumnType.NUMBER);
		map.put("SMALLINT", ColumnType.NUMBER);
		map.put("MEDIUMINT", ColumnType.NUMBER);
		map.put("INTEGER", ColumnType.NUMBER);
		map.put("INT", ColumnType.NUMBER);
		map.put("BIGINT", ColumnType.NUMBER);
		map.put("FLOAT", ColumnType.NUMBER);
		map.put("DOUBLE", ColumnType.NUMBER);
		map.put("DECIMAL", ColumnType.NUMBER);
			
		map.put("CHAR", ColumnType.VARCHAR);
		map.put("TINYBLOB", ColumnType.VARCHAR);
		map.put("TINYTEXT", ColumnType.VARCHAR);
		map.put("BLOB", ColumnType.VARCHAR);
		map.put("TEXT", ColumnType.VARCHAR);
		map.put("MEDIUMBLOB", ColumnType.VARCHAR);
		map.put("MEDIUMTEXT", ColumnType.VARCHAR);
		map.put("LOGNGBLOB", ColumnType.VARCHAR);
		map.put("LONGTEXT", ColumnType.VARCHAR);
		map.put("VARCHAR", ColumnType.VARCHAR);	
		
		map.put("DATE", ColumnType.SYSDATE);
		map.put("DATETIME", ColumnType.SYSTIME);
		map.put("TIMESTAMP", ColumnType.TIMESTAMP);
		
		//oracle
		map.put("VARCHAR2", ColumnType.VARCHAR);
		map.put("NUMBER", ColumnType.NUMBER);
		map.put("LONG", ColumnType.NUMBER);
		
	}

	public static ColumnObject convent(String name, String type, boolean isKey, int columnSize, int decimalDigits,DBTestConfig config) {
		ColumnObject columnObject = new ColumnObject();
		columnObject.setName(name);
		columnObject.setValueType(map.get(type));
		columnObject.setColumnSize(columnSize);
		columnObject.setDecimalDigits(decimalDigits);
		columnObject.setIncrease(isKey);
		if (map.get(type) == ColumnType.VARCHAR) {
			columnObject.setValue(name);
		} else if (map.get(type) == ColumnType.NUMBER) {
			if(isKey)
				columnObject.setValue(String.valueOf(config!=null?config.firstId():1L));
			else
				columnObject.setValue(String.valueOf(config!=null?config.defaultNumber():1L));
		}
		
		return columnObject;
	}
}
