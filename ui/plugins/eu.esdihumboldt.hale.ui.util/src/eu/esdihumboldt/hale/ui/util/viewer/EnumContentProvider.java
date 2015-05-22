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

package eu.esdihumboldt.hale.ui.util.viewer;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Content provider that takes an enum class as input an provides its elements.
 * 
 * @author Simon Templer
 */
public class EnumContentProvider implements IStructuredContentProvider {

	private static final ALogger log = ALoggerFactory.getLogger(EnumContentProvider.class);

	private static EnumContentProvider instance;

	/**
	 * Get the content provider singleton instance.
	 * 
	 * @return the content provider instance
	 */
	public static EnumContentProvider getInstance() {
		synchronized (EnumContentProvider.class) {
			if (instance == null) {
				instance = new EnumContentProvider();
			}
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	protected EnumContentProvider() {
		super();
	}

	/**
	 * @see IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	/**
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Class<?> && ((Class<?>) inputElement).isEnum()) {
			@SuppressWarnings("unchecked")
			Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) inputElement;
			try {
				Method method = enumClass.getMethod("values");
				method.setAccessible(true);
				return (Object[]) method.invoke(null);
			} catch (Exception e) {
				log.error("Could not get values from enum.");
			}
		}
		return new Object[] {};
	}

}
