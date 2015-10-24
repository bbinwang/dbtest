package com.diorsunion.dbtest.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.springframework.aop.framework.Advised;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Class AnnTestUtils.
 *
 * @author harley-dog
 */
public class AnnTestUtils {
	
	private static final Log logger = LogFactory.getLog(AnnTestUtils.class);
	
	/**
	 * set field.
	 *
	 * @param target the target
	 * @param name the name
	 * @param value the value
	 */
	public static void setField(Object target, String name, Object value) {
		ReflectionTestUtils.setField(unwrap(target), name, value);
	}
	
	/**
	 * set field.
	 *
	 * @param target the target
	 * @param name the name
	 * @param value the value
	 * @param type the type
	 */
	public static void setField(Object target, String name, Object value, Class<?> type) {
		ReflectionTestUtils.setField(unwrap(target), "notifyEngineBO", value, type);
	}
	
	/**
	 * Unwrap.
	 *
	 * @param <T> the
	 * @param proxiedInstance the proxied instance
	 * @return the t
	 */
	@SuppressWarnings("unchecked")  
    private static <T> T unwrap(T proxiedInstance) {  
        if (proxiedInstance instanceof Advised) {  
            try {  
                return unwrap((T) ((Advised) proxiedInstance).getTargetSource().getTarget());  
            } catch (Exception e) {  
                Assert.fail("unwrap to proxy exception:" + proxiedInstance.getClass());
            }  
        }  
        return proxiedInstance;  
    }
	
	public static void assertDBEquals(String expected, String sql, DataSource dataSource) {		
		Connection connection = DataSourceUtils.getConnection(dataSource);
		Assert.assertEquals(expected, getActual( connection, sql));
	}
	
	public static void assertDBCount(int expected, String table, DataSource dataSource) {		
		Connection connection = DataSourceUtils.getConnection(dataSource);
		String sql = "select count(*) from "+table ;
		Assert.assertEquals(expected, Integer.parseInt(getActual( connection, sql)));
	}
	
	private static String getActual(Connection connection, String sql) {
		String actual = null;
		Statement stmt;
		try {
			stmt = connection.createStatement();
			logger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				Object obj = rs.getObject(1);
				if(obj!=null){
					actual = obj.toString();
					logger.debug("result:"+actual);
				}
			}
			else{
				logger.warn("sql is no result");
			}
			stmt.close();
		} catch (SQLException e) {
			logger.error("sql error",e);
			e.printStackTrace();
		}
		return actual;
	}
	
}
