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
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Factory for {@link Definition} labels
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface DefinitionLabelFactory {

	/**
	 * Create a label for the given definition
	 * 
	 * @param parent the parent composite
	 * @param definition the definition
	 * @param verbose show parent type if definition is a
	 *            {@link PropertyDefinition}
	 * 
	 * @return the control presenting the label
	 */
	public Control createLabel(Composite parent, Definition<?> definition, boolean verbose);

}
