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

package eu.esdihumboldt.hale.io.appschema.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the feature chaining configuration for an entire join cell.
 * 
 * <p>
 * Internally, {@link ChainConfiguration} instances are stored in a {@link Map}
 * using the chain index as key.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class JoinConfiguration {

	String joinCellId;
	Map<Integer, ChainConfiguration> chains;

	/**
	 * Default constructor.
	 */
	public JoinConfiguration() {
		this.chains = new HashMap<Integer, ChainConfiguration>();
	}

	/**
	 * @return the join cell ID
	 */
	public String getJoinCellId() {
		return joinCellId;
	}

	/**
	 * @param joinCellId the join cell ID to set
	 */
	public void setJoinCellId(String joinCellId) {
		this.joinCellId = joinCellId;
	}

	/**
	 * Returns a copy of the internal structure holding the configuration for
	 * all chains.
	 * 
	 * @return a {@link Map} holding the configuration for all chains
	 */
	public Map<Integer, ChainConfiguration> getChains() {
		return new HashMap<Integer, ChainConfiguration>(chains);
	}

	/**
	 * @param chainIdx the chain index
	 * @return the configuration for the specified chain, or <code>null</code>
	 *         if none is found
	 */
	public ChainConfiguration getChain(int chainIdx) {
		return chains.get(chainIdx);
	}

	/**
	 * @param chainIdx the chain index
	 * @param chain the chain configuration to set
	 */
	public void putChain(int chainIdx, ChainConfiguration chain) {
		this.chains.put(chainIdx, chain);
	}

}
