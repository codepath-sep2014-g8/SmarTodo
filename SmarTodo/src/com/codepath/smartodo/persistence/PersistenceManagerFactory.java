package com.codepath.smartodo.persistence;


/**
 * This factory facilitates a pluggable persistence manager for the app.
 * Uses a simple singleton pattern.
 * 
 * @author Damodar
 */
public class PersistenceManagerFactory {
	private static PersistenceManager instance = null;
	
	public static synchronized PersistenceManager getInstance() {
		if (instance == null) {
			instance = new ParsePersistenceManager();
		}
		return instance;
	}
}
