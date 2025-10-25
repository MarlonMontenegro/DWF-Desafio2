package sv.edu.udb.www.desafio2.models;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sv.edu.udb.www.desafio2.entities.LiteraryGenre;
import sv.edu.udb.www.desafio2.utils.JpaUtil;

import java.util.List;

public class LiteraryGenreModel {

    public List<LiteraryGenre> listarGeneros() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<LiteraryGenre> q = em.createQuery(
                    "SELECT g FROM LiteraryGenre g ORDER BY g.name", LiteraryGenre.class);
            return q.getResultList();
        } finally { em.close(); }
    }


    public int insertarGenero(LiteraryGenre g) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(g);
            em.getTransaction().commit();
            return 1;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return 0;
        } finally { em.close(); }
    }


    public int actualizarGenero(LiteraryGenre g) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(g);
            em.getTransaction().commit();
            return 1;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return 0;
        } finally { em.close(); }
    }


    public int eliminarGenero(Integer id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            LiteraryGenre mg = em.find(LiteraryGenre.class, id);
            if (mg != null) {
                em.remove(mg);
                em.getTransaction().commit();
                return 1;
            } else {
                em.getTransaction().rollback();
                return 0;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return 0;
        } finally { em.close(); }
    }


    public LiteraryGenre buscarPorId(Integer id) {
        EntityManager em = JpaUtil.getEntityManager();
        try { return em.find(LiteraryGenre.class, id); }
        finally { em.close(); }
    }

    public List<LiteraryGenre> buscarPorNombre(String term) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<LiteraryGenre> q = em.createQuery(
                    "SELECT g FROM LiteraryGenre g WHERE LOWER(g.name) LIKE :q ORDER BY g.name",
                    LiteraryGenre.class);
            q.setParameter("q", "%" + term.toLowerCase() + "%");
            return q.getResultList();
        } finally { em.close(); }
    }
}
