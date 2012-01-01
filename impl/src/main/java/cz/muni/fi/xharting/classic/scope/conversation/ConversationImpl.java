package cz.muni.fi.xharting.classic.scope.conversation;

import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.core.Conversation;

import com.google.common.primitives.Ints;

/**
 * Delegates method calls to the CDI conversation bean.
 * 
 * @author Jozef Hartinger
 * 
 */
@ConversationScoped
@Named("org.jboss.seam.core.conversation")
public class ConversationImpl extends Conversation implements Serializable {

    private static final long serialVersionUID = 2440395617666629619L;
    private Integer concurrentRequestTimeout = Synchronized.DEFAULT_TIMEOUT;

    @Inject
    private javax.enterprise.context.Conversation delegate;

    @Override
    public void killAllOthers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getTimeout() {
        return Ints.checkedCast(delegate.getTimeout());
    }

    @Override
    public void setTimeout(Integer timeout) {
        delegate.setTimeout(timeout);
    }

    @Override
    public Integer getConcurrentRequestTimeout() {
        return concurrentRequestTimeout;
    }

    @Override
    public void setConcurrentRequestTimeout(Integer concurrentRequestTimeout) {
        this.concurrentRequestTimeout = concurrentRequestTimeout;
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getViewId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setViewId(String outcome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean redirect() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endAndRedirect() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean endAndRedirect(boolean endBeforeRedirect) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void leave() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean begin() {
        return begin(false, false);
    }

    @Override
    public void beginNested() {
        begin(false, true);
    }

    @Override
    public boolean begin(boolean join, boolean nested) {
        if (nested) {
            throw new UnsupportedOperationException("Nested conversations not supported by the Classic extension.");
        }
        if (join && !delegate.isTransient()) {
            return false;
        }
        delegate.begin();
        return true;
    }

    @Override
    public void end() {
        end(false);
    }

    @Override
    public void endBeforeRedirect() {
        end(true);
    }

    @Override
    public void end(boolean beforeRedirect) {
        if (beforeRedirect) {
            throw new UnsupportedOperationException();
        }
        delegate.end();
    }

    @Override
    public boolean isLongRunning() {
        return !delegate.isTransient();
    }

    @Override
    public boolean isNested() {
        return false;
    }

    @Override
    public String getParentId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRootId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pop() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean redirectToParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void root() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean redirectToRoot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void changeFlushMode(FlushModeType flushMode) {
        throw new UnsupportedOperationException();
    }

}
