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

package eu.esdihumboldt.hale.ui.views.report.properties.tree;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is an abstract class which will contain
 * messages from reports.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractList extends ArrayList {

	/**
	 * Version
	 */
	private static final long serialVersionUID = -8688703744233650933L;

	/**
	 * @param list reports
	 */
	@SuppressWarnings("unchecked")
	public AbstractList(Collection<?> list) {
		this.addAll(list);
	}
	
	/**
	 * Default constructor.
	 */
	public AbstractList(){
		/* do nothing */
	}
}
