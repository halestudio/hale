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

package eu.esdihumboldt.hale.ui.views.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportSession;
import eu.esdihumboldt.hale.common.core.report.writer.ReportReader;

/**
 * LabelProvider for {@link ReportList}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportListLabelProvider extends LabelProvider {

	private final Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>();
	private final SimpleDateFormat df = new SimpleDateFormat("HH:mm yyyy-MM-dd");

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		for (Image i : imageCache.values())
			i.dispose();

		imageCache.clear();
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
		else if (element instanceof Report<?>) {
			// get the right image
			Report<?> report = (Report<?>) element;

			String img = "icons/signed_yes.gif";
			if (!report.isSuccess()) {
				img = "icons/error.gif";
			}
			else if (report.hasWarnings() && report.hasErrors()) {
				img = "icons/errorwarning_tab.gif";
			}
			else if (report.hasErrors()) {
				img = "icons/error_log.gif";
			}
			else if (report.hasWarnings()) {
				img = "icons/warning.gif";
			}

			return getImage(img);
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Report<?>) {
			return ((Report<?>) element).getTaskName();
		}
		else if (element instanceof ReportSession) {
			long id = ((ReportSession) element).getId();
			if (id == ReportReader.UNKNOWN_SESSION) {
				// session information is not present usually for imported logs
				return "Import";
			}
			return df.format(new Date(id));
		}

		return "Unhandled type";
	}

	/**
	 * Get an Image from cache or resource.
	 * 
	 * @param img name of file
	 * 
	 * @return the Image
	 */
	protected Image getImage(String img) {
		ImageDescriptor descriptor = null;

		descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin("eu.esdihumboldt.hale.ui.views.report", img);
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
