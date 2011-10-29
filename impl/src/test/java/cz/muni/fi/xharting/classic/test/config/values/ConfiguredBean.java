package cz.muni.fi.xharting.classic.test.config.values;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("configuredBean")
@Scope(ScopeType.APPLICATION)
public class ConfiguredBean {

    public enum PhoneticAlphabet {
        ALPHA, BRAVO, CHARLIE, DELTA;
    }

    protected String string;
    protected Boolean referenceBool;
    protected boolean primitiveBool;
    protected Integer referenceInteger;
    protected int primitiveInteger;
    protected Long referenceLong;
    protected long primitiveLong;
    protected Float referenceFloat;
    protected float primitiveFloat;
    protected Double referenceDouble;
    protected double primitiveDouble;
    protected Character referenceChar;
    protected char primitiveChar;
    protected String[] stringArray;
    protected Set<String> set;
    protected List<Class<?>> list;
    protected Map<String, String> map;
    protected PhoneticAlphabet enumeration;
    protected BigInteger bigInteger;
    protected BigDecimal bigDecimal;
    protected Class<?> clazz;

    public String getString() {
        return string;
    }

    public Boolean getReferenceBool() {
        return referenceBool;
    }

    public boolean getPrimitiveBool() {
        return primitiveBool;
    }

    public Integer getReferenceInteger() {
        return referenceInteger;
    }

    public int getPrimitiveInteger() {
        return primitiveInteger;
    }

    public Long getReferenceLong() {
        return referenceLong;
    }

    public long getPrimitiveLong() {
        return primitiveLong;
    }

    public Float getReferenceFloat() {
        return referenceFloat;
    }

    public float getPrimitiveFloat() {
        return primitiveFloat;
    }

    public Double getReferenceDouble() {
        return referenceDouble;
    }

    public double getPrimitiveDouble() {
        return primitiveDouble;
    }

    public Character getReferenceChar() {
        return referenceChar;
    }

    public char getPrimitiveChar() {
        return primitiveChar;
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public Set<String> getSet() {
        return set;
    }

    public List<Class<?>> getList() {
        return list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public PhoneticAlphabet getEnumeration() {
        return enumeration;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
