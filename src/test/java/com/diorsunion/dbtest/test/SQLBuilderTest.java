package com.diorsunion.dbtest.test;

import com.diorsunion.dbtest.db.SqlBuilder;
import com.diorsunion.dbtest.db.SqlBuilderFactory;
import com.diorsunion.dbtest.enums.DBType;
import org.junit.Test;

/**
 * Created by dingshan.yyj on 2015/4/27.
 */
public class SQLBuilderTest {
    @Test
    public void testConvertDBLabelToEntityLabel(){
        String source = "hello_world_Jack_zz_A";
        String expect = "helloWorldJackZzA";
        String result = SqlBuilder.convertDBLabelToEntityLabel(source);
        assert(expect == result);
        source = "hello_world";
        expect = "helloWorld";
        result = SqlBuilder.convertDBLabelToEntityLabel(source);
        assert(expect == result);
        source = "hello_world_zz";
        expect = "helloWorldZz";
        result = SqlBuilder.convertDBLabelToEntityLabel(source);
        assert(expect == result);
    }

    @Test
    public void testEntityLabelToDBLabel(){
        String source = "HelloWorldJackZzA";
        String expect = "hello_world_jack_zz_a";
        String result = SqlBuilder.convertEntityLabelToDBLabel(source);
        assert(expect == result);
        source = "HelloWorld";
        expect = "hello_world";
        result = SqlBuilder.convertEntityLabelToDBLabel(source);
        assert(expect == result);
        source = "jarQueryId";
        expect = "jar_query_id";
        result = SqlBuilder.convertEntityLabelToDBLabel(source);
        assert(expect == result);
    }

    @Test
    public void testGenerateCreateSql(){
        SqlBuilder hsql = SqlBuilderFactory.create(DBType.HSQL);
        String s1 = "create table a(id int PRIMARY KEY NOT NULL IDENTITY,name varchar(2048),date datetime,hello_world bigint)";
        String r1 = hsql.getCreateTable("a", Hello.class);
        assert(s1.equalsIgnoreCase(r1));


        String s2 = "create table hello(zz int,name varchar(2048),date datetime,hello_world bigint)";
        String r2 = hsql.getCreateTable("hello", Hello1.class);
        assert(s2.equalsIgnoreCase(r2));

        SqlBuilder mysql = SqlBuilderFactory.create(DBType.MYSQL);
        String s3 = "create table a(id int primary key AUTO_INCREMENT,name varchar(2048),date datetime,hello_world bigint)";
        String r3 = mysql.getCreateTable("a", Hello.class);
        assert(s3.equalsIgnoreCase(r3));
    }
}
