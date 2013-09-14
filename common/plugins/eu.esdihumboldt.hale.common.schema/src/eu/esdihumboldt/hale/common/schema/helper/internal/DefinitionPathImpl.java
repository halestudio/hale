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

package eu.esdihumboldt.hale.common.schema.helper.internal;

import java.util.List;

import com.google.common.collect.ImmutableList;

import eu.esdihumboldt.hale.common.schema.helper.DefinitionPath;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Default definition path implementation.
 * 
 * @author Simon Templer
 */
public class DefinitionPathImpl implements DefinitionPath {

	private final List<Definition<?>> path;

	/**
	 * Create a definition path.
	 * 
	 * @param path the list of definitions defining the path
	 */
	public DefinitionPathImpl(List<Definition<?>> path) {
		super();
		this.path = ImmutableList.copyOf(path);
	}

	/**
	 * Create an empty definition path.
	 */
	public DefinitionPathImpl() {
		this(ImmutableList.<Definition<?>> of());
	}

	@Override
	public List<Definition<?>> getPath() {
		return path;
	}

	@Override
	public DefinitionPath subPath(Definition<?> child) {
		return new DefinitionPathImpl(ImmutableList.<Definition<?>> builder().addAll(path)
				.add(child).build());
	}

	@Override
	public DefinitionPath subPath(DefinitionPath append) {
		return new DefinitionPathImpl(ImmutableList.<Definition<?>> builder().addAll(path)
				.addAll(append.getPath()).build());
	}

}
