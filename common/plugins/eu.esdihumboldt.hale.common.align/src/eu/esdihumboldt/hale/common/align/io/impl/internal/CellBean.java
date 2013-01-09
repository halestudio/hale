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

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Bean representing a {@link Cell}
 * 
 * @author Simon Templer
 */
public class CellBean {

	private List<NamedEntityBean> source = new ArrayList<NamedEntityBean>();

	private List<NamedEntityBean> target = new ArrayList<NamedEntityBean>();

	private List<ParameterValueBean> transformationParameters = new ArrayList<ParameterValueBean>();

	private String transformationIdentifier;

	private String id;

	/**
	 * Default constructor. Creates an empty cell bean.
	 */
	public CellBean() {
		super();
	}

	/**
	 * Create a cell bean based on the given cell
	 * 
	 * @param cell the cell
	 */
	public CellBean(Cell cell) {
		this.transformationIdentifier = cell.getTransformationIdentifier();

		if (cell.getTransformationParameters() != null) {
			for (Entry<String, ParameterValue> param : cell.getTransformationParameters().entries()) {
				transformationParameters.add(new ParameterValueBean(param.getKey(), param
						.getValue()));
			}
		}

		if (cell.getSource() != null) {
			for (Entry<String, ? extends Entity> sourceEntity : cell.getSource().entries()) {
				source.add(new NamedEntityBean(sourceEntity.getKey(), sourceEntity.getValue()));
			}
		}

		for (Entry<String, ? extends Entity> targetEntity : cell.getTarget().entries()) {
			target.add(new NamedEntityBean(targetEntity.getKey(), targetEntity.getValue()));
		}

		this.id = cell.getId();
	}

	/**
	 * Create a cell based on the information in the cell bean if possible.
	 * Otherwise a corresponding error message should be added to the report.
	 * 
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @return the created cell or <code>null</code>
	 */
	public MutableCell createCell(IOReporter reporter, TypeIndex sourceTypes, TypeIndex targetTypes) {
		MutableCell cell = new DefaultCell();

		cell.setTransformationIdentifier(getTransformationIdentifier());

		if (transformationParameters != null && !transformationParameters.isEmpty()) {
			ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
			for (ParameterValueBean param : transformationParameters) {
				parameters.put(param.getName(), param.createParameterValue());
			}
			cell.setTransformationParameters(parameters);
		}

		cell.setId(id);

		try {
			cell.setSource(createEntities(source, sourceTypes, SchemaSpaceID.SOURCE));
			cell.setTarget(createEntities(target, targetTypes, SchemaSpaceID.TARGET));
		} catch (Throwable e) {
			reporter.error(new IOMessageImpl("Could not create cell", e));
			return null;
		}

		return cell;
	}

	private static ListMultimap<String, ? extends Entity> createEntities(
			List<NamedEntityBean> namedEntities, TypeIndex types, SchemaSpaceID schemaSpace) {
		if (namedEntities == null || namedEntities.isEmpty()) {
			return null;
		}

		ListMultimap<String, Entity> result = ArrayListMultimap.create();

		for (NamedEntityBean namedEntity : namedEntities) {
			result.put(namedEntity.getName(),
					namedEntity.getEntity().createEntity(types, schemaSpace));
		}

		return result;
	}

	/**
	 * Get the source entities
	 * 
	 * @return the source entities
	 */
	public List<NamedEntityBean> getSource() {
		return source;
	}

	/**
	 * Set the source entities
	 * 
	 * @param source the source entities to set
	 */
	public void setSource(List<NamedEntityBean> source) {
		this.source = source;
	}

	/**
	 * Get the target entities
	 * 
	 * @return the target
	 */
	public List<NamedEntityBean> getTarget() {
		return target;
	}

	/**
	 * Set the target entities
	 * 
	 * @param target the target entities to set
	 */
	public void setTarget(List<NamedEntityBean> target) {
		this.target = target;
	}

	/**
	 * Get the transformation parameters
	 * 
	 * @return the transformation parameters
	 */
	public List<ParameterValueBean> getTransformationParameters() {
		return transformationParameters;
	}

	/**
	 * Set the transformation parameters
	 * 
	 * @param transformationParameters the transformation parameters to set
	 */
	public void setTransformationParameters(List<ParameterValueBean> transformationParameters) {
		this.transformationParameters = transformationParameters;
	}

	/**
	 * Get the transformation identifier
	 * 
	 * @return the transformation identifier
	 */
	public String getTransformationIdentifier() {
		return transformationIdentifier;
	}

	/**
	 * Set the transformation identifier
	 * 
	 * @param transformationIdentifier the transformation identifier to set
	 */
	public void setTransformationIdentifier(String transformationIdentifier) {
		this.transformationIdentifier = transformationIdentifier;
	}

	/**
	 * Set the id
	 * 
	 * @param id the id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the id
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}
