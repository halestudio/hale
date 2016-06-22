/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.transformations.type.retype;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.TransformationTreeImpl;
import eu.esdihumboldt.hale.common.align.service.FunctionService;
import eu.esdihumboldt.hale.common.align.service.impl.AlignmentFunctionService;
import eu.esdihumboldt.hale.common.align.tgraph.TGraph;
import eu.esdihumboldt.hale.common.align.tgraph.impl.TGraphImpl;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.io.xslt.XslTransformationUtil;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractTransformationTraverser;
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractVelocityXslTypeTransformation;

/**
 * XSLT representation of the Retype function.
 * 
 * @author Simon Templer
 */
public class XslRetype extends AbstractVelocityXslTypeTransformation
		implements XslTypeTransformation {

	private static final String CONTEXT_PARAM_SELECT_INSTANCES = "select_instances";

	private static final String CONTEXT_PARAM_PROPERTIES = "properties";

	private static final String CONTEXT_PARAM_INLINE_TEMPLATE = "inline_name";

	@Override
	protected void configureTemplate(final VelocityContext context, final Cell typeCell)
			throws TransformationException {
		Type source = (Type) CellUtil.getFirstEntity(typeCell.getSource());

		TypeEntityDefinition ted = source.getDefinition();
		context.put(CONTEXT_PARAM_SELECT_INSTANCES, XslTransformationUtil.selectInstances(ted,
				context().getSourceContext(ted.getDefinition()), context().getNamespaceContext()));

		context.put(CONTEXT_PARAM_INLINE_TEMPLATE, context().getInlineTemplateName(typeCell));

		String properties = createPropertiesFragment(typeCell);
		context.put(CONTEXT_PARAM_PROPERTIES, properties);
	}

	/**
	 * Create a XSL fragment for populating the properties of a target instance.
	 * 
	 * @param typeCell the associated type cell
	 * @return the XSL fragment
	 * @throws TransformationException if creating the fragment failed
	 */
	protected String createPropertiesFragment(final Cell typeCell) throws TransformationException {
		final TransformationTree tree = new TransformationTreeImpl(context().getAlignment(),
				typeCell);
		FunctionService functionService = new AlignmentFunctionService(context().getAlignment());

		/*
		 * Create the transformation graph derived from the transformation tree
		 * and perform context matching.
		 */
		/*
		 * XXX proxying multi-result nodes omitted for now, see
		 * proxyMultiResultNodes imlementation
		 */
//		final TGraph graph = new TGraphImpl(tree).proxyMultiResultNodes().performContextMatching();
		final TGraph graph = new TGraphImpl(tree, functionService).performContextMatching();

		// TODO tree as GraphML as informative annotation into XSLT?
		try {
			StringWriter propsOut = new StringWriter();
			try {
				AbstractTransformationTraverser trav = new RetypeTraverser(context(), propsOut,
						typeCell);
				trav.traverse(graph);
			} finally {
				propsOut.close();
			}

			return propsOut.toString();
		} catch (Exception e) {
			throw new TransformationException("Failed to create property transformations", e);
		}
	}
}
