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

package eu.esdihumboldt.hale.io.gml.ui.wfs;

import java.io.InputStream;
import java.net.URI;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.io.source.AbstractProviderSource;
import eu.esdihumboldt.hale.ui.io.source.AbstractSource;

/**
 * Abstract base implementation for import sources based on WFS
 * 
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Simon Templer
 */
public abstract class AbstractWFSSource<P extends ImportProvider> extends AbstractProviderSource<P> {

	private WfsUrlFieldEditor sourceURL;

	/**
	 * @see ImportSource#createControls(Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		// caption
		new Label(parent, SWT.NONE); // placeholder

		Label caption = new Label(parent, SWT.NONE);
		caption.setText(getCaption());
		caption.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1));

		// source file
		sourceURL = createWfsFieldEditor(parent);
		sourceURL.setPage(getPage());

		sourceURL.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					updateState(false);
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					updateState(false);
				}
			}
		});

		// provider selection

		// label
		Label providerLabel = new Label(parent, SWT.NONE);
		providerLabel.setText("Import as");

		// create provider combo
		ComboViewer providers = createProviders(parent);
		providers.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		// initial state update
		updateState(true);
	}

	/**
	 * Get the caption for the source page
	 * 
	 * @return the caption
	 */
	protected abstract String getCaption();

	/**
	 * Create the WFS field editor to use in the page.
	 * 
	 * @param parent the parent composite
	 * @return the WFS field editor
	 */
	protected abstract WfsUrlFieldEditor createWfsFieldEditor(Composite parent);

	/**
	 * @see AbstractProviderSource#getSource()
	 */
	@Override
	protected LocatableInputSupplier<? extends InputStream> getSource() {
		try {
			URI uri = sourceURL.getURL().toURI();
			return new DefaultInputSupplier(uri);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * @see AbstractProviderSource#isValidSource()
	 */
	@Override
	protected boolean isValidSource() {
		return sourceURL.isValid() && sourceURL.getURL() != null;
	}

	/**
	 * @see AbstractSource#onActivate()
	 */
	@Override
	public void onActivate() {
		sourceURL.setFocus();
	}

}
