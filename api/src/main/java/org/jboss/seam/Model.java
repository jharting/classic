package org.jboss.seam;

/**
 * Base class of metamodels. For a class which is neither an entity nor a Seam component, the concrete type of the metamodel
 * object will be Model. For components or entities it is a subclass of Model.
 * 
 * @author Gavin King
 * 
 */
public class Model {

    protected Model() {
    }

    public Model(Class<?> beanClass) {
        throw new UnsupportedOperationException();
    }

    public final Class<?> getBeanClass() {
        throw new UnsupportedOperationException();
    }

    public static Model forClass(Class clazz) {
        throw new UnsupportedOperationException();
    }

    static String getModelName(Class clazz) {
        return clazz.getName() + ".model";
    }

}
