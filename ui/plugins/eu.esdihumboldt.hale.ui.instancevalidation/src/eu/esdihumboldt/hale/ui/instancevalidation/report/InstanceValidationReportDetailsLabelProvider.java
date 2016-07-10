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

package eu.esdihumboldt.hale.ui.instancevalidation.report;

import java.text.MessageFormat;

import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationMessage;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;

/**
 * Label provider for the instance validation report details page.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidationReportDetailsLabelProvider extends StyledCellLabelProvider implements
		ILabelProvider {

	private final InstanceValidationReportDetailsContentProvider contentProvider;
	private final DefinitionLabelProvider dlp = new DefinitionLabelProvider(null, false, true);

	/**
	 * Constructor.
	 * 
	 * @param contentProvider the content provider
	 */
	public InstanceValidationReportDetailsLabelProvider(
			InstanceValidationReportDetailsContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	/**
	 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();

		String label = getText(element);
		int newLine = label.indexOf('\n');
		if (newLine > -1)
			label = label.substring(0, newLine) + " ...";

		StyledString text = new StyledString(label);

		if (!(element instanceof InstanceValidationMessage)) {
			TreePath treePath = cell.getViewerRow().getTreePath();
			boolean isLimited = contentProvider.isLimited(treePath);
			text.append(' ');
			if (!isLimited)
				text.append(
						MessageFormat.format("({0} warnings)",
								contentProvider.getMessageCount(treePath)),
						StyledString.COUNTER_STYLER);
			else
				text.append(MessageFormat.format("(showing {0} of {1})",
						InstanceValidationReportDetailsContentProvider.LIMIT,
						contentProvider.getMessageCount(treePath)), StyledString.COUNTER_STYLER);
		}

		cell.setText(text.getString());
		cell.setStyleRanges(text.getStyleRanges());
		cell.setImage(getImage(element));

		super.update(cell);
	}

	/**
	 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		dlp.dispose();

		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
	 */
	@Override
	public String getToolTipText(Object element) {
		if (element instanceof InstanceValidationMessage) {
			String message = ((InstanceValidationMessage) element).getMessage();
			if (message.indexOf('\n') > -1)
				return message;
		}

		return super.getToolTipText(element);
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof Definition<?>)
			return dlp.getImage(element);
		else if (element instanceof String)
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		else
			return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof QName)
			return ((QName) element).getLocalPart(); // path
		else if (element instanceof Definition<?>)
			return dlp.getText(element); // path alternative
		else if (element instanceof String)
			return (String) element; // category
		else if (element instanceof InstanceValidationMessage)
			return ((InstanceValidationMessage) element).getMessage(); // message
		else
			return String.valueOf(element); // shouldn't happen
	}
}
