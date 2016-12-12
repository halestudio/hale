/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.core.io.project;

import java.util.List;

import de.fhg.igd.osgi.util.configuration.ConfigurationItem;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Unmodifiable complex configuration service.
 * 
 * @author Simon Templer
 */
public class UnmodifiableComplexConfigurationService implements ComplexConfigurationService {

	private final ComplexConfigurationService decoratee;

	/**
	 * Create an unmodifiable complex configuration service based on the given
	 * instance
	 * 
	 * @param decoratee the complex configuration service to wrap
	 */
	public UnmodifiableComplexConfigurationService(ComplexConfigurationService decoratee) {
		super();
		this.decoratee = decoratee;
	}

	private void unsupported() {
		throw new UnsupportedOperationException("Manipulating the configuration is not supported");
	}

	@Override
	public void setProperty(String name, Value value) {
		unsupported();
	}

	@Override
	public String get(String key) {
		return decoratee.get(key);
	}

	@Override
	public Value getProperty(String name) {
		return decoratee.getProperty(name);
	}

	@Override
	public String get(String key, String defaultValue) {
		return decoratee.get(key, defaultValue);
	}

	@Override
	public List<String> getList(String key) {
		return decoratee.getList(key);
	}

	@Override
	public List<String> getList(String key, List<String> defaultValue) {
		return decoratee.getList(key, defaultValue);
	}

	@Override
	public Integer getInt(String key) throws NumberFormatException {
		return decoratee.getInt(key);
	}

	@Override
	public Integer getInt(String key, Integer defaultValue) throws NumberFormatException {
		return decoratee.getInt(key, defaultValue);
	}

	@Override
	public Boolean getBoolean(String key) {
		return decoratee.getBoolean(key);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return decoratee.getBoolean(key, defaultValue);
	}

	@Override
	public <T extends ConfigurationItem> T get(Class<T> cls) {
		return decoratee.get(cls);
	}

	@Override
	public void set(String key, String value) {
		unsupported();
	}

	@Override
	public void setList(String key, List<String> value) {
		unsupported();
	}

	@Override
	public void setInt(String key, int value) {
		unsupported();
	}

	@Override
	public void setBoolean(String key, boolean value) {
		unsupported();
	}

	@Override
	public <T extends ConfigurationItem> void set(T item) {
		unsupported();
	}

}
