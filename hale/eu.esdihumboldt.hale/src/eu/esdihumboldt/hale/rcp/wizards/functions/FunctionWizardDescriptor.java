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

package eu.esdihumboldt.hale.rcp.wizards.functions;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface FunctionWizardDescriptor extends FunctionWizardFactory {

	/**
	 * Get the wizard name
	 * 
	 * @return the wizard name
	 */
	public abstract String getName();

	/**
	 * Get the wizard icon
	 * 
	 * @return an {@link ImageDescriptor} for the icon or <code>null</code>
	 *   if none is available
	 */
	public abstract ImageDescriptor getIcon();

	/**
	 * Get the function wizard factory
	 * 
	 * @return the function wizard factory or <code>null</code> if the
	 *   creation failed
	 */
	public abstract FunctionWizardFactory getFactory();

	/**
	 * Determines if the descriptor represents an augmentation
	 * 
	 * @return if the descriptor represents an augmentation
	 */
	public abstract boolean isAugmentation();

}