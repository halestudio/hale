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
package eu.esdihumboldt.hale.rcp.wizards.functions.core;

import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizardPage;

/**
 * ConcatenationofAttributesWizardpage
 * 
 * @author Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ConcatenationOfAttributesWizardPage extends
		AbstractSingleComposedCellWizardPage {

	/**
	 * 
	 */
	private final Image addImage = CoreFunctionWizardsPlugin
			.getImageDescriptor("icons/add.gif").createImage();

	/**
	 * 
	 */
	private final Image removeImage = CoreFunctionWizardsPlugin
			.getImageDescriptor("icons/remove.gif").createImage();

	/**
	 * 
	 */
	ListViewer listViewer;

	/**
	 * 
	 */
	private Text seperatorText;

	/**
	 * @param pageName
	 */
	public ConcatenationOfAttributesWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
	}

	/**
	 * @param parent
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
		this.setPageComplete(this.isPageComplete());

		Composite page = new Composite(parent, SWT.NONE);
		super.setControl(page);
		page.setLayout(new GridLayout(3, false));
		Group sourceGroup = new Group(page, SWT.NONE);
		sourceGroup.setLayout(new GridLayout(SWT.FILL_WINDING, true));
		sourceGroup.setText("Source Items");

		final Label seperatorLabel = new Label(sourceGroup, SWT.NONE);
		seperatorLabel.setText("Seperator: ");
		this.seperatorText = new Text(sourceGroup, SWT.SINGLE | SWT.BORDER);
		this.seperatorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		this.seperatorText.setText(":");
		TreeSet<SchemaItem> sourceTreeSet = (TreeSet<SchemaItem>) getParent()
				.getSourceItems();
		String[] localNames = new String[sourceTreeSet.size()];
		int k = 0;
		for (Iterator<SchemaItem> iterator = sourceTreeSet.iterator(); iterator
				.hasNext(); k++) {
			localNames[k] = iterator.next().getEntity().getLocalname();
		}

		// source value selection
		final Label comboLabel = new Label(sourceGroup, SWT.NONE);
		comboLabel.setText("Source Items: ");
		final Combo combo = new Combo(sourceGroup, SWT.DROP_DOWN | SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final ComboViewer comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setInput(localNames);

		// add source value
		Button addButton = new Button(page, SWT.PUSH);
		addButton.setImage(this.addImage);
		addButton.setToolTipText("Add a Concatenation Attribute");
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!combo.getText().equals("")) {
					ConcatenationOfAttributesWizardPage.this.listViewer
							.add(combo.getText());
				}
			}
		});

		// remove target value
		final Button removeButton = new Button(page, SWT.PUSH);
		removeButton.setImage(this.removeImage);
		removeButton
				.setToolTipText("Remove selected Concatenations Attributes");
		removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ConcatenationOfAttributesWizardPage.this.listViewer
						.getList().getItems().length != 0) {
					for (String selection : ConcatenationOfAttributesWizardPage.this.listViewer
							.getList().getSelection()) {
						ConcatenationOfAttributesWizardPage.this.listViewer
								.getList().remove(selection);
					}
				}
			}
		});

		// list
		List list = new List(page, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		this.listViewer = new ListViewer(list);
		this.listViewer.setContentProvider(new ArrayContentProvider());

	}

	/**
	 * @return the ListViewer
	 */
	public ListViewer getListViewer() {
		return this.listViewer;
	}

	/**
	 * @param listViewer
	 */
	public void setListViewer(ListViewer listViewer) {
		this.listViewer = listViewer;
	}

	/**
	 * @return the separator
	 */
	public Text getSeperatorText() {
		return this.seperatorText;
	}

	/**
	 * @param seperatorText
	 */
	public void setSeperatorText(Text seperatorText) {
		this.seperatorText = seperatorText;
	}

}
