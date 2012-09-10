/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.codelist.selector;

import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.codelist.CodeList;

/**
 * Interface for components to select code lists from.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface CodeListSelector {

	/**
	 * Get the control.
	 * 
	 * @return the control
	 */
	public Control getControl();

	/**
	 * Get the selected code list.
	 * 
	 * @return the selected code list or <code>null</code>
	 */
	public CodeList getCodeList();
}
