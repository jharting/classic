package cz.muni.fi.xharting.classic.test.log;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

@Name("user")
@AutoCreate
public class User {

    private String username = "jharting";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
