/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General public abstract License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General public abstract License for more details.
 *
 * You should have received a copy of the GNU Lesser General public abstract
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.core;

import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.util.StaticLookup;

/**
 * Allows the conversation timeout to be set per-conversation, and the conversation description and switchable outcome to be set
 * when the application requires workspace management functionality.
 * 
 * @author Gavin King
 * 
 */
public abstract class Conversation {
    public abstract void killAllOthers();

    /**
     * Get the timeout for this conversation instance.
     * 
     * @return the timeout in millis
     */
    public abstract Integer getTimeout();

    /**
     * Set the timeout for this converstaion instance.
     * 
     * @param timeout the timeout in millis
     */
    public abstract void setTimeout(Integer timeout);

    public abstract Integer getConcurrentRequestTimeout();

    public abstract void setConcurrentRequestTimeout(Integer concurrentRequestTimeout);

    /**
     * Get the conversation id.
     */
    public abstract String getId();

    public abstract String getDescription();

    public abstract String getViewId();

    /**
     * Sets the description of this conversation, for use in the conversation list, breadcrumbs, or conversation switcher.
     */
    public abstract void setDescription(String description);

    /**
     * Sets the JSF outcome to be used when we switch back to this conversation from the conversation list, breadcrumbs, or
     * conversation switcher.
     */
    public abstract void setViewId(String outcome);

    /**
     * Switch back to the last defined view-id for the current conversation.
     * 
     * @return true if a redirect occurred
     */
    public abstract boolean redirect();

    /**
     * End a child conversation and redirect to the last defined view-id for the parent conversation.
     * 
     * @return true if a redirect occurred
     */
    public abstract boolean endAndRedirect();

    /**
     * End a child conversation and redirect to the last defined view-id for the parent conversation.
     * 
     * @param endBeforeRedirect should the conversation be destroyed before the redirect?
     * @return true if a redirect occurred
     */
    public abstract boolean endAndRedirect(boolean endBeforeRedirect);

    /**
     * Leave the scope of the current conversation
     */
    public abstract void leave();

    /**
     * Start a long-running conversation, if no long-running conversation is active.
     * 
     * @return true if a new long-running conversation was begun
     */
    public abstract boolean begin();

    /**
     * Start a nested conversation.
     * 
     * @throws IllegalStateException if no long-running conversation was active
     */
    public abstract void beginNested();

    /**
     * Begin or join a conversation, or begin a new nested conversation.
     * 
     * @param join if a conversation is active, should we join it?
     * @param nested if a conversation is active, should we start a new nested conversation?
     * @return true if a new long-running conversation was begun
     */
    public abstract boolean begin(boolean join, boolean nested);

    /**
     * End a long-runnning conversation.
     */
    public abstract void end();

    /**
     * End a long-runnning conversation and destroy it before a redirect.
     */
    public abstract void endBeforeRedirect();

    /**
     * End a long-runnning conversation.
     * 
     * @param beforeRedirect should the conversation be destroyed before any redirect?
     */
    public abstract void end(boolean beforeRedirect);

    /**
     * Is this conversation long-running? Note that this method returns false even when the conversation has been temporarily
     * promoted to long-running for the course of a redirect, so it does what the user really expects.
     */
    public abstract boolean isLongRunning();

    /**
     * Is this conversation a nested conversation?
     */
    public abstract boolean isNested();

    /**
     * Get the id of the immediate parent of a nested conversation
     */
    public abstract String getParentId();

    /**
     * Get the id of root conversation of a nested conversation
     */
    public abstract String getRootId();

    /**
     * "Pop" the conversation stack, switching to the parent conversation
     */
    public abstract void pop();

    /**
     * Pop the conversation stack and redirect to the last defined view-id for the parent conversation.
     * 
     * @return true if a redirect occurred
     */
    public abstract boolean redirectToParent();

    /**
     * Switch to the root conversation
     */
    public abstract void root();

    /**
     * Switch to the root conversation and redirect to the last defined view-id for the root conversation.
     * 
     * @return true if a redirect occurred
     */
    public abstract boolean redirectToRoot();

    /**
     * Change the flush mode of all Seam-managed persistence contexts in this conversation.
     */
    public abstract void changeFlushMode(FlushModeType flushMode);

    public static Conversation instance() {
        return StaticLookup.lookupBean(Conversation.class);
    }
}
