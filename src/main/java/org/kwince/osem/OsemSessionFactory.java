package org.kwince.osem;

/**
 * Interface that manages creation and removing of sessions. Usually an
 * application has a single {@link OsemSessionFactory} instance and threads
 * servicing client requests obtain {@link OsemSession} instances from this
 * factory.
 * <p/>
 * The internal state of a {@link OsemSessionFactory} is immutable. Once it is
 * created this internal state is set.
 * <p/>
 * Implementors <strong>must</strong> be threadsafe.
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 * 
 */
public interface OsemSessionFactory {
    /**
     * 
     * @return
     */
    OsemSession getCurrentSession();

    void removeCurrentSession();
}
