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

package eu.esdihumboldt.hale.common.align.merge;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * General Alignment Merger settings.
 * 
 * @author Simon Templer
 */
public class MergeSettings {

	private static final AtomicBoolean transferContextsToJoinFocus = new AtomicBoolean(
			defaultTransferContextsToJoinFocus());

	private static boolean defaultTransferContextsToJoinFocus() {
		String setting = System.getenv("ALIGNMENT_MERGER_TRANSFER_CONTEXTS_TO_JOIN_FOCUS");
		if (setting == null) {
			return false;
		}
		else {
			return Boolean.parseBoolean(setting);
		}
	}

	/**
	 * States if contexts should be tried to be transferred to the Join focus,
	 * if applicable.
	 * 
	 * @return <code>true</code> if the setting is enabled, <code>false</code>
	 *         otherwise
	 */
	public static boolean isTransferContextsToJoinFocus() {
		return transferContextsToJoinFocus.get();
	}

	/**
	 * Set if contexts should be tried to be transferred to the Join focus, if
	 * applicable.
	 * 
	 * @param enabled <code>true</code> if the setting should be enabled,
	 *            <code>false</code> otherwise
	 */
	public static void setTransferContextsToJoinFocus(boolean enabled) {
		transferContextsToJoinFocus.set(enabled);
	}

}
