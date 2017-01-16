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

package eu.esdihumboldt.hale.io.wfs.ui;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nullable;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.base.Predicate;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.wfs.PartitioningWFSInputSupplier;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.io.source.AbstractProviderSource;
import eu.esdihumboldt.hale.ui.io.source.AbstractSource;
import eu.esdihumboldt.hale.ui.io.source.URLSourceURIFieldEditor;
import eu.esdihumboldt.hale.ui.util.io.URIFieldEditor;

/**
 * Abstract base implementation for import sources based on WFS
 * 
 * @param <P> the supported {@link IOProvider} type
 * 
 * @author Simon Templer
 */
public abstract class AbstractWFSSource<P extends ImportProvider> extends AbstractProviderSource<P> {

	private URLSourceURIFieldEditor sourceURL;

	/**
	 * @see ImportSource#createControls(Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(4, false));

		// caption
		new Label(parent, SWT.NONE); // placeholder

		Label caption = new Label(parent, SWT.NONE);
		caption.setText(getCaption());
		caption.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 3, 1));

		// source file
		// target URL field
		sourceURL = new URLSourceURIFieldEditor("sourceURL", "URL:", parent) {

			// the following methods are overridden so the capabilities button
			// may appear on the same line

			@Override
			public int getNumberOfControls() {
				return super.getNumberOfControls() + 1;
			}

			@Override
			protected void doFillIntoGrid(Composite parent, int numColumns) {
				super.doFillIntoGrid(parent, numColumns - 1);
			}

		};
		sourceURL.setPage(getPage());

		// set custom URI filter
		sourceURL.setURIFilter(createHistoryURIFilter());

		// set content types for URI field
		Collection<IOProviderDescriptor> factories = getConfiguration().getFactories();
		HashSet<IContentType> supportedTypes = new HashSet<IContentType>();
		for (IOProviderDescriptor factory : factories) {
			supportedTypes.addAll(factory.getSupportedTypes());
		}

		sourceURL.setContentTypes(supportedTypes);

		sourceURL.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					getPage().setMessage(null);
					updateState(true);
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					getPage().setMessage(null);
					updateState(true);
				}
			}
		});

		// button to determine from capabilities
		Button capButton = new Button(parent, SWT.PUSH);
		capButton.setText("...");
		capButton.setToolTipText("Determine based on WFS Capabilities");
		capButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				determineSource(sourceURL);
			}
		});

		// provider selection

		// label
		Label providerLabel = new Label(parent, SWT.NONE);
		providerLabel.setText("Import as");

		// create provider combo
		ComboViewer providers = createProviders(parent);
		providers.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));

		// initial state update
		updateState(true);
	}

	/**
	 * @return a custom filter for the URI history
	 */
	@Nullable
	protected Predicate<? super URI> createHistoryURIFilter() {
		return new Predicate<URI>() {

			@Override
			public boolean apply(URI input) {
				String expectedRequest = getWFSRequestValue();
				URIBuilder uri = new URIBuilder(input);
				for (NameValuePair param : uri.getQueryParams()) {
					if ("request".equalsIgnoreCase(param.getName())
							&& expectedRequest.equals(param.getValue())) {
						return true;
					}
				}
				return false;
			}
		};
	}

	/**
	 * @return the value to use for the REQUEST parameter for a WFS request with
	 *         this source
	 */
	protected abstract String getWFSRequestValue();

	/**
	 * Let the user determine the source URL to use, e.g. though a dialog or
	 * wizard.
	 * 
	 * @param sourceURL the source URL field to edit
	 */
	protected abstract void determineSource(URIFieldEditor sourceURL);

	/**
	 * Get the caption for the source page
	 * 
	 * @return the caption
	 */
	protected abstract String getCaption();

	/**
	 * @see AbstractProviderSource#getSource()
	 */
	@Override
	protected LocatableInputSupplier<? extends InputStream> getSource() {
		try {
			URI uri = sourceURL.getURI();
			return new PartitioningWFSInputSupplier(uri);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * @see AbstractProviderSource#isValidSource()
	 */
	@Override
	protected boolean isValidSource() {
		return sourceURL.isValid() && sourceURL.getURI() != null;
	}

	/**
	 * @see AbstractSource#onActivate()
	 */
	@Override
	public void onActivate() {
		sourceURL.setFocus();
	}

}
