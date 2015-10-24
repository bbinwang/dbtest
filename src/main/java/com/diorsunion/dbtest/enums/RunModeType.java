package com.diorsunion.dbtest.enums;

import com.diorsunion.dbtest.exception.DBTestException;

/**
 * The Enum ColumnType.
 * 
 * @author harley-dog
 */
public enum RunModeType {

	CLEAN_INSERT,
	INSERT_CLEAN;

	/**
	 * Gets the by name.
	 * 
	 * @param name
	 *            the name
	 * @return the by name
	 */
	public static RunModeType getByName(String name) {
		for (RunModeType columnType : RunModeType.values()) {
			if (columnType.toString().equals(name)) {
				return columnType;
			}
		}
		throw new DBTestException("can't find this type in RunModeType!");
	}
}
