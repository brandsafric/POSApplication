/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chm.posapp.DAO;

import com.chm.posapp.DAO.exceptions.NonexistentEntityException;
import com.chm.posapp.DAO.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.chm.posapp.entity.Functions;
import com.chm.posapp.entity.Users;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Jag
 */
public class UsersJpaController implements Serializable {

    public UsersJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Users users) throws PreexistingEntityException, Exception {
        if (users.getFunctionsList() == null) {
            users.setFunctionsList(new ArrayList<Functions>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Functions> attachedFunctionsList = new ArrayList<Functions>();
            for (Functions functionsListFunctionsToAttach : users.getFunctionsList()) {
                functionsListFunctionsToAttach = em.getReference(functionsListFunctionsToAttach.getClass(), functionsListFunctionsToAttach.getId());
                attachedFunctionsList.add(functionsListFunctionsToAttach);
            }
            users.setFunctionsList(attachedFunctionsList);
            em.persist(users);
            for (Functions functionsListFunctions : users.getFunctionsList()) {
                functionsListFunctions.getUsersList().add(users);
                functionsListFunctions = em.merge(functionsListFunctions);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsers(users.getId()) != null) {
                throw new PreexistingEntityException("Users " + users + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Users users) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users persistentUsers = em.find(Users.class, users.getId());
            List<Functions> functionsListOld = persistentUsers.getFunctionsList();
            List<Functions> functionsListNew = users.getFunctionsList();
            List<Functions> attachedFunctionsListNew = new ArrayList<Functions>();
            for (Functions functionsListNewFunctionsToAttach : functionsListNew) {
                functionsListNewFunctionsToAttach = em.getReference(functionsListNewFunctionsToAttach.getClass(), functionsListNewFunctionsToAttach.getId());
                attachedFunctionsListNew.add(functionsListNewFunctionsToAttach);
            }
            functionsListNew = attachedFunctionsListNew;
            users.setFunctionsList(functionsListNew);
            users = em.merge(users);
            for (Functions functionsListOldFunctions : functionsListOld) {
                if (!functionsListNew.contains(functionsListOldFunctions)) {
                    functionsListOldFunctions.getUsersList().remove(users);
                    functionsListOldFunctions = em.merge(functionsListOldFunctions);
                }
            }
            for (Functions functionsListNewFunctions : functionsListNew) {
                if (!functionsListOld.contains(functionsListNewFunctions)) {
                    functionsListNewFunctions.getUsersList().add(users);
                    functionsListNewFunctions = em.merge(functionsListNewFunctions);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = users.getId();
                if (findUsers(id) == null) {
                    throw new NonexistentEntityException("The users with id " + id + " no longer exists.");
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
            Users users;
            try {
                users = em.getReference(Users.class, id);
                users.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The users with id " + id + " no longer exists.", enfe);
            }
            List<Functions> functionsList = users.getFunctionsList();
            for (Functions functionsListFunctions : functionsList) {
                functionsListFunctions.getUsersList().remove(users);
                functionsListFunctions = em.merge(functionsListFunctions);
            }
            em.remove(users);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Users> findUsersEntities() {
        return findUsersEntities(true, -1, -1);
    }

    public List<Users> findUsersEntities(int maxResults, int firstResult) {
        return findUsersEntities(false, maxResults, firstResult);
    }

    private List<Users> findUsersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Users.class));
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

    public Users findUsers(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Users.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Users> rt = cq.from(Users.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
