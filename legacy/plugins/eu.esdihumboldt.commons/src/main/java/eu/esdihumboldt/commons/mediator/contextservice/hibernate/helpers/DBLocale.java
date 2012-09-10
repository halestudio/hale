/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers;

import java.util.Locale;

/**
 * This class used to enable translation from the java.util.List<Locale> to
 * java.util.Set<Locale>, that should be persisted via hibernate.
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: DBLocale.java,v 1.1 2007-12-17 15:12:27 pitaeva Exp $
 */
public class DBLocale {

	private Locale db_locale;

	/** position of this locale in the List */
	private int pos;

	/** unique identifier to store this DBLocale in the database. */
	private long id;

	/** no args constructor for hibernate */
	public DBLocale() {
	}

	/**
	 * @return the db_locale
	 */
	public Locale getDb_locale() {
		return db_locale;
	}

	/**
	 * @param db_locale
	 *            the db_locale to set
	 */
	public void setDb_locale(Locale db_locale) {
		this.db_locale = db_locale;
	}

	/**
	 * @return the pos
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @param pos
	 *            the pos to set
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

}
