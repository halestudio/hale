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

package eu.esdihumboldt.hale.ui.views.report.properties.details.tree;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.ReportSession;
import eu.esdihumboldt.hale.ui.views.report.properties.details.ReportDetailsPage;

/**
 * LabelProvider for {@link FilteredTree} in {@link ReportDetailsPage}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportTreeLabelProvider extends LabelProvider {

	private Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>();
	
	@Override
	public void dispose() {
		for (Image i : imageCache.values()) {
			i.dispose();
		}
		
		imageCache.clear();
	}
	
	@Override
	public String getText(Object obj) {
		if (obj instanceof Message) {
			return ((Message) obj).getFormattedMessage();
		}
		
		return obj.toString();
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof ReportSession) {
			String img = "icons/compressed_folder_obj.gif";
			
			return getImage(img);
		}
		else if (element instanceof Message) {
			// get the right image
			Message message = (Message) element;
			
			String img = "icons/warning.gif";
			if (message.getStackTrace() != null && !message.getStackTrace().equals("")) {
				img = "icons/error_log.gif";
			}

			return getImage(img);
		}
		return null;
	}
	
	/**
	 * Get an Image from cache or resource.
	 * 
	 * @param img name of file
	 * 
	 * @return the Image
	 */
	private Image getImage(String img) {
		ImageDescriptor descriptor = null;
		
		// TODO Platform.getBundle(ReportList.ID) does not work so here is a static plugin path!
		descriptor = AbstractUIPlugin.imageDescriptorFromPlugin("eu.esdihumboldt.hale.ui.views.report", img);
		if (descriptor == null) {
			return null;
		}
		
		Image image = imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}
}
