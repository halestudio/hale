/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.oml.internal.goml.rdf;

import java.util.UUID;

import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.FeatureClass;
import eu.esdihumboldt.hale.io.oml.internal.goml.omwg.Property;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * This class can be used to store the identifier of objects.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
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
		}
		else {
			this.about = this.about + "/" + attributeName;
		}

	}

	/**
	 * Use this constructor for {@link FeatureClass} objects. If no namespace is
	 * given, a default one will be assigned.
	 * 
	 * @param namespace the namespace to use.
	 * @param typeName the localPart of the FetaureType's name.
	 */
	public About(String namespace, String typeName) {
		super();
		if (namespace == null) {
			namespace = "http://xsdi.org/schema";
		}
		if (namespace.endsWith("/") || typeName.startsWith("/")) {
			this.about = namespace + typeName;
		}
		else {
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

	@Override
	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	/**
	 * @return the uid
	 */
	@Override
	public UUID getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(UUID uid) {
		this.uid = uid;
	}

}
