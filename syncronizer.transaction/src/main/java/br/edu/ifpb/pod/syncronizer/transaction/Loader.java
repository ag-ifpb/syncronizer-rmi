package br.edu.ifpb.pod.syncronizer.transaction;

import br.edu.ifpb.pod.syncronizer.core.remote.TransactionCoord;
import br.edu.ifpb.pod.syncronizer.core.remote.TransactionManager;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author douglasgabriel
 * @version 0.1
 */
public class Loader {
    
    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2002);
        TransactionManager postgres = (TransactionManager) registry.lookup("trans");
        registry = LocateRegistry.getRegistry("localhost", 2000);
        TransactionManager mySql = (TransactionManager) registry.lookup("trans");
        registry = LocateRegistry.getRegistry("localhost", 2004);
        TransactionManager datastore = (TransactionManager) registry.lookup("trans");
        registry = LocateRegistry.createRegistry(2005);
        registry.bind("transCoord", new TransactionCoordImpl(postgres, mySql, datastore));
    }
}
