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

package eu.esdihumboldt.hale.ui.io.source;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.io.ImportSource;

/**
 * Abstract {@link ImportSource} implementation offering provider selection.
 * 
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Simon Templer
 * @since 2.5
 */
public abstract class AbstractProviderSource<P extends ImportProvider> extends AbstractSource<P> {

	private ComboViewer providers;

	/**
	 * Create the provider selector combo viewer. Once created it can be
	 * retrieved using {@link #getProviders()}. This should be called in
	 * {@link #createControls(Composite)}.
	 * 
	 * @param parent the parent composite
	 * @return the created combo viewer
	 */
	protected ComboViewer createProviders(Composite parent) {
		// create provider combo
		providers = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
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

		// process selection changes
		providers.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				onProviderSelectionChanged(event);
			}
		});

		return providers;
	}

	/**
	 * Called when the provider selection changes.
	 * 
	 * @param event the selection changed event
	 */
	protected void onProviderSelectionChanged(SelectionChangedEvent event) {
		updateState(false);
	}

	/**
	 * Get the provider selector combo viewer.
	 * 
	 * @return the combo viewer with the I/O providers
	 */
	protected ComboViewer getProviders() {
		return providers;
	}

	/**
	 * Update the provider selector when the content type has changed. This is
	 * based on the content type stored in the source configuration.
	 */
	protected void updateProvider() {
		IContentType contentType = getConfiguration().getContentType();
		if (contentType != null) {
			IOProviderDescriptor lastSelected = null;
			ISelection provSel = providers.getSelection();
			if (!provSel.isEmpty() && provSel instanceof IStructuredSelection) {
				lastSelected = (IOProviderDescriptor) ((IStructuredSelection) provSel)
						.getFirstElement();
			}

			List<IOProviderDescriptor> supported = HaleIO.filterFactories(getConfiguration()
					.getFactories(), contentType);
			providers.setInput(supported);

			if (lastSelected != null && supported.contains(lastSelected)) {
				// reuse old selection
				providers.setSelection(new StructuredSelection(lastSelected), true);
			}
			else if (!supported.isEmpty()) {
				// select first provider
				providers.setSelection(new StructuredSelection(supported.get(0)), true);
			}

			providers.getControl().setEnabled(supported.size() > 1);
		}
		else {
			providers.setInput(null);
			providers.getControl().setEnabled(false);
		}

	}

	/**
	 * Update the page state. This includes setting a provider factory on the
	 * wizard if applicable and setting the complete state of the page.<br>
	 * <br>
	 * This should be called in {@link #createControls(Composite)} to initialize
	 * the page state.
	 * 
	 * @param updateContentType if <code>true</code> the content type and the
	 *            supported providers will be updated before updating the page
	 *            state
	 */
	protected void updateState(boolean updateContentType) {
		if (updateContentType) {
			updateContentType();
		}

		// update provider factory
		ISelection provSel = providers.getSelection();
		if (!provSel.isEmpty() && provSel instanceof IStructuredSelection) {
			getConfiguration().setProviderFactory(
					(IOProviderDescriptor) ((IStructuredSelection) provSel).getFirstElement());
		}
		else {
			getConfiguration().setProviderFactory(null);
		}

		getPage().setPageComplete(
				isValidSource() && getConfiguration().getContentType() != null
						&& getConfiguration().getProviderFactory() != null);
	}

	/**
	 * Update the content type.<br>
	 * <br>
	 * The default implementation only calls {@link #updateProvider()}.
	 * Subclasses may add logic to change the content type in the source
	 * configuration.
	 */
	protected void updateContentType() {
		// update provider selector
		updateProvider();
	}

	/**
	 * Configures the provider with the input supplier obtained using
	 * {@link #getSource()} as source.
	 * 
	 * @see AbstractSource#updateConfiguration(ImportProvider)
	 * @see #getSource()
	 */
	@Override
	public boolean updateConfiguration(P provider) {
		boolean ok = super.updateConfiguration(provider);
		if (!ok) {
			return ok;
		}

		LocatableInputSupplier<? extends InputStream> source = getSource();
		if (source != null) {
			provider.setSource(source);
			return true;
		}

		return false;
	}

	/**
	 * Get the source to configure the import provider with.
	 * 
	 * @return the input supplier as source for the import provider or
	 *         <code>null</code> if no valid source can be created
	 * 
	 * @see #isValidSource()
	 */
	protected abstract LocatableInputSupplier<? extends InputStream> getSource();

	/**
	 * Determines if the current page state will result in a valid source for
	 * the import provider. Used among others to determine the complete state of
	 * the wizard page.
	 * 
	 * @return if the source is valid
	 * 
	 * @see #getSource()
	 */
	protected abstract boolean isValidSource();

}
