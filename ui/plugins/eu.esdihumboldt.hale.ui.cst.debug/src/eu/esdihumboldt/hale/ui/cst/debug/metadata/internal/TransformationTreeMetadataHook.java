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

package eu.esdihumboldt.hale.ui.cst.debug.metadata.internal;

import java.io.ByteArrayOutputStream;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.ui.cst.debug.metadata.TransformationTreeMetadata;

/**
 * Transformation tree hook storing the transformation tree in the instance
 * metadata.
 * 
 * @author Simon Templer
 */
public class TransformationTreeMetadataHook implements TransformationTreeHook,
		TransformationTreeMetadata {

	private static final ALogger log = ALoggerFactory
			.getLogger(TransformationTreeMetadataHook.class);

	/**
	 * @see TransformationTreeHook#processTransformationTree(TransformationTree,
	 *      TreeState, MutableInstance)
	 */
	@Override
	public void processTransformationTree(TransformationTree tree, TreeState state,
			MutableInstance target) {
		if (state == TreeState.SOURCE_POPULATED) { // TODO key per state - for
													// now only support this
													// state

			TreeGraphProvider prov = new TreeGraphMLProvider(tree);
			Graph graph = prov.generateGraph();
			GraphMLWriter writer = new GraphMLWriter(graph);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				writer.outputGraph(out);
			} catch (Exception e) {
				log.error("Error converting GraphML Graph to String", e);
			}

			String graphstring = new String(out.toByteArray());

			target.setMetaData(KEY_POPULATED_TREE, graphstring);
		}
	}
}
