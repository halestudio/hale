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

package eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.io.oml.internal.goml.rdf.About;
import eu.esdihumboldt.hale.io.oml.internal.goml.rdf.Resource;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IParameter;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.ITransformation;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IResource;

/**
 * This class represents <xs:group name="transformation">.
 * 
 * @author Thorsten Reitz, Marian de Vries
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
public class Transformation implements ITransformation {

	private About about;
	private IResource resource;
	private String label;
	private List<IParameter> parameters;

	// getters / setters .......................................................

	public Transformation() {
		this.parameters = new ArrayList<IParameter>();
	}

	public Transformation(IResource iResource) {
		this();
		this.resource = iResource;
	}

	/**
	 * @return the service
	 */
	@Override
	public IResource getService() {
		return resource;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(Resource resource) {
		this.resource = resource;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public List<IParameter> getParameters() {
		return this.parameters;
	}

	public Map<String, IParameter> getParameterMap() {
		Map<String, IParameter> result = new HashMap<String, IParameter>();
		for (IParameter p : this.parameters) {
			result.put(p.getName(), p);
		}
		return result;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<IParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the about
	 */
	@Override
	public About getAbout() {
		return about;
	}

	/**
	 * @param about the about to set
	 */
	public void setAbout(About about) {
		this.about = about;
	}

}
