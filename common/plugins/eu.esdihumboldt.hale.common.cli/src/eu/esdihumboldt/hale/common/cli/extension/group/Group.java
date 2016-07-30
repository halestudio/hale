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

package eu.esdihumboldt.hale.common.cli.extension.group;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Represents a CLI command group. Usually provided through the corresponding
 * extension point.
 * 
 * @author Simon Templer
 */
@Immutable
public final class Group implements Identifiable {

	private final String id;
	private final String name;
	@Nullable
	private final String parent;
	@Nullable
	private final String description;

	/**
	 * Create a command group.
	 * 
	 * @param id the group id
	 * @param name the group name
	 * @param parent the parent group or <code>null</code>
	 * @param description the group description or <code>null</code>
	 */
	public Group(String id, String name, @Nullable String parent, @Nullable String description) {
		super();
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.description = description;
	}

	/**
	 * @return the group id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return the category name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the category description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

}
