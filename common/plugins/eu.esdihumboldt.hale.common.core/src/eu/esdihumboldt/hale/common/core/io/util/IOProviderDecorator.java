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

package eu.esdihumboldt.hale.common.core.io.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Decorator for {@link IOProvider}s.
 * 
 * @param <T> the provider type
 * @author Simon Templer
 */
public abstract class IOProviderDecorator<T extends IOProvider> implements IOProvider {

	/**
	 * The internal I/O provider.
	 */
	protected final T internalProvider;

	/**
	 * Create an I/O provider decorator.
	 * 
	 * @param internalProvider the I/O provider to decorate
	 */
	public IOProviderDecorator(T internalProvider) {
		super();
		this.internalProvider = internalProvider;
	}

	@Override
	public IOReport execute(ProgressIndicator progress)
			throws IOProviderConfigurationException, IOException {
		return internalProvider.execute(progress);
	}

	@Override
	public IOReporter createReporter() {
		return internalProvider.createReporter();
	}

	@Override
	public boolean isCancelable() {
		return internalProvider.isCancelable();
	}

	@Override
	public void validate() throws IOProviderConfigurationException {
		internalProvider.validate();
	}

	@Override
	public void setContentType(IContentType contentType) {
		internalProvider.setContentType(contentType);
	}

	@Override
	public IContentType getContentType() {
		return internalProvider.getContentType();
	}

	@Override
	public void setCharset(Charset charset) {
		internalProvider.setCharset(charset);
	}

	@Override
	public Charset getCharset() {
		return internalProvider.getCharset();
	}

	@Override
	public void setServiceProvider(ServiceProvider serviceProvider) {
		internalProvider.setServiceProvider(serviceProvider);
	}

	@Override
	public Set<String> getSupportedParameters() {
		return internalProvider.getSupportedParameters();
	}

	@Override
	public void setParameter(String name, Value value) {
		internalProvider.setParameter(name, value);
	}

	@Override
	public Value getParameter(String name) {
		return internalProvider.getParameter(name);
	}

	@Override
	public void loadConfiguration(Map<String, Value> configuration) {
		internalProvider.loadConfiguration(configuration);
	}

	@Override
	public void storeConfiguration(Map<String, Value> configuration) {
		internalProvider.storeConfiguration(configuration);
	}

	@Override
	public void setActionId(String action) {
		internalProvider.setActionId(action);
	}

	@Override
	public String getActionId() {
		return internalProvider.getActionId();
	}

}
