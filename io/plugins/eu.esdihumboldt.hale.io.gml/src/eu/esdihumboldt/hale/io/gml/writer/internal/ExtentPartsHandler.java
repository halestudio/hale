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

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.InstanceCollectionDecorator;
import eu.esdihumboldt.util.Pair;

/**
 * Handler for writing instances split by spatial extent.
 * 
 * @author Florian Esser
 */
public class ExtentPartsHandler implements MultipartHandler {

	/**
	 * Key to be used for instances that do not have exactly one geometry
	 * property.
	 */
	public static final String KEY_NO_GEOMETRY = "NOGEOM";

	/**
	 * Decorator for {@link InstanceCollection}s that adds a quadtree key
	 * 
	 * @author Florian Esser
	 */
	public static class TreeKeyDecorator extends InstanceCollectionDecorator {

		private final String treeKey;

		/**
		 * Create a TreeKeyDecorator
		 * 
		 * @param decoratee InstanceCollection to decroate
		 * @param treeKey The tree key of the tile represented by this
		 *            InstanceCollection
		 */
		public TreeKeyDecorator(InstanceCollection decoratee, String treeKey) {
			super(decoratee);
			this.treeKey = treeKey;
		}

		/**
		 * @return the tree key of the tile
		 */
		public String getTreeKey() {
			return treeKey;
		}

	}

	private final Map<String, URI> keyToTargetMapping;
	private final Map<String, String> idToKeyMapping;

	/**
	 * Create the handler
	 * 
	 * @param keyToTargetMapping Mapping between tree key and target file
	 * @param idToKeyMapping Mapping between the GML IDs and the associated tree
	 *            key
	 */
	public ExtentPartsHandler(Map<String, URI> keyToTargetMapping,
			Map<String, String> idToKeyMapping) {
		this.keyToTargetMapping = keyToTargetMapping;
		this.idToKeyMapping = idToKeyMapping;
	}

	@Override
	public String getTargetFilename(InstanceCollection part, URI location) {
		if (!(part instanceof TreeKeyDecorator)) {
			throw new IllegalArgumentException("InstanceCollection must be a TreeKeyDecorator");
		}

		TreeKeyDecorator dec = (TreeKeyDecorator) part;
		return getTargetFilename(dec.getTreeKey(), location);
	}

	/**
	 * Build the target file name for a tree key
	 * 
	 * @param treeKey Tree key
	 * @param location Original target location
	 * @return The modified file name
	 */
	public static String getTargetFilename(String treeKey, URI location) {
		if (KEY_NO_GEOMETRY.equals(treeKey)) {
			return Paths.get(location).normalize().toString();
		}

		Path origPath = Paths.get(location).normalize();
		Pair<String, String> nameAndExt = DefaultMultipartHandler
				.getFileNameAndExtension(origPath.toString());

		return String.format("%s%s%s.%s.%s", origPath.getParent().toString(), File.separator,
				nameAndExt.getFirst(), treeKey, nameAndExt.getSecond());
	}

	@Override
	public PrefixAwareStreamWriter getDecoratedWriter(PrefixAwareStreamWriter writer, URI target) {
		Map<String, URI> idToTargetMapping = idToKeyMapping.entrySet().stream().collect(
				Collectors.toMap(Map.Entry::getKey, e -> keyToTargetMapping.get(e.getValue())));
		LocalReferenceUpdater updater = new LocalReferenceUpdater(idToTargetMapping, target);

		return new ReferenceUpdatingStreamWriter(writer, updater);
	}
}