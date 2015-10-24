package com.diorsunion.dbtest;

import com.diorsunion.dbtest.annotation.DBTestConfig;
import com.diorsunion.dbtest.annotation.DataSet;
import com.diorsunion.dbtest.annotation.DataSets;
import com.diorsunion.dbtest.db.SqlBuilder;
import com.diorsunion.dbtest.db.SqlBuilderFactory;
import com.diorsunion.dbtest.enums.DBType;
import com.diorsunion.dbtest.util.ColumnObject;
import com.diorsunion.dbtest.util.CustomStringResolve;
import com.diorsunion.dbtest.util.DBDefault;
import com.diorsunion.dbtest.util.SqlHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBTest {
    private DBTest() {
    }

    public static DBTest getInstance() {
        return new DBTest();
    }

    public final static List<DataSet> getDateSets(Method method) {
        List<DataSet> allDataSet = new ArrayList<DataSet>();

        DataSet dataSet = method.getAnnotation(DataSet.class);
        if (dataSet != null) {
            allDataSet.add(dataSet);
        }
        DataSets dataSets = method.getAnnotation(DataSets.class);
        if (dataSets != null) {
            DataSet[] dsArray = dataSets.value();
            for (DataSet ds : dsArray) {
                allDataSet.add(ds);
            }
        }
        return allDataSet;
    }


    private final static Log logger = LogFactory.getLog(DBTest.class);
    /**
     * Inits the data.
     *
     * @param dataSources the data sources
     * @param method      the method
     * @throws SQLException                 the sQL exception
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException                 the sAX exception
     * @throws IOException                  Signals that an I/O exception has occurred.
     */
    public static void initData(Map<String, DataSource> dataSources, Method method) throws SQLException, ParserConfigurationException, SAXException, IOException {
        List<DataSet> dataSets = getDateSets(method);
        if (!dataSets.isEmpty()) {
            for (int i = dataSets.size() - 1; i >= 0; i--) {
                DataSet ds = dataSets.get(i);
                DataSource dataSource = getDataSource(dataSources, ds.dataSource());
                Connection connection = dataSource.getConnection();
                clearSingleData(connection, ds, SqlBuilder.getTableName(ds));
            }
            for (DataSet ds : dataSets) {
                DataSource dataSource = getDataSource(dataSources, ds.dataSource());
                Connection connection = dataSource.getConnection();
                initSingleData(connection, ds, method);
            }

        }
    }

    /**
     * Gets the data source.
     *
     * @param dataSources the data sources
     * @param dsName      the ds name
     * @return the data source
     */
    private final static DataSource getDataSource(Map<String, DataSource> dataSources, String dsName) {
        if (dsName != null && dsName.length() != 0) {
            return dataSources.get(dsName);
        }
        return dataSources.get(Constants.DEFAULT_DATASOURCE);
    }

    /**
     * init the data.
     *
     * @param connection the connection
     * @param dataSet    the data set
     * @param method     the method
     * @throws SQLException                 the sQL exception
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException                 the sAX exception
     * @throws IOException                  Signals that an I/O exception has occurred.
     */
    private final static void initSingleData(Connection connection, DataSet dataSet, Method method) throws SQLException, ParserConfigurationException, SAXException, IOException {
        String tableName = SqlBuilder.getTableName(dataSet);
        List<ColumnObject> list = SqlBuilder.getColumnsByClass(dataSet.entityClass());

        if (dataSet.custom() != null && dataSet.custom().length() > 0) {
            changeColumnsByCustomAnnotation(dataSet.custom(), list);
        }

        String[] sqls = SqlHelper.prepareSqls(
                SqlHelper.getDatabaseType(connection, dataSet.dbType()),
                tableName, dataSet.number(), list);
        SqlHelper.executeBatch(connection, sqls,true);
    }

    private final static void clearSingleData(Connection connection, DataSet dataSet, String tableName) throws SQLException {
        if(!Object.class.equals(dataSet.entityClass())){
            SqlHelper.execute(connection, "drop table if exists "+tableName, false);
            DBType dbType = SqlHelper.getDatabaseType(connection, dataSet.dbType());
            SqlBuilder sqlBuilder = SqlBuilderFactory.create(dbType);
            String createTableSql = sqlBuilder.getCreateTable(tableName,dataSet.entityClass());
            SqlHelper.execute(connection,createTableSql,true);
        }else if(tableName!=null && !"".equals(tableName)){
            SqlHelper.execute(connection, "delete from " + tableName,  true);
        }
    }

    /**
     * Gets the columns by db.
     *
     * @param tableName  the table name
     * @param connection the connection
     * @return the columns by db
     * @throws SQLException the sQL exception
     */
    private final static List<ColumnObject> getColumnsByDB(String tableName, Connection connection, DBTestConfig config) throws SQLException {
        List<ColumnObject> list = new ArrayList<ColumnObject>();
        DatabaseMetaData metadata = connection.getMetaData();
        List<String> primaryKeys = SqlHelper.getPrimaryKeys(tableName, metadata);
        ResultSet resultSet = metadata.getColumns(null, "%", tableName, "%");
        while (resultSet.next()) {
            String name = resultSet.getString("COLUMN_NAME");
            String type = resultSet.getString("TYPE_NAME");
            int columnSize = resultSet.getInt("COLUMN_SIZE");
            int decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
            //remove mysql bigint unsigned 之类
            type = type.indexOf(" ") == -1 ? type : type.substring(0, type.indexOf(" "));

            //int size = resultSet.getInt("COLUMN_SIZE");
            boolean isKey = primaryKeys.contains(name);
            ColumnObject columnObject = DBDefault.convent(name, type, isKey, columnSize, decimalDigits, config);
            list.add(columnObject);
        }
        return list;
    }

    /**
     * @param list
     * @return
     */
    private final static void changeColumnsByCustomAnnotation(String customString, List<ColumnObject> list) {
        CustomStringResolve customStringResolve = new CustomStringResolve();
        for (ColumnObject columnObject : list) {
            customStringResolve.changeColumnObject(customString, columnObject);
        }
    }


}
