package br.edu.ifpb.pod.syncronizer;

import br.edu.ifpb.pod.syncronizer.core.PropertiesFileManager;
import br.edu.ifpb.pod.syncronizer.core.remote.TransactionManager;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author douglasgabriel
 * @version 0.1
 */
public class TransactionManagerImpl extends UnicastRemoteObject implements TransactionManager{
    
    //gerenciador de entidades utilizado na manipulação do banco de dados
    private EntityManager entityManager;
    //gerenciador do arquivo de propriedades
    private PropertiesFileManager prop;
    //objeto responsável por manter a referência para a transação ativada
    private EntityTransaction transaction;
    
    public TransactionManagerImpl () throws RemoteException{
        super();
    }
    
    public TransactionManagerImpl (EntityManager entityManager, PropertiesFileManager prop) throws RemoteException, IOException{
        super();
        this.entityManager = entityManager;
        this.prop = prop;
    }

    @Override
    public void prepare() throws RemoteException {
        transaction = entityManager.getTransaction();
        transaction.begin();
        prop.begin();
    }

    @Override
    public void commit() throws RemoteException {
        if (transaction.isActive())
            transaction.commit();
        prop.commit();
    }

    @Override
    public void rollback() throws RemoteException {
        if (transaction.isActive())
            transaction.rollback();
        transaction = null;
        prop.rollback();
    }

}
