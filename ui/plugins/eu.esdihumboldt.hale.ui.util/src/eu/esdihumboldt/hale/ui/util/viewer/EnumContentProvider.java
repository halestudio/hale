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

package eu.esdihumboldt.hale.ui.util.viewer;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

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
				return (Object[]) method.invoke(null);
			} catch (Exception e) {
				log.error("Could not get values from enum.");
			}
		}
		return new Object[] {};
	}

}
