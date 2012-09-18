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

package eu.esdihumboldt.hale.ui.io;

import java.util.Collection;

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

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.ui.HaleWizardPage;

/**
 * Wizard page that allows selecting an I/O provider
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class ExportSelectProviderPage<P extends ExportProvider, W extends ExportWizard<P>> extends
		IOWizardPage<P, W> {

	/**
	 * Default constructor
	 */
	public ExportSelectProviderPage() {
		super("export.selProvider");
		setTitle("Export format");
		setDescription("Please select a format to export to");
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));

		// create provider combo
		ComboViewer providers = new ComboViewer(page, SWT.DROP_DOWN | SWT.READ_ONLY);
		providers.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		providers.setContentProvider(ArrayContentProvider.getInstance());
		providers.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IOProviderDescriptor) {
					return ((IOProviderDescriptor) element).getDisplayName();
				}
				return super.getText(element);
			}

		});
		Collection<IOProviderDescriptor> factories = getWizard().getFactories();
		providers.setInput(factories);

		// set selection
		if (!factories.isEmpty()) {
			providers.setSelection(new StructuredSelection(factories.iterator().next()), true);
		}

		// process current selection
		ISelection selection = providers.getSelection();
		setPageComplete(!selection.isEmpty());
		updateWizard(selection);

		// process selection changes
		providers.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				setPageComplete(!selection.isEmpty());
				updateWizard(selection);
			}
		});
	}

	/**
	 * Update the wizard
	 * 
	 * @param selection the current selection
	 */
	private void updateWizard(ISelection selection) {
		if (selection.isEmpty()) {
			providerFactoryChanged(null);
		}
		else if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object element = sel.getFirstElement();
			providerFactoryChanged((IOProviderDescriptor) element);
		}
	}

	/**
	 * Called when the provider factory has been initialized or changed
	 * 
	 * @param providerFactory the provider factory
	 */
	protected void providerFactoryChanged(IOProviderDescriptor providerFactory) {
		getWizard().setProviderFactory(providerFactory);
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(P provider) {
		return true;
	}

}
