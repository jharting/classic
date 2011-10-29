package cz.muni.fi.xharting.classic.scope.page;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;

/**
 * The page context. Begins during the invoke application phase prior to rendering a page, and lasts until the end of any invoke
 * application phase of a faces request originating from that page. Non-faces requests do not propagate the page scope.
 */
@Inherited
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface PageScoped {

    @SuppressWarnings("all")
    public static class PageScopedLiteral extends AnnotationLiteral<PageScoped> implements PageScoped {
        public static final PageScoped INSTANCE = new PageScopedLiteral();
    }
}
