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

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import java.util.Collection;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.viewer.TipProvider;

/**
 * Extended label provider for definitions
 * 
 * @author Simon Templer
 */
public class SchemaExplorerLabelProvider extends StyledDefinitionLabelProvider implements
		TipProvider {

	private final Color typeCellColor;
	private final Color propertyCellColor;
	private final Color augmentedColor;
	private final Color indirectMappingColor;

	/**
	 * Default constructor
	 */
	public SchemaExplorerLabelProvider() {
		super();

		final Display display = PlatformUI.getWorkbench().getDisplay();

		typeCellColor = new Color(display, 150, 190, 120);
		propertyCellColor = new Color(display, 190, 220, 170);
		augmentedColor = new Color(display, 184, 181, 220);
		indirectMappingColor = new Color(display, 245, 245, 145);
	}

	/**
	 * @see TipProvider#getToolTip(Object)
	 */
	@Override
	public String getToolTip(Object element) {
		if (element instanceof EntityDefinition) {
			element = ((EntityDefinition) element).getDefinition();
		}

		if (element instanceof Definition<?>) {
			String description = ((Definition<?>) element).getDescription();
			if (description != null && !description.isEmpty()) {
				return description;
			}
		}

		return null;
	}

	/**
	 * @see StyledCellLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		typeCellColor.dispose();
		propertyCellColor.dispose();
		augmentedColor.dispose();
		indirectMappingColor.dispose();

		super.dispose();
	}

	/**
	 * @see IColorProvider#getForeground(Object)
	 */
	@Override
	public Color getForeground(Object element) {
		// default foreground
		return null;
	}

	/**
	 * @see IColorProvider#getBackground(Object)
	 */
	@Override
	public Color getBackground(Object element) {
		if (element instanceof EntityDefinition) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
			Alignment alignment = as.getAlignment();

			EntityDefinition entityDef = (EntityDefinition) element;
			return getEntityBackground(entityDef, alignment, entityDef.getPropertyPath().isEmpty());
		}

		return null;
	}

	private Color getEntityBackground(EntityDefinition entityDef, Alignment alignment,
			boolean isType) {
		// check for directly associated cells
		Collection<? extends Cell> cells = alignment.getCells(entityDef);

		if (!cells.isEmpty()) {
			if (isType) {
				return typeCellColor;
			}

			for (Cell cell : cells) {
				if (!AlignmentUtil.isAugmentation(cell)) {
					return propertyCellColor;
				}
			}

			return augmentedColor;
		}

		// check for cells associated to children of the entity definition
		cells = alignment.getCells(entityDef.getType(), entityDef.getSchemaSpace());

		for (Cell cell : cells) {
			ListMultimap<String, ? extends Entity> entities;
			switch (entityDef.getSchemaSpace()) {
			case SOURCE:
				entities = cell.getSource();
				break;
			case TARGET:
				entities = cell.getTarget();
				break;
			default:
				throw new IllegalStateException(
						"Entity definition with illegal schema space encountered");
			}

			if (entities != null) {
				for (Entity entity : entities.values()) {
					if (AlignmentUtil.isParent(entityDef, entity.getDefinition())) {
						return indirectMappingColor;
					}
				}
			}
		}

		// default color
		return null;
	}

}
