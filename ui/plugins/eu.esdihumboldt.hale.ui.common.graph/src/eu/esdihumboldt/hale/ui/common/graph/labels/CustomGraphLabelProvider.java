/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.common.graph.labels;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.IFigureProvider;

import eu.esdihumboldt.hale.common.align.extension.function.Function;
import eu.esdihumboldt.hale.common.align.helper.TestValues;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.graph.figures.EntityFigureWithData;
import eu.esdihumboldt.hale.ui.common.graph.figures.FunctionFigure;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.shapes.FingerPost;

/**
 * Label Provider for transformation specialized Mapping Graph.
 * 
 * @author Yasmina Kammeyer
 */
public class CustomGraphLabelProvider extends GraphLabelProvider {

	private TypeDefinition source;
	private TypeDefinition target;

	/**
	 * Default constructor
	 * 
	 * @param provider the service provider that may be needed to obtain cell
	 *            explanations, may be <code>null</code>
	 */
	public CustomGraphLabelProvider(ServiceProvider provider) {
		super(provider);
		new ArrayList<TypeDefinition>();
	}

	/**
	 * @see IFigureProvider#getFigure(Object)
	 */
	@Override
	public IFigure getFigure(Object element) {

		CustomShapeFigure figure = null;
		if (element instanceof Cell) {
			setSourceTypeEntityDefinition((Cell) element);
			setTargetTypeEntityDefinition((Cell) element);
			figure = (CustomShapeFigure) super.getFigure(element);
		}

		if (element instanceof Function) {
			figure = new FunctionFigure(getCustomFigureFont());
		}

		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}

		if (element instanceof EntityDefinition) {
			String contextText = AlignmentUtil.getContextText((EntityDefinition) element);
			switch (((EntityDefinition) element).getSchemaSpace()) {
			case SOURCE:
				// If it is a property, it may have a value
				if (element instanceof PropertyEntityDefinition) {

					String dataValue = getValueFromEntity(element);

					figure = new EntityFigureWithData(new FingerPost(10, SWT.RIGHT), contextText,
							"Data:" + dataValue, getCustomFigureFont());
				}
				else {
					figure = new EntityFigureWithData(new FingerPost(10, SWT.RIGHT), contextText,
							null, getCustomFigureFont());
				}
				break;
			case TARGET:
				// If it is a property, it may have a value
				if (element instanceof PropertyEntityDefinition) {

					String dataValue = getValueFromEntity(element);

					figure = new EntityFigureWithData(new FingerPost(10, SWT.LEFT), contextText,
							"Result:" + dataValue, getCustomFigureFont());
				}
				else {
					figure = new EntityFigureWithData(new FingerPost(10, SWT.LEFT), contextText,
							null, getCustomFigureFont());
				}
				break;
			}
		}

		if (figure != null) {
			// because the graph will be vertical aligned, use more width
			figure.setMaximumWidth(MAX_FIGURE_WIDTH * 2);
		}

		return figure;
	}

	/**
	 * Set the source TypeEntityDefinition
	 * 
	 * @param cell The cell the TypeDefintion gets extracted from
	 */
	private void setSourceTypeEntityDefinition(Cell cell) {
		if (cell == null) {
			return;
		}
		if (cell.getSource() == null) {
			return;
		}
		EntityDefinition entity = cell.getSource().values().iterator().next().getDefinition();
		// If the cell's source is a type
		if (entity instanceof TypeEntityDefinition) {
			source = (TypeDefinition) entity.getDefinition();
		}
		// or extract the type
		// XXX: properties of different types used for join?
//		else if (entity instanceof PropertyEntityDefinition) {
//			// only add types with instances
//			if (entity.getType().getSubTypes() == Collections.EMPTY_LIST) {
//				source = entity.getType();
//			}
//		}
	}

	/**
	 * Set the target TypeEntityDefinition
	 * 
	 * @param cell The cell the TypeEntityDefintion gets extracted from
	 */
	private void setTargetTypeEntityDefinition(Cell cell) {
		if (cell == null) {
			return;
		}
		if (cell.getTarget() == null) {
			return;
		}
		EntityDefinition entity = cell.getTarget().values().iterator().next().getDefinition();
		// If the cell's target is a type
		if (entity instanceof TypeEntityDefinition) {
			target = (TypeDefinition) entity.getDefinition();
		}
		// or extract the type
		else if (entity instanceof PropertyEntityDefinition) {
			target = entity.getType();
		}
	}

	/**
	 * @param entity should be a PropertyEntityDefinition
	 * @return the extracted value of the object
	 */
	protected String getValueFromEntity(Object entity) {

		TestValues instanceValue = (TestValues) PlatformUI.getWorkbench().getService(
				TestValues.class);

		if (entity instanceof PropertyEntityDefinition) {
			// create propertyEntityDefinition with stored type
			// because the cell's property may has a parent type attached
			PropertyEntityDefinition ent = null;
			Object result = null;
			// Decide if source or target property
			if (((PropertyEntityDefinition) entity).getSchemaSpace() == SchemaSpaceID.SOURCE) {

				ent = new PropertyEntityDefinition(source,
						((PropertyEntityDefinition) entity).getPropertyPath(),
						((PropertyEntityDefinition) entity).getSchemaSpace(),
						((PropertyEntityDefinition) entity).getFilter());
				result = instanceValue.get(ent);
				if (result instanceof Instance) {
					return extractValue((Instance) result);
				}
			}
			else {
				ent = new PropertyEntityDefinition(target,
						((PropertyEntityDefinition) entity).getPropertyPath(),
						((PropertyEntityDefinition) entity).getSchemaSpace(),
						((PropertyEntityDefinition) entity).getFilter());
				result = instanceValue.get(ent);
				if (result instanceof Instance) {
					return extractValue((Instance) result);
				}
			}

			if (result == null) {
				return "no value";
			}
			// call toString() method for everything else
			return result.toString();
		}
		return "no value";
	}

	/**
	 * 
	 * @param instance the instance to get the value from
	 * @return the data value as a String
	 */
	private String extractValue(Instance instance) {

		if (instance == null) {
			return null;
		}
		// if there is no value, try the children
		// this could happen for recursive rename
		if (instance.getValue() == null) {
			StringBuilder sb = new StringBuilder();
			Object[] nextChild;
			for (QName child : instance.getPropertyNames()) {
				nextChild = instance.getProperty(child);
				// add the value to build a String
				if (nextChild != null) {
					if (nextChild[0] instanceof Instance) {
						if (((Instance) nextChild[0]).getValue() != null) {
							// next children
							// name:
							// value
							sb.append(((Instance) nextChild[0]).getDefinition().getDisplayName());
							sb.append(":\n\t");
							sb.append(((Instance) nextChild[0]).getValue().toString());
							sb.append("\n");
						}
					}
					else {
						sb.append(child.getLocalPart());
						sb.append(":\n\t");
						sb.append(nextChild[0].toString());
						sb.append("\n");
					}
				}
			}
			return sb.toString();
		}

		String result = instance.getValue().toString();
		if (result != null) {
			return result;
		}
		return null;
	}
}
