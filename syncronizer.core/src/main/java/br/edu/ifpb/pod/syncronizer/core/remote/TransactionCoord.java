package br.edu.ifpb.pod.syncronizer.core.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author douglasgabriel
 */
public interface TransactionCoord extends Remote{
    
    void prepareAll () throws RemoteException;
    void commitAll () throws RemoteException;
    void rollbackAll() throws RemoteException;
}
