/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import static org.jboss.seam.ComponentType.ENTITY_BEAN;
import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.ComponentType.MESSAGE_DRIVEN_BEAN;
import static org.jboss.seam.ComponentType.STATEFUL_SESSION_BEAN;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.ScopeType.PAGE;
import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.ScopeType.UNSPECIFIED;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.NamingException;
import javax.servlet.http.HttpSessionActivationListener;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.DataBinderClass;
import org.jboss.seam.annotations.DataSelectorClass;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Import;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.PerNestedConversation;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.bpm.BeginTask;
import org.jboss.seam.annotations.bpm.EndTask;
import org.jboss.seam.annotations.bpm.StartTask;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.faces.Validator;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.intercept.InterceptorType;
import org.jboss.seam.annotations.intercept.Interceptors;
import org.jboss.seam.annotations.security.PermissionCheck;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.security.RoleCheck;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.MethodExpression;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.databinding.DataBinder;
import org.jboss.seam.databinding.DataSelector;
import org.jboss.seam.intercept.Interceptor;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Metamodel class for component classes.
 * 
 * A Seam component is any class with a @Name annotation.
 * 
 * @author Thomas Heute
 * @author Gavin King
 * 
 */
@Scope(ScopeType.APPLICATION)
public class Component extends Model {
    // only used for tests
    public Component(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    // only used for tests
    public Component(Class<?> clazz, String componentName) {
        throw new UnsupportedOperationException();
    }

    // only used for tests
    public Component(Class<?> clazz, Context applicationContext) {
        throw new UnsupportedOperationException();
    }

    public Component(Class<?> clazz, String componentName, ScopeType componentScope, boolean startup, String[] dependencies, String jndiName) {
        throw new UnsupportedOperationException();
    }

    public void addInterceptor(Object interceptorInstance) {
        throw new UnsupportedOperationException();
    }

    public void addInterceptor(Interceptor interceptor) {
        throw new UnsupportedOperationException();
    }

    public boolean beanClassHasAnnotation(Class annotationType) {
        throw new UnsupportedOperationException();
    }

    public boolean beanClassHasAnnotation(String annotationName) {
        throw new UnsupportedOperationException();
    }

    public boolean businessInterfaceHasAnnotation(Class annotationType) {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public ComponentType getType() {
        throw new UnsupportedOperationException();
    }

    public ScopeType getScope() {
        throw new UnsupportedOperationException();
    }

    public List<Interceptor> getInterceptors(InterceptorType type) {
        throw new UnsupportedOperationException();
    }

    public List<Object> createUserInterceptors(InterceptorType type) {
        throw new UnsupportedOperationException();
    }

    /**
     * For use with Seam debug page.
     * 
     * @return the server-side interceptor stack
     */
    public List<Interceptor> getServerSideInterceptors() {
        throw new UnsupportedOperationException();
    }

    /**
     * For use with Seam debug page.
     * 
     * @return the client-side interceptor stack
     */
    public List<Interceptor> getClientSideInterceptors() {
        throw new UnsupportedOperationException();
    }

    public Method getDestroyMethod() {
        throw new UnsupportedOperationException();
    }

    public Collection<Method> getRemoveMethods() {
        throw new UnsupportedOperationException();
    }

    public Method getRemoveMethod(String name) {
        throw new UnsupportedOperationException();
    }

    public boolean hasPreDestroyMethod() {
        throw new UnsupportedOperationException();
    }

    public boolean hasPostConstructMethod() {
        throw new UnsupportedOperationException();
    }

    public boolean hasPrePassivateMethod() {
        throw new UnsupportedOperationException();
    }

    public boolean hasPostActivateMethod() {
        throw new UnsupportedOperationException();
    }

    public boolean hasDestroyMethod() {
        throw new UnsupportedOperationException();
    }

    public boolean hasCreateMethod() {
        throw new UnsupportedOperationException();
    }

    public Method getCreateMethod() {
        throw new UnsupportedOperationException();
    }

    public boolean hasUnwrapMethod() {
        throw new UnsupportedOperationException();
    }

    public Method getUnwrapMethod() {
        throw new UnsupportedOperationException();
    }

    public List<BijectedAttribute<Out>> getOutAttributes() {
        throw new UnsupportedOperationException();
    }

    public List<BijectedAttribute<In>> getInAttributes() {
        throw new UnsupportedOperationException();
    }

    public boolean needsInjection() {
        throw new UnsupportedOperationException();
    }

    public boolean needsOutjection() {
        throw new UnsupportedOperationException();
    }

    public void destroy(Object bean) {
        throw new UnsupportedOperationException();
    }

    public void initialize(Object bean) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * Inject context variable values into @In attributes of a component instance.
     * 
     * @param bean a Seam component instance
     * @param enforceRequired should we enforce required=true?
     */
    public void inject(Object bean, boolean enforceRequired) {
        throw new UnsupportedOperationException();
    }

    /**
     * Null out any @In attributes of a component instance.
     * 
     * @param bean a Seam component instance
     */
    public void disinject(Object bean) {
        throw new UnsupportedOperationException();
    }

    /**
     * Outject context variable values from @Out attributes of a component instance.
     * 
     * @param bean a Seam component instance
     * @param enforceRequired should we enforce required=true?
     */
    public void outject(Object bean, boolean enforceRequired) {
        throw new UnsupportedOperationException();
    }

    public boolean isInstance(Object bean) {
        throw new UnsupportedOperationException();
    }

    public static Set<Class> getBusinessInterfaces(Class clazz) {
        throw new UnsupportedOperationException();
    }

    public Set<Class> getBusinessInterfaces() {
        throw new UnsupportedOperationException();
    }

    public static String getComponentName(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    public static Component forName(String name) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(Class<?> clazz, boolean create) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(Class<?> clazz, ScopeType scope) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(Class<?> clazz, ScopeType scope, boolean create) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(String name) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(String name, boolean create) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(String name, boolean create, boolean allowAutocreation) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(String name, ScopeType scope) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(String name, ScopeType scope, boolean create) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstance(String name, ScopeType scope, boolean create, boolean allowAutocreation) {
        throw new UnsupportedOperationException();
    }

    public static Object getInstanceFromFactory(String name) {
        throw new UnsupportedOperationException();
    }

    public Object newInstance() {
        throw new UnsupportedOperationException();
    }

    public boolean hasDefaultRemoveMethod() {
        throw new UnsupportedOperationException();
    }

    public Method getDefaultRemoveMethod() {
        throw new UnsupportedOperationException();
    }

    public void callCreateMethod(Object instance) {
        if (hasCreateMethod()) {
            callComponentMethod(instance, getCreateMethod());
        }
    }

    public void callDestroyMethod(Object instance) {
        if (hasDestroyMethod()) {
            callComponentMethod(instance, getDestroyMethod());
        }
    }

    public void callPreDestroyMethod(Object instance) {
        if (hasPreDestroyMethod()) {
            callComponentMethod(instance, getPreDestroyMethod());
        }
    }

    public void callPostConstructMethod(Object instance) {
        if (hasPostConstructMethod()) {
            callComponentMethod(instance, getPostConstructMethod());
        }
    }

    public void callPrePassivateMethod(Object instance) {
        if (hasPrePassivateMethod()) {
            callComponentMethod(instance, getPrePassivateMethod());
        }
    }

    public void callPostActivateMethod(Object instance) {
        if (hasPostActivateMethod()) {
            callComponentMethod(instance, getPostActivateMethod());
        }
    }

    public Method getPostActivateMethod() {
        throw new UnsupportedOperationException();
    }

    public Method getPrePassivateMethod() {
        throw new UnsupportedOperationException();
    }

    public Method getPostConstructMethod() {
        throw new UnsupportedOperationException();
    }

    public Method getPreDestroyMethod() {
        throw new UnsupportedOperationException();
    }

    public long getTimeout() {
        throw new UnsupportedOperationException();
    }

    public Object callComponentMethod(Object instance, Method method, Object... parameters) {
        throw new UnsupportedOperationException();
    }

    public boolean isInterceptionEnabled() {
        throw new UnsupportedOperationException();
    }

    public boolean isStartup() {
        throw new UnsupportedOperationException();
    }

    public boolean isSynchronize() {
        throw new UnsupportedOperationException();
    }

    public String[] getDependencies() {
        throw new UnsupportedOperationException();
    }

    public boolean isLifecycleMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    public boolean isConversationManagementMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    public List<BijectedAttribute> getPersistenceContextAttributes() {
        throw new UnsupportedOperationException();
    }

    public Collection<Namespace> getImports() {
        throw new UnsupportedOperationException();
    }

    public Namespace getNamespace() {
        throw new UnsupportedOperationException();
    }

    public boolean isPerNestedConversation() {
        throw new UnsupportedOperationException();
    }

    public boolean hasConversationManagementMethods() {
        throw new UnsupportedOperationException();
    }

    public boolean isSecure() {
        throw new UnsupportedOperationException();
    }
    
    public interface BijectedAttribute<T extends Annotation>
    {
       public String getName();
       public T getAnnotation();
       public Class getType();
       public void set(Object bean, Object value);
       public Object get(Object bean);
    }
}
