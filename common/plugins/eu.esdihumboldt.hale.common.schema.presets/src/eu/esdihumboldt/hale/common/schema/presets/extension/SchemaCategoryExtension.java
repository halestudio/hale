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

package eu.esdihumboldt.hale.common.schema.presets.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.hale.common.schema.presets.extension.internal.SchemaCategoryImpl;

/**
 * Extension point for schema categories.
 * 
 * @author Simon Templer
 */
public class SchemaCategoryExtension extends IdentifiableExtension<SchemaCategory> {

	private static SchemaCategoryExtension instance;

	/**
	 * The default category.
	 */
	public static final SchemaCategory DEFAULT_CATEGORY = new SchemaCategory() {

		@Override
		public Iterable<SchemaPreset> getSchemas() {
			return SchemaPresetExtension.getInstance().getSchemas(null);
		}

		@Override
		public String getName() {
			return "Others";
		}

		@Override
		public String getId() {
			return null;
		}
	};

	/**
	 * Get the extension singleton instance.
	 * 
	 * @return the extension instance
	 */
	public static final SchemaCategoryExtension getInstance() {
		synchronized (SchemaCategoryExtension.class) {
			if (instance == null) {
				instance = new SchemaCategoryExtension();
			}
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	private SchemaCategoryExtension() {
		super(SchemaPresetExtension.ID);
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	@Override
	protected SchemaCategory create(String elementId, IConfigurationElement element) {
		if ("category".equals(element.getName())) {
			return new SchemaCategoryImpl(element, elementId);
		}
		return null;
	}

	@Override
	public SchemaCategory get(String id) {
		if (id == null) {
			return DEFAULT_CATEGORY;
		}
		return super.get(id);
	}

	@Override
	public Collection<SchemaCategory> getElements() {
		List<SchemaCategory> results = new ArrayList<>(super.getElements());
		results.add(DEFAULT_CATEGORY);
		return results;
	}

}
