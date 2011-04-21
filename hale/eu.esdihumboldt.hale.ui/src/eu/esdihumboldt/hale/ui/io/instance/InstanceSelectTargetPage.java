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

package eu.esdihumboldt.hale.ui.io.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.instance.io.InstanceValidatorFactory;
import eu.esdihumboldt.hale.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.instance.io.InstanceWriterFactory;
import eu.esdihumboldt.hale.ui.io.ExportSelectTargetPage;
import eu.esdihumboldt.hale.ui.io.IOWizardListener;

/**
 * Wizard page that allows selecting a target instance file and a corresponding
 * validator
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class InstanceSelectTargetPage extends ExportSelectTargetPage<InstanceWriter, 
		InstanceWriterFactory, InstanceExportWizard> implements IOWizardListener<InstanceWriter, InstanceWriterFactory, InstanceExportWizard> {

	private ComboViewer validators;
	
	/**
	 * @see ExportSelectTargetPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		super.createContent(page);
		// page has a 3-column grid layout
		
		Group validatorGroup = new Group(page, SWT.NONE);
		validatorGroup.setText("Validation");
		validatorGroup.setLayoutData(GridDataFactory.swtDefaults()
				.align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false)
				.span(3, 1)
				.indent(8, 10)
				.create());
		validatorGroup.setLayout(GridLayoutFactory.swtDefaults()
				.numColumns(1)
				.margins(10, 8)
				.create());
		
		Label vabel = new Label(validatorGroup, SWT.NONE);
		vabel.setText("Please select a validator if you want to validate the exported file");
		vabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		
		validators = new ComboViewer(validatorGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		validators.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		validators.setContentProvider(ArrayContentProvider.getInstance());
		validators.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IOProviderFactory<?>) {
					return ((IOProviderFactory<?>) element).getDisplayName();
				}
				return super.getText(element);
			}
			
		});
		updateInput();
		
		// process selection changes
		validators.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				updateWizard(selection);
			}
		});
	}

	/**
	 * Update the input of the validator combo viewer
	 */
	private void updateInput() {
		//TODO remember last selection and try to retain it?
		
		List<Object> input = new ArrayList<Object>();
		input.add("No validation");
		
		ContentType contentType = getWizard().getContentType();
		if (contentType != null) {
			Collection<InstanceValidatorFactory> factories = OsgiUtils.getServices(InstanceValidatorFactory.class);
			factories = HaleIO.filterFactories(factories, contentType);
			input.addAll(factories);
		}
		
		validators.setInput(input);
		
		// set selection
		validators.setSelection(new StructuredSelection(
				input.get(0)), true);
		
		// process current selection
		ISelection selection = validators.getSelection();
		updateWizard(selection);
	}
	
	/**
	 * Update the wizard 
	 * 
	 * @param selection the current selection
	 */
	private void updateWizard(ISelection selection) {
		if (selection.isEmpty()) {
			getWizard().setValidatorFactory(null);
		}
		else if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object element = sel.getFirstElement();
			if (element instanceof InstanceValidatorFactory) {
				getWizard().setValidatorFactory((InstanceValidatorFactory) element);
			}
			else {
				// element that disables validating
				getWizard().setValidatorFactory(null);
			}
		}
	}

	/**
	 * @see ExportSelectTargetPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		super.onShowPage();
		
		updateInput();
	}

	/**
	 * @see IOWizardListener#contentTypeChanged(ContentType)
	 */
	@Override
	public void contentTypeChanged(ContentType contentType) {
		updateInput();
	}

	/**
	 * @see IOWizardListener#providerFactoryChanged(IOProviderFactory)
	 */
	@Override
	public void providerFactoryChanged(InstanceWriterFactory providerFactory) {
		// do nothing
	}

}
