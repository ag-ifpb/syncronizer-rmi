package br.edu.ifpb.pod.syncronizer.manager;

import br.edu.ifpb.pod.syncronizer.core.Professor;
import br.edu.ifpb.pod.syncronizer.core.remote.SyncDatabase;
import br.edu.ifpb.pod.syncronizer.core.remote.TransactionCoord;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimerTask;

/**
 * Classe responsável por cuidar da sincronização dos bancos de dados.
 * 
 * @author douglasgabriel
 * @version 0.1
 */
public class Syncronizer extends TimerTask{

    private SyncDatabase syncPostgres;
    private SyncDatabase syncMySql;
    private SyncDatabase syncDatastore;
    private TransactionCoord transaction;

    public Syncronizer(SyncDatabase syncPostgres, SyncDatabase syncMySql
            , SyncDatabase syncDatastore, TransactionCoord transaction) {
        this.syncPostgres = syncPostgres;
        this.syncMySql = syncMySql;
        this.syncDatastore = syncDatastore;
        this.transaction = transaction;
    }
    
    /**
     * Solicita as aplicações gerenciadoras de banco de dados as entidades que
     * foram atualizadas. Uma vez com as entidades atualizadas de cada banco, o
     * processamento segue respeitando os níveis de prioridades, configurado da
     * seguinte forma: Postgres -> MySQL -> Datastore. Assim, para cada entidade
     * não sincronizada, é feito o processo de sincronização.
     */
    @Override
    public void run() {        
        try{            
            Map<Integer, Professor> mapPostgres = syncPostgres.getUpdatedEntities();
            Map<Integer, Professor> mapMySql = syncMySql.getUpdatedEntities();
            Map<Integer, Professor> mapDataStore = syncDatastore.getUpdatedEntities();
            for (int i : mapPostgres.keySet()){
                transaction.prepareAll();
                syncMySql.syncEntity(mapPostgres.get(i));
                syncDatastore.syncEntity(mapPostgres.get(i));
                transaction.commitAll();
            }
            // Porque as alterações no postgres são prioritárias, todas entram
            Map<Integer, Professor> entitiesToUpdate = mapPostgres;
            for (int i : mapMySql.keySet())
                if (!entitiesToUpdate.containsKey(i)){
                    transaction.prepareAll();
                    syncPostgres.syncEntity(mapMySql.get(i));
                    syncDatastore.syncEntity(mapMySql.get(i));
                    transaction.commitAll();
                    entitiesToUpdate.put(i, mapMySql.get(i));
                }
            for (int i : mapDataStore.keySet())
                if (!entitiesToUpdate.containsKey(i)){
                    transaction.prepareAll();
                    syncPostgres.syncEntity(mapDataStore.get(i));
                    syncMySql.syncEntity(mapDataStore.get(i));
                    transaction.commitAll();
                }            
        }catch (RemoteException e){
            try {
                transaction.rollbackAll();
                e.printStackTrace();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

}
