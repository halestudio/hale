/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.util.config

import java.nio.charset.StandardCharsets

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

import groovy.transform.CompileStatic

/**
 * Helper for converting a Config from and to YAML.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ConfigYaml {

	/**
	 * Load a configuration from a YAML file.
	 *
	 * @param yamlFile the YAML file
	 * @return the loaded configuration map
	 */
	static Config load(File yamlFile) {
		Config result
		yamlFile.withInputStream { result = load(it) }
		result
	}

	/**
	 * Load a configuration from an input stream.
	 *
	 * @param input the input stream
	 * @return the loaded configuration map
	 */
	static Config load(InputStream input) {
		Yaml yaml = new Yaml(new SafeConstructor());
		Map result = yaml.load(input)
		new Config(result ?: [:])
	}

	/**
	 * Save configuration to a YAML file.
	 *
	 * @param yamlFile the YAML file
	 */
	static void save(Config config, File yamlFile) {
		DumperOptions options = new DumperOptions()
		// options.explicitStart = true
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
		Yaml yaml = new Yaml(options);
		yamlFile.withWriter(StandardCharsets.UTF_8.name()) {
			yaml.dump(config.asMap(), it)
		}
	}

}
