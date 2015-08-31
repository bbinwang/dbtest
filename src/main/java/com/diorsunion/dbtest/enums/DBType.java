package com.diorsunion.dbtest.enums;

public enum DBType {
	UN_DEFINED(-1), ORACLE(0), MYSQL(1) ,HSQL(2);
	private int i;

	private DBType(int i) {
		this.i = i;
	}

	public int value() {
		return this.i;
	}

	public static DBType valueOf(int i) {
		for (DBType t : values()) {
			if (t.value() == i) {
				return t;
			}
		}
		throw new IllegalArgumentException("Invalid SqlType:" + i);
	}
}