package com.diorsunion.dbtest.annotation;

import com.diorsunion.dbtest.enums.DBType;

import java.lang.annotation.*;




/**
 * 数据定义annotation.
 * 
 * <p>
 * 基本用法：
 * <pre><code>@DataSet</code>(tbName="TABLE_NAME",number=5)</pre>
 * <p>
 * 默认取spring配置中第一个数据源，如果多数据源。如：
 * <br>
 * <pre><code>@DataSet</code>(tbName="rpt_z_cpmprice",number=5,dataSource="dataSourceSds")</pre>
 * <p>
 * 可以在全局配置外指定自己测试需要的个性化数据。如：
 * <pre>
 * <code>@DataSet</code>(tbName="TABLE_NAME", 
 *           customColumns={"configkey","configvalue"}, 
 *           customRows={  
 *               <code>@CustomRow</code>({"TEST_KEY","hello world"}),  
 *               <code>@CustomRow</code>({"CONTRACT_VERSION_KEY","2"})  
 *           })  
 *   })
 * </pre>
 * <p>
 * 可以在全局配置外临时指定当前测试的列的属性。如：
 * <br>
 * <pre>
 * <code>@DataSet</code>(tbName="TABLE_NAME",number=3,columns={
 *        <code>@Column</code>(name="ID",valueType=ColumnType.NUMBER,value="99999901"),
 *        <code>@Column</code>(name="NAME",valueType=ColumnType.VARCHAR,value="name") 
 * })  
 * </pre>
 * <p>
 * 可以通过文件自定义数据。如：
 * <br>
 * <pre>
 * <code>@DataSet</code>(tbName="SIZE_INFO",dataFile="size_info.csv")  
 * </pre>
 * <p>
 * @author harley-dog
 */
@Target({ElementType.METHOD,ElementType.TYPE})   
@Retention(RetentionPolicy.RUNTIME) 
@Inherited
@Documented
public @interface DataSet {
	/**
	 * 数据源.
	 *
	 * @return the string
	 */
	String dataSource() default "";

	/**
	 * 表实体
	 *
	 * @return the string
	 */
	Class<?> entityClass() default Object.class;

	/**
	 * 表名，如果为null，则用entity.className
	 *
	 * @return the string
	 */
	String tableName() default "";
	
	/**
	 * 插入数据条数（必选）.
	 *
	 * @return the int
	 */
	int number() default 1;
	
	/**
	 * 自定义.
	 *
	 * @return the string
	 */
	String custom() default "";

	/**
	 * 数据库类型(如果数据源获取不到数据库类型(如TDDL),自行指定).
	 *
	 * @return the dB type
	 */
	DBType dbType() default DBType.UN_DEFINED;
}
