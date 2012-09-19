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

package eu.esdihumboldt.hale.io.oml.internal.goml.align;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.model.align.ISchema;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * This {@link Schema} type contains some metadata on a schema mapped in an
 * {@link Alignment}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
public class Schema implements ISchema {

	/**
	 * TODO add comment
	 */
	private String location;

	/**
	 * The {@link Formalism} used to express this schema.
	 */
	private Formalism formalism;

	/**
	 * Identifier of this {@link Schema} object
	 */
	private IAbout about;

	/**
	 * A {@link List} of user-definable Labels that may be given to a SChema.
	 */
	private List<String> labels;

	// constructors ............................................................

	/**
	 * @param location
	 * @param formalism
	 */
	public Schema(String location, Formalism formalism) {
		super();
		this.location = location;
		this.formalism = formalism;
		this.labels = new ArrayList<String>();
	}

	// getters / setters .......................................................

	/**
	 * @return the location
	 */
	@Override
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the formalism
	 */
	@Override
	public Formalism getFormalism() {
		return formalism;
	}

	/**
	 * @param formalism the formalism to set
	 */
	public void setFormalism(Formalism formalism) {
		this.formalism = formalism;
	}

	/**
	 * @return the about
	 */
	@Override
	public IAbout getAbout() {
		return about;
	}

	/**
	 * @param about the about to set
	 */
	public void setAbout(IAbout about) {
		this.about = about;
	}

	/**
	 * @return the labels
	 */
	@Override
	public List<String> getLabels() {
		return labels;
	}

	/**
	 * @param labels the labels to set
	 */
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

}
