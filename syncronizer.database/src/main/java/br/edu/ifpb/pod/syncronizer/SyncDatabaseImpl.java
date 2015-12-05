package br.edu.ifpb.pod.syncronizer;

import br.edu.ifpb.pod.syncronizer.core.PropertiesFileManager;
import br.edu.ifpb.pod.syncronizer.core.remote.SyncDatabase;
import br.edu.ifpb.pod.syncronizer.core.Professor;
import br.edu.ifpb.pod.syncronizer.dao.ProfessorDao;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * Classe responsável pelas operações envolvidas no processo de sincronização
 * dos bancos.
 * 
 * @author douglasgabriel
 * @version 0.1
 */
public class SyncDatabaseImpl extends UnicastRemoteObject implements SyncDatabase {

    /**
     * Responsável pelas operações com o banco gerenciado pela aplicação.
     */
    private ProfessorDao professorDao;
    /**
     * Responsável pelas operações com o arquivo de propriedades utilizado na
     * detecção de atualizações no banco.
     */
    private PropertiesFileManager prop;

    public SyncDatabaseImpl() throws RemoteException {
        super();
    }

    public SyncDatabaseImpl(EntityManager em, PropertiesFileManager prop) throws RemoteException, IOException {
        super();
        this.professorDao = new ProfessorDao(em);
        this.prop = prop;
    }

    /**
     * Compara a lista de todas as entidades presentes no banco de dados gerenciado
     * com os hashs de entidades presente no arquivo de propriedade, a fim de
     * detectar mudanças. Em caso de mudança, a entidade é adicionada a um mapa
     * de chave igual ao código da entidade e valor igual a entidade e o retorna
     * para que os procedimentos de sincronização destas entidades sejam realizados.
     */
    @Override
    public Map<Integer, Professor> getUpdatedEntities() throws RemoteException {
        Map<Integer, Professor> updatedEntities = new HashMap<>();
        try {
            //recuperando lista do banco de dados
            List<Professor> professoresDbMaster = professorDao.findAll();
            //para cada entidade retornada, realiza a comparação
            for (Professor professorMaster : professoresDbMaster) {
                //recuperano hash antigo da entidade atual
                String md5backup = prop.getProperty("" + professorMaster.getCodigo());
                //gerando um novo hash da entidade recuperada agora
                String md5Master = getEntityHash(professorMaster.toString());
                /*caso o hash não conste no arquivo de propriedades ou sejam diferentes
                o hash é adicionado ao arquivo de propriedades e a entidade é adicionada
                no mapa.*/
                if (md5backup == null || !md5backup.equals(md5Master)) {
                    prop.persist(professorMaster.getCodigo(), md5Master);
                    updatedEntities.put(professorMaster.getCodigo(), professorMaster);
                }
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
        return updatedEntities;
    }

    /**
     * Adiciona a entidade a ser sincronizada ao contexto de persistência e ao cache
     * do arquivo de propriedades.
     */
    @Override
    public void syncEntity(Professor professor) throws RemoteException {
        if (professorDao.exists(professor.getCodigo())) {
            professorDao.update(professor);
        } else {
            professorDao.persist(professor);
        }
        try {
            prop.persist(professor.getCodigo(), getEntityHash(professor.toString()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RemoteException();
        }
    }

    /**
     * Gera um hash de todas as entidades presentes no banco de dados.
     */
    @Override
    public String getDatabaseHash() throws RemoteException {
        try {
            StringBuilder sb = new StringBuilder();
            for (Professor professor : professorDao.findAll()) {
                //um hash da entidade é gerado e adicionado ao final da string
                sb.append(getEntityHash(professor.toString()));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Gera o hash de uma única entidade.
     * 
     * @param string string que representa o estado da entidade.
     */
    private String getEntityHash(String string) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return new String(messageDigest.digest(string.getBytes()));
    }

    /**
     * Sincroniza o arquivo de propriedades com os dados presentes no banco de dados.
     * É útil quando identifica-se que os bancos já encontram-se sincronizados,
     * porém o arquivo de propriedade ainda não. Fato que acontece quando a aplicação
     * encontra-se inativa por um tempo em que outra aplicação sincronize os dados.
     */
    @Override
    public void syncGhosts() throws RemoteException {
        Map<Integer, Professor> updatedEntities = getUpdatedEntities();
        try {
            prop.begin();
            for (int i : updatedEntities.keySet()) {
                prop.persist(i, getEntityHash(updatedEntities.get(i).toString()));
            }
            prop.commit();
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

}
