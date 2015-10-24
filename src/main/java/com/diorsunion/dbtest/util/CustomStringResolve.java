package com.diorsunion.dbtest.util;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class CustomStringResolve.
 *
 * @author harley-dog
 */
public class CustomStringResolve {
	
	/** The special. */
	private Map<String,String> special = new HashMap<String, String>();
	
	/** The back special. */
	private Map<String,String> backSpecial = new HashMap<String, String>();	
	
	/**
	 * Instantiates a new custom string resolve.
	 */
	public CustomStringResolve() {
		init("\\\\=", "#special#1#", "=");
		init("\\\\;", "#special#2#", ";");
		init("\\\\,", "#special#3#", ",");
		init("\\\\^", "#special#4#", "^");	
	}
	
	/**
	 * Inits the.
	 *
	 * @param src the src
	 * @param mid the mid
	 * @param result the result
	 */
	private void init(String src,String mid,String result){
		special.put(src, mid);
		backSpecial.put(mid, result);
	}
	
	/**
	 * Convent special.
	 *
	 * @param str the str
	 * @return the string
	 */
	public String conventSpecial(String str){
		for (String key : special.keySet()) {
			str = str.replaceAll(key, special.get(key));	
		}
		return str;
	}
	
	/**
	 * Convent back special.
	 *
	 * @param str the str
	 * @return the string
	 */
	public String conventBackSpecial(String str){
		for (String key : backSpecial.keySet()) {
			str = str.replaceAll(key, backSpecial.get(key));	
		}
		return str;
	}

	/**
	 * Find custom.
	 *
	 * @param customString the custom string
	 * @return the map
	 */
	public Map<String,String> findCustom(String customString){
		String string = conventSpecial(customString);
		
		Map<String,String> customs = new HashMap<String,String>();
		String[] cc = string.split(";");
		for (String c : cc) {
			String[] kv = c.split("=");
			if(kv.length != 2){
				throw new RuntimeException("Custom data format error!");
			}
			customs.put(kv[0], kv[1]);	
		}
		return customs;
	}
	
	/**
	 * Change column object.
	 *
	 * @param customString the custom string
	 * @param columnObject the column object
	 * @return the column object
	 */
	public ColumnObject changeColumnObject(String customString,ColumnObject columnObject) {
		Map<String, String> customs = findCustom(customString);
		for (String key : customs.keySet()) {
			if (key.toUpperCase()
					.equals(columnObject.name.toUpperCase())) {

				String value = customs.get(key);
				if (value.startsWith("{") && value.endsWith("}")) {
					String v = value.substring(1, value.length() - 1);
					String[] customArr = v.split(",");
					String[] arr = new String[customArr.length];
					for (int i = 0; i < customArr.length; i++) {
						arr[i] = conventBackSpecial(customArr[i]);
					}
					columnObject.customs = arr;
					continue;
				}
				if (value.startsWith("[") && value.endsWith("]")) {
					String v = value.substring(1, value.length() - 1);
					columnObject.range = v;
					continue;
				}
				if (value.contains("^")) {
					columnObject.increase =true;

					String[] v = value.split("\\^");
					if (v.length != 1 && v.length != 2) {
						throw new RuntimeException("Custom date format error! ^ error!");
					}
					columnObject.value = conventBackSpecial(v[0]);
					if (v.length == 2) {
						int step = columnObject.step;
						try {
							step = Integer.parseInt(v[1]);
						} catch (Exception e) {
							throw new RuntimeException("Custom date format error! ^ step is not number!");
						}
						columnObject.step= step;
					}
					continue;
				}
				else{
					columnObject.increase = false;
				}
				columnObject.value = conventBackSpecial(customs.get(key));
			}
		}
		return columnObject;
	}
	
}
