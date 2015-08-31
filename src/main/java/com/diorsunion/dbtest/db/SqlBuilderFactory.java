package com.diorsunion.dbtest.db;

import com.diorsunion.dbtest.db.impl.HSQLSqlBuilder;
import com.diorsunion.dbtest.db.impl.MysqlSqlBuilder;
import com.diorsunion.dbtest.db.impl.OracleSqlBuilder;
import com.diorsunion.dbtest.enums.DBType;


/**
 *
 * @author 王尼玛
 */
public class SqlBuilderFactory {	
	
	/**
	 * Gets the value.
	 *
	 * @param dbType the type
	 * @return the sql
	 */
	public static SqlBuilder create(DBType dbType){
		if(dbType.equals(DBType.ORACLE)){
			return new OracleSqlBuilder();
		} else if(dbType.equals(DBType.MYSQL)){
            return new MysqlSqlBuilder();
        }else if(dbType.equals(DBType.HSQL)){
            return new HSQLSqlBuilder();
        }else{
			return null;
		}
	}

}
