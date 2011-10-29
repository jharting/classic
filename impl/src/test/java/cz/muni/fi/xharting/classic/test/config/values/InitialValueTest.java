package cz.muni.fi.xharting.classic.test.config.values;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InitialValueTest {

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", ConfiguredBean.class, ConfiguredBeanWithSetters.class).addAsResource(
                "cz/muni/fi/xharting/classic/test/config/values/initial-value-components.xml", "META-INF/components.xml");
    }

    @Test
    public void testElementsWithFields(@Named("configuredBean") ConfiguredBean bean) {
        testFlatValues(bean);
        testNonFlatValues(bean);
    }

    @Test
    public void testElementsWithMethods(@Named("configuredBeanWithSetters") ConfiguredBeanWithSetters bean) {
        testFlatValues(bean);
        testNonFlatValues(bean);
        assertEquals(21, bean.getInvocationCount());
    }

    private void testFlatValues(ConfiguredBean bean) {
        assertEquals("alpha", bean.getString());
        assertEquals(true, bean.getReferenceBool());
        assertEquals(false, bean.getPrimitiveBool());
        assertEquals(Integer.valueOf(13), bean.getReferenceInteger());
        assertEquals(26, bean.getPrimitiveInteger());
        assertEquals(Long.valueOf(13l), bean.getReferenceLong());
        assertEquals(26l, bean.getPrimitiveLong());
        assertEquals(Float.valueOf(13.3f), bean.getReferenceFloat());
        assertEquals(26.6f, bean.getPrimitiveFloat(), 0f);
        assertEquals(Double.valueOf(123.45d), bean.getReferenceDouble());
        assertEquals(234.56d, bean.getPrimitiveDouble(), 0d);
        assertEquals(Character.valueOf('a'), bean.getReferenceChar());
        assertEquals('b', bean.getPrimitiveChar());
        assertEquals(ConfiguredBean.PhoneticAlphabet.CHARLIE, bean.getEnumeration());
        assertEquals(BigInteger.valueOf(7l), bean.getBigInteger());
        assertEquals(BigDecimal.valueOf(7.7d), bean.getBigDecimal());
        assertEquals(NullPointerException.class, bean.getClazz());
    }

    @SuppressWarnings("serial")
    private void testNonFlatValues(ConfiguredBean bean) {
        assertTrue(Arrays.equals(bean.getStringArray(), new String[] { "alpha", "bravo" }));
        assertEquals(new HashSet<String>() {
            {
                add("charlie");
                add("delta");
            }
        }, bean.getSet());
        assertEquals(new ArrayList<Class<?>>() {
            {
                add(Integer.class);
                add(String.class);
            }
        }, bean.getList());
        assertEquals(new HashMap<String, String>() {
            {
                put("foo", "bar");
                put("baz", "foo");
            }
        }, bean.getMap());
    }
}
