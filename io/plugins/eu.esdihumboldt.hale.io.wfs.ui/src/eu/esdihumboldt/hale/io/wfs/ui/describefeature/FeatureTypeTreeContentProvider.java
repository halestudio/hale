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

package eu.esdihumboldt.hale.io.wfs.ui.describefeature;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * This tree content provider showing a collection of feature types identified
 * by their qualified names, grouped by their namespace.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class FeatureTypeTreeContentProvider implements ITreeContentProvider {

	private ListMultimap<String, QName> nameSpaces;

	/**
	 * To make extending this class possible.
	 */
	public FeatureTypeTreeContentProvider() {
	}

	@Override
	public void dispose() {
		// do nothing
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement == null) {
			nameSpaces = null;
			return new Object[0];
		}

		@SuppressWarnings("unchecked")
		Collection<? extends QName> types = (Collection<? extends QName>) inputElement;
		nameSpaces = ArrayListMultimap.create();
		for (QName type : types) {
			String ns = type.getNamespaceURI();
			nameSpaces.put(ns, type);
		}

		return nameSpaces.keySet().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof String)
			return nameSpaces.get((String) parentElement).toArray();
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof QName) {
			String ns = ((QName) element).getNamespaceURI();
			return ns;
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof String && !nameSpaces.get((String) element).isEmpty();
	}
}
