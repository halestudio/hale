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

package eu.esdihumboldt.hale.ui.views.properties.definition.typedefinition;

import java.util.Collections;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeDefinitionContentProvider;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;

/**
 * Property section that shows the type structure.
 * 
 * @author Simon Templer
 */
public class TypeDefinitionStructure extends DefaultDefinitionSection<TypeDefinition> {

	private TreeViewer tree;

	private final StyledDefinitionLabelProvider definitionImages = new StyledDefinitionLabelProvider(
			new DefinitionLabelProvider(null) {

				@Override
				public String getText(Object element) {
					if (element instanceof PropertyDefinition) {
						return super.getText(element)
								+ " : "
								+ ((PropertyDefinition) element).getPropertyType().getName()
										.getLocalPart();
					}
					return super.getText(element);
				}

			});

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite page = getWidgetFactory().createComposite(parent);
		page.setLayout(new FillLayout());

		tree = new TreeViewer(page);
		tree.setContentProvider(new TypeDefinitionContentProvider(tree));
		tree.setLabelProvider(definitionImages);
		// no comparator to show order
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if (getDefinition() != null)
			tree.setInput(Collections.singleton(getDefinition()));
		else
			tree.setInput(Collections.emptySet());
	}

	/**
	 * @see AbstractPropertySection#shouldUseExtraSpace()
	 */
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	/**
	 * @see AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose() {
		definitionImages.dispose();

		super.dispose();
	}

}
