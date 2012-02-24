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

package eu.esdihumboldt.hale.ui.util.components;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;

/**
 * Link class based on URIs
 * @author Patrick Lieb
 */
public class URILink{
	
	private SelectionAdapter adapter;
	
	private Link link;
	
	/**
	 * Creates a {@link Link} based on an URI
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 * @param uri the URI of the file
	 * @param text the text which should be displayed
	 */
	public URILink(Composite parent, int style, final URI uri, String text){
		adapter = createDefaultSelectionAdapter(uri);
		link = new Link(parent, style);
		link.addSelectionListener(adapter);
		link.setText(text);
	}
	
	/**
	 * Refresh the UriLink with the given URI
	 * @param uri the URI of the of the file
	 */
	public void refresh(URI uri){
		link.removeSelectionListener(adapter);
		if(uri == null) return;
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
	public Link getLink(){
		return link;
	}

	// create the the SelectionAdapter for the UriLink
	private SelectionAdapter createDefaultSelectionAdapter(final URI uri){
		return new SelectionAdapter(){
			
			private URI removeFragment(URI uri) throws URISyntaxException{
				String uristring = uri.toString();
				uristring = uristring.substring(0, uristring.indexOf("#"));
				return new URI(uristring);
			}

			// the URI has to be an existing file on the local drive or on a server
			@Override
			public void widgetSelected(SelectionEvent e) {
				URI newuri = uri;
				try {
					if(uri.getScheme().equals("http")){
						try {
							Desktop.getDesktop().browse(uri);
						} catch (IOException e1) {
							MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Opening Error", "No default application set!");
						}
						return;
					}
					if(uri.toString().contains("#")){
						newuri =  removeFragment(uri);
					}
					File file = new File(newuri);
					if(file.exists()){
						try {
							Desktop.getDesktop().open(file);
						} catch (IOException e2) {
								try {
									Desktop.getDesktop().browse(newuri);
								} catch (IOException e1) {
									MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Opening Error", "No default application set!");
								}
						}
					} else {
						try {
							Desktop.getDesktop().browse(newuri);
						} catch (IOException e1) {
							MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Opening Error", "No default application set!");
						}
					}
				} catch (URISyntaxException e1) {
					// do nothing
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
