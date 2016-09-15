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

package eu.esdihumboldt.hale.common.instance.geometry;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.common.instance.geometry.impl.internal.CodeDefinitionFactory;
import eu.esdihumboldt.hale.common.instance.geometry.impl.internal.WKTDefinitionFactory;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.util.definition.AbstractObjectFactory;

/**
 * Provides support for converting {@link CRSDefinition} to string and vice
 * versa based on the {@link CRSDefinitionFactory}ies available as OSGi
 * services.
 * 
 * @author Simon Templer
 */
public class CRSDefinitionManager extends
		AbstractObjectFactory<CRSDefinition, CRSDefinitionFactory<?>> {

	private CRSDefinitionManager() {
		super();
	}

	/**
	 * Get the CRS definition manager instance
	 * 
	 * @return the CRS definition manager instance
	 */
	public static CRSDefinitionManager getInstance() {
		if (instance == null) {
			instance = new CRSDefinitionManager();
		}

		return instance;
	}

	private static CRSDefinitionManager instance;

	/**
	 * @see AbstractObjectFactory#getDefinitions()
	 */
	@Override
	protected List<CRSDefinitionFactory<?>> getDefinitions() {
		List<CRSDefinitionFactory<?>> result = new ArrayList<CRSDefinitionFactory<?>>();

		// TODO make configurable again? e.g. via extension point?
		result.add(new CodeDefinitionFactory());
		result.add(new WKTDefinitionFactory());

		return result;
	}

}
