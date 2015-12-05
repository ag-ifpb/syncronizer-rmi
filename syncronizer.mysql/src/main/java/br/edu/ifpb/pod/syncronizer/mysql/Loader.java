package br.edu.ifpb.pod.syncronizer.mysql;
import br.edu.ifpb.pod.syncronizer.SyncDatabaseImpl;
import br.edu.ifpb.pod.syncronizer.TransactionManagerImpl;
import br.edu.ifpb.pod.syncronizer.core.PropertiesFileManager;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 *
 * @author douglasgabriel
 * @version 0.1
 */
public class Loader {

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, IOException {
        System.setProperty("java.rmi.server.hostname", "localhost");
        EntityManager em = Persistence
                .createEntityManagerFactory("syncronizerMySql")
                .createEntityManager();
        String propChecksumFile = "checksum.properties";
        PropertiesFileManager prop = new PropertiesFileManager(propChecksumFile);
        Registry registry = LocateRegistry.createRegistry(1999);
        registry.bind("sync", new SyncDatabaseImpl(em, prop));
        registry = LocateRegistry.createRegistry(2000);
        registry.bind("trans", new TransactionManagerImpl(em, prop));
    }
}
