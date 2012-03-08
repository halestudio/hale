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

package eu.esdihumboldt.hale.ui.service.schema.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Tests on {@link TypeEntityDefinition}s based on the {@link AlignmentService}.
 *
 * @author Kai Schwierczek
 */
public class TypeEntityDefinitionTester extends PropertyTester {
	
	/**
	 * The property namespace for this tester.
	 */
	public static final String NAMESPACE = "eu.esdihumboldt.hale.ui.service.shema.type";

	/**
	 * The property that specifies if a cell may be removed.
	 */
	public static final String PROPERTY_TYPE_ALLOW_MARK_UNMAPPABLE = "allow_mark_unmappable";
	
	/**
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver == null)
			return false;
		
		if (property.equals(PROPERTY_TYPE_ALLOW_MARK_UNMAPPABLE) && receiver instanceof TypeEntityDefinition) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
			return as.getAlignment().getCells((TypeEntityDefinition) receiver).isEmpty();
		}
		
		return false;
	}
}
