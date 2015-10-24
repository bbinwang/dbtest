package com.diorsunion.dbtest.annotation;

import com.diorsunion.dbtest.enums.ConfigType;

import java.lang.annotation.*;

/**
 * The Interface AnnTestConfig.
 *
 * @author harley-dog
 */
@Target({ElementType.METHOD,ElementType.TYPE})   
@Retention(RetentionPolicy.RUNTIME) 
@Inherited
@Documented
public @interface DBTestConfig {
	
	/**
	 * 配置类型.
	 *
	 * @return the config type
	 */
	ConfigType configBy() default ConfigType.DB;

	/**
	 * 主键起始数字.
	 *
	 * @return the long
	 */
	long firstId() default 1L;
	
	/**
	 * 数字默认的值.
	 *
	 * @return the long
	 */
	long defaultNumber() default 1L;
}
