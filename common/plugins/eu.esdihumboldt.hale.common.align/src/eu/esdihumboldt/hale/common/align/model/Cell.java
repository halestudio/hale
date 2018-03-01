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

package eu.esdihumboldt.hale.common.align.model;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ListMultimap;

/**
 * An alignment cell represents a mapping between two entities
 * 
 * @author Simon Templer
 */
public interface Cell {

	/**
	 * Property name for {@link Priority}.
	 */
	public static final String PROPERTY_PRIORITY = "priority";

	/**
	 * Property name for the {@link TransformationMode}.
	 */
	public static final String PROPERTY_TRANSFORMATION_MODE = "transformationMode";

	/**
	 * The default transformation mode.
	 */
	public static final TransformationMode DEFAULT_TRANSFORMATION_MODE = TransformationMode.active;

	/**
	 * Property name for disabled for.
	 */
	public static final String PROPERTY_DISABLE_FOR = "disabled_for";

	/**
	 * Property name for enabling a disabled cell again. Trying to enable a
	 * cell, which was disabled in a base alignment will fail and result in an
	 * exception.
	 */
	public static final String PROPERTY_ENABLE_FOR = "enable_for";

	/**
	 * Get the source entities. For each the name is mapped to the entity.
	 * Multiple entities may share the same name. The map may not be modified.
	 * 
	 * @return the source entities, may be <code>null</code>
	 */
	public ListMultimap<String, ? extends Entity> getSource();

	/**
	 * Get the target entities. For each the name is mapped to the entity.
	 * Multiple entities may share the same name. The map may not be modified.
	 * 
	 * @return the target entities
	 */
	public ListMultimap<String, ? extends Entity> getTarget();

	/**
	 * Get the transformation parameters that shall be applied to the
	 * transformation specified by {@link #getTransformationIdentifier()}. The
	 * map may not be modified.
	 * 
	 * @return the transformation parameters, parameter names are mapped to
	 *         parameter values, may be <code>null</code>
	 */
	public ListMultimap<String, ParameterValue> getTransformationParameters();

	/**
	 * Get the annotations of the given type.
	 * 
	 * @param type the annotation type identifier as registered in the
	 *            corresponding extension point
	 * @return the list of annotation objects or an empty list
	 */
	public List<?> getAnnotations(String type);

	/**
	 * Get the annotation types present in the cell.
	 * 
	 * @return the set of annotation type identifiers
	 */
	public Set<String> getAnnotationTypes();

	/**
	 * Add a new annotation object. The annotation object will be of the type
	 * associated with the {@link AnnotationDescriptor} registered for the given
	 * type identifier.
	 * 
	 * @param type the annotation type identifier as registered in the
	 *            corresponding extension point
	 * @return a new annotation object as created by
	 *         {@link AnnotationDescriptor#create()} or <code>null</code> if no
	 *         annotation definition with that type identifier exists
	 */
	public Object addAnnotation(String type);

	/**
	 * Add an existing annotation object.
	 * 
	 * @param type the annotation type identifier as registered in the
	 *            corresponding extension point
	 * @param annotation annotation object to add
	 */
	void addAnnotation(String type, Object annotation);

	/**
	 * Remove the given annotation object.
	 * 
	 * @param type the annotation type identifier as registered in the
	 *            corresponding extension point
	 * @param annotation the annotation object associated to the annotation type
	 *            that should be removed
	 */
	public void removeAnnotation(String type, Object annotation);

	/**
	 * Get the cell documentation. This essentially are key-value pairs similar
	 * to cell annotations but represented only by a string value.
	 * 
	 * @return documentation types mapped to string values, changes are
	 *         reflected in the cell (not thread safe)
	 */
	public ListMultimap<String, String> getDocumentation();

	/**
	 * Get the identifier for the transformation referenced by the cell.
	 * 
	 * @return the transformation identifier
	 */
	public String getTransformationIdentifier();

	/**
	 * Get the id for identifying the cell.
	 * 
	 * @return the id
	 */
	public String getId();

	/**
	 * Returns the cell IDs this cell is disabled for.
	 * 
	 * @return the cell IDs this cell is disabled for
	 */
	public Set<String> getDisabledFor();

	/**
	 * Returns the priority for the cell.
	 * 
	 * @return the {@link Priority priority}.
	 */
	public Priority getPriority();

	/**
	 * Get the cell transformation mode. Only applicable for type cells.
	 * 
	 * @return the cell transformation mode
	 */
	public TransformationMode getTransformationMode();

	/**
	 * Returns whether the cell is included from a base alignment or not.
	 * 
	 * @return whether the cell is included from a base alignment or not
	 */
	public boolean isBaseCell();
}
