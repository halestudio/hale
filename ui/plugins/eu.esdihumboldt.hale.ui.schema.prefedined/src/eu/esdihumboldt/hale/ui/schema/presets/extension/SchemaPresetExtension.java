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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.schema.presets.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.hale.ui.schema.presets.extension.internal.PredefinedSchemaImpl;

/**
 * Extension point for predefined schemas.
 * 
 * @author Simon Templer
 */
public class SchemaPresetExtension extends IdentifiableExtension<SchemaPreset> {

	/**
	 * The extension point ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.schema.presets";

	private static SchemaPresetExtension instance;

	/**
	 * Get the extension singleton instance.
	 * 
	 * @return the extension instance
	 */
	public static final SchemaPresetExtension getInstance() {
		synchronized (SchemaPresetExtension.class) {
			if (instance == null) {
				instance = new SchemaPresetExtension();
			}
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private SchemaPresetExtension() {
		super(ID);
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	@Override
	protected SchemaPreset create(String elementId, IConfigurationElement element) {
		if ("schema".equals(element.getName())) {
			return new PredefinedSchemaImpl(element, elementId);
		}

		return null;
	}

}
