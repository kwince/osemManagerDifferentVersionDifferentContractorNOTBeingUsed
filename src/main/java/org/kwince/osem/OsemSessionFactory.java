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
     * Get the current osem session associated with the current thread. If no
     * session is available, new osem session is created.
     * 
     * @return osem session
     */
    OsemSession getCurrentSession();

    /**
     * Remove the osem session object associated with the current thread
     */
    void removeCurrentSession();
}
