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

package eu.esdihumboldt.hale.io.wfs.ui.describefeature;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.ui.common.CommonSharedImages;

/**
 * Label provider for feature types identified by {@link QName}s. Strings are
 * interpreted as namespace groups.
 * 
 * @author Simon Templer
 */
public final class FeatureTypeTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof QName) {
			QName name = (QName) element;
//			if (name.getPrefix() == null || name.getPrefix().isEmpty()) {
			return name.getLocalPart();
//			}
//			else {
//				return name.getPrefix() + ':' + name.getLocalPart();
//			}
		}
		else if (element instanceof String) {
			String str = (String) element;
			if (str.equals(XMLConstants.NULL_NS_URI)) {
				return "(default namespace)";
			}
			return str;
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof QName) {
			return CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_DEFINITION_CONCRETE_FT);
		}
		else if (element instanceof String) {
			return CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_DEFINITION_GROUP);
		}
		return super.getImage(element);
	}

}