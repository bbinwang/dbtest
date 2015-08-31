package com.diorsunion.dbtest.enums;

import com.diorsunion.dbtest.exception.DBTestException;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * The Enum ColumnType.
 * 
 * @author 王尼玛
 */
public enum ColumnType {

	/** NULL. */
	NULL(false),
	/** 空字符. */
	BLANK(false), 
	/** 常量. */
	VARCHAR(true),
	/** 数字. */
	NUMBER(false), 
	/** 当前日期. */
	SYSDATE(true), 
	/** 当前时间. */
	SYSTIME(true),
	/** 当前时间戳. */
	TIMESTAMP(true),
	/** 自定义函数. */
	FUNCTION(false); 
	
	private boolean isQuotation;
	
	private ColumnType(boolean isQuotation) {
		this.isQuotation = isQuotation;
	}

	/**
	 * Gets the by name.
	 * 
	 * @param name
	 *            the name
	 * @return the by name
	 */
    public static ColumnType getByName(String name) {
        for (ColumnType columnType : ColumnType.values()) {
            if (columnType.toString().equals(name)) {
                return columnType;
            }
        }
        throw new DBTestException("can't find "+name+" in ColumnType!");
    }

	public boolean isQuotation() {
		return isQuotation;
	}
	
}
