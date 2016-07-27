/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.server.wms.wizard.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import de.fhg.igd.mapviewer.server.wms.WMSConfiguration;
import de.fhg.igd.mapviewer.server.wms.WMSMapServer;

/**
 * Extend the BasicConfigurationPage by a list holding all WMS already exist, so
 * the user can select a stored WMS for his orthophoto. Also a button is shown
 * to add new WMS to the list.
 * 
 * @author Benedikt Hiemenz
 */
public class BasicConfigurationWithWMSListPage extends BasicConfigurationPage {

	private static final Log log = LogFactory.getLog(BasicConfigurationWithWMSListPage.class);

	// map holding all stored WMS
	private HashMap<String, String> map;
	private ArrayList<String> listUI;

	// list viewing stored WMS
	private ComboViewer comboViewer;

	/**
	 * Default constructor
	 * 
	 * @param configuration the WMS configuration
	 */
	public BasicConfigurationWithWMSListPage(WMSConfiguration configuration) {
		super(configuration);

		this.map = new HashMap<String, String>();
		this.listUI = new ArrayList<String>();
	}

	@Override
	public void createComponent() {
		super.createComponent();

		Button storeWMS = new Button(getComposite(), SWT.PUSH);
		storeWMS.setText(Messages.WMSListConfigurationPage_2);

		// add new WMS to list and refresh
		storeWMS.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// store WMS i UI list
				map.put(getServiceName(), getServiceURL());
				getListForUI(listUI);
				comboViewer.refresh();
			}
		});

		// free line
		new Text(getComposite(), SWT.NONE).setEditable(false);
		new Text(getComposite(), SWT.NONE).setEditable(false);
		new Text(getComposite(), SWT.NONE).setEditable(false);
		new Text(getComposite(), SWT.NONE).setEditable(false);
		new Text(getComposite(), SWT.NONE).setEditable(false);

		// add text and combo viewer
		Text text = new Text(getComposite(), SWT.NONE);
		text.setEditable(false);
		text.setText(Messages.WMSListConfigurationPage_0);

		// get a list of all WMS

		// get all WMS, storing as WMS Map Server
		Preferences PREF_SERVERS = new WMSMapServer().getPreferences();

		String[] prefs = null;
		try {
			prefs = PREF_SERVERS.childrenNames();

			// put all in map
			for (String current : prefs) {

				Preferences child = PREF_SERVERS.node(current);
				map.put(current, child.get("baseUrl", "baseUrl"));
			}

		} catch (BackingStoreException e) {
			log.warn(Messages.WMSListConfigurationPage_1, e); // $NON-NLS-1$
		}

		// get all WMS, storing as extension points
		IConfigurationElement[] allER = Platform.getExtensionRegistry()
				.getConfigurationElementsFor("de.fhg.igd.mapviewer.server.MapServer");

		for (IConfigurationElement current : allER) {
			String name = "";
			String url = "";

			// name is stored directly as attribute
			name = current.getAttribute("name");

			// url is stored as child
			for (IConfigurationElement child : current.getChildren()) {
				if (child.getAttribute("name").equals("baseUrl")) {
					url = child.getAttribute("value");
				}
			}
			// store everything into map
			if (name != null && !name.isEmpty() && url != null && !url.isEmpty()) {
				map.put(name, url);
			}
		}

		// show stored WMS as DropDown
		comboViewer = new ComboViewer(getComposite(), SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		comboViewer.setContentProvider(new ArrayContentProvider());
		getListForUI(listUI);
		comboViewer.setInput(listUI);
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0) {
					String currentSelection = (String) selection.getFirstElement();
					name.setStringValue(currentSelection);
					location.setStringValue(map.get(currentSelection));
				}
			}
		});
	}

	@Override
	public boolean updateConfiguration(WMSConfiguration configuration) {

		// only location need to be valid
		if (location.isValid()) {
			configuration.setBaseUrl(location.getStringValue());
			configuration.setName(getServiceName());
			return true;
		}
		return false;
	}

	/**
	 * Put all map entries into a given list
	 * 
	 * @param list list to fill
	 */
	private void getListForUI(ArrayList<String> list) {

		list.clear();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			list.add(entry.getKey());
		}
	}
}
