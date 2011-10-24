package org.jboss.seam.util;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.beanManager.BeanManagerLocator;

public class StaticLookup {

	public static BeanManager lookupBeanManager() {
		BeanManagerLocator locator = new BeanManagerLocator();
		if (locator.isBeanManagerAvailable()) {
			return locator.getBeanManager();
		}
		throw new IllegalStateException("Unable to lookup BeanManager");
	}

	@SuppressWarnings("unchecked")
	public static <T> T lookupBean(Class<T> clazz) {
		BeanManager manager = lookupBeanManager();
		Set<Bean<?>> beans = manager.getBeans(clazz);
		Bean<?> bean = manager.resolve(beans);
		if (bean == null) {
			throw new UnsatisfiedResolutionException("Unable to lookup "
					+ clazz.getName());
		}
		CreationalContext<?> ctx = manager.createCreationalContext(bean);
		return (T) manager.getReference(bean, clazz, ctx);
	}
}
