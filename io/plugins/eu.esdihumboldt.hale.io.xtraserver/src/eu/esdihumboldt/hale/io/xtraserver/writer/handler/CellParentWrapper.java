/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.writer.handler;

import static eu.esdihumboldt.hale.common.align.model.functions.JoinFunction.PARAMETER_JOIN;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils;
import eu.esdihumboldt.hale.io.jdbc.constraints.DatabaseTable;

/**
 * Parameter decorator, that provides access to the parent cell
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class CellParentWrapper implements Cell {

	private final Cell wrappedCell;
	private final Cell parentTypeCell;

	/**
	 * Constructor.
	 * 
	 * @param parentTypeCell type cell
	 * @param wrappedCell property cell
	 */
	public CellParentWrapper(final Cell parentTypeCell, final Cell wrappedCell) {
		this.parentTypeCell = parentTypeCell;
		this.wrappedCell = wrappedCell;
	}

	/**
	 * Returns the parent type cell
	 * 
	 * @return type cell
	 */
	public Cell getParentCell() {
		return parentTypeCell;
	}

	/**
	 * Returns the type mapping cell representation
	 * 
	 * @return type mapping cell representation
	 */
	public Entity getParentCellSource() {
		ListMultimap<String, ? extends Entity> sourceEntities = parentTypeCell.getSource();
		if (sourceEntities != null && !sourceEntities.isEmpty()) {
			return sourceEntities.values().iterator().next();
		}
		return null;
	}

	private EntityDefinition getParentCellSourceType() {
		if (parentTypeCell.getTransformationIdentifier().equals(JoinFunction.ID)) {
			List<ParameterValue> parameters = parentTypeCell.getTransformationParameters()
					.get(PARAMETER_JOIN);
			if (!parameters.isEmpty()) {
				final JoinParameter joinParameter = parameters.get(0).as(JoinParameter.class);
				return joinParameter.getTypes().iterator().next();
			}
		}

		return getParentCellSource().getDefinition();
	}

	/**
	 * Returns the table name of the property cell or the parent type cell. If the
	 * source of the alignment is not a database schema, the name of the type is
	 * returned.
	 * 
	 * @return table name
	 */
	public String getTableName() {
		final Property sourceProperty = AppSchemaMappingUtils.getSourceProperty(wrappedCell);
		if (sourceProperty != null) {
			final DatabaseTable table = sourceProperty.getDefinition().getDefinition()
					.getParentType().getConstraint(DatabaseTable.class);
			if (table.isTable()) {
				return table.getTableName();
			}
			else {
				final DatabaseTable tableOfType = sourceProperty.getDefinition().getType()
						.getConstraint(DatabaseTable.class);
				if (tableOfType.isTable()) {
					return table.getTableName();
				}
				else {
					return sourceProperty.getDefinition().getType().getDisplayName();
				}
			}
		}
		final EntityDefinition sourceType = getParentCellSourceType();
		final DatabaseTable table = sourceType.getType().getConstraint(DatabaseTable.class);
		if (!table.isTable()) {
			return sourceType.getType().getDisplayName();
		}
		else {
			return table.getTableName();
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getSource()
	 */
	@Override
	public ListMultimap<String, ? extends Entity> getSource() {
		return wrappedCell.getSource();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getTarget()
	 */
	@Override
	public ListMultimap<String, ? extends Entity> getTarget() {
		return wrappedCell.getTarget();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getTransformationParameters()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getTransformationParameters() {
		return wrappedCell.getTransformationParameters();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getAnnotations(java.lang.String)
	 */
	@Override
	public List<?> getAnnotations(String type) {
		return wrappedCell.getAnnotations(type);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getAnnotationTypes()
	 */
	@Override
	public Set<String> getAnnotationTypes() {
		return wrappedCell.getAnnotationTypes();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#addAnnotation(java.lang.String)
	 */
	@Override
	public Object addAnnotation(String type) {
		return wrappedCell.addAnnotation(type);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#addAnnotation(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void addAnnotation(String type, Object annotation) {
		wrappedCell.addAnnotation(type, annotation);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#removeAnnotation(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void removeAnnotation(String type, Object annotation) {
		wrappedCell.removeAnnotation(type, annotation);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getDocumentation()
	 */
	@Override
	public ListMultimap<String, String> getDocumentation() {
		return wrappedCell.getDocumentation();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getTransformationIdentifier()
	 */
	@Override
	public String getTransformationIdentifier() {
		return wrappedCell.getTransformationIdentifier();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getId()
	 */
	@Override
	public String getId() {
		return wrappedCell.getId();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getDisabledFor()
	 */
	@Override
	public Set<String> getDisabledFor() {
		return wrappedCell.getDisabledFor();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getPriority()
	 */
	@Override
	public Priority getPriority() {
		return wrappedCell.getPriority();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#getTransformationMode()
	 */
	@Override
	public TransformationMode getTransformationMode() {
		return wrappedCell.getTransformationMode();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Cell#isBaseCell()
	 */
	@Override
	public boolean isBaseCell() {
		return wrappedCell.isBaseCell();
	}
}
