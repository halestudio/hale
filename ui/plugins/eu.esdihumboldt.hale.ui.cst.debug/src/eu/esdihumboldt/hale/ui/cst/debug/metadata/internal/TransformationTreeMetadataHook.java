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

			DotProvider dotProv = new TreeToDotProvider(tree);
			String dot = dotProv.generateGraph();
			// writeDotFile(dot);

			target.setMetaData(KEY_POPULATED_TREE, dot);
		}
	}

	/**
	 * XXX test-method for file-output delete later
	 * 
	 * @param dot the dot-graph
	 */
	/*
	 * private void writeDotFile(String dot) { File dotFile = new
	 * File("C://Users/Calico//Desktop//dot.txt");
	 * 
	 * PrintWriter output; BufferedWriter buffer; FileWriter fileWriter; try {
	 * dotFile.createNewFile();
	 * 
	 * fileWriter = new FileWriter(dotFile); buffer = new
	 * BufferedWriter(fileWriter); output = new PrintWriter(buffer); try {
	 * output.print(dot);
	 * 
	 * } finally { output.close(); buffer.close(); fileWriter.close();
	 * 
	 * } } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

}
