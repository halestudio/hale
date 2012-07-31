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

package eu.esdihumboldt.hale.ui.instancevalidation.report;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationMessage;

/**
 * Content provider for the instance validation report details page.
 *
 * @author Kai Schwierczek
 */
public class InstanceValidationReportDetailsContentProvider implements ITreeContentProvider {

	/**
	 * @see ITreeContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * @see ITreeContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	/**
	 * @see ITreeContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Collection<?>) {
			Map<String, List<InstanceValidationMessage>> map = new HashMap<String, List<InstanceValidationMessage>>();
			for (Object o : (Collection<?>) inputElement) {
				if (o instanceof InstanceValidationMessage) {
					InstanceValidationMessage message = ((InstanceValidationMessage) o);
					List<InstanceValidationMessage> messages = map.get(message.getMessage());
					if (messages == null) {
						messages = new LinkedList<InstanceValidationMessage>();
						map.put(message.getMessage(), messages);
					}
					messages.add(message);
				} else
					throw new IllegalArgumentException("Input must be a Collection<InstanceValidationReport>");
			}
			return map.values().toArray();
		}
		return new Object[0];
	}

	/**
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		return new Object[0];
	}

	/**
	 * @see ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/**
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

}
