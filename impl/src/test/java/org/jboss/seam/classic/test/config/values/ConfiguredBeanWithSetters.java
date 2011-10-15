package org.jboss.seam.classic.test.config.values;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("configuredBeanWithSetters")
@Scope(ScopeType.APPLICATION)
public class ConfiguredBeanWithSetters extends ConfiguredBean {

    private int invocationCount = 0;
    
    public void setString(String string) {
        invocationCount++;
        this.string = string;
    }

    public void setReferenceBool(Boolean referenceBool) {
        invocationCount++;
        this.referenceBool = referenceBool;
    }

    public void setPrimitiveBool(boolean primitiveBool) {
        invocationCount++;
        this.primitiveBool = primitiveBool;
    }

    public void setReferenceInteger(Integer referenceInteger) {
        invocationCount++;
        this.referenceInteger = referenceInteger;
    }

    public void setPrimitiveInteger(int primitiveInteger) {
        invocationCount++;
        this.primitiveInteger = primitiveInteger;
    }

    public void setReferenceLong(Long referenceLong) {
        invocationCount++;
        this.referenceLong = referenceLong;
    }

    public void setPrimitiveLong(long primitiveLong) {
        invocationCount++;
        this.primitiveLong = primitiveLong;
    }

    public void setReferenceFloat(Float referenceFloat) {
        invocationCount++;
        this.referenceFloat = referenceFloat;
    }

    public void setPrimitiveFloat(float primitiveFloat) {
        invocationCount++;
        this.primitiveFloat = primitiveFloat;
    }

    public void setReferenceDouble(Double referenceDouble) {
        invocationCount++;
        this.referenceDouble = referenceDouble;
    }

    public void setPrimitiveDouble(double primitiveDouble) {
        invocationCount++;
        this.primitiveDouble = primitiveDouble;
    }

    public void setReferenceChar(Character referenceChar) {
        invocationCount++;
        this.referenceChar = referenceChar;
    }

    public void setPrimitiveChar(char primitiveChar) {
        invocationCount++;
        this.primitiveChar = primitiveChar;
    }

    public void setStringArray(String[] stringArray) {
        invocationCount++;
        this.stringArray = stringArray;
    }

    public void setSet(Set<String> set) {
        invocationCount++;
        this.set = set;
    }

    public void setList(List<Class<?>> list) {
        invocationCount++;
        this.list = list;
    }

    public void setMap(Map<String, String> map) {
        invocationCount++;
        this.map = map;
    }

    public void setEnumeration(PhoneticAlphabet enumeration) {
        invocationCount++;
        this.enumeration = enumeration;
    }

    public void setBigInteger(BigInteger bigInteger) {
        invocationCount++;
        this.bigInteger = bigInteger;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        invocationCount++;
        this.bigDecimal = bigDecimal;
    }

    public void setClazz(Class<?> clazz) {
        invocationCount++;
        this.clazz = clazz;
    }

    public int getInvocationCount() {
        return invocationCount;
    }
}
