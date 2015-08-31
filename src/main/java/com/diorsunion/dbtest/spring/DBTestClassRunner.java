package com.diorsunion.dbtest.spring;

import com.diorsunion.dbtest.Constants;
import com.diorsunion.dbtest.DBTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * The Class ObunitSpringJUnit4ClassRunner.
 *
 * @author 王尼玛
 */
public class DBTestClassRunner extends SpringJUnit4ClassRunner {

    private static final Log logger = LogFactory.getLog(DBTestClassRunner.class);

    /**
     * Instantiates a new obunit spring j unit4 class runner.
     *
     * @param clazz the clazz
     * @throws org.junit.runners.model.InitializationError the initialization error
     */
    public DBTestClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        logger.info("dbtest begin：method=" + frameworkMethod.getMethod());
        long t1 = System.currentTimeMillis();
        try {
            prepareData(frameworkMethod.getMethod());
        } catch (Exception e) {
            logger.error(frameworkMethod.getMethod() + "is error!", e);
            Description description = describeChild(frameworkMethod);
            new EachTestNotifier(notifier, description).addFailure(e);
            return;
        }
        logger.info("dbtest end：method=" + frameworkMethod.getMethod() + "，time cost：" + (System.currentTimeMillis() - t1));
        super.runChild(frameworkMethod, notifier);
    }

    /**
     * Prepare data.
     *
     * @param method the method
     * @throws java.sql.SQLException                          the sQL exception
     * @throws java.io.IOException                            Signals that an I/O exception has occurred.
     * @throws javax.xml.parsers.ParserConfigurationException the parser configuration exception
     * @throws org.xml.sax.SAXException                       the sAX exception
     */
    private void prepareData(Method method) throws SQLException, IOException, ParserConfigurationException, SAXException {
        logger.debug("begin data prepare!");
        DBTest helper = DBTest.getInstance();
        ApplicationContext ctx = getApplicationContent(method);
        helper.initData(getDataAllSource(ctx), method);
        logger.debug("end data prepare!");
    }


    /**
     * Gets the application content.
     *
     * @param method the method
     * @return the application content
     */
    private ApplicationContext getApplicationContent(Method method) {
        TestContextManager testContextManager = getTestContextManager();
        try {
            Method m = testContextManager.getClass().getDeclaredMethod("getTestContext", new Class[]{});
            m.setAccessible(true);
            TestContext testContext = (TestContext) m.invoke(testContextManager, new Object[]{});
            return testContext.getApplicationContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, DataSource> getDataAllSource(ApplicationContext ctx) {
        Map<String, DataSource> map = new HashMap<String, DataSource>();
        String[] beanNames = ctx.getBeanNamesForType(DataSource.class);
        for (String beanName : beanNames) {
            map.put(beanName, (DataSource) ctx.getBean(beanName));
        }
        map.put(Constants.DEFAULT_DATASOURCE, (DataSource) ctx.getBean(beanNames[0]));
        return map;
    }
}
