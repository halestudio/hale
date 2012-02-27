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

package eu.esdihumboldt.hale.ui.views.properties.definition.typedefinition;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeDefinitionContentProvider;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;

/**
 * Property section that shows the type structure.
 * @author Simon Templer
 */
public class TypeDefinitionStructure extends DefaultDefinitionSection<TypeDefinition> {

	private TreeViewer tree;
	
	private DefinitionLabelProvider definitionImages = new DefinitionLabelProvider();
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
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
		tree.setInput(getDefinition());
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
