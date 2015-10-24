package com.diorsunion.dbtest.util;

import com.diorsunion.dbtest.enums.ColumnType;
import org.apache.ibatis.type.TypeHandler;

import java.util.Arrays;
import java.util.Date;

/**
 * The Class ColumnObject.
 *
 * @author harley-dog
 */
public class ColumnObject {

    public String name;

    public ColumnType valueType;

    public TypeHandler typeHandler;

    public int columnSize;

    public int decimalDigits;

    public String value;

    public boolean increase = false;

    public int step = 1;

    public String range;

    public String[] customs;

    @Override
    public String toString() {
        Date date = new Date();
        return "ColumnObject [name=" + name + ", valueType=" + valueType
                + ", columnSize=" + columnSize + ", decimalDigits="
                + decimalDigits + ", value=" + value + ", increase=" + increase
                + ", step=" + step + ", range=" + range + ", customs="
                + Arrays.toString(customs) + "]";
    }
}
