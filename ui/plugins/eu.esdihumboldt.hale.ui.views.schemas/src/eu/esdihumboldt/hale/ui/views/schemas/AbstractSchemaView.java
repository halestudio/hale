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
package eu.esdihumboldt.hale.ui.views.schemas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.util.viewer.ViewerMenu;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.EntitySchemaExplorer;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.SchemaExplorer;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.ServiceSchemaExplorer;

/**
 * A view that shows a single schema available in the schema service.
 * 
 * @author Simon Templer
 */
public abstract class AbstractSchemaView extends PropertiesViewPart {

	private SchemaExplorer explorer;
	private ServiceSchemaExplorer explorerManager;

	private final SchemaSpaceID schemaSpace;

	/**
	 * Create a view that shows the schema identified by the given schema space.
	 * 
	 * @param schemaSpace the schema space
	 */
	public AbstractSchemaView(SchemaSpaceID schemaSpace) {
		super();
		this.schemaSpace = schemaSpace;
	}

	@Override
	public void createViewControl(Composite _parent) {
		Composite modelComposite = new Composite(_parent, SWT.NONE);
		modelComposite.setLayout(new FillLayout());

		explorer = new EntitySchemaExplorer(modelComposite, getTitle(), schemaSpace);
		explorerManager = new ServiceSchemaExplorer(explorer, schemaSpace);

		// context menu
		new ViewerMenu(getSite(), explorer.getTreeViewer());

		// register selection provider
		getSite().setSelectionProvider(explorer.getTreeViewer());
	}

	@Override
	public void setFocus() {
		explorer.getTreeViewer().getControl().setFocus();
	}

	@Override
	public void dispose() {
		if (explorerManager != null) {
			explorerManager.dispose();
		}

		super.dispose();
	}

}
