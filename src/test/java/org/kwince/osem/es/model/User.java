package org.kwince.osem.es.model;

import java.util.Date;

import org.kwince.osem.es.annotation.Document;
import org.kwince.osem.es.annotation.Id;
import org.kwince.osem.es.annotation.ObjectProperty;
import org.kwince.osem.es.annotation.Property;
import org.kwince.osem.es.annotation.Transient;
import org.kwince.osem.es.model.common.Name;

@Document(name = "user_doc")
public class User {
    @Id
    @Property(name = "user_name")
    private String username;
    private String password;
    private Name name;
    @ObjectProperty(name = "name_alias")
    private Name alias;
    private Date birthdate;
    @Transient
    private String sessionId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Name getAlias() {
        return alias;
    }

    public void setAlias(Name alias) {
        this.alias = alias;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

}
