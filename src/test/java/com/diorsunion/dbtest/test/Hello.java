package com.diorsunion.dbtest.test;

import javax.persistence.GeneratedValue;
import java.util.Date;

/**
 * @author harley-dog on 2015/4/30.
 */

public class Hello {
    @GeneratedValue
    private int id;
    private String name;
    private Date date;
    private long helloWorld;
}
