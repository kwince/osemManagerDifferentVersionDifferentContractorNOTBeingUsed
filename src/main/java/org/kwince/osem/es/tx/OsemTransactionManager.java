package org.kwince.osem.es.tx;

import org.kwince.osem.OsemSessionFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * {@link org.springframework.transaction.PlatformTransactionManager}
 * 
 * <p>
 * XXX: TBD
 * 
 * @author Allan Ramirez (ramirezag@gmail.com)
 */
public class OsemTransactionManager extends AbstractPlatformTransactionManager {
    /**
     * 
     */
    private static final long serialVersionUID = 1119382042978433935L;

    private OsemSessionFactory sessionFactory;

    public void setSessionFactory(OsemSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public OsemSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        // TODO Auto-generated method stub

    }

    @Override
    protected Object doGetTransaction() throws TransactionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        // TODO Auto-generated method stub

    }

}
