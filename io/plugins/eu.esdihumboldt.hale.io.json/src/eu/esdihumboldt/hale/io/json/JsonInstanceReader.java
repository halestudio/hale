/*
 * Copyright (c) 2023 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json;

import java.io.IOException;

import javax.xml.namespace.QName;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.parameter.AbstractParameterValueDescriptor;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.json.internal.JsonInstanceCollection;
import eu.esdihumboldt.hale.io.json.internal.JsonReadMode;
import eu.esdihumboldt.hale.io.json.internal.JsonToInstance;

/**
 * Reader for Json/GeoJson data.
 * 
 * @author Simon Templer
 */
public class JsonInstanceReader extends AbstractInstanceReader {

	private static final ALogger log = ALoggerFactory.getLogger(JsonInstanceReader.class);

	@SuppressWarnings("javadoc")
	public static class DefaultTypeParameterDescriptor extends AbstractParameterValueDescriptor {

		public DefaultTypeParameterDescriptor() {
			super(null, Value.of(new QName("namespace", "localname").toString()));
		}

		@Override
		public String getSampleDescription() {
			return "The type name is represented like in the given example, with the namespace in curly braces.";
		}
	}

	/**
	 * Name of the parameter that specifies the read mode.
	 */
	public static final String PARAM_READ_MODE = "mode";

	/**
	 * Name of the parameter that specifies the default type to assume for an
	 * instance.
	 */
	public static final String PARAM_DEFAULT_TYPE = "defaultType";

	/**
	 * Name of the parameter that specifies if the default type should be used
	 * for all instances (i.e. use no other mechanisms to detect the type).
	 */
	public static final String PARAM_FORCE_DEFAULT_TYPE = "forceDefaultType";

	private InstanceCollection instances;

	/**
	 * Default constructor
	 */
	public JsonInstanceReader() {
		super();

		addSupportedParameter(PARAM_READ_MODE);
		addSupportedParameter(PARAM_DEFAULT_TYPE);
		addSupportedParameter(PARAM_FORCE_DEFAULT_TYPE);
	}

	@Override
	public InstanceCollection getInstances() {
		return instances;
	}

	@Override
	public boolean isCancelable() {
		// actual
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Creating " + getDefaultTypeName() + " parser", ProgressIndicator.UNKNOWN);

		try {
			boolean expectGeoJson = true; // currently defaults to true, no
											// major difference in functionality

			QName defaultTypeName = getDefaultType();
			TypeDefinition type = null;
			if (defaultTypeName != null) {
				type = getSourceSchema().getType(defaultTypeName);
			}

			JsonToInstance translator = new JsonToInstance(getReadMode(), expectGeoJson, type,
					isForceDefaultType(), getSourceSchema(), SimpleLog.fromLogger(log));
			instances = new JsonInstanceCollection(translator, getSource(), getCharset());

			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error("Error preparing reading {0}", getDefaultTypeName(), e);
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return reporter;
	}

	/**
	 * Set the read mode to use.
	 * 
	 * @param mode the mode for reading Json
	 */
	public void setReadMode(JsonReadMode mode) {
		if (mode == null) {
			setParameter(PARAM_READ_MODE, Value.NULL);
		}
		else {
			setParameter(PARAM_READ_MODE, Value.of(mode.toString()));
		}
	}

	/**
	 * @return the mode to use for reading Json
	 */
	public JsonReadMode getReadMode() {
		JsonReadMode value = getParameter(PARAM_READ_MODE).as(JsonReadMode.class);
		if (value == null)
			return JsonReadMode.auto;
		else
			return value;
	}

	/**
	 * Set the default type to use for read instances. Other mechanisms to
	 * determine the type may take precedence.
	 * 
	 * @param defaultType the name of the default type to use
	 */
	public void setDefaultType(QName defaultType) {
		if (defaultType == null) {
			setParameter(PARAM_DEFAULT_TYPE, Value.NULL);
		}
		else {
			setParameter(PARAM_DEFAULT_TYPE, Value.of(defaultType.toString()));
		}
	}

	/**
	 * @return the name of the default type to use for read instances, may be
	 *         <code>null</code>
	 */
	public QName getDefaultType() {
		String name = getParameter(PARAM_DEFAULT_TYPE).as(String.class);
		if (name != null) {
			return QName.valueOf(name);
		}
		return null;
	}

	/**
	 * Set if the default type specified should be forced to be used for all
	 * instances. This disables any other mechanisms to determine the type of
	 * the instance.
	 * 
	 * @param force <code>true</code> if the configured default type should
	 *            always be used, <code>false</code> otherwise
	 */
	public void setForceDefaultType(boolean force) {
		setParameter(PARAM_FORCE_DEFAULT_TYPE, Value.of(force));
	}

	/**
	 * @return if the default type should be forced to be used for all instances
	 */
	public boolean isForceDefaultType() {
		return getParameter(PARAM_FORCE_DEFAULT_TYPE).as(Boolean.class, false);
	}

	@Override
	protected String getDefaultTypeName() {
		return "JSON";
	}

}
