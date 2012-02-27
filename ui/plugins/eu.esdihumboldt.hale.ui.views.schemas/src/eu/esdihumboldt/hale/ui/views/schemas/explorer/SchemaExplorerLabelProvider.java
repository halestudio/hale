/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.viewer.TipProvider;

/**
 * Extended label provider for definitions
 * @author Simon Templer
 */
public class SchemaExplorerLabelProvider extends StyledCellLabelProvider
		implements TipProvider, ILabelProvider, IColorProvider {
	
	private final DefinitionLabelProvider defaultLabels = new DefinitionLabelProvider();
	
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
	 * @see StyledCellLabelProvider#update(ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		
		StyledString text = new StyledString(defaultLabels.getText(element));
		
		cell.setImage(defaultLabels.getImage(element));
		
//		boolean defContext = true;
		if (element instanceof EntityDefinition) {
			EntityDefinition entityDef = (EntityDefinition) element;
			element = entityDef.getDefinition();
//			
//			List<ChildContext> path = entityDef.getPropertyPath();
//			if (path != null && !path.isEmpty()) {
//				ChildContext lastContext = path.get(path.size() - 1);
//				defContext = lastContext.getContextName() == null;
//			}
		}
		//TODO different styling for non-default context?
		
		// append cardinality
		if (element instanceof ChildDefinition<?>) {
			Cardinality cardinality = null;
			if (((ChildDefinition<?>) element).asGroup() != null) {
				cardinality = ((ChildDefinition<?>) element).asGroup().getConstraint(Cardinality.class);
			}
			else if (((ChildDefinition<?>) element).asProperty() != null) {
				cardinality = ((ChildDefinition<?>) element).asProperty().getConstraint(Cardinality.class);
			}
			
			if (cardinality != null) {
				// only append cardinality if it isn't 1/1
				if (cardinality.getMinOccurs() != 1 || cardinality.getMaxOccurs() != 1) {
					String card = " " + MessageFormat.format("({0}..{1})", 
							new Object[]{Long.valueOf(cardinality.getMinOccurs()), 
							(cardinality.getMaxOccurs() == Cardinality.UNBOUNDED)?("n"):(Long.valueOf(cardinality.getMaxOccurs()))});
					text.append(card, StyledString.COUNTER_STYLER);
				}
			}
		}
		
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
		
		Color foreground = getForeground(cell.getElement());
		cell.setForeground(foreground);
		
		Color background = getBackground(cell.getElement());
		cell.setBackground(background);
		
		super.update(cell);
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
	 * Only implemented because of use with {@link PatternFilter} and
	 * {@link ViewerComparator}
	 * 
	 * @see ILabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		return null;
	}

	/**
	 * Only implemented for use with {@link PatternFilter} and 
	 * {@link ViewerComparator}
	 * 
	 * @see ILabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof EntityDefinition) {
			element = ((EntityDefinition) element).getDefinition();
		}
		
		if (element instanceof Definition<?>) {
			return ((Definition<?>) element).getDisplayName();
		}
		
		return null;
	}

	/**
	 * @see StyledCellLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		defaultLabels.dispose();
		
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
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
			Alignment alignment = as.getAlignment();
			
			EntityDefinition entityDef = (EntityDefinition) element;
			return getEntityBackground(entityDef, alignment, 
					entityDef.getPropertyPath().isEmpty());
		}
		
		return null;
	}

	private Color getEntityBackground(EntityDefinition entityDef,
			Alignment alignment, boolean isType) {
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
				throw new IllegalStateException("Entity definition with illegal schema space encountered");
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
