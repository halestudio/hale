/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.schemas;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.SchemaExplorer;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.SingleTypeSchemaExplorer;

/**
 * A Schema View that sets focus on one type relation. It reacts on Navigation
 * Selections
 * 
 * @author Yasmina Kammeyer
 */
public class SchemasViewOneTypeFocus extends SchemasView {

	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.schemas.typefocus"; //$NON-NLS-1$

	/**
	 * Wrapper method to create a different content provider.
	 * 
	 * @param parent The Composite parent
	 * @param title The title
	 * @param schemaSpace ID
	 * @return A type only schema explorer
	 */
	@Override
	protected SchemaExplorer createSchemaExplorer(Composite parent, String title,
			SchemaSpaceID schemaSpace) {
		if (parent != null && title != null && schemaSpace != null) {
			return new SingleTypeSchemaExplorer(parent, title, schemaSpace);
		}
		return null;
	}

	/**
	 * @see PropertiesViewPart#getViewContext()
	 */
	@Override
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.schema_explorer";
	}
}
