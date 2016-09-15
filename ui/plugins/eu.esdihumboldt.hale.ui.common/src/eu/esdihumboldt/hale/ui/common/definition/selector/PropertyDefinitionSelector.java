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

package eu.esdihumboldt.hale.ui.common.definition.selector;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Objects;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.util.selector.AbstractUniformSelector;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * Entity selector for {@link PropertyDefinition}s with complete property paths
 * (represented in an {@link EntityDefinition}).
 * 
 * @author Simon Templer
 */
public class PropertyDefinitionSelector extends AbstractUniformSelector<EntityDefinition> {

	private TypeDefinition parentType;

	private final SchemaSpaceID ssid;

	/**
	 * Create an entity selector for {@link Property} entities
	 * 
	 * @param ssid the schema space, may be null
	 * @param parent the parent composite
	 * @param parentType the parent type
	 */
	public PropertyDefinitionSelector(Composite parent, TypeDefinition parentType,
			SchemaSpaceID ssid) {
		super(parent, new DefinitionLabelProvider(null, false, true), null);

		this.parentType = parentType;
		this.ssid = ssid;
	}

	/**
	 * Set the parent type
	 * 
	 * @param parentType the parentType to set
	 */
	public void setParentType(TypeDefinition parentType) {
		boolean forceUpdate = this.parentType != null
				&& !Objects.equal(this.parentType, parentType);

		this.parentType = parentType;
		// reset candidates?? refresh viewer?
		if (forceUpdate) {
			// reset selection
			setSelection(new StructuredSelection());
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.selector.AbstractSelector#createSelectionDialog(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected AbstractViewerSelectionDialog<EntityDefinition, ?> createSelectionDialog(
			Shell parentShell) {
		String title;
		if (ssid == null) {
			title = "Select property";
		}
		else {
			switch (ssid) {
			case SOURCE:
				title = "Select source property";
				break;
			case TARGET:
				title = "Select target property";
				break;
			default:
				title = "Select property";
			}
		}
		return new PropertyDefinitionDialog(parentShell, ssid, parentType, title,
				getSelectedObject());
	}

}
