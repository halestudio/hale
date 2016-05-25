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

package eu.esdihumboldt.hale.ui.schema.presets;

import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.io.source.AbstractProviderSource;
import eu.esdihumboldt.hale.ui.schema.presets.internal.SchemaPresetSelector;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class PresetsSource extends AbstractProviderSource<SchemaReader> {

	private SchemaPresetSelector selector;
	private Label description;

	/**
	 * @see AbstractProviderSource#getSource()
	 */
	@Override
	protected LocatableInputSupplier<? extends InputStream> getSource() {
		if (selector != null && selector.getSelectedObject() != null) {
			return selector.getSelectedObject().getLocation();
		}
		return null;
	}

	/**
	 * @see AbstractProviderSource#isValidSource()
	 */
	@Override
	protected boolean isValidSource() {
		return selector.getSelectedObject() != null;
	}

	/**
	 * @see AbstractProviderSource#updateContentType()
	 */
	@Override
	protected void updateContentType() {
		LocatableInputSupplier<? extends InputStream> source = getSource();
		IContentType contentType = null;
		if (source != null) {
			URI loc = source.getLocation();
			String filename = null;
			if (loc != null) {
				filename = loc.getPath();
			}
			contentType = HaleIO.findContentType(SchemaReader.class, source, filename);
		}
		getConfiguration().setContentType(contentType);

		super.updateContentType();
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
		selector = new SchemaPresetSelector(parent);
		selector.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (description != null) {
					SchemaPreset schema = selector.getSelectedObject();
					if (schema != null && schema.getDescription() != null) {
						description.setText(schema.getDescription());
					}
					else {
						description.setText("");
					}
				}
				updateState(true);
			}
		});
		controlData.applyTo(selector.getControl());

		// skipper
		Composite empty = new Composite(parent, SWT.NONE);
		GridDataFactory.swtDefaults().hint(1, 1).applyTo(empty);

		// description label
		description = new Label(parent, SWT.WRAP);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(description);

		// preset label
		label = new Label(parent, SWT.NONE);
		label.setText("Import as");
		labelData.applyTo(label);

		// create provider combo
		ComboViewer providers = createProviders(parent);
		controlData.applyTo(providers.getControl());

		// prevent selector appearing very small
		parent.pack();

		// initial state update
		updateState(true);
	}

}
