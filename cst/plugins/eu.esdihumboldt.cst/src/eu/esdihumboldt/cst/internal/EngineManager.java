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

package eu.esdihumboldt.cst.internal;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.engine.EngineExtension;
import eu.esdihumboldt.hale.common.align.extension.engine.EngineFactory;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Transformation engine manager. Holds transformation engine instances during a
 * transformation process.
 * 
 * @author Simon Templer
 */
public class EngineManager {

	private static final ALogger log = ALoggerFactory.getLogger(EngineManager.class);

	private final Map<String, TransformationEngine> engines = new HashMap<String, TransformationEngine>();

	/**
	 * Dispose all transformation engines
	 */
	public synchronized void dispose() {
		for (TransformationEngine engine : engines.values()) {
			try {
				engine.dispose();
			} catch (Throwable e) {
				log.warn("Error disposing transformation engine", e);
			}
		}
	}

	/**
	 * Get the transformation engine with the given ID
	 * 
	 * @param engineId the transformation engine ID
	 * @param log the transformation log to report any errors to
	 * @return the transformation engine or <code>null</code> if none with the
	 *         given ID was found or the creation failed
	 */
	public synchronized TransformationEngine get(String engineId, TransformationLog log) {
		TransformationEngine engine = engines.get(engineId);

		if (engine == null) {
			EngineExtension ee = EngineExtension.getInstance();
			EngineFactory engineFactory = ee.getFactory(engineId);

			if (engineFactory == null) {
				log.error(log.createMessage(MessageFormat.format(
						"Transformation engine with ID {0} not found.", engineId), null));
			}
			else {
				try {
					TransformationEngine tmp = engineFactory.createExtensionObject();
					tmp.setup();
					engine = tmp;
				} catch (Exception e) {
					log.error(log.createMessage("Could not create transformation engine", e));
				}
			}
		}

		return engine;
	}

}
