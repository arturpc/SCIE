package br.gov.df.dftrans.scie.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
public final class HibernateUtil  { 
	private EntityManagerFactory Factory;
	private EntityManager manager;
	
	public HibernateUtil() {
		//Cria tabelas no banco
		setFactory(Persistence.createEntityManagerFactory("db_cadschool"));
		setManager(getFactory().createEntityManager());
	}	

	public void close(){
		getManager().close();
		getFactory().close();
	}
	
	//getteres and setteres
	public EntityManagerFactory getFactory() {
		return Factory;
	}

	public void setFactory(EntityManagerFactory factory) {
		Factory = factory;
	}

	public EntityManager getManager() {
		return manager;
	}

	public void setManager(EntityManager manager) {
		this.manager = manager;
	}	
}