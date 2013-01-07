/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.ui.style;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
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
