/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the project web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.dataaccess.abstractionmodel;

import java.util.List;

import org.opengis.metadata.MetaData;

/**
 * This interface describes a collection of (spatial) objects that have been
 * retrieved from the Data Access Component Framework or that have to be put
 * there. It is called "Abstracted" because it does not directly reflect a
 * single encoding or schema, but can instead be used to described data coming
 * from all kinds of data sources.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface AbstractedDataSet extends DAMObject {

	/**
	 * @return a List of the {@link BasicElement} that make up this
	 *         {@link AbstractedDataSet}.
	 */
	public List<BasicElement> getElements();

	/**
	 * @return the {@link MetaData} describing the source and quality of this
	 *         {@link AbstractedDataSet}.
	 */
	public MetaData getMetadata();
}
