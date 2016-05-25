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

package eu.esdihumboldt.hale.common.schema.presets.extension.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaCategory;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPresetExtension;

/**
 * Default {@link SchemaCategory} implementation.
 * 
 * @author Simon Templer
 */
public class SchemaCategoryImpl implements SchemaCategory {

	private final IConfigurationElement element;
	private final String elementId;

	/**
	 * Constructor.
	 * 
	 * @param element the configuration element
	 * @param elementId the identifier
	 */
	public SchemaCategoryImpl(IConfigurationElement element, String elementId) {
		this.element = element;
		this.elementId = elementId;
	}

	@Override
	public String getName() {
		return element.getAttribute("name");
	}

	@Override
	public String getId() {
		return elementId;
	}

	@Override
	public Iterable<SchemaPreset> getSchemas() {
		return SchemaPresetExtension.getInstance().getSchemas(getId());
	}

}
