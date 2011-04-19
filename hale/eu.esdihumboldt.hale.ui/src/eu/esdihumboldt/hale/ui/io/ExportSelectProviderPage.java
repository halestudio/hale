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

package eu.esdihumboldt.hale.ui.io;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.ui.HaleSharedImages;
import eu.esdihumboldt.hale.ui.HaleUIPlugin;
import eu.esdihumboldt.hale.ui.HaleWizardPage;

/**
 * Wizard page that allows selecting an I/O provider
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * @param <T> the {@link IOProviderFactory} type used in the wizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ExportSelectProviderPage<P extends IOProvider, T extends IOProviderFactory<P>, 
	W extends ExportWizard<P, T>> extends IOWizardPage<P, T, W> {

	/**
	 * Default constructor
	 */
	public ExportSelectProviderPage() {
		super("export.selProvider");
		setTitle("Select a format");
		setDescription("Please select a format to export to");
		setImageDescriptor(HaleUIPlugin.getDefault().getImageRegistry().getDescriptor(
				HaleSharedImages.IMG_EXPORT_WIZARD));
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(1, false));
		
		// create provider combo
		ComboViewer providers = new ComboViewer(page, SWT.SIMPLE | SWT.READ_ONLY);
		providers.setContentProvider(ArrayContentProvider.getInstance());
		providers.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IOProviderFactory<?>) {
					return ((IOProviderFactory<?>) element).getDisplayName();
				}
				return super.getText(element);
			}
			
		});
		providers.setInput(getWizard().getFactories());
		
		// process current selection
		ISelection selection = providers.getSelection();
		setPageComplete(selection.isEmpty());
		updateWizard(selection);
		
		// process selection changes
		providers.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				setPageComplete(selection.isEmpty());
				updateWizard(selection);
			}
		});
	}

	/**
	 * Update the wizard 
	 * 
	 * @param selection the current selection
	 */
	@SuppressWarnings("unchecked")
	private void updateWizard(ISelection selection) {
		if (selection.isEmpty()) {
			getWizard().setProviderFactory(null);
		}
		else if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object element = sel.getFirstElement();
			getWizard().setProviderFactory((T) element);
		}
	}

}
