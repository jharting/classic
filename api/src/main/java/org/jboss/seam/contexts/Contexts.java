/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;


/**
 * Provides access to the current contexts associated with the thread.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 */
public class Contexts {

    public static Context getEventContext() {
        throw new UnsupportedOperationException();
    }

    public static Context getMethodContext() {
        throw new UnsupportedOperationException();
    }

    public static Context getPageContext() {
        throw new UnsupportedOperationException();
    }

    public static Context getSessionContext() {
        throw new UnsupportedOperationException();
    }

    public static Context getApplicationContext() {
        throw new UnsupportedOperationException();
    }

    public static Context getConversationContext() {
        throw new UnsupportedOperationException();
    }

    public static Context getBusinessProcessContext() {
        throw new UnsupportedOperationException();
    }

    public static boolean isConversationContextActive() {
        throw new UnsupportedOperationException();
    }

    public static boolean isEventContextActive() {
        throw new UnsupportedOperationException();
    }

    public static boolean isMethodContextActive() {
        throw new UnsupportedOperationException();
    }

    public static boolean isPageContextActive() {
        throw new UnsupportedOperationException();
    }

    public static boolean isSessionContextActive() {
        throw new UnsupportedOperationException();
    }

    public static boolean isApplicationContextActive() {
        throw new UnsupportedOperationException();
    }

    public static boolean isBusinessProcessContextActive() {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove the named component from all contexts.
     */
    public static void removeFromAllContexts(String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Search for a named attribute in all contexts, in the following order: method, event, page, conversation, session,
     * business process, application.
     * 
     * @throw new UnsupportedOperationException();
     */
    public static Object lookupInStatefulContexts(String name) {
        throw new UnsupportedOperationException();
    }
}
