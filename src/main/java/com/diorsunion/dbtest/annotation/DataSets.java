package com.diorsunion.dbtest.annotation;

import java.lang.annotation.*;



/**
 * The Interface Column.
 *
 * @author harley-dog
 */
@Target({ElementType.METHOD,ElementType.TYPE})   
@Retention(RetentionPolicy.RUNTIME) 
@Inherited
@Documented
public @interface DataSets {
	
	DataSet[] value(); 
	
}
