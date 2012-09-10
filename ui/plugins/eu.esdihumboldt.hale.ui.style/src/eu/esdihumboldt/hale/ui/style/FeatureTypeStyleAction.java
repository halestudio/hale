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

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionImages;
import eu.esdihumboldt.hale.ui.style.dialog.FeatureStyleDialog;

/**
 * Action that opens a style editor for a certain type.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FeatureTypeStyleAction extends Action {

	private static final ALogger log = ALoggerFactory.getLogger(FeatureTypeStyleAction.class);

	private static DefinitionImages images;

	private final TypeDefinition type;

	private final DataSet dataSet;

	/**
	 * Creates an action for editing a feature type style.
	 * 
	 * @param type the type definition
	 * @param dataSet the type data set
	 */
	public FeatureTypeStyleAction(final TypeDefinition type, DataSet dataSet) {
		super(type.getName().getLocalPart());

		this.type = type;
		this.dataSet = dataSet;

		init();

		setImageDescriptor(ImageDescriptor.createFromImage(images.getImage(type)));
	}

	/**
	 * Initialize the resources
	 */
	private static void init() {
		if (images == null) {
			images = new DefinitionImages();
		}
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		try {
			FeatureStyleDialog dialog = new FeatureStyleDialog(type, dataSet);
			dialog.setBlockOnOpen(false);
			dialog.open();
		} catch (Exception e) {
			log.error("Error opening style editor dialog", e); //$NON-NLS-1$
		}
	}

}
