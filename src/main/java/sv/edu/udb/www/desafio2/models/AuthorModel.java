package sv.edu.udb.www.desafio2.models;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sv.edu.udb.www.desafio2.entities.Author;
import sv.edu.udb.www.desafio2.utils.JpaUtil;

import java.util.List;

public class AuthorModel {

    public List<Author> listarAutores() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Author> q = em.createQuery(
                    "SELECT a FROM Author a ORDER BY a.id", Author.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public int insertarAutor(Author author) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(author);
            em.getTransaction().commit();
            return 1;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return 0;
        } finally {
            em.close();
        }
    }

    public int actualizarAutor(Author author) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(author);
            em.getTransaction().commit();
            return 1;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return 0;
        } finally {
            em.close();
        }
    }

    public int eliminarAutor(Integer id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Author a = em.find(Author.class, id);
            if (a != null) {
                em.remove(a);
                em.getTransaction().commit();
                return 1;
            } else {
                em.getTransaction().rollback();
                return 0;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return 0;
        } finally {
            em.close();
        }
    }

    public Author buscarPorId(Integer id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Author.class, id);
        } finally {
            em.close();
        }
    }

    public List<Author> buscarPorNombre(String term) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Author> q = em.createQuery(
                    "SELECT a FROM Author a WHERE LOWER(a.fullName) LIKE :q ORDER BY a.fullName",
                    Author.class
            );
            q.setParameter("q", "%" + term.toLowerCase() + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Author> listarPorGenero(Integer genreId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            if (genreId == null) {
                return em.createQuery("SELECT a FROM Author a ORDER BY a.id", Author.class)
                        .getResultList();
            }
            return em.createQuery(
                            "SELECT a FROM Author a WHERE a.literaryGenre.id = :gid ORDER BY a.id", Author.class)
                    .setParameter("gid", genreId)
                    .getResultList();
        } finally { em.close(); }
    }

    public boolean existeAutorPorNombreYFecha(String fullName, java.time.LocalDate birthDate) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Long cnt = em.createQuery(
                            "SELECT COUNT(a) FROM Author a WHERE LOWER(a.fullName)=:n AND a.birthDate=:b", Long.class)
                    .setParameter("n", fullName.toLowerCase())
                    .setParameter("b", birthDate)
                    .getSingleResult();
            return cnt != null && cnt > 0;
        } finally { em.close(); }
    }

    public List<Author> listarFiltradoPorNombre(String term) {
        if (term == null || term.isBlank()) return listarAutores();
        return buscarPorNombre(term);
    }

}
