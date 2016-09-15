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

package eu.esdihumboldt.hale.ui.codelist.inspire;

import java.io.InputStream;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.inspire.codelists.CodeListRef;
import eu.esdihumboldt.hale.io.codelist.inspire.reader.INSPIRECodeListConstants;
import eu.esdihumboldt.hale.ui.codelist.inspire.internal.CodeListSelector;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.io.source.AbstractSource;

/**
 * Source for code lists from INSPIRE registry.
 * 
 * @author Simon Templer
 */
public class RegistrySource extends AbstractSource<CodeListReader> {

	private CodeListSelector selector;
	private Label description;

	private LocatableInputSupplier<? extends InputStream> getSource() {
		if (selector != null && selector.getSelectedObject() != null) {
			return new DefaultInputSupplier(selector.getSelectedObject().getLocation());
		}
		return null;
	}

	private boolean isValidSource() {
		return selector.getSelectedObject() != null;
	}

	/**
	 * @see ImportSource#createControls(Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		GridLayoutFactory parentLayout = GridLayoutFactory.swtDefaults().numColumns(2)
				.equalWidth(false);
		parentLayout.applyTo(parent);

		GridDataFactory labelData = GridDataFactory//
				.swtDefaults()//
				.align(SWT.BEGINNING, SWT.CENTER);

		GridDataFactory controlData = GridDataFactory//
				.swtDefaults()//
				.align(SWT.FILL, SWT.CENTER)//
				.grab(true, false);

		// preset label
		Label label = new Label(parent, SWT.NONE);
		label.setText("Select preset:");
		labelData.applyTo(label);

		// preset selector
		selector = new CodeListSelector(parent);
		selector.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (description != null) {
					CodeListRef schema = selector.getSelectedObject();
					if (schema != null && schema.getDescription() != null) {
						description.setText(schema.getDescription());
					}
					else {
						description.setText("");
					}
				}
				updateState();
			}
		});
		controlData.applyTo(selector.getControl());

		// skipper
		Composite empty = new Composite(parent, SWT.NONE);
		GridDataFactory.swtDefaults().hint(1, 1).applyTo(empty);

		// description label
		description = new Label(parent, SWT.WRAP);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(description);

		// prevent selector appearing very small
		parent.pack();

		// initial configuration (fixed values)
		getConfiguration().setContentType(
				HalePlatform.getContentTypeManager().getContentType(
						INSPIRECodeListConstants.CONTENT_TYPE_ID));
		getConfiguration().setProviderFactory(
				IOProviderExtension.getInstance().getFactory(INSPIRECodeListConstants.PROVIDER_ID));

		// initial state update
		updateState();
	}

	/**
	 * Update the page state based on the selection.
	 */
	protected void updateState() {
		getPage().setPageComplete(isValidSource());
	}

	/**
	 * Configures the provider with the input supplier obtained using
	 * {@link #getSource()} as source.
	 * 
	 * @see AbstractSource#updateConfiguration(ImportProvider)
	 * @see #getSource()
	 */
	@Override
	public boolean updateConfiguration(CodeListReader provider) {
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

}
