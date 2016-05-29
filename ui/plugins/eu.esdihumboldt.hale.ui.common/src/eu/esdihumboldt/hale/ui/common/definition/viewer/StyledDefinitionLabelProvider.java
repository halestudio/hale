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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import java.text.MessageFormat;

import javax.annotation.Nullable;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.ui.common.service.population.Population;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;

/**
 * Extended label provider for definitions.
 * 
 * @author Simon Templer
 */
public class StyledDefinitionLabelProvider extends StyledCellLabelProvider
		implements ILabelProvider, IColorProvider {

	private final ILabelProvider defaultLabels;
	private final boolean suppressCardinality;

	/**
	 * Default constructor.
	 * 
	 * Styled definition label provider without support for style legend images.
	 */
	public StyledDefinitionLabelProvider() {
		this(new DefinitionLabelProvider(null));
	}

	/**
	 * Default constructor
	 * 
	 * @param associatedViewer the associated viewer (needed for style legend
	 *            support) or <code>null</code>
	 */
	public StyledDefinitionLabelProvider(@Nullable Viewer associatedViewer) {
		this(new DefinitionLabelProvider(associatedViewer));
	}

	/**
	 * Create a styled label provider based on the given plain label provider
	 * for definitions.
	 * 
	 * @param definitionLabelProvider the definition label provider
	 */
	public StyledDefinitionLabelProvider(ILabelProvider definitionLabelProvider) {
		this(definitionLabelProvider, false);
	}

	/**
	 * Create a styled label provider based on the given plain label provider
	 * for definitions.
	 * 
	 * @param definitionLabelProvider the definition label provider
	 * @param suppressCardinality if displaying the cardinality should be
	 *            suppressed
	 */
	public StyledDefinitionLabelProvider(ILabelProvider definitionLabelProvider,
			boolean suppressCardinality) {
		super();

		this.suppressCardinality = suppressCardinality;
		this.defaultLabels = definitionLabelProvider;
	}

	/**
	 * @see StyledCellLabelProvider#update(ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();

		element = extractElement(element);

		StyledString text = new StyledString(defaultLabels.getText(element));

		cell.setImage(defaultLabels.getImage(element));

		String contextText = null;
		String countText = null;
		if (element instanceof EntityDefinition) {
			PopulationService ps = PlatformUI.getWorkbench().getService(PopulationService.class);
			if (ps != null) {
				Population pop = ps.getPopulation((EntityDefinition) element);
				int count = pop.getOverallCount();
				int parents = pop.getParentsCount();
				switch (count) {
				case Population.UNKNOWN:
					countText = "\u00d7?";
					break;
				case 0:
					break;
				default:
					countText = "\u00d7" + count;
					if (parents != count) {
						countText += " (" + parents + ")";
					}
				}
			}

			contextText = AlignmentUtil.getContextText((EntityDefinition) element);
			element = ((EntityDefinition) element).getDefinition();
		}

		// append cardinality
		if (!suppressCardinality && element instanceof ChildDefinition<?>) {
			Cardinality cardinality = null;
			if (((ChildDefinition<?>) element).asGroup() != null) {
				cardinality = ((ChildDefinition<?>) element).asGroup()
						.getConstraint(Cardinality.class);
			}
			else if (((ChildDefinition<?>) element).asProperty() != null) {
				cardinality = ((ChildDefinition<?>) element).asProperty()
						.getConstraint(Cardinality.class);
			}

			if (cardinality != null) {
				// only append cardinality if it isn't 1/1
				if (cardinality.getMinOccurs() != 1 || cardinality.getMaxOccurs() != 1) {
					String card = " "
							+ MessageFormat.format("({0}..{1})",
									new Object[] { Long.valueOf(cardinality.getMinOccurs()),
											(cardinality.getMaxOccurs() == Cardinality.UNBOUNDED)
													? ("n")
													: (Long.valueOf(cardinality.getMaxOccurs())) });
					text.append(card, StyledString.COUNTER_STYLER);
				}
			}
		}

		if (contextText != null) {
			contextText = " " + contextText;
			text.append(contextText, StyledString.DECORATIONS_STYLER);
		}

		if (countText != null) {
			countText = " " + countText;
			text.append(countText, StyledString.QUALIFIER_STYLER);
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
	 * Extract the cell definition or entity definition.<br>
	 * <br>
	 * This default implementation just returns the element.
	 * 
	 * @param element the element associated to a cell
	 * @return the extracted definition or entity definition
	 */
	protected Object extractElement(Object element) {
		return element;
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
		element = extractElement(element);

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
		// default background
		return null;
	}

}
