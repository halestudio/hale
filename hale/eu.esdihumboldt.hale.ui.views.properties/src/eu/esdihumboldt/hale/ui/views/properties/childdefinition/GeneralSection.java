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

package eu.esdihumboldt.hale.ui.views.properties.childdefinition;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.ui.views.properties.definition.AbstractDefinitionSection;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefinitionDescriptionSection;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefinitionLocationSection;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefinitionNameSection;

/**
 * TODO Type description
 * @author Patrick Lieb
 */
public class GeneralSection extends AbstractDefinitionSection{
	
	private ChildDefinition<?> childdefinition;
	
	private Text description;
	
	private Text location;
	
	private Text namespace;
	
	private Text localname;
	
	private DefinitionDescriptionSection descriptionsection;
	
	private DefinitionLocationSection locationsection;
	
	private DefinitionNameSection namesection;
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
	descriptionsection = new DefinitionDescriptionSection();
	locationsection = new DefinitionLocationSection();
	namesection = new DefinitionNameSection();
	descriptionsection.createControls(parent, aTabbedPropertySheetPage);
	description = descriptionsection.getDescription();
	locationsection.createControls(parent, aTabbedPropertySheetPage);
	location = locationsection.getLocation();
	namesection.createControls(parent, aTabbedPropertySheetPage);
	namespace = namesection.getNamespace();
	localname = namesection.getLocalName();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.AbstractSection#setInput(java.lang.Object)
	 */
	@Override
	protected void setInput(Object input) {
		Assert.isTrue(input instanceof ChildDefinition<?>);
		this.childdefinition = (ChildDefinition<?>) input;
		AbstractDefinitionSection.setDefinition(childdefinition.getParentType());
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		descriptionsection.refresh();
		description = descriptionsection.getDescription();
		locationsection.refresh();
		location = locationsection.getLocation();
		namesection.refresh();
		namespace = namesection.getNamespace();
		localname = namesection.getLocalName();
	}
}
