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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all feature chaining settings for an entire app-schema mapping
 * configuration.
 * 
 * <p>
 * Internally, {@link JoinConfiguration} instances are stored in a {@link Map}
 * using cell IDs as keys.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class FeatureChaining {

	Map<String, JoinConfiguration> joins;

	/**
	 * Default constructor.
	 */
	public FeatureChaining() {
		this.joins = new HashMap<String, JoinConfiguration>();
	}

	/**
	 * Returns a copy of the internal structure holding the configuration for
	 * all joins.
	 * 
	 * @return a {@link Map} holding the configuration for all joins
	 */
	public Map<String, JoinConfiguration> getJoins() {
		return new HashMap<String, JoinConfiguration>(joins);
	}

	/**
	 * Replaces the current join configurations with the provided ones.
	 * 
	 * @param joins the join configurations to set
	 */
	public void replaceJoins(Map<String, JoinConfiguration> joins) {
		// TODO: should I make this method thread-safe?
		this.joins.clear();
		this.joins.putAll(joins);
	}

	/**
	 * @param joinCellId the join cell ID
	 * @return the configuration of all chains in the join cell, or an empty
	 *         collection if none is found
	 */
	public List<ChainConfiguration> getChains(String joinCellId) {
		List<ChainConfiguration> chains = new ArrayList<ChainConfiguration>();

		JoinConfiguration joinConf = joins.get(joinCellId);
		if (joinConf != null) {
			Map<Integer, ChainConfiguration> chainMap = joinConf.getChains();
			if (chainMap != null) {
				List<Integer> cellIndexes = new ArrayList<Integer>(chainMap.keySet());
				Collections.sort(cellIndexes);

				for (Integer cellIdx : cellIndexes) {
					chains.add(chainMap.get(cellIdx));
				}
			}
		}

		return chains;
	}

	/**
	 * @param joinCellId the join cell ID
	 * @param chainIdx the chain index
	 * @return the chain configuration, or <code>null</code> if none is found
	 */
	public ChainConfiguration getChain(String joinCellId, int chainIdx) {
		ChainConfiguration chain = null;

		JoinConfiguration joinConf = joins.get(joinCellId);
		if (joinConf != null) {
			chain = joinConf.getChain(chainIdx);
		}

		return chain;
	}

	/**
	 * Adds a new chain configuration to the join configuration for the
	 * specified cell (a new {@link JoinConfiguration} is created if none
	 * exists).
	 * 
	 * @param joinCellId the join cell ID
	 * @param chainIdx the chain index
	 * @param chain the chain configuration
	 */
	public void putChain(String joinCellId, int chainIdx, ChainConfiguration chain) {
		if (joinCellId == null || joinCellId.isEmpty()) {
			throw new IllegalArgumentException("joinCellId must be set");
		}
		if (chain == null) {
			throw new IllegalArgumentException("chain must be set");
		}

		JoinConfiguration joinConf = joins.get(joinCellId);
		if (joinConf == null) {
			joinConf = new JoinConfiguration();
			joinConf.setJoinCellId(joinCellId);
			joins.put(joinCellId, joinConf);
		}
		joinConf.chains.put(chainIdx, chain);
	}

}
