package com.diorsunion.dbtest.util;

import com.diorsunion.dbtest.ColumnObject;
import com.diorsunion.dbtest.DBTest;
import com.diorsunion.dbtest.db.SqlBuilder;
import com.diorsunion.dbtest.db.SqlBuilderFactory;
import com.diorsunion.dbtest.enums.DBType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlHelper {
	
	private static final Log logger = LogFactory.getLog(DBTest.class);


	/**
	 * 获取数据库类型.
	 * 
	 * @param connection the connection
	 * @return the database type
	 * @throws SQLException the SQL exception
	 */
	public static DBType getDatabaseType(Connection connection, DBType dbType) throws SQLException {
		if (!dbType.equals(DBType.UN_DEFINED)) {
			return dbType;
		} else {
			DatabaseMetaData dbmd = connection.getMetaData();
			String dataBaseType = dbmd.getDatabaseProductName();
			return DBType.valueOf(dataBaseType.split(" ")[0].toUpperCase());
		}
	}
	
	/**
	 * Gets the primary keys.
	 *
	 * @param tableName the table name
	 * @param metadata the metadata
	 * @return the primary keys
	 * @throws SQLException the sQL exception
	 */
	public static List<String> getPrimaryKeys(String tableName, DatabaseMetaData metadata) throws SQLException {
		List<String> primaryKeyList = new ArrayList<String>();
		ResultSet primaryKeys = metadata.getPrimaryKeys(null, null, tableName);
		while (primaryKeys.next()) {
			String primaryKeyColumn = primaryKeys.getString("COLUMN_NAME");
			primaryKeyList.add(primaryKeyColumn);
		}
		return primaryKeyList;
	}
	
	/**
	 * 批量执行sql.
	 * 
	 * @param connection the connection
	 * @param sqls the sqls
	 * @throws SQLException the SQL exception
	 */
	public static void executeBatch(Connection connection, String[] sqls,boolean isClose ) throws SQLException {
		Statement stmt = connection.createStatement();
		for (String sql : sqls) {
			stmt.addBatch(sql);
			logger.info(sql);
		}
		stmt.executeBatch();
		if(!connection.getAutoCommit()){
			connection.commit();
		}
		stmt.close();
		connection.close();
	}

	/**
	 * Execute.
	 * 
	 * @param connection the connection
	 * @param sql the sql
     * @throws SQLException the SQL exception
	 */
	public static void execute(Connection connection, String sql, boolean isClose) throws SQLException {
		Statement stmt = connection.createStatement();
		logger.info(sql);
		stmt.execute(sql);
		if(!connection.getAutoCommit()){
			connection.commit();
		}
		stmt.close();
        if(isClose){
            connection.close();
        }
	}

	
	/**
	 * 准备 sql.
	 * 
	 * @param tableName the table name
	 * @param number the number
	 * @param list the list
	 * @return the string[]
	 */
	public static String[] prepareSqls(DBType databaseType, String tableName,
			int number, List<ColumnObject> list) {
		SqlBuilder sqlBuilder = SqlBuilderFactory.create(databaseType);
		int row = number;
//		if (!customList.isEmpty()) {
//			List<String[]> values = new ArrayList<String[]>(customList.values());
//			row = values.get(0).length;
//		}
		String[] sqls = new String[row];
		for (int i = 0; i < row; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append("insert into ");
			sb.append(tableName);
			sb.append("(");

			for (int j = 0; j < list.size(); j++) {
				ColumnObject columnObject = list.get(j);
				if (j != 0)
					sb.append(",");
				sb.append(columnObject.getName());
			}

			sb.append(") values(");

			for (int j = 0; j < list.size(); j++) {
				ColumnObject columnObject = list.get(j);
				if (j != 0)
					sb.append(",");
                sb.append(sqlBuilder.getValue(columnObject, i));
			}
			sb.append(")");
			sqls[i] = sb.toString();
		}
		return sqls;
	}
}
