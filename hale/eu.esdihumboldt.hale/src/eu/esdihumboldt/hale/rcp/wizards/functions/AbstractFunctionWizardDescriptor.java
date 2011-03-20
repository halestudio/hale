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

import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

import eu.esdihumboldt.hale.Messages;


/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractFunctionWizardDescriptor implements
		FunctionWizardDescriptor {

	/**
	 * The configuration element
	 */
	protected final IConfigurationElement conf;

	/**
	 * Constructor
	 * 
	 * @param conf the configuration element describing the
	 *   {@link FunctionWizardFactory}
	 */
	public AbstractFunctionWizardDescriptor(IConfigurationElement conf) {
		this.conf = conf;
	}
	
	/**
	 * @see FunctionWizardDescriptor#getName()
	 */
	public String getName() {
		return conf.getAttribute(Messages.AbstractFunctionWizardDescriptor_0); //$NON-NLS-1$
	}

	/**
	 * @see FunctionWizardDescriptor#getIcon()
	 */
	public ImageDescriptor getIcon() {
		URL url = getIconURL(Messages.AbstractFunctionWizardDescriptor_1); //$NON-NLS-1$
		
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		else {
			return null;
		}
	}

	/**
	 * Utility method to get the URL of an icon defined in the
	 *   configuration element
	 *   
	 * @param iconAttribute the icon attribute name
	 * @return the icon URL or <code>null</code> if none is defined
	 */
	protected URL getIconURL(String iconAttribute) {
		String icon = conf.getAttribute(iconAttribute);
		if (icon != null && !icon.isEmpty()) {
			String contributor = conf.getDeclaringExtension().getContributor().getName();
			Bundle bundle = Platform.getBundle(contributor);
			
			if (bundle != null) {
				return bundle.getResource(icon);
			}
		}
		
		return null;
	}

	/**
	 * @see FunctionWizardFactory#createWizard(AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return getFactory().createWizard(selection);
	}

	/**
	 * @see FunctionWizardFactory#supports(AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		return getFactory().supports(selection);
	}

}
