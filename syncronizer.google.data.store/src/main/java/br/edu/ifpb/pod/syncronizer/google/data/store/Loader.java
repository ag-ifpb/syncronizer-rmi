package br.edu.ifpb.pod.syncronizer.google.data.store;

import ag.ifpb.pod.rmi.core.DatastoreService;
import br.edu.ifpb.pod.syncronizer.core.PropertiesFileManager;
import br.edu.ifpb.pod.syncronizer.google.data.store.dao.Transaction;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 *
 * @author douglasgabriel
 * @version 0.1
 */
public class Loader {

    /**
     * Método responsável por recuperar a instância da API de manipulação do banco
     * Datastore da Google. Além disto, é responsável também por instanciar os
     * objetos que serão compartilhados pelos gerenciadores de sincronização e
     * transação, bem como disponibilizá-los para futuros lookups via RMI.
     */
    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException, IOException {
        System.setProperty("java.rmi.server.hostname", "localhost");
        Registry registry = LocateRegistry.getRegistry("200.129.71.228", 9090);
        DatastoreService datastoreService = (DatastoreService) registry.lookup("DatastoreService");
        Transaction transaction = new Transaction(datastoreService);
        String propChecksumFile = "checksum.properties";
        PropertiesFileManager prop = new PropertiesFileManager(propChecksumFile);
        Registry localRegistry = LocateRegistry.createRegistry(2003);
        localRegistry.bind("sync", new SyncDatabaseImpl(datastoreService, prop, transaction));
        localRegistry = LocateRegistry.createRegistry(2004);
        localRegistry.bind("trans", new TransactionManagerImpl(datastoreService, prop, transaction));
        new Scanner(System.in).next();
    }
}
