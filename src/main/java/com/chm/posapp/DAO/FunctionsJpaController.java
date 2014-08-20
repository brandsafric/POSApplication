/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chm.posapp.DAO;

import com.chm.posapp.DAO.exceptions.NonexistentEntityException;
import com.chm.posapp.DAO.exceptions.PreexistingEntityException;
import com.chm.posapp.entity.Functions;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.chm.posapp.entity.Users;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Jag
 */
public class FunctionsJpaController implements Serializable {

    public FunctionsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Functions functions) throws PreexistingEntityException, Exception {
        if (functions.getUsersList() == null) {
            functions.setUsersList(new ArrayList<Users>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Users> attachedUsersList = new ArrayList<Users>();
            for (Users usersListUsersToAttach : functions.getUsersList()) {
                usersListUsersToAttach = em.getReference(usersListUsersToAttach.getClass(), usersListUsersToAttach.getId());
                attachedUsersList.add(usersListUsersToAttach);
            }
            functions.setUsersList(attachedUsersList);
            em.persist(functions);
            for (Users usersListUsers : functions.getUsersList()) {
                usersListUsers.getFunctionsList().add(functions);
                usersListUsers = em.merge(usersListUsers);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFunctions(functions.getId()) != null) {
                throw new PreexistingEntityException("Functions " + functions + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Functions functions) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Functions persistentFunctions = em.find(Functions.class, functions.getId());
            List<Users> usersListOld = persistentFunctions.getUsersList();
            List<Users> usersListNew = functions.getUsersList();
            List<Users> attachedUsersListNew = new ArrayList<Users>();
            for (Users usersListNewUsersToAttach : usersListNew) {
                usersListNewUsersToAttach = em.getReference(usersListNewUsersToAttach.getClass(), usersListNewUsersToAttach.getId());
                attachedUsersListNew.add(usersListNewUsersToAttach);
            }
            usersListNew = attachedUsersListNew;
            functions.setUsersList(usersListNew);
            functions = em.merge(functions);
            for (Users usersListOldUsers : usersListOld) {
                if (!usersListNew.contains(usersListOldUsers)) {
                    usersListOldUsers.getFunctionsList().remove(functions);
                    usersListOldUsers = em.merge(usersListOldUsers);
                }
            }
            for (Users usersListNewUsers : usersListNew) {
                if (!usersListOld.contains(usersListNewUsers)) {
                    usersListNewUsers.getFunctionsList().add(functions);
                    usersListNewUsers = em.merge(usersListNewUsers);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = functions.getId();
                if (findFunctions(id) == null) {
                    throw new NonexistentEntityException("The functions with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Functions functions;
            try {
                functions = em.getReference(Functions.class, id);
                functions.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The functions with id " + id + " no longer exists.", enfe);
            }
            List<Users> usersList = functions.getUsersList();
            for (Users usersListUsers : usersList) {
                usersListUsers.getFunctionsList().remove(functions);
                usersListUsers = em.merge(usersListUsers);
            }
            em.remove(functions);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Functions> findFunctionsEntities() {
        return findFunctionsEntities(true, -1, -1);
    }

    public List<Functions> findFunctionsEntities(int maxResults, int firstResult) {
        return findFunctionsEntities(false, maxResults, firstResult);
    }

    private List<Functions> findFunctionsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Functions.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Functions findFunctions(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Functions.class, id);
        } finally {
            em.close();
        }
    }

    public int getFunctionsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Functions> rt = cq.from(Functions.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
