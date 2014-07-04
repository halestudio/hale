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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
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

	private ListViewer providers;

	private Collection<IOProviderDescriptor> factories;

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

		// create provider list viewer
		providers = new ListViewer(page, SWT.BORDER);
		providers.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		providers.setContentProvider(ArrayContentProvider.getInstance());
		providers.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IOProviderDescriptor) {
					return ((IOProviderDescriptor) element).getDisplayName();
				}
				return "Custom configuration: " + super.getText(element);
			}

		});
		factories = getWizard().getFactories();
		List<Object> input = new ArrayList<>();

		// check for presets
		Collection<String> presets = getWizard().getPresets();
		if (presets != null && !presets.isEmpty()) {
			input.addAll(presets);
		}

		input.addAll(factories);
		providers.setInput(input);

		providers.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (canFlipToNextPage()) {
					getContainer().showPage(getNextPage());
					return;
				}
			}
		});

		// set initial selection
//		if (!factories.isEmpty()) {
//			providers.setSelection(new StructuredSelection(factories.iterator().next()), true);
//		}

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
		setErrorMessage(null);
		if (selection.isEmpty()) {
			providerFactoryChanged(null);
			setMessage(null);
		}
		else if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object element = sel.getFirstElement();
			if (element instanceof IOProviderDescriptor) {
				IOProviderDescriptor desc = (IOProviderDescriptor) element;

				String descrText = desc.getDescription();
				if (descrText != null) {
					setMessage(descrText, INFORMATION);
				}
				else {
					setMessage(null);
				}

				providerFactoryChanged(desc);
			}
			else {
				if (getWizard().setPreset(element.toString())) {
					String msg = null;
					// try to get preset description
					IOConfiguration conf = getWizard().getProjectService().getExportConfiguration(
							element.toString());
					if (conf != null) {
						Value val = conf.getProviderConfiguration().get(
								ExportConfigurations.PARAM_CONFIGURATION_DESCRIPTION);
						if (val != null) {
							String descr = val.as(String.class);
							if (descr != null && !descr.isEmpty()) {
								msg = descr;
							}
						}
					}

					if (msg == null) {
						msg = MessageFormat.format("Use custom export configuration ''{0}''",
								element);
					}
					setMessage(msg, INFORMATION);
				}
				else {
					setMessage(null);
					setErrorMessage("Invalid preset");
				}
			}
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

	@Override
	public void loadPreSelection(IOConfiguration conf) {
		for (IOProviderDescriptor desc : factories) {
			if (desc.getIdentifier().equals(conf.getProviderId()))
				providers.setSelection(new StructuredSelection(desc), true);
		}
	}
}
