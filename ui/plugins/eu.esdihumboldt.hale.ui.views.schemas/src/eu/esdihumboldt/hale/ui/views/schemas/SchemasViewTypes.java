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
import eu.esdihumboldt.hale.ui.views.schemas.explorer.SchemaExplorer;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.TypesSchemaExplorer;

/**
 * This view serves as a navigation by creating a content provider that shows
 * types (type cells).
 * 
 * @author Yasmina Kammeyer
 */
public class SchemasViewTypes extends SchemasView {

	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.schemas.typesonly"; //$NON-NLS-1$

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
			return new TypesSchemaExplorer(parent, title, schemaSpace);
		}
		return null;
	}

}
