package br.edu.ifpb.pod.syncronizer.transaction;

import br.edu.ifpb.pod.syncronizer.core.remote.TransactionCoord;
import br.edu.ifpb.pod.syncronizer.core.remote.TransactionManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe responsável por gerenciar as transações em todas as aplicações gerenciadoras
 * de banco de dados.
 * 
 * @author douglasgabriel
 * @version 0.1
 */
public class TransactionCoordImpl extends UnicastRemoteObject implements TransactionCoord{

    private TransactionManager postgresTrans;
    private TransactionManager mySqlTrans;
    private TransactionManager datastoreTrans;

    public TransactionCoordImpl(TransactionManager postgresTrans
            , TransactionManager mySqlTrans
            , TransactionManager datastoreTrans) throws RemoteException{
        super();
        this.postgresTrans = postgresTrans;
        this.mySqlTrans = mySqlTrans;
        this.datastoreTrans = datastoreTrans;
    }
    
    @Override
    public void prepareAll() throws RemoteException {
        mySqlTrans.prepare();
        postgresTrans.prepare();
        datastoreTrans.prepare();
    }

    @Override
    public void commitAll() throws RemoteException {
        mySqlTrans.commit();
        postgresTrans.commit();
        datastoreTrans.commit();
    }

    @Override
    public void rollbackAll() throws RemoteException {
        mySqlTrans.rollback();
        postgresTrans.rollback();
        datastoreTrans.rollback();
    }

}
