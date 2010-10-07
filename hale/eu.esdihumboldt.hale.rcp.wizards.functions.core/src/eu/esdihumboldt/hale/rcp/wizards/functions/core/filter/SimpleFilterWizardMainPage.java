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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.filter;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizardPage;

/**
 * This class implemets a main page for the FilterWizard
 * 
 * @author Anna Pitaev, Logica; Simon Templer, FhG IGD
 * @version $Id$
 */
public class SimpleFilterWizardMainPage extends AbstractSingleComposedCellWizardPage {

	private final String initialCQL;
	
	private Label CQLLabel;
	private Text CQLEditor;
	
	/**
	 * Constructor
	 * 
	 * @param pageName the page name
	 * @param title the page title
	 * @param initialCQL the initial CQL string
	 */
	protected SimpleFilterWizardMainPage(String pageName, String title, String initialCQL) {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName); 
		setDescription("Enter your CQL-Expression to proceed filter operation.");
		
		this.initialCQL = (initialCQL == null)?(""):(initialCQL);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);

		// create a composite to hold the widgets
		Composite composite = new Composite(parent, SWT.NULL);
		// create layout for this wizard page
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginLeft = 0;
		gl.marginTop = 20;
		gl.marginRight = 70;
		composite.setLayout(gl);
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.setFont(parent.getFont());

		// labels
		this.CQLLabel = new Label(composite, SWT.TITLE);
		this.CQLLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		FontData labelFontData = parent.getFont().getFontData()[0];
		labelFontData.setStyle(SWT.BOLD);
		this.CQLLabel.setFont(new Font(parent.getDisplay(), labelFontData));
		this.CQLLabel.setText("CQL Filter Expression");
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Available variables\n(double click to insert)");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		// source area
		this.CQLEditor = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI
				| SWT.V_SCROLL);
		// TODO replace it with the selected source FeatureType value
		this.CQLEditor.setText(initialCQL);
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = 1;
		this.CQLEditor.setLayoutData(gd);
		
		// variable list
		
		SchemaItem typeItem = FilterUtils2.getParentTypeItem(getParent().getSourceItems().iterator().next());
		Set<String> variables = new TreeSet<String>();
		if (typeItem.hasChildren()) {
			for (SchemaItem child : typeItem.getChildren()) {
				variables.add(child.getName().getLocalPart());
			}
		}
		
		List list = new List(composite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		final ListViewer varList = new ListViewer(list);
		GridData lgd = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd.heightHint = list.getItemHeight() * 5;
		varList.getControl().setLayoutData(lgd);
		varList.setContentProvider(new ArrayContentProvider());
		varList.setInput(variables);
		varList.getList().addMouseListener(new MouseAdapter() {

			/**
			 * @see MouseAdapter#mouseDoubleClick(MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int index = varList.getList().getSelectionIndex();
				if (index >= 0) {
					String var = varList.getList().getItem(index);
					CQLEditor.insert(var);
					CQLEditor.setFocus();
				}
			}
			
		});
		
		Button clearButton = new Button(composite, SWT.PUSH);
		clearButton.setText("Clear/remove filter");
		clearButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CQLEditor.setText("");
			}
			
		});

		setErrorMessage(null); // should not initially have error message
		super.setControl(composite);
	}
	
	/**
	 * Get the CQL String
	 * 
	 * @return the CQL String
	 */
	public String getCQL() {
		return CQLEditor.getText();
	}

}
