package com.diorsunion.dbtest.config;

import com.diorsunion.dbtest.annotation.DBTestConfig;

import java.lang.reflect.Method;

public class ConfigManager {
	
	/**
	 * 取方法上的Clean集合.
	 * 
	 * @param method
	 *            the method
	 * @return the date sets
	 */
	public static DBTestConfig getConfig(Method method) {
		DBTestConfig config = method.getAnnotation(DBTestConfig.class);
		
		// 如果方法上没有注释，试着去类上取
		if (config == null) {
			config =  method.getDeclaringClass().getAnnotation(DBTestConfig.class);
		}
		
		return config;
	}
}
