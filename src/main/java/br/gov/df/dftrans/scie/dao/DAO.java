package br.gov.df.dftrans.scie.dao;


import br.gov.df.dftrans.scie.exceptions.EntityNotFoundException;
import br.gov.df.dftrans.scie.exceptions.InsertException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


public abstract class DAO<T> {
    
    protected static final Integer EMPTY = 0;
    protected static EntityManagerFactory factory = Persistence.createEntityManagerFactory("sciePU");
    @PersistenceContext(unitName = "scie")
    protected static LogAlteracaoBancoDAO logdao = LogAlteracaoBancoDAO.logAlteracaoBancoDAO();

    public abstract T add(T entity) throws InsertException;
    public abstract T get(Object id) throws EntityNotFoundException;
    
    
    protected TypedQuery<T> createNamedQuery(String query, Class classe, EntityManager entity) {
        return entity.createNamedQuery(query, classe);
    }
}

