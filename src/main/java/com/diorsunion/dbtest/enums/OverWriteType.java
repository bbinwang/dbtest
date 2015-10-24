package com.diorsunion.dbtest.enums;

import com.diorsunion.dbtest.exception.DBTestException;

/**
 * The Enum ColumnType.
 * 
 * @author harley-dog
 */
public enum OverWriteType {

	OVER_WRITE,
	NOT_OVER_WRITE;

	/**
	 * Gets the by name.
	 * 
	 * @param name
	 *            the name
	 * @return the by name
	 */
	public static OverWriteType getByName(String name) {
		for (OverWriteType columnType : OverWriteType.values()) {
			if (columnType.toString().equals(name)) {
				return columnType;
			}
		}
		throw new DBTestException("can't find this type in OverWriteType!");
	}
}
