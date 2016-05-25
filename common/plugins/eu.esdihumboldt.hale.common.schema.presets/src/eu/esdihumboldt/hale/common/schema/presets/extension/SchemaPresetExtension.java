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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import eu.esdihumboldt.hale.common.schema.presets.extension.internal.PredefinedSchemaImpl;

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

	private SetMultimap<String, SchemaPreset> categorySchemas;

	private boolean initialized = false;

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
			SchemaPreset sp = new PredefinedSchemaImpl(element, elementId);
			if (categorySchemas == null) {
				categorySchemas = LinkedHashMultimap.create();
			}
			categorySchemas.put(sp.getCategoryId(), sp);
			return sp;
		}

		return null;
	}

	/**
	 * Get the schemas associated to the category with the given ID
	 * 
	 * @param category the category ID, may be <code>null</code>
	 * @return the list of schemas or an empty list
	 */
	public List<SchemaPreset> getSchemas(String category) {
		if (!initialized || categorySchemas == null) {
			// initialize
			getElements();
		}

		if (categorySchemas != null) {
			Set<SchemaPreset> res = categorySchemas.get(category);
			return new ArrayList<SchemaPreset>(res);
		}

		return Collections.emptyList();
	}

	@Override
	public Collection<SchemaPreset> getElements() {
		try {
			return super.getElements();
		} finally {
			initialized = true;
		}
	}

}
