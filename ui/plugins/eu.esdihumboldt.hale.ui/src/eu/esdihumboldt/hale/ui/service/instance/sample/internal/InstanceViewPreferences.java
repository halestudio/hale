/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.service.instance.sample.internal;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.ui.service.instance.sample.Sampler;
import eu.esdihumboldt.hale.ui.service.instance.sample.internal.sampler.first.FirstSampler;
import eu.esdihumboldt.hale.ui.service.instance.sample.internal.sampler.skip.SkipSampler;

/**
 * Constants related to instance sampling preferences.
 * 
 * @author Simon Templer
 */
public class InstanceViewPreferences {

	/**
	 * Identifier of {@link FirstSampler}.
	 */
	public static final String SAMPLER_FIRST = "first";

	/**
	 * Identifiers mapped to samplers.
	 * 
	 * XXX may be replaced at a later point by an extension point
	 */
	public static final BiMap<String, Sampler> SAMPLERS = ImmutableBiMap.<String, Sampler> of(
			SAMPLER_FIRST, new FirstSampler(), "skip", new SkipSampler());

	/**
	 * Determine if instance sampling is enabled in the given configuration.
	 * 
	 * @param config the configuration service
	 * @return if instance sampling is enabled
	 */
	public static boolean samplingEnabled(IConfigurationService config) {
		return config.getBoolean(KEY_ENABLED, ENABLED_DEFAULT);
	}

	/**
	 * Determine if occurring values should use the external source data instead
	 * if the instance service sources.
	 * 
	 * @param config the configuration service
	 * @return if occurring values should use the external source data
	 */
	public static boolean occurringValuesUseExternalData(IConfigurationService config) {
		return samplingEnabled(config)
				&& config.getBoolean(KEY_OCCURRING_VALUES_USE_EXTERNAL,
						OCCURRING_VALUES_EXTERNAL_DEFAULT);
	}

	/**
	 * Constant defining if the complete source data should be used for
	 * occurring values by default.
	 */
	public static final boolean OCCURRING_VALUES_EXTERNAL_DEFAULT = false;

	/**
	 * The key for the configuration specifying whether in occurring values the
	 * complete source data should be used.
	 */
	public static final String KEY_OCCURRING_VALUES_USE_EXTERNAL = "instances.sampling.ov_complete";

	/**
	 * Constant defining if instance sampling is enabled by default.
	 */
	public static final boolean ENABLED_DEFAULT = false;

	/**
	 * The key for the configuration specifying whether the instance sampling is
	 * enabled.
	 */
	public static final String KEY_ENABLED = "instances.sampling.enabled";

	/**
	 * Key for the configuration specifying the selected sampler.
	 */
	public static final String KEY_SAMPLER = "instances.sampling.sampler";

	/**
	 * Prefix of the key for the settings of an individual sampler. The sampler
	 * identifier is supposed to be appended to the prefix.
	 */
	public static final String KEY_SETTINGS_PREFIX = "instances.sampling.settings.";

}
