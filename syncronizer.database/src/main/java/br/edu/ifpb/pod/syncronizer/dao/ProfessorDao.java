package br.edu.ifpb.pod.syncronizer.dao;

import br.edu.ifpb.pod.syncronizer.core.Professor;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author douglasgabriel
 * @version 0.1
 */
public class ProfessorDao {

    private EntityManager em;

    public ProfessorDao(EntityManager em) {
        this.em = em;
    }

    public void persist(Object object) {
        em.persist(object);
    }

    public void update(Professor professor) {
        em.merge(professor);
    }

    public List<Professor> findAll() {
        em.clear();
        return em.createQuery("Select p FROM Professor p").getResultList();
    }

    public boolean exists(int codigo) {        
        try {
            em.clear();
            return (em.createQuery(
                    "SELECT p FROM Professor p WHERE p.codigo = " + codigo
            ).getSingleResult() != null);
        } catch (Exception e) {
            return false;
        }
    }

}
