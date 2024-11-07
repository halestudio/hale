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

package eu.esdihumboldt.cst.functions.groovy;

import java.util.Collection;
import java.util.List;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.functions.core.Join;
import eu.esdihumboldt.cst.functions.core.join.IndexJoinHandler;
import eu.esdihumboldt.cst.functions.core.join.JoinHandler;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexContribution;

/**
 * Type transformation that joins multiple instances of different source types
 * into one target instance, based on matching properties. The transformation
 * also applies a Groovy script that can be used to control the target instance
 * creation.
 *
 * @author Simon Templer
 */
public class GroovyJoin extends GroovyRetype implements JoinFunction, InstanceIndexContribution {

	/**
	 * The log
	 */
	private static final ALogger LOG = ALoggerFactory.getLogger(GroovyJoin.class);

	/**
	 * The function ID. Not named <code>ID</code> to avoid shadowing
	 * {@link JoinFunction#ID}.
	 */
	public static final String GROOVY_JOIN_ID = "eu.esdihumboldt.cst.functions.groovy.join";

	/**
	 * The function ID.
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.groovy.join";

	@Override
	public InstanceHandler<? super TransformationEngine> getInstanceHandler() {
		boolean useIndexJoinHandler = false;

		String setting = System.getProperty("hale.functions.use_index_join_handler");

		if (setting == null) {
			setting = System.getenv("HALE_FUNCTIONS_USE_INDEX_JOIN_HANDLER");
		}

		if (setting != null) {
			try {
				useIndexJoinHandler = Boolean.valueOf(setting);
			} catch (Throwable e) {
				LOG.error("Error applying index join handler setting: " + setting, e);
			}
		}

		return useIndexJoinHandler ? new IndexJoinHandler() : new JoinHandler();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexContribution#getIndexContribution(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public Collection<List<PropertyEntityDefinition>> getIndexContribution(Cell cell) {
		return Join.getJoinContribution(cell);
	}

}
