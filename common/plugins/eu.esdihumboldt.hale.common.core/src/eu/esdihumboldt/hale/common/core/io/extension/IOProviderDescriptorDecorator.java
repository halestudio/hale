/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.extension;

import java.net.URL;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.parameter.IOProviderParameter;

/**
 * Decorator for an {@link IOProviderDescriptor}.
 * 
 * @author Simon Templer
 */
public abstract class IOProviderDescriptorDecorator implements IOProviderDescriptor {

	private final IOProviderDescriptor descriptor;

	/**
	 * @param descriptor the decoratee
	 */
	public IOProviderDescriptorDecorator(IOProviderDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
	}

	@Override
	public boolean allowConfigure() {
		return descriptor.allowConfigure();
	}

	@Override
	public int compareTo(ExtensionObjectDefinition o) {
		return descriptor.compareTo(o);
	}

	@Override
	public boolean configure() {
		return descriptor.configure();
	}

	@Override
	public IOProvider createExtensionObject() throws Exception {
		return descriptor.createExtensionObject();
	}

	@Override
	public void dispose(IOProvider arg0) {
		descriptor.dispose(arg0);
	}

	@Override
	public Set<IContentType> getSupportedTypes() {
		return descriptor.getSupportedTypes();
	}

	@Override
	public Set<IContentType> getConfigurationTypes() {
		return descriptor.getConfigurationTypes();
	}

	@Override
	public Set<IOProviderParameter> getProviderParameter() {
		return descriptor.getProviderParameter();
	}

	@Override
	public String getDescription() {
		return descriptor.getDescription();
	}

	@Override
	public String getDisplayName() {
		return descriptor.getDisplayName();
	}

	@Override
	public URL getIconURL() {
		return descriptor.getIconURL();
	}

	@Override
	public String getIdentifier() {
		return descriptor.getIdentifier();
	}

	@Override
	public Class<? extends IOProvider> getProviderType() {
		return descriptor.getProviderType();
	}

	@Override
	public String getTypeName() {
		return descriptor.getTypeName();
	}

}
