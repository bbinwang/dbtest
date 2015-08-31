package com.diorsunion.dbtest.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by dingshan.yyj on 2015/4/30.
 */
@Entity(name="hello")
public class Hello1 {
    @Column(name="zz")
    private int id;
    private String name;
    private Date date;
    private long helloWorld;
}
