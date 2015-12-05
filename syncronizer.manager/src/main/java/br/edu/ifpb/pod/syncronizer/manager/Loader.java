package br.edu.ifpb.pod.syncronizer.manager;

import br.edu.ifpb.pod.syncronizer.core.remote.SyncDatabase;
import br.edu.ifpb.pod.syncronizer.core.remote.TransactionCoord;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.Timer;

/**
 *
 * @author douglasgabriel
 * @version 0.1
 */
public class Loader {

    /**
     * Recupera as instâncias das interfaces que serão manipuladas no processo
     * de sincronização, utilizando RMI. Além disto, agenda a tarefa de sincronização
     * de bancos para executar a cara 5min.
     */
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2001);
        SyncDatabase postgres = (SyncDatabase) registry.lookup("sync");
        registry = LocateRegistry.getRegistry("localhost", 1999);
        SyncDatabase mySql = (SyncDatabase) registry.lookup("sync");
        registry = LocateRegistry.getRegistry("localhost", 2003);
        SyncDatabase datastore = (SyncDatabase) registry.lookup("sync");
        registry = LocateRegistry.getRegistry("localhost", 2005);
        TransactionCoord transaction = (TransactionCoord) registry.lookup("transCoord");
        updateGhosts(postgres, mySql, datastore, transaction);
        new Timer().scheduleAtFixedRate(new Syncronizer(postgres, mySql, datastore, transaction), Calendar.getInstance().getTime(), 300000);
    }

    /**
     * Método chamado ao estartar a aplicação com o intuito de verificar se os
     * bancos já estão atualizados, caso positivo, os bancos de backup também
     * serão, do contrário os bancos serão atualizados.
     */
    public static void updateGhosts(SyncDatabase postgres
            , SyncDatabase mySql, SyncDatabase datastore
            , TransactionCoord transaction) {
        try{
            String md5Postgres = postgres.getDatabaseHash();
            String md5MySql = mySql.getDatabaseHash();
            String md5Datastore = datastore.getDatabaseHash();
            if (md5Postgres.equals(md5MySql) && md5MySql.equals(md5Datastore)){
                postgres.syncGhosts();
                mySql.syncGhosts();
                datastore.syncGhosts();
            }else {
                new Syncronizer(postgres, mySql, datastore, transaction).run();
            }
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

}
