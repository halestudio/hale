/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.io.source.AbstractProviderSource;

/**
 * Source for configuring a JDBC connection.
 * 
 * @author Simon Templer
 */
public class JDBCSource extends AbstractProviderSource<ImportProvider> {

	private ComboViewer driver;

	private Text host;

	private Text database;

	@Override
	public void createControls(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		GridDataFactory labelData = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER);
		GridDataFactory compData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false);

		// driver
		Label labelDriver = new Label(page, SWT.NONE);
		labelDriver.setText("Driver");
		labelData.applyTo(labelDriver);

		driver = JDBCComponents.createDriverSelector(page);
		compData.applyTo(driver.getControl());

		// driver selection listener
		driver.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateState(false);
			}
		});

		// host
		Label labelHost = new Label(page, SWT.NONE);
		labelHost.setText("Host(:Port)");
		labelData.applyTo(labelHost);

		host = new Text(page, SWT.BORDER | SWT.SINGLE);
		compData.applyTo(host);
		host.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateState(false);
			}
		});

		// database
		Label labelDatabase = new Label(page, SWT.NONE);
		labelDatabase.setText("Database");
		labelData.applyTo(labelDatabase);

		database = new Text(page, SWT.BORDER | SWT.SINGLE);
		compData.applyTo(database);
		database.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateState(false);
			}
		});

		// preset label
		Label providerLabel = new Label(page, SWT.NONE);
		providerLabel.setText("Import as");
		labelData.applyTo(providerLabel);

		// create provider combo
		ComboViewer providers = createProviders(page);
		compData.applyTo(providers.getControl());

		// content type is set through source registration

		// initial state update
		updateState(true);
	}

	private URI buildURI() {
		return JDBCComponents.buildURI(getPage(), driver, host, database);
	}

	@Override
	protected LocatableInputSupplier<? extends InputStream> getSource() {
		final URI uri = buildURI();

		if (uri != null) {
			return new LocatableInputSupplier<InputStream>() {

				@Override
				public URI getLocation() {
					return uri;
				}

				@Override
				public URI getUsedLocation() {
					return uri;
				}

				@Override
				public InputStream getInput() throws IOException {
					return null;
				}
			};
		}

		return null;
	}

	@Override
	protected boolean isValidSource() {
		return buildURI() != null;
	}

}
