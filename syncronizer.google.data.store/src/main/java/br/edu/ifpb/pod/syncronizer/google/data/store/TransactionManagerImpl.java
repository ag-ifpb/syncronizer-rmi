package br.edu.ifpb.pod.syncronizer.google.data.store;

import ag.ifpb.pod.rmi.core.DatastoreService;
import br.edu.ifpb.pod.syncronizer.core.PropertiesFileManager;
import br.edu.ifpb.pod.syncronizer.core.remote.TransactionManager;
import br.edu.ifpb.pod.syncronizer.google.data.store.dao.Transaction;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author douglasgabriel
 * @version 0.1
 */
public class TransactionManagerImpl extends UnicastRemoteObject implements TransactionManager{
    
    private DatastoreService datastoreService;
    private Transaction transaction;
    private PropertiesFileManager prop;

    public TransactionManagerImpl(DatastoreService datastoreService, PropertiesFileManager prop, Transaction transaction) throws RemoteException{
        super();
        this.datastoreService = datastoreService;
        this.prop = prop;
        this.transaction = transaction;
    }
    
    @Override
    public void prepare() throws RemoteException {
        transaction.begin();
        prop.begin();
    }

    @Override
    public void commit() throws RemoteException {
        transaction.commit();
        prop.commit();
    }

    @Override
    public void rollback() throws RemoteException {
        transaction.rollback();
        prop.rollback();
    }

}
