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

package eu.esdihumboldt.hale.ui.function.common;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.util.selector.AbstractSelector;
import eu.esdihumboldt.hale.ui.util.selector.AbstractUniformSelector;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * Entity selector
 * 
 * @param <F> the field type
 * @author Simon Templer
 */
public abstract class EntitySelector<F extends ParameterDefinition> extends
		AbstractUniformSelector<EntityDefinition> {

	private final F field;

	private final SchemaSpaceID ssid;

	/**
	 * Create an entity selector.
	 * 
	 * @param ssid the schema space
	 * @param field the field definition, may be <code>null</code>
	 * @param parent the parent composite
	 * @param filters the viewer filters
	 */
	public EntitySelector(final SchemaSpaceID ssid, F field, Composite parent,
			ViewerFilter[] filters) {
		super(parent, new StyledDefinitionLabelProvider(new DefinitionLabelProvider(null, true,
				true), true), filters);
		this.field = field;
		this.ssid = ssid;
	}

	/**
	 * Get the field definition associated with the selector.
	 * 
	 * @return the field definition
	 */
	public F getField() {
		return field;
	}

	/**
	 * @see AbstractSelector#createSelectionDialog(Shell)
	 */
	@Override
	protected AbstractViewerSelectionDialog<EntityDefinition, ?> createSelectionDialog(
			Shell parentShell) {
		return createEntityDialog(parentShell, ssid, field);
	}

	/**
	 * Create the dialog for selecting an entity.
	 * 
	 * @param parentShell the parent shell for the dialog
	 * @param ssid the schema space
	 * @param field the field definition
	 * @return the entity dialog
	 */
	protected abstract EntityDialog createEntityDialog(Shell parentShell, SchemaSpaceID ssid,
			F field);

	/**
	 * Get the selected entity
	 * 
	 * @return the selected entity or <code>null</code>
	 */
	public Entity getEntity() {
		EntityDefinition def = getSelectedObject();

		if (def != null) {
			return createEntity(def);
		}

		return null;
	}

	/**
	 * Create an entity for the given entity definition
	 * 
	 * @param element the entity definition
	 * @return the entity
	 */
	protected abstract Entity createEntity(EntityDefinition element);

}
