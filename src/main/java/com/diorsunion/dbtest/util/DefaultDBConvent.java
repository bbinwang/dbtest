package com.diorsunion.dbtest.util;

import com.diorsunion.dbtest.enums.ColumnType;

import java.util.HashMap;
import java.util.Map;

public class DefaultDBConvent {
	
	private static Map<String,ColumnType> map = new HashMap<String,ColumnType>();
	
	public DefaultDBConvent(){
		map.put("BIGINT", ColumnType.NUMBER);
		map.put("TINYINT", ColumnType.NUMBER);
		map.put("INT", ColumnType.NUMBER);

		map.put("LONGTEXT", ColumnType.VARCHAR);
		map.put("TEXT", ColumnType.VARCHAR);
		map.put("VARCHAR", ColumnType.VARCHAR);	
		
		map.put("DATETIME", ColumnType.SYSTIME);
	}
	
	public static ColumnType convent(String key) {
		return map.get(key) != null ? map.get(key) : ColumnType.VARCHAR;
	}
}
