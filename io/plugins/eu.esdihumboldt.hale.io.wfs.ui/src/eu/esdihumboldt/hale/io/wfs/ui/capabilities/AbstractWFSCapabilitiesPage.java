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

package eu.esdihumboldt.hale.io.wfs.ui.capabilities;

import java.net.URL;
import java.util.Arrays;

import javax.annotation.Nullable;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.io.wfs.WFSVersion;
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities;
import eu.esdihumboldt.hale.ui.util.viewer.EnumContentProvider;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage;

/**
 * Page for specifying the capabilities URL (and loading the capabilities).
 * 
 * @author Simon Templer
 * @param <T> the configuration object type
 */
public abstract class AbstractWFSCapabilitiesPage<T> extends ConfigurationWizardPage<T> {

	/**
	 * Represents no object.
	 */
	private static enum NoObject {
		NONE;
	}

	/**
	 * The capabilities URL editor
	 */
	private WFSCapabilitiesFieldEditor location;
	private ComboViewer versionSelect;

	/**
	 * Constructor
	 * 
	 * @param wizard the parent wizard
	 */
	public AbstractWFSCapabilitiesPage(ConfigurationWizard<? extends T> wizard) {
		super(wizard, "wfsCapabilities");
		setTitle("WFS Capabilities");
		setMessage("Please specify the GetCapabilities URL of the WFS");
	}

	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(2, false));

		// capabilities field
		location = new WFSCapabilitiesFieldEditor("location", "GetCapabilities URL", page) {

			@Override
			protected WFSVersion getWFSVersion() {
				if (versionSelect != null) {
					ISelection sel = versionSelect.getSelection();
					if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
						Object selected = ((IStructuredSelection) sel).getFirstElement();
						if (NoObject.NONE.equals(selected)) {
							return null;
						}
						return (WFSVersion) selected;
					}
				}
				return super.getWFSVersion();
			}

		};
		location.setPage(this);
		location.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					update();
				}
			}
		});

		String currentValue = getCapabilitiesURL(getWizard().getConfiguration());
		if (currentValue != null) {
			location.setValue(currentValue);
		}

		// version field
		Label vLabel = new Label(page, SWT.NONE);
		vLabel.setText("WFS version");

		versionSelect = new ComboViewer(page, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(versionSelect.getControl());
		versionSelect.setContentProvider(new EnumContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				Object[] values = super.getElements(inputElement);
				// add a NoObject
				values = Arrays.copyOf(values, values.length + 1, Object[].class);
				values[values.length - 1] = NoObject.NONE;
				return values;
			}

		});
		versionSelect.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof NoObject) {
					return "Based on URL / server default";
				}
				return super.getText(element);
			}

		});
		versionSelect.setInput(WFSVersion.class);
		versionSelect.setSelection(new StructuredSelection(NoObject.NONE));
		versionSelect.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				location.revalidate();
			}
		});

		setControl(page);

		update();
	}

	/**
	 * Determine an initial capabilities URL from the current configuration.
	 * 
	 * @param configuration the configuration object
	 * @return the capabilities URL or <code>null</code>
	 */
	@Nullable
	protected String getCapabilitiesURL(T configuration) {
		return null;
	}

	@Override
	public boolean updateConfiguration(T configuration) {
		if (location.isValid()) {
			boolean result = updateConfiguration(configuration, location.getUsedUrl(),
					location.getCapabilities());
			if (result) {
				updateRecent();
			}
			return result;
		}

		return false;
	}

	/**
	 * @return the recently loaded WFS capabilities
	 */
	public WFSCapabilities getCapabilities() {
		return location.getCapabilities();
	}

	/**
	 * @return the URL of the recently loaded WFS capabilities
	 */
	public String getCapabilitiesURL() {
		return location.getUsedUrl().toString();
	}

	/**
	 * Update the configuration
	 * 
	 * @param configuration the WMS configuration
	 * @param capabilitiesUrl the capabilities URL
	 * @param capabilities the loaded capabilities or <code>null</code>
	 * @return if the page is valid
	 */
	protected abstract boolean updateConfiguration(T configuration, URL capabilitiesUrl,
			WFSCapabilities capabilities);

	/**
	 * Update the list of recently used WFSes
	 */
	public void updateRecent() {
		if (location != null) {
			location.updateRecent();
		}
	}

	private void update() {
		setPageComplete(location.isValid());
	}

}
