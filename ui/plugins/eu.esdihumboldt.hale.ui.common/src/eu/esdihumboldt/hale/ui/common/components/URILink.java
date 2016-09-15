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

package eu.esdihumboldt.hale.ui.common.components;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;

import com.google.common.io.ByteStreams;

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;

/**
 * Link class based on URIs
 * 
 * @author Patrick Lieb
 */
public class URILink {

	private SelectionAdapter adapter;

	private final Link link;

	/**
	 * Creates a {@link Link} based on an URI
	 * 
	 * @param parent a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style the style of control to construct
	 * @param uri the URI of the file
	 * @param text the text which should be displayed
	 */
	public URILink(Composite parent, int style, final URI uri, String text) {
		adapter = createDefaultSelectionAdapter(uri);
		link = new Link(parent, style);
		link.addSelectionListener(adapter);
		link.setText(text);
	}

	/**
	 * Refresh the UriLink with the given URI
	 * 
	 * @param uri the URI of the of the file
	 */
	public void refresh(URI uri) {
		link.removeSelectionListener(adapter);
		if (uri == null)
			return;
		adapter = createDefaultSelectionAdapter(uri);
		link.addSelectionListener(adapter);
	}

	/**
	 * @see Link#setLayoutData(Object)
	 * @param layoutData the new layout data for the receiver
	 */
	public void setLayoutData(Object layoutData) {
		link.setLayoutData(layoutData);
	}

	/**
	 * @return the instance of a link
	 */
	public Link getLink() {
		return link;
	}

	// create the the SelectionAdapter for the UriLink
	private SelectionAdapter createDefaultSelectionAdapter(final URI uri) {
		return new SelectionAdapter() {

			private URI removeFragment(URI uri) throws URISyntaxException {
				String uristring = uri.toString();
				uristring = uristring.substring(0, uristring.indexOf("#"));
				return new URI(uristring);
			}

			// the URI has to be an existing file on the local drive or on a
			// server
			@Override
			public void widgetSelected(SelectionEvent e) {
				URI newuri = uri;
				try {
					// online resource
					if (uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
						try {
							Desktop.getDesktop().browse(uri);
						} catch (IOException e1) {
							MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
									"Opening Error", "No default application set!");
						}
						return;
					}
					if (uri.toString().contains("#")) {
						newuri = removeFragment(uri);
					}

					// local resource or bundle resource
					if (DefaultInputSupplier.SCHEME_LOCAL.equals(newuri.getScheme())
							|| "bundleentry".equals(newuri.getScheme())) {
						// cannot be opened by system
						// so copy resource to temporary file
						String name = newuri.getPath();
						int index = name.lastIndexOf('/');
						if (index >= 0) {
							name = name.substring(index + 1);
						}

						if (!name.isEmpty()) {
							File tmpFile = Files.createTempFile("resource", name).toFile();
							try (OutputStream out = new FileIOSupplier(tmpFile).getOutput();
									InputStream in = new DefaultInputSupplier(newuri).getInput()) {
								ByteStreams.copy(in, out);
							}
							tmpFile.deleteOnExit();
							newuri = tmpFile.toURI();
						}
					}

					// try creating a file
					File file = new File(newuri);
					if (file.exists()) {
						try {
							Desktop.getDesktop().open(file);
						} catch (IOException e2) {
							try {
								Desktop.getDesktop().browse(newuri);
							} catch (IOException e1) {
								MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
										"Opening Error", "No default application set!");
							}
						}
					}
					else {
						try {
							Desktop.getDesktop().browse(newuri);
						} catch (IOException e1) {
							MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
									"Opening Error", "No default application set!");
						}
					}
				} catch (Exception e1) {
					// ignore
				}
			}
		};
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		link.setText(text);
	}
}
