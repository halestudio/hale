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

package eu.esdihumboldt.hale.common.schema.model.validate.factory.extension;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.validate.factory.ValidatorFactory;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Extension point for {@link ValidatorFactory}ies.
 * 
 * @author Simon Templer
 */
public class ValidatorFactoryExtension extends IdentifiableExtension<ValidatorFactoryDescriptor> {

	private static final ALogger log = ALoggerFactory.getLogger(ValidatorFactoryExtension.class);

	/**
	 * Extension point ID.
	 */
	public static final String ID = "eu.esdihumboldt.hale.schema.validatorfactory";

	/**
	 * The extension instance.
	 */
	public static final ValidatorFactoryExtension INSTANCE = new ValidatorFactoryExtension();

	/**
	 * Maps constraint types to descriptors.
	 */
	private final Map<Class<?>, ValidatorFactoryDescriptor> typeToDescriptor = new HashMap<>();

	/**
	 * Create a {@link Validator} from its {@link Value} representation.
	 * 
	 * @param value the value
	 * @param validatorId the validator identifier
	 * @return the validator or <code>null</code>
	 * @throws Exception if the creation fails
	 */
	public static Validator fromValue(Value value, String validatorId) throws Exception {
		ValidatorFactoryDescriptor descriptor = INSTANCE.get(validatorId);
		if (descriptor != null && descriptor.getFactory() != null) {
			return descriptor.getFactory().restore(value);
		}
		return null;
	}

	/**
	 * Create a {@link Value} representation of a {@link Validator}.
	 * 
	 * @param validator the validator
	 * @return a pair of the validator ID and the validator's value
	 *         representation, or <code>null</code>
	 * @throws Exception if creating the value representation fails
	 */
	@SuppressWarnings("unchecked")
	public static Pair<String, Value> toValue(Validator validator) throws Exception {
		ValidatorFactoryDescriptor descriptor = INSTANCE.getForValidator(validator);
		if (descriptor != null && descriptor.getFactory() != null) {
			@SuppressWarnings("rawtypes")
			ValidatorFactory factory = descriptor.getFactory();
			return new Pair<>(descriptor.getId(), factory.store(validator));
		}
		return null;
	}

	/**
	 * Default constructor.
	 */
	private ValidatorFactoryExtension() {
		super(ID);
	}

	@Override
	protected ValidatorFactoryDescriptor create(String id, IConfigurationElement conf) {
		try {
			ValidatorFactoryDescriptor desc = new ValidatorFactoryDescriptor(id, conf);
			synchronized (typeToDescriptor) {
				typeToDescriptor.put(desc.getValidatorType(), desc);
			}
			return desc;
		} catch (Exception e) {
			log.error("Could not create validator factory with id " + id, e);
			return null;
		}
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * Get the validator descriptor applicable for the given validator object.
	 * 
	 * @param validator the validator object
	 * @return the descriptor or <code>null</code> if none is available
	 */
	@SuppressWarnings("unchecked")
	public ValidatorFactoryDescriptor getForValidator(Validator validator) {
		if (validator == null)
			return null;

		Class<? extends Validator> validatorType = validator.getClass();
		while (validatorType != null) {
			ValidatorFactoryDescriptor desc = getForType(validatorType);
			if (desc != null) {
				return desc;
			}

			// check superclass
			if (validatorType.getSuperclass() != null
					&& Validator.class.isAssignableFrom(validatorType.getSuperclass())) {
				validatorType = (Class<? extends Validator>) validatorType.getSuperclass();
			}
			else {
				validatorType = null;
			}
		}

		return null;
	}

	/**
	 * Get the validator descriptor directly associated to the given validator
	 * type.
	 * 
	 * @param validatorType the constraint type
	 * @return the descriptor or <code>null</code> if none is registered for the
	 *         constraint type
	 */
	private ValidatorFactoryDescriptor getForType(Class<? extends Validator> validatorType) {
		synchronized (typeToDescriptor) {
			ValidatorFactoryDescriptor desc = typeToDescriptor.get(validatorType);
			if (desc != null)
				return desc;
		}

		// look for eventually not yet loaded definitions with the type
		for (ValidatorFactoryDescriptor desc : getElements()) {
			if (validatorType.equals(desc.getValidatorType())) {
				return desc;
			}
		}

		return null;
	}

}
