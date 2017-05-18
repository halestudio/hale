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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import eu.esdihumboldt.hale.common.core.io.DOMValueUtil;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.ConstraintUtil;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintExtension;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension.ValueConstraintFactoryDescriptor;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultGroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder;
import groovy.util.GroovyTestCase;

/**
 * Base class for {@link ValueConstraintFactory} tests.
 * 
 * @author Simon Templer
 * @param <T> the constraint type
 */
public abstract class AbstractValueConstraintFactoryTest<T> extends GroovyTestCase {

	private static final QName DEF_PROPERTY_TYPE_NAME = new QName("TestPropertyType");
	private static final QName DEF_TYPE_NAME = new QName("TestType");
	private static final QName DEF_NAME = new QName("test");

	/**
	 * Test storing a constraint, restoring it and compare both using an empty
	 * type index and a default constraint definition.
	 * 
	 * @param constraint the constraint to store
	 * @throws Exception if an error occurs during storing, restoring or
	 *             comparing the constraint
	 */
	protected void storeRestoreTest(T constraint) throws Exception {
		storeRestoreTest(constraint, null, null);
	}

	/**
	 * Test storing a constraint, restoring it and compare both.
	 * 
	 * @param constraint the constraint to store
	 * @param typeIndex the type index (as context for storing/restoring),
	 *            <code>null</code> for an empty index
	 * @param constraintDef the definition the constraint is associated to (as
	 *            context for storing/restoring), for <code>null</code> the
	 *            method will try to generate a default definition based on the
	 *            constraint type
	 * @throws Exception if an error occurs during storing, restoring or
	 *             comparing the constraint
	 */
	@SuppressWarnings("unchecked")
	protected void storeRestoreTest(T constraint, Map<TypeDefinition, Value> typeIndex,
			Definition<?> constraintDef) throws Exception {
		// conversion service may be needed for Value conversions
		TestUtil.startConversionService();

		ValueConstraintFactoryDescriptor desc = ValueConstraintExtension.INSTANCE
				.getForConstraint(constraint);
		// provide defaults for null parameters
		if (typeIndex == null) {
			typeIndex = new HashMap<>();
		}
		if (constraintDef == null) {
			constraintDef = getDefaultConstraintDefinition(constraint.getClass());
		}

		@SuppressWarnings("rawtypes")
		ValueConstraintFactory factory = desc.getFactory();
		Value val = factory.store(constraint, new MapTypeReferenceBuilder(typeIndex));

		T read;
		if (val != null) {
			// to DOM
			NSDOMBuilder builder = NSDOMBuilder.newBuilder(new HashMap<String, String>());
			Element elem = DOMValueUtil.valueTag(builder, "test", val);

			// from DOM
			Value res = DOMValueUtil.fromTag(elem);

			// bimap for reverse index
			BiMap<TypeDefinition, Value> types = HashBiMap.create(typeIndex);

			read = (T) factory.restore(res, constraintDef, new MapTypeResolver(types.inverse()),
					new OsgiClassResolver());
		}
		else {
			// fall back to default constraint
			Class<?> constraintType = ConstraintUtil.getConstraintType(constraint.getClass());
			read = (T) ConstraintUtil.getDefaultConstraint(constraintType, constraintDef);
		}

		compare(constraint, read);
	}

	/**
	 * Create a default definition where the given constraint type is
	 * applicable. This is decided on whether the constraint implements certain
	 * (marker) interfaces.
	 * 
	 * @param constraintType the constraint type
	 * @return the default definition
	 */
	protected Definition<?> getDefaultConstraintDefinition(Class<?> constraintType) {
		TypeDefinition type = new DefaultTypeDefinition(DEF_TYPE_NAME);
		if (TypeConstraint.class.isAssignableFrom(constraintType)) {
			return type;
		}
		else if (PropertyConstraint.class.isAssignableFrom(constraintType)) {
			return new DefaultPropertyDefinition(DEF_NAME, type,
					new DefaultTypeDefinition(DEF_PROPERTY_TYPE_NAME));
		}
		else if (GroupPropertyConstraint.class.isAssignableFrom(constraintType)) {
			return new DefaultGroupPropertyDefinition(DEF_NAME, type, false);
		}

		// fall back to type definition
		return type;
	}

	/**
	 * Compare an original and restored constraint. Should throw an exception or
	 * error if the constraints are not equal.
	 * 
	 * @param org the original constraint
	 * @param restored the restored constraint
	 * @throws Exception if the constraints are not equal
	 */
	protected abstract void compare(T org, T restored) throws Exception;

}
