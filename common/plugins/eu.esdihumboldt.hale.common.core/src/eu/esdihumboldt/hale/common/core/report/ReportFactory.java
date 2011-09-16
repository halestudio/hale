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

import java.util.ArrayList;
import java.util.List;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.util.definition.AbstractObjectFactory;

/**
 * Factory for Reports.
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportFactory extends AbstractObjectFactory<Report<?>, ReportDefinition<?>>{

	/**
	 * @see eu.esdihumboldt.util.definition.AbstractObjectFactory#getDefinitions()
	 */
	@Override
	protected Iterable<ReportDefinition<?>> getDefinitions() {
		List<ReportDefinition<?>> result = new ArrayList<ReportDefinition<?>>();
		for (ReportDefinition<?> def : OsgiUtils.getServices(ReportDefinition.class)) {
			result.add(def);
		}
		return result;
	}

}
