package org.kwince.osem;

public interface OsemSessionFactory {
    OsemSession getCurrentSession();

    void removeCurrentSession();
}
