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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelection;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;

/**
 * Descriptor for {@link FunctionWizardFactory}(ie)s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FunctionWizardDescriptor implements FunctionWizardFactory {
	
	private static final Log log = LogFactory.getLog(FunctionWizardDescriptor.class);

	private final IConfigurationElement conf;
	
	private FunctionWizardFactory factory = null;
	
	/**
	 * Constructor
	 * 
	 * @param conf the configuration element describing the
	 *   {@link FunctionWizardFactory}
	 */
	public FunctionWizardDescriptor(final IConfigurationElement conf) {
		super();
		
		this.conf = conf;
	}
	
	/**
	 * Get the wizard name
	 * 
	 * @return the wizard name
	 */
	public String getName() {
		return conf.getAttribute("name");
	}
	
	/**
	 * Get the wizard icon
	 * 
	 * @return an {@link ImageDescriptor} for the icon or <code>null</code>
	 *   if none is available
	 */
	public ImageDescriptor getIcon() {
		URL url = getIconURL("icon");
		
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
	 * @param iconAttribute
	 * @return
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
	 * Get the function wizard factory
	 * 
	 * @return the function wizard factory or <code>null</code> if the
	 *   creation failed
	 */
	public FunctionWizardFactory getFactory() {
		if (factory == null) {
			try {
				factory = (FunctionWizardFactory) conf.createExecutableExtension("class");
			} catch (CoreException e) {
				log.error("Error creating the function wizard factory", e);
			}
		}
		
		return factory;
	}
	
	/**
	 * @see FunctionWizardFactory#createWizard(CellSelection)
	 */
	@Override
	public FunctionWizard createWizard(CellSelection cellSelection) {
		return getFactory().createWizard(cellSelection);
	}

	/**
	 * @see FunctionWizardFactory#createWizard(SchemaSelection, AlignmentService)
	 */
	@Override
	public FunctionWizard createWizard(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		return getFactory().createWizard(schemaSelection, alignmentService);
	}

	/**
	 * @see FunctionWizardFactory#supports(CellSelection)
	 */
	@Override
	public boolean supports(CellSelection cellSelection) {
		return getFactory().supports(cellSelection);
	}

	/**
	 * @see FunctionWizardFactory#supports(SchemaSelection, AlignmentService)
	 */
	@Override
	public boolean supports(SchemaSelection schemaSelection,
			AlignmentService alignmentService) {
		return getFactory().supports(schemaSelection, alignmentService);
	}

}
