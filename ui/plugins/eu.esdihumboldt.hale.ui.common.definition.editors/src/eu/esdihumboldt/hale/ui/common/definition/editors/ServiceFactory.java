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

package eu.esdihumboldt.hale.ui.common.definition.editors;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;

/**
 * Service factory for the attribute editor factory.
 *
 * @author Kai Schwierczek
 */
public class ServiceFactory extends AbstractServiceFactory {
	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator, IServiceLocator)
	 */
	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {
		if (serviceInterface.equals(AttributeEditorFactory.class)) {
			return new DefaultAttributeEditorFactory();
		}

		return null;
	}
}
