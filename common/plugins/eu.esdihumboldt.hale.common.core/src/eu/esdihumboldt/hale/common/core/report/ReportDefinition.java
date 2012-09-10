/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.core.report;

import eu.esdihumboldt.util.definition.ObjectDefinition;

/**
 * String representations of {@link Report} are explicitly allowed to span
 * multiple lines. Identifiers must begin with the {@link #ID_PREFIX}.
 * 
 * @author Andreas Burchert
 * @param <T> the report type
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface ReportDefinition<T extends Report<?>> extends ObjectDefinition<T> {

	/**
	 * The common ID prefix for all message definitions
	 */
	public static final String ID_PREFIX = "!REPORT_";

	// concrete typed interface

}
