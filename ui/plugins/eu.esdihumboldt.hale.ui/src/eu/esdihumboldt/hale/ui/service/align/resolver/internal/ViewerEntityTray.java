/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.service.align.resolver.internal;

import java.util.Collections;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.align.io.impl.dummy.EntityToDef;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractEntityType;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;

/**
 * Dialog tray that displays details on a JAXB entity that could not be resolved
 * as schema entity.
 * 
 * @author Simon Templer
 */
public class ViewerEntityTray extends DialogTray {

	private final EntityDefinition entity;

	/**
	 * @param entity the JAXB entity
	 * @param schemaSpace the schema space
	 */
	public ViewerEntityTray(AbstractEntityType entity, SchemaSpaceID schemaSpace) {
		this(EntityToDef.toDummyDef(entity, schemaSpace));
	}

	/**
	 * @param entity the entity definition
	 */
	public ViewerEntityTray(EntityDefinition entity) {
		this.entity = entity;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		GridLayoutFactory.fillDefaults().applyTo(page);

		// text / description
		Label label = new Label(page, SWT.WRAP);
		label.setText(
				"An entity that is referenced in a mapping cell cannot be resolved. This may be due to a changed schema, with for instance changed namespaces or property names. To update the cell please select a replacement entity to the left, a candidate may already be selected. Press Skip to leave the cell entity unchanged or Cancel to skip all remaining entities.\n\nBelow is a (inaccurate) reconstruction of the original entity for reference:");
		GridDataFactory.swtDefaults().hint(300, SWT.DEFAULT).applyTo(label);

		// viewer for dummy entity
		TreeViewer viewer = new TreeViewer(page, SWT.BORDER);
		viewer.setContentProvider(new EntityContentProvider());
		viewer.setLabelProvider(new StyledDefinitionLabelProvider(viewer));
		viewer.setInput(Collections.singleton(entity));
		viewer.getControl().setEnabled(false);
		viewer.expandAll();
		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());

		return page;
	}
}
