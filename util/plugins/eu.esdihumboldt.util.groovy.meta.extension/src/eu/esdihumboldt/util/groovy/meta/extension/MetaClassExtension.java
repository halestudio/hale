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

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

/**
 * Extension point for Groovy delegating meta classes.
 * 
 * @author Simon Templer
 */
public class MetaClassExtension extends IdentifiableExtension<MetaClassDescriptor> {

	/**
	 * The extension ID.
	 */
	public static final String ID = "eu.esdihumboldt.util.groovy.meta";

	/**
	 * Default constructor.
	 */
	public MetaClassExtension() {
		super(ID, false, false);
	}

	@Override
	protected MetaClassDescriptor create(String id, IConfigurationElement conf) {
		return new MetaClassDescriptor(id, conf);
	}

	@Override
	protected String getIdAttributeName() {
		return "implementation";
	}

}
