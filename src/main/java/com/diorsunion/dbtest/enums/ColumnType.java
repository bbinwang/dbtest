package com.diorsunion.dbtest.enums;

import com.diorsunion.dbtest.exception.DBTestException;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * The Enum ColumnType.
 * 
 * @author harley-dog
 */
public enum ColumnType {

	NULL(false),
	BLANK(false),
	VARCHAR(true),
	NUMBER(false),
	SYSDATE(true),
	SYSTIME(true),
	TIMESTAMP(true),
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
