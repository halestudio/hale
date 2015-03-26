/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
import java.io.OutputStream;
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

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.ui.io.target.AbstractTarget;

/**
 * Target for JDBC URLs.
 * 
 * @author Simon Templer
 */
public class JDBCTarget extends AbstractTarget<ExportProvider> {

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
				updateState();
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
				updateState();
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
				updateState();
			}
		});

		// initial state update
		updateState();
	}

	/**
	 * Update the page state.
	 */
	protected void updateState() {
		setValid(buildURI() != null);
	}

	private URI buildURI() {
		return JDBCComponents.buildURI(getPage(), driver, host, database);
	}

	@Override
	public boolean updateConfiguration(ExportProvider provider) {
		final URI targetURI = buildURI();
		if (targetURI != null) {
			provider.setTarget(new LocatableOutputSupplier<OutputStream>() {

				@Override
				public OutputStream getOutput() throws IOException {
					throw new UnsupportedOperationException();
				}

				@Override
				public URI getLocation() {
					return targetURI;
				}
			});
			return true;
		}
		return false;
	}

}
