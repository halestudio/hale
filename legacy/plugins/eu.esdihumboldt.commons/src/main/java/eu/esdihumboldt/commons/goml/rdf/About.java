/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.commons.goml.rdf;

import java.util.UUID;

import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.specification.cst.rdf.IAbout;

/**
 * This class can be used to store the identifier of objects.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class About implements IAbout {

	private UUID uid;
	private String about;

	// constructors ............................................................

	/**
	 * Use this constructor if you want to build the {@link About} for a
	 * {@link Property}. If no namespace is given, a default one will be
	 * assigned.
	 */
	public About(String namespace, String typeName, String attributeName) {
		this(namespace, typeName);
		if (typeName.endsWith("/") || attributeName.startsWith("/")) {
			this.about = this.about + attributeName;
		} else {
			this.about = this.about + "/" + attributeName;
		}

	}

	/**
	 * Use this constructor for {@link FeatureClass} objects. If no namespace is
	 * given, a default one will be assigned.
	 * 
	 * @param namespace
	 *            the namespace to use.
	 * @param typeName
	 *            the localPart of the FetaureType's name.
	 */
	public About(String namespace, String typeName) {
		super();
		if (namespace == null) {
			namespace = "http://xsdi.org/schema";
		}
		if (namespace.endsWith("/") || typeName.startsWith("/")) {
			this.about = namespace + typeName;
		} else {
			this.about = namespace + "/" + typeName;
		}
	}

	public About(String name) {
		super();
		this.about = name;
	}

	public About(UUID uid) {
		super();
		this.uid = uid;
	}

	// getters / setters .......................................................

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	/**
	 * @return the uid
	 */
	public UUID getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(UUID uid) {
		this.uid = uid;
	}

}
