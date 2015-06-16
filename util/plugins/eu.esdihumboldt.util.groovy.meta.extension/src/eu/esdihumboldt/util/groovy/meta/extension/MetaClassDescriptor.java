/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.util.groovy.meta.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Meta class extension descriptor.
 * 
 * @author Simon Templer
 */
public class MetaClassDescriptor implements Identifiable {

	private final String id;
	private final Class<?> metaClass;
	private final Class<?> forClass;
	private final boolean forArray;

	/**
	 * Constructor.
	 * 
	 * @param id the id
	 * @param conf the configuration element
	 */
	public MetaClassDescriptor(String id, IConfigurationElement conf) {
		this.id = id;

		metaClass = ExtensionUtil.loadClass(conf, "implementation");
		forClass = ExtensionUtil.loadClass(conf, "for");
		String arrayValue = conf.getAttribute("forArray");
		forArray = (arrayValue == null) ? (false) : (Boolean.parseBoolean(arrayValue));
	}

	/**
	 * @return the delegating meta class implementation class
	 */
	public Class<?> getMetaClass() {
		return metaClass;
	}

	/**
	 * @return the class the meta class should be registered for
	 */
	public Class<?> getForClass() {
		return forClass;
	}

	/**
	 * @return if the meta class should be applied for arrays of the
	 *         {@link #getForClass()}
	 */
	public boolean isForArray() {
		return forArray;
	}

	@Override
	public String getId() {
		return id;
	}

}
