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

package eu.esdihumboldt.hale.ui.views.properties;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;

/**
 * Link class based on URIs
 * @author Patrick Lieb
 */
public class UriLink{
	
	private SelectionAdapter adapter;
	
	private Link link;
	
	/**
	 * Creates a {@link Link} based on an URI
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 * @param uri the URI of the file
	 * @param data the FormData for the layout
	 * @param text the text which should be displayed
	 */
	public UriLink(Composite parent, int style, final URI uri, FormData data, String text){
		adapter = createDefaultSelectionAdapter(uri);
		link = new Link(parent, style);
		link.addSelectionListener(adapter);
		link.setLayoutData(data);
		link.setText(text);
		link.setBackground(new Color(new Device(){

			@Override
			public long internal_new_GC(GCData data) {
				return 0;
			}

			@Override
			public void internal_dispose_GC(long hDC, GCData data) {
				// not needed
				
			}},255,255,255));
	}
	
	/**
	 * @param uri the URI of the of the file
	 */
	public void refresh(URI uri){
		link.removeSelectionListener(adapter);
		if(uri == null) return;
		adapter = createDefaultSelectionAdapter(uri);
		link.addSelectionListener(adapter);
	}
	
	/**
	 * @return the instance of a link
	 */
	public Link getLink(){
		return link;
	}

	private SelectionAdapter createDefaultSelectionAdapter(final URI uri){
		return new SelectionAdapter(){
			
			private URI removeFragment(URI uri) throws URISyntaxException{
				String uristring = uri.toString();
				uristring = uristring.substring(0, uristring.indexOf("#"));
				return new URI(uristring);
			}
			
			private void giveError(){
				MessageBox error = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
				error.setText("Opening Error");
				error.setMessage("No standard application is set!");
				error.open();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				URI newuri = uri;
				try {
					if(uri.getScheme().equals("http")){
						try {
							Desktop.getDesktop().browse(uri);
						} catch (IOException e1) {
							giveError();
						}
						return;
					}
					if(uri.toString().contains("#")){
						newuri =  removeFragment(uri);
					}
					// works only on files
					File file = new File(newuri);
					if(file.exists()){
						try {
							Desktop.getDesktop().open(file);
						} catch (IOException e2) {
								try {
									Desktop.getDesktop().browse(newuri);
								} catch (IOException e1) {
									giveError();
								}
						}
					} else {
						try {
							Desktop.getDesktop().browse(newuri);
						} catch (IOException e1) {
							giveError();
						}
					}
				} catch (URISyntaxException e1) {
					//
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
