package com.diorsunion.dbtest;

import com.diorsunion.dbtest.enums.ColumnType;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * The Class ColumnObject.
 * 
 * @author 王尼玛
 */
public class ColumnObject {

	/** 名称. */
	private String name;

	/** 值类型. */
	private ColumnType valueType;

    /**对应数据库里的类型**/
    private TypeHandler typeHandler;

    /**字段类型**/
    private Field field;

	/** 字段长度. */
	private int columnSize;

	/** 字段精度. */
	private int decimalDigits;

	/** 值. */
	private String value;

	/** 是否自增. */
	private boolean increase = false;
	
	/** 自增步长. */
	private int step = 1;
	
	/** 范围. */
	private String range;

	/** 自定义数据. */
	private String[] customs;

	/**
	 * 获取 名称.
	 * 
	 * @return the 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置 名称.
	 * 
	 * @param name
	 *            the new 名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取 值类型.
	 * 
	 * @return the 值类型
	 */
	public ColumnType getValueType() {
		return valueType;
	}

	/**
	 * 设置 值类型.
	 * 
	 * @param valueType
	 *            the new 值类型
	 */
	public void setValueType(ColumnType valueType) {
		this.valueType = valueType;
	}

	/**
	 * 获取 值.
	 * 
	 * @return the 值
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 设置 值.
	 * 
	 * @param value
	 *            the new 值
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Checks if is 是否自增.
	 * 
	 * @return the 是否自增
	 */
	public boolean isIncrease() {
		return increase;
	}

	/**
	 * 设置 是否自增.
	 * 
	 * @param increase
	 *            the new 是否自增
	 */
	public void setIncrease(boolean increase) {
		this.increase = increase;
	}

	/**
	 * Gets the column size.
	 * 
	 * @return the column size
	 */
	public int getColumnSize() {
		return columnSize;
	}

	/**
	 * Sets the column size.
	 * 
	 * @param columnSize
	 *            the new column size
	 */
	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	/**
	 * Gets the decimal digits.
	 * 
	 * @return the decimal digits
	 */
	public int getDecimalDigits() {
		return decimalDigits;
	}

	/**
	 * Sets the decimal digits.
	 * 
	 * @param decimalDigits
	 *            the new decimal digits
	 */
	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	/**
	 * Gets the customs.
	 *
	 * @return the customs
	 */
	public String[] getCustoms() {
		return customs;
	}

	/**
	 * Sets the customs.
	 *
	 * @param customs the new customs
	 */
	public void setCustoms(String[] customs) {
		this.customs = customs;
	}

	/**
	 * Gets the step.
	 *
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * Sets the step.
	 *
	 * @param step the new step
	 */
	public void setStep(int step) {
		this.step = step;
	}
	
	/**
	 * Gets the range.
	 *
	 * @return the range
	 */
	public String getRange() {
		return range;
	}

	/**
	 * Sets the range.
	 *
	 * @param range the new range
	 */
	public void setRange(String range) {
		this.range = range;
	}

    public TypeHandler getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(TypeHandler typeHandler) {
        this.typeHandler = typeHandler;
    }

    @Override
	public String toString() {
		return "ColumnObject [name=" + name + ", valueType=" + valueType
				+ ", columnSize=" + columnSize + ", decimalDigits="
				+ decimalDigits + ", value=" + value + ", increase=" + increase
				+ ", step=" + step + ", range=" + range + ", customs="
				+ Arrays.toString(customs) + "]";
	}

}
