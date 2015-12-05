package br.edu.ifpb.pod.syncronizer.core.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author douglasgabriel
 */
public interface TransactionManager extends Remote{

    void prepare () throws RemoteException;
    void commit () throws RemoteException;
    void rollback() throws RemoteException;
    
}
