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

import java.net.URI;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.io.jdbc.extension.DriverConfiguration;
import eu.esdihumboldt.hale.io.jdbc.extension.DriverConfigurationExtension;
import eu.esdihumboldt.util.Pair;

/**
 * Utilities for JDBC UI components.
 * 
 * @author Simon Templer
 */
public class JDBCComponents {

	/**
	 * Create a component for selecting a JDBC driver.
	 * 
	 * @param parent the parent composite
	 * @return the combo viewer for selecting the driver
	 */
	public static ComboViewer createDriverSelector(Composite parent) {
		ComboViewer driver = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
		driver.setContentProvider(ArrayContentProvider.getInstance());
		driver.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Pair<?, ?>) {
					@SuppressWarnings("unchecked")
					Pair<DriverConfiguration, Driver> driverInfo = (Pair<DriverConfiguration, Driver>) element;
					return driverInfo.getFirst().getName();
				}
				return super.getText(element);
			}

		});

		// driver input
		List<Pair<DriverConfiguration, Driver>> drivers = new ArrayList<>();
		for (DriverConfiguration dc : DriverConfigurationExtension.getInstance().getElements()) {

			if (dc.isFileBased())
				continue;

			// determine associated driver
			Driver jdbcDriver = null;
			Enumeration<Driver> enDrivers = DriverManager.getDrivers();
			while (enDrivers.hasMoreElements()) {
				Driver candidate = enDrivers.nextElement();
				if (dc.matchesDriver(candidate)) {
					jdbcDriver = candidate;
					break;
				}
			}

//			if (driver != null) {
			// XXX ignore if the driver is null, seems to work nonetheless
			drivers.add(new Pair<>(dc, jdbcDriver));
//			}
		}
		driver.setInput(drivers);
		if (!drivers.isEmpty()) {
			// by default select a driver if possible
			driver.setSelection(new StructuredSelection(drivers.get(0)));
		}

		return driver;
	}

	/**
	 * Build a JDBC URI from UI components
	 * 
	 * @param page the dialog page
	 * @param driver the driver selector
	 * @param host the host field
	 * @param database the database field
	 * @return the JDBC URI or <code>null</code>
	 */
	@Nullable
	public static URI buildURI(DialogPage page, ComboViewer driver, Text host, Text database) {
		page.setErrorMessage(null);
		if (driver != null) {
			ISelection sel = driver.getSelection();
			if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
				@SuppressWarnings({ "unchecked" })
				Pair<DriverConfiguration, Driver> driverInfo = (Pair<DriverConfiguration, Driver>) ((IStructuredSelection) sel)
						.getFirstElement();
				try {
					URI uri = driverInfo.getFirst().getURIBuilder().createJdbcUri(host.getText(),
							database.getText());
					if (driverInfo.getSecond() == null
							|| driverInfo.getSecond().acceptsURL(uri.toString())) {
						return uri;
					}
				} catch (Exception e) {
					page.setErrorMessage(e.getLocalizedMessage());
				}
			}

			page.setMessage(null);
		}
		else {
			page.setMessage("Please select a JDBC database driver", DialogPage.INFORMATION);
		}

		return null;
	}

}
