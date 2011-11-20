package cz.muni.fi.xharting.classic.test.event;

import java.io.FileNotFoundException;
import java.util.Collection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

@Name("observingBean")
@Scope(ScopeType.APPLICATION)
public class ObservingBean {

    private int fooObserverCalled = 0;
    private int barObserverCalled = 0;
    private int fooBarObserverCalled = 0;
    private int asyncObserverCalled = 0;
    private int timedEventObserverCalled = 0;
    private int bazObserverCalled = 0;

    private String p1;
    private Integer p2;
    private Collection<?> p3;
    private String timedEventPayload;

    @Observer({ "foo", "bar" })
    public void fooBarObserver() {
        fooBarObserverCalled++;
    }

    @Observer("foo")
    public void fooObserver() {
        fooObserverCalled++;
    }

    @Observer("bar")
    public void barObserver() {
        barObserverCalled++;
    }
    
    @Observer("baz")
    public void bazObserver()
    {
        bazObserverCalled++;
    }

    @Observer("parameters")
    public void parameterizedObserver(String p1, Integer p2, Collection<?> p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    @Observer("asynchronous")
    public void asyncEventObserver() {
        try {
            Thread.sleep(1000);
            asyncObserverCalled++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Observer("timed")
    public void timedEventObserver(String payload) {
        this.timedEventPayload = payload;
        timedEventObserverCalled++;
    }

    public int getFooObserverCalled() {
        return fooObserverCalled;
    }

    public int getBarObserverCalled() {
        return barObserverCalled;
    }

    public int getFooBarObserverCalled() {
        return fooBarObserverCalled;
    }

    public int getAsyncObserverCalled() {
        return asyncObserverCalled;
    }

    public int getTimedEventObserverCalled() {
        return timedEventObserverCalled;
    }

    public String getP1() {
        return p1;
    }

    public Integer getP2() {
        return p2;
    }

    public Collection<?> getP3() {
        return p3;
    }
    
    public String getTimedEventPayload() {
        return timedEventPayload;
    }

    public int getBazObserverCalled() {
        return bazObserverCalled;
    }

    public void reset() {
        fooObserverCalled = 0;
        barObserverCalled = 0;
        fooBarObserverCalled = 0;
        asyncObserverCalled = 0;
        timedEventObserverCalled = 0;
        bazObserverCalled = 0;
        p1 = null;
        p2 = null;
        p3 = null;
    }

    @Observer("exception")
    public void exception() {
        throw new IllegalStateException("runtime exception");
    }

    @Observer("checkedException")
    public void checkedException() throws Exception {
        throw new FileNotFoundException("checked exception");
    }
}
