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

package eu.esdihumboldt.hale.io.oml.internal.model.align.ext;

import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IResource;

/**
 * The superinterface for all transformations.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ITransformation {

	/**
	 * @return a List of Parameters that this Transformation accepts.
	 */
	public List<IParameter> getParameters();

	/**
	 * @return the URI grounding this Transformation.
	 */
	public IResource getService();

	/**
	 * @return the label of this Transformation.
	 */
	public String getLabel();

	/**
	 * @return the identifying metadata of this Transformation.
	 */
	public IAbout getAbout();

}
