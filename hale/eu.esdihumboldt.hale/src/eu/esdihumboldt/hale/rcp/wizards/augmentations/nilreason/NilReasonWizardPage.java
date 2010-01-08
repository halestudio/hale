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

package eu.esdihumboldt.hale.rcp.wizards.augmentations.nilreason;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.cst.corefunctions.NilReasonFunction.NilReasonType;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.AugmentationWizardPage;

/**
 * Main {@link NilReasonWizard} page
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NilReasonWizardPage extends AugmentationWizardPage {
	
	private NilReasonType type = NilReasonType.unknown;

	/**
	 * @see AugmentationWizardPage#AugmentationWizardPage(String, String, ImageDescriptor)
	 */
	public NilReasonWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		
		page.setLayout(new GridLayout(1, false));
		
		SchemaItem item = getParent().getItem();
		
		String ftName;
		String atName;
		if (item.isAttribute()) {
			ftName = item.getParent().getName().getLocalPart();
			atName = item.getName().getLocalPart();
		}
		else {
			ftName = item.getName().getLocalPart();
			atName = null;
		}
		
		Group group = new Group(page, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setLayout(new GridLayout(1, false));
		if (atName != null) {
			group.setText("Please select a Nil reason for " + 
					ftName + "." + atName + ":");
		}
		else {
			group.setText("Please select a default Nil reason for the attributes of " + 
					ftName + ":");
		}
		
		Button unpopulated = new Button(group, SWT.RADIO);
		unpopulated.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				type = NilReasonType.unpopulated;
			}
			
		});
		if (atName != null) {
			unpopulated.setText("unpopulated (all instances of " + ftName + " will have no value for " + atName + ")");
		}
		else {
			unpopulated.setText("unpopulated (if an attribute has no value then all instances of " + ftName + " will have no value for this attribute)");
		}
		
		Button unknown = new Button(group, SWT.RADIO);
		unknown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				type = NilReasonType.unknown;
			}
			
		});
		if (atName != null) {
			unknown.setText("unknown (only some instances of " + ftName + " will have no value for " + atName + ")");
		}
		else {
			unknown.setText("unknown (if an attribute has no value that doesn't mean that all instances of " + ftName + " have no value for this attribute)");
		}
		
		switch (type) {
		case unpopulated:
			unpopulated.setSelection(true);
			break;
		default:
			unknown.setSelection(true);
			break;
		}
		
		setControl(page);
	}

	/**
	 * @return the type
	 */
	public NilReasonType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(NilReasonType type) {
		this.type = type;
	}

}
