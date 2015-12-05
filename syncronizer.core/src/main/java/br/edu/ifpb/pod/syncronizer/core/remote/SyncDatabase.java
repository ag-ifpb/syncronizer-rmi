package br.edu.ifpb.pod.syncronizer.core.remote;

import br.edu.ifpb.pod.syncronizer.core.Professor;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 *
 * @author douglasgabriel
 */
public interface SyncDatabase extends Remote{
    
    /**
     * Retorna um mapa contendo as entidades que foram atualizadas no banco.
     */
    Map<Integer, Professor> getUpdatedEntities() throws RemoteException;
    /**
     * Adiciona a entidade informada ao contexto de persistência do banco.
     */
    void syncEntity (Professor professor) throws RemoteException;
    /**
     * Gera um hash da junção do estado de todas a entidades presentes no banco.
     */
    String getDatabaseHash () throws RemoteException;
    
    /**
     * Sincroniza os arquivos de propriedades utilizados na verificação de atualizações
     * dos bancos de dados.
     */
    void syncGhosts ()throws RemoteException;
    
}
