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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.scripting.groovy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.common.scripting.Script;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.editors.AbstractAttributeEditor;
import eu.esdihumboldt.hale.ui.util.IColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovyColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovySourceViewerUtil;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.source.SourceViewerKeyBindings;

/**
 * Editor for groovy scripts.
 * 
 * @author Kai Schwierczek
 */
public class GroovyEditor extends AbstractAttributeEditor<String> {

	private final Composite composite;
	private final Script script;
//	private final Class<?> binding;
	private final SourceViewer viewer;
	private TableViewer varTable;
	private Collection<PropertyEntityDefinition> variables = Collections.emptySet();
	private String currentValue = "";
	private boolean valid;
	private final TestValues testValues;
	private final ControlDecoration decorator;
	private IColorManager colorManager;

	/**
	 * Default constructor.
	 * 
	 * @param parent the parent composite
	 * @param script the script object
	 * @param binding the target binding
	 */
	public GroovyEditor(Composite parent, Script script, Class<?> binding) {
		this.script = script;
		this.colorManager = new GroovyColorManager();
//		this.binding = binding;
		testValues = new InstanceTestValues();

		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().create());

		viewer = createAndLayoutTextField(composite);
		viewer.configure(new SimpleGroovySourceViewerConfiguration(colorManager));
		IDocument document = new Document("");
		GroovySourceViewerUtil.setupDocument(document);
		viewer.setDocument(document);

		viewer.getControl().addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				// color manager needs to be disposed
				colorManager.dispose();
			}
		});

		// control decoration
		decorator = new ControlDecoration(viewer.getControl(), SWT.LEFT | SWT.TOP, composite);
		// set initial status
		decorator.hide();
		// set image
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_ERROR);
		decorator.setImage(fieldDecoration.getImage());

		viewer.getDocument().addDocumentListener(new IDocumentListener() {

			@Override
			public void documentChanged(DocumentEvent event) {
				// update value
				String newValue = event.getDocument().get();
				fireValueChanged(VALUE, currentValue, newValue);
				currentValue = newValue;

				validate();
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				// ignore
			}
		});

		// variables
		Label label = new Label(composite, SWT.NONE);
		label.setText("Available variables (double click to insert)");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		// variables table
		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);
		varTable = new TableViewer(tableComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		TableViewerColumn column = new TableViewerColumn(varTable, SWT.NONE);
		columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(1, false));
		varTable.setContentProvider(ArrayContentProvider.getInstance());
		varTable.setLabelProvider(new DefinitionLabelProvider(null, true, true) {

			/**
			 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return GroovyEditor.this.script.getVariableName((PropertyEntityDefinition) element);
			}
		});
		varTable.getTable().addMouseListener(new MouseAdapter() {

			/**
			 * @see MouseAdapter#mouseDoubleClick(MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int index = varTable.getTable().getSelectionIndex();
				if (index >= 0) {
					String var = varTable.getTable().getItem(index).getText();
					Point selRange = viewer.getSelectedRange();
					try {
						viewer.getDocument().replace(selRange.x, selRange.y, var);
					} catch (BadLocationException ble) {
						// ignore
					}
				}
			}
		});

		validate();
	}

	/**
	 * Validates the current input against the currently available variables.
	 */
	private void validate() {
		// also the returned type isn't checked.
		String result = GroovyEditor.this.script.validate(currentValue, createPropertyValues(),
				HaleUI.getServiceProvider());
		boolean oldValid = valid;
		valid = result == null;
		if (valid)
			decorator.hide();
		else {
			decorator.setDescriptionText(result);
			decorator.show();
		}
		if (valid != oldValid)
			fireStateChanged(IS_VALID, oldValid, valid);
	}

	/**
	 * Returns an {@link Iterable} for the current variables for use with the
	 * {@link Script}.
	 * 
	 * @return an {@link Iterable} for the current variables for use with the
	 *         {@link Script}
	 */
	protected Iterable<PropertyValue> createPropertyValues() {
		Collection<PropertyValue> values = new ArrayList<PropertyValue>(variables.size());
		for (PropertyEntityDefinition property : variables)
			values.add(new PropertyValueImpl(testValues.get(property), property));

		return values;
	}

	/**
	 * Create the text field.
	 * 
	 * @param parent the parent composite
	 * @return the input text field.
	 */
	private SourceViewer createAndLayoutTextField(Composite parent) {
		IVerticalRuler ruler = createRuler();
		SourceViewer viewer = new SourceViewer(parent, ruler, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.getControl().setLayoutData(
				GridDataFactory.fillDefaults().grab(true, false).indent(7, 0).create());
		viewer.getTextWidget().setFont(JFaceResources.getTextFont());

		SourceViewerKeyBindings.installDefault(viewer);

		return viewer;
	}

	/**
	 * Create the vertical ruler for the source viewer.
	 * 
	 * @return the vertical ruler
	 */
	private IVerticalRuler createRuler() {
		final Display display = Display.getCurrent();
		CompositeRuler ruler = new CompositeRuler(3);
		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
		lineNumbers.setBackground(display.getSystemColor(SWT.COLOR_GRAY)); // SWT.COLOR_INFO_BACKGROUND));
		lineNumbers.setForeground(display.getSystemColor(SWT.COLOR_BLACK)); // SWT.COLOR_INFO_FOREGROUND));
		lineNumbers.setFont(JFaceResources.getTextFont());
		ruler.addDecorator(0, lineNumbers);
		return ruler;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return composite;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(String value) {
		viewer.getDocument().set(value);
		currentValue = value;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getValue()
	 */
	@Override
	public String getValue() {
		return currentValue;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) {
		setValue(text);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getAsText()
	 */
	@Override
	public String getAsText() {
		return getValue();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return valid;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.editors.AbstractAttributeEditor#setVariables(java.util.Collection)
	 */
	@Override
	public void setVariables(Collection<PropertyEntityDefinition> properties) {
		variables = properties;
		varTable.setInput(variables);
		validate();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getValueType()
	 */
	@Override
	public String getValueType() {
		return script.getId();
	}
}
