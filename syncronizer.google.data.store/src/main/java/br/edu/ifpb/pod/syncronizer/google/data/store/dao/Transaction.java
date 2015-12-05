package br.edu.ifpb.pod.syncronizer.google.data.store.dao;

import ag.ifpb.pod.rmi.core.DatastoreService;
import ag.ifpb.pod.rmi.core.TeacherTO;
import br.edu.ifpb.pod.syncronizer.core.Professor;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por gerenciar as transações envolvendo a manipulação de entidades
 * pela API Datastore.
 *
 * @author douglasgabriel
 * @version 0.1
 */
public class Transaction {

    private List<Professor> persistenceContext;
    private DatastoreService datastoreService;

    public Transaction(DatastoreService datastoreService) {
        this.datastoreService = datastoreService;
    }
    
    public void persist (Professor professor){
        persistenceContext.add(professor);
    }
    
    public void remove (Professor professor){
        persistenceContext.remove(professor);
    }
    
    public void begin (){
        persistenceContext = new ArrayList<>();
    }
    
    public void commit () throws RemoteException{
        if (persistenceContext == null)
            throw new RemoteException("No current transaction");
        for (Professor professor : persistenceContext){
            datastoreService.createTeacher(professorToTeacher(professor));
        }
    }
    
    public void rollback (){
        persistenceContext = null;
    }
    
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

    private TeacherTO professorToTeacher(Professor professor) {
        TeacherTO teacher = new TeacherTO();
        teacher.setName(professor.getNome());
        teacher.setCode(professor.getCodigo());
        teacher.setAbbrev(professor.getAbreviacao());
        teacher.setActive(professor.isAtivo());
        return teacher;
    }
    
}
