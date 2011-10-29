package cz.muni.fi.xharting.classic.test.scope.stateless;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import cz.muni.fi.xharting.classic.scope.stateless.StatelessScoped;

@StatelessScoped
public class Book {

    private static final AtomicInteger identifier = new AtomicInteger();
    private static volatile int lastDestroyed = 0;
    
    private volatile int id;

    public Book() {
    }

    public int getId() {
        return id;
    }
    
    @PostConstruct
    public void postConstruct()
    {
        this.id = identifier.getAndIncrement();
    }
    
    @PreDestroy
    public void preDestroy()
    {
        lastDestroyed = id;
    }

    public static int getLastDestroyed() {
        return lastDestroyed;
    }
}
