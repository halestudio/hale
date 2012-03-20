/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.style.editors;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.geotools.styling.Rule;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.Filter;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Editor for {@link Rule}s
 * @param <T> the {@link Symbolizer} type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class RuleEditor<T extends Symbolizer> implements Editor<Rule> {
	
	private final StyleBuilder styleBuilder = new StyleBuilder();
	
	private final Composite page;
	
	private final Editor<Filter> filterEditor;
	
	private final Editor<? extends Symbolizer> symbolizerEditor;
	
	/**
	 * Creates a {@link Rule} editor
	 * 
	 * @param parent the parent composite
	 * @param typeDefinition the type Definition
	 * @param filter the initial {@link Filter}
	 * @param symbolizerType the {@link Symbolizer} type
	 * @param symbolizer the initial {@link Symbolizer}
	 * @param symbolizerFactory a {@link Symbolizer} editor factory
	 */
	public RuleEditor(Composite parent, TypeDefinition typeDefinition, Filter filter,
			Class<T> symbolizerType, T symbolizer, EditorFactory<T> symbolizerFactory) {
		super();
	
		page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(1, false));
		
		// filter
		Label filterLabel = new Label(page, SWT.NONE);
		filterLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		filterLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		filterLabel.setText(Messages.RuleEditor_FilterLabel);
		
		filterEditor = new FilterEditor(page, typeDefinition, filter);
		filterEditor.getControl().setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));
		
		// symbolizer
		Label symbolLabel = new Label(page, SWT.NONE);
		symbolLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		symbolLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		symbolLabel.setText(symbolizerType.getSimpleName());
		
		symbolizerEditor = symbolizerFactory.createEditor(page, symbolizer);
		symbolizerEditor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return page;
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public Rule getValue() {
		Rule rule = styleBuilder.createRule(symbolizerEditor.getValue());
		rule.setFilter(filterEditor.getValue());
		
		return rule;
	}

	/**
	 * @see Editor#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return filterEditor.isChanged() || symbolizerEditor.isChanged();
	}

	/**
	 * @see Editor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Rule value) {
		throw new UnsupportedOperationException("Setting the value on the rule editor not allowed, use the constructor instead."); //$NON-NLS-1$
	}

}
