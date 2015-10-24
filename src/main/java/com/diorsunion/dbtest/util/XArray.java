package com.diorsunion.dbtest.util;

/**
 * The Class XArray.
 *
 * @author harley-dog
 */
public class XArray {

	/**
	 * Exchange.
	 *
	 * @param a the a
	 * @return the int[][]
	 */
	public static int[][] exchange(int[][] a) {
		int b[][] = new int[a[1].length][a.length];
		int len_a = a.length;
		int len_b = b.length;

		for (int i = 0; i < len_b; i++) {
			for (int j = 0; j < len_a; j++)
				b[i][j] = a[j][i];
		}
		return b;
	}

	/**
	 * Exchange.
	 *
	 * @param a the a
	 * @return the string[][]
	 */
	public static String[][] exchange(String[][] a) {
		String b[][] = new String[a[1].length][a.length];
		int len_a = a.length;
		int len_b = b.length;

		for (int i = 0; i < len_b; i++) {
			for (int j = 0; j < len_a; j++)
				b[i][j] = a[j][i];
		}
		return b;
	}
	
	/**
	 * Cut.
	 *
	 * @param a the a
	 * @param start the start
	 * @return the string[]
	 */
	public static String[] subArray(String[] a,int start) {
		String b[] = new String[a.length-start]; 
		for (int i = 0; i < b.length; i++) {
			b[i] = a[i+start];
		}
		return b;
	}

}
