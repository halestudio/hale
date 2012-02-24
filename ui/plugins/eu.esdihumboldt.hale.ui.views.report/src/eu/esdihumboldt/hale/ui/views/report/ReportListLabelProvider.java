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

package eu.esdihumboldt.hale.ui.views.report;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * LabelProvider for {@link ReportList}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportListLabelProvider implements ILabelProvider  {

	private Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>();
	
	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		//System.err.println("LabelProvider.isLabelProperty(): ");
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Image getImage(Object element) {
		if (element instanceof String) {
			String img = "icons/compressed_folder_obj.gif";
			ImageDescriptor descriptor = null;
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
		else if (element instanceof Report) {
			// get the right image
			Report report = (Report) element;
			
			String img = "icons/signed_yes.gif";
			if (!report.isSuccess()) {
				img = "icons/error.gif";
			} else if (report.getWarnings().size() > 0 && report.getErrors().size() > 0) {
				img = "icons/errorwarning_tab.gif";
			} else if (report.getErrors().size() > 0) {
				img = "icons/error_log.gif";
			} else if (report.getWarnings().size() > 0) {
				img = "icons/warning.gif";
			}
			
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
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			return (String)element;
		}
		else if (element instanceof Report) {
			return ((Report) element).getTaskName();
		}
		
		return "Unhandled type";
	}
}