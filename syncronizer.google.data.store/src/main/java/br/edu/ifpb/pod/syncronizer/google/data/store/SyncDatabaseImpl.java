package br.edu.ifpb.pod.syncronizer.google.data.store;

import ag.ifpb.pod.rmi.core.DatastoreService;
import ag.ifpb.pod.rmi.core.TeacherTO;
import br.edu.ifpb.pod.syncronizer.core.Professor;
import br.edu.ifpb.pod.syncronizer.core.PropertiesFileManager;
import br.edu.ifpb.pod.syncronizer.core.remote.SyncDatabase;
import br.edu.ifpb.pod.syncronizer.google.data.store.dao.Transaction;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe responsável pelas operações envolvidas no processo de sincronização
 * dos bancos.
 *
 * @author douglasgabriel
 * @version 0.1
 */
public class SyncDatabaseImpl extends UnicastRemoteObject implements SyncDatabase {

    private DatastoreService datastoreService;
    //objeto responsável por gerenciar as transações na manipulação do banco de dados
    private Transaction transaction;
    private PropertiesFileManager prop;

    public SyncDatabaseImpl(DatastoreService datastoreService, PropertiesFileManager prop, Transaction transaction) throws RemoteException{
        super();
        this.datastoreService = datastoreService;
        this.transaction = transaction;
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
            List<Professor> professoresDbMaster = teacherToProfessor(datastoreService.listTeachers());
            for (Professor professorMaster : professoresDbMaster) {
                String md5backup = prop.getProperty("" + professorMaster.getCodigo());
                String md5Master = getEntityHash(professorMaster.toString());
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
     * Converte o objeto retornado pela API em um objeto {@link Professor}, utilizado
     * na troca de informações no sistema.
     */
    private List<Professor> teacherToProfessor(List<TeacherTO> listToConvert) {
        List<Professor> result = new ArrayList<>();
        for (TeacherTO teacher : listToConvert) {
            Professor professor = new Professor();
            professor.setCodigo(teacher.getCode());
            professor.setNome(teacher.getName());
            professor.setAbreviacao(teacher.getAbbrev());
            professor.setAtivo(teacher.isActive());
            result.add(professor);
        }
        return result;
    }

    /**
     * Converte o objeto manipulado pelo sistema em um objeto {@link TeacherTO},
     * manipulado pela API.
     */
    private TeacherTO professorToTeacher(Professor professor) {
        TeacherTO teacher = new TeacherTO();
        teacher.setName(professor.getNome());
        teacher.setCode(professor.getCodigo());
        teacher.setAbbrev(professor.getAbreviacao());
        teacher.setActive(professor.isAtivo());
        return teacher;
    }

    /**
     * Adiciona a entidade a ser sincronizada ao contexto de persistência e ao cache
     * do arquivo de propriedades.
     */
    @Override
    public void syncEntity(Professor professor) throws RemoteException {
        transaction.persist(professor);
        try {
            prop.persist(professor.getCodigo(), getEntityHash(professor.toString()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RemoteException();
        }
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
     * Gera um hash de todas as entidades presentes no banco de dados.
     */
    @Override
    public String getDatabaseHash() throws RemoteException {
        try {
            StringBuilder sb = new StringBuilder();
            for (Professor professor : teacherToProfessor(datastoreService.listTeachers())){
                sb.append(getEntityHash(professor.toString()));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
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
