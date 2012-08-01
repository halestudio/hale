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

package eu.esdihumboldt.hale.ui.common.definition;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.common.Editor;

/**
 * Factory for editors based on {@link PropertyDefinition}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface AttributeEditorFactory {

	/**
	 * Create an attribute editor for the given attribute
	 * 
	 * @param parent the parent composite of the editor control
	 * @param attribute the attribute definition
	 * 
	 * @return the attribute editor or <code>null</code> if no editor could be
	 *         created for the attribute
	 */
	public Editor<?> createEditor(Composite parent, PropertyDefinition attribute);

}
