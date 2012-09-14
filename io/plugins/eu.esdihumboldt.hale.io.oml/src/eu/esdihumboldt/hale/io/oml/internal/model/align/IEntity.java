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

package eu.esdihumboldt.hale.io.oml.internal.model.align;

import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.ITransformation;
import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IAbout;

/**
 * Represents any object that might be mapped inside an {@link ICell}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface IEntity {

	/**
	 * @return the label
	 */
	public List<String> getLabel();

	/**
	 * @return the transformation
	 */
	public ITransformation getTransformation();

	/**
	 * @return identification Metadata
	 */
	public IAbout getAbout();

}
