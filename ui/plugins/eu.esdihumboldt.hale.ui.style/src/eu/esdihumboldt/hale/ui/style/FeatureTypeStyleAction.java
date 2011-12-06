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
package eu.esdihumboldt.hale.ui.style;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.opengis.feature.type.FeatureType;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.ui.style.dialog.FeatureStyleDialog;
import eu.esdihumboldt.hale.ui.style.internal.InstanceStylePlugin;

/**
 * Action that opens a style editor for a certain feature type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FeatureTypeStyleAction extends Action {
	
	private static final ALogger log = ALoggerFactory.getLogger(FeatureTypeStyleAction.class);
	
	private static ImageDescriptor featureImage;
	private static ImageDescriptor abstractImage;
	
	private final FeatureType type;
	
	/**
	 * Creates an action for editing a feature type style
	 * 
	 * @param type the feature type
	 */
	public FeatureTypeStyleAction(final FeatureType type) {
		super(type.getName().getLocalPart());
		
		this.type = type;
		
		init();
		
//		setImageDescriptor(
//				(FeatureTypeHelper.isAbstract(type))
//				?(abstractImage):(featureImage));
		setImageDescriptor(featureImage);
	}

	/**
	 * Initialize the resources
	 */
	private static void init() {
		if (featureImage == null) {
			featureImage = InstanceStylePlugin.getImageDescriptor(
					"/icons/concrete_ft.png"); //$NON-NLS-1$
		}
		
		if (abstractImage == null) {
			abstractImage = InstanceStylePlugin.getImageDescriptor(
					"/icons/abstract_ft.png"); //$NON-NLS-1$
		}
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		try {
			FeatureStyleDialog dialog = new FeatureStyleDialog(type);
			dialog.setBlockOnOpen(false);
			dialog.open();
		} catch (Exception e) {
			log.error("Error opening style editor dialog", e); //$NON-NLS-1$
		}
	}

}
