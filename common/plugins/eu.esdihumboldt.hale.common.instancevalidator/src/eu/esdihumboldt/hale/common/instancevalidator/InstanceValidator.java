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
package eu.esdihumboldt.hale.common.instancevalidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IProgressMonitor;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;
import eu.esdihumboldt.hale.common.instance.extension.validation.ConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ConstraintValidatorExtension;
import eu.esdihumboldt.hale.common.instance.extension.validation.GroupPropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.TypeConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationLocation;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReport;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReporter;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.impl.DefaultInstanceValidationMessage;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.impl.DefaultInstanceValidationReporter;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.ConstraintUtil;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.SkipValidation;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;

/**
 * Validator for instances using constraints.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidator {

	private static final ALogger log = ALoggerFactory.getLogger(InstanceValidator.class);

	// XXX Data views show only warnings, if something will be changed to errors
	// they need an update, too.

	/**
	 * Validates the given instances using all constraints that are validatable.
	 * 
	 * @param instances the instances to validate
	 * @param monitor the progress monitor
	 * @return a report of the validation
	 */
	public static InstanceValidationReport validateInstances(InstanceCollection instances,
			IProgressMonitor monitor) {
		monitor.beginTask("Instance validation",
				instances.hasSize() ? instances.size() : IProgressMonitor.UNKNOWN);

		InstanceValidationReporter reporter = new DefaultInstanceValidationReporter(false);
		reporter.setSuccess(false);
		ATransaction trans = log.begin("Instance validation");
		InstanceValidationContext context = new InstanceValidationContext();
		ResourceIterator<Instance> iterator = instances.iterator();
		try {
			while (iterator.hasNext()) {
				if (monitor.isCanceled())
					return reporter;
				Instance instance = iterator.next();
				validateInstance(instance, reporter, instance.getDefinition().getName(),
						new ArrayList<QName>(), false, instances.getReference(instance), context,
						null);
				monitor.worked(1);
			}
		} finally {
			iterator.close();
			trans.end();
		}
		validateContext(context, reporter);
		reporter.setSuccess(true);
		return reporter;
	}

	private static void validateContext(InstanceValidationContext context,
			InstanceValidationReporter reporter) {
		ConstraintValidatorExtension extension = ConstraintValidatorExtension.getInstance();

		for (Entry<Class<TypeConstraint>, TypeConstraintValidator> validator : extension
				.getTypeConstraintValidators().entrySet()) {
			validateContext(context, validator.getValue(), validator.getKey(), reporter);
		}

		for (Entry<Class<PropertyConstraint>, PropertyConstraintValidator> validator : extension
				.getPropertyConstraintValidators().entrySet()) {
			validateContext(context, validator.getValue(), validator.getKey(), reporter);
		}

		for (Entry<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator> validator : extension
				.getGroupPropertyConstraintValidators().entrySet()) {
			validateContext(context, validator.getValue(), validator.getKey(), reporter);
		}
	}

	private static void validateContext(InstanceValidationContext context,
			ConstraintValidator validator, Class<?> constraintClass,
			InstanceValidationReporter reporter) {
		try {
			validator.validateContext(context, reporter);
		} catch (ValidationException e) {
			reporter.warn(new DefaultInstanceValidationMessage(null, null,
					Collections.<QName> emptyList(), constraintClass.getSimpleName(),
					e.getMessage()));
		} catch (Exception e) {
			log.error("Error performing instance validation", e);
		}
	}

	/**
	 * Validates the given object. The created reports messages do not have an
	 * {@link InstanceReference} set.
	 * 
	 * @param object the object to validate (i. e. an instance, group or basic
	 *            value)
	 * @param childDef the child definition of the given object
	 * @return a report of the validation
	 */
	public static InstanceValidationReporter validate(Object object, ChildDefinition<?> childDef) {
		InstanceValidationReporter reporter = new DefaultInstanceValidationReporter(false);
		reporter.setSuccess(false);
		InstanceValidationContext context = new InstanceValidationContext();

		// first a special case for Choice-Flag
		// XXX a better way to do this than coding this special case?
		boolean onlyCheckExistingChildren = false;
		if (childDef.asGroup() != null) {
			GroupPropertyConstraintValidator validator = ConstraintValidatorExtension.getInstance()
					.getGroupPropertyConstraintValidators().get(ChoiceFlag.class);
			if (validator != null)
				try {
					validator.validateGroupPropertyConstraint(new Object[] { object },
							childDef.asGroup().getConstraint(ChoiceFlag.class), childDef.asGroup(),
							context);
				} catch (ValidationException vE) {
					reporter.warn(new DefaultInstanceValidationMessage(null, null,
							Collections.<QName> emptyList(), ChoiceFlag.class.getSimpleName(),
							vE.getMessage()));
				}
			onlyCheckExistingChildren = childDef.asGroup().getConstraint(ChoiceFlag.class)
					.isEnabled();
		}
		// then validate the object as if it were a lone property value
		validateChildren(new Object[] { object }, childDef, reporter, null, new ArrayList<QName>(),
				onlyCheckExistingChildren, null, context);

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Validates the given {@link Instance}. The created reports messages do not
	 * have an {@link InstanceReference} set.
	 * 
	 * @param instance the instance to validate
	 * @return a report of the validation
	 */
	public static InstanceValidationReporter validate(Instance instance) {
		InstanceValidationReporter reporter = new DefaultInstanceValidationReporter(false);
		reporter.setSuccess(false);
		InstanceValidationContext context = new InstanceValidationContext();
		validateInstance(instance, reporter, instance.getDefinition().getName(),
				new ArrayList<QName>(), false, null, context, null);
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Validates the instances value against existing
	 * {@link TypeConstraintValidator}s and calls
	 * {@link #validateGroupChildren(Group, InstanceValidationReporter, QName, List, boolean, InstanceReference, InstanceValidationContext, ChildDefinition)}
	 * .
	 * 
	 * @param instance the instance to validate
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing
	 *            children (in case of a choice) or not
	 * @param reference the instance reference
	 * @param context the instance validation context
	 * @param presentIn the child definition this instance is present in, if
	 *            applicable
	 */
	private static void validateInstance(Instance instance, InstanceValidationReporter reporter,
			QName type, List<QName> path, boolean onlyCheckExistingChildren,
			InstanceReference reference, InstanceValidationContext context,
			@Nullable ChildDefinition<?> presentIn) {
		TypeDefinition typeDef = instance.getDefinition();

		if (skipValidation(typeDef, instance)) {
			return;
		}

		for (Entry<Class<TypeConstraint>, TypeConstraintValidator> entry : ConstraintValidatorExtension
				.getInstance().getTypeConstraintValidators().entrySet())
			try {
				entry.getValue().validateTypeConstraint(instance,
						typeDef.getConstraint(entry.getKey()), context);
			} catch (ValidationException vE) {
				reporter.warn(new DefaultInstanceValidationMessage(reference, type,
						new ArrayList<QName>(path), entry.getKey().getSimpleName(),
						vE.getMessage()));
			}

		validateGroupChildren(instance, reporter, type, path, onlyCheckExistingChildren, reference,
				context, presentIn);
	}

	/**
	 * Determines if validation should be skipped for a certain property type
	 * and value.
	 * 
	 * @param typeDef the property type
	 * @param value the property value
	 * @return if validation should be skipped for the property and its children
	 */
	protected static boolean skipValidation(TypeDefinition typeDef, Object value) {
		SkipValidation skip = typeDef.getConstraint(SkipValidation.class);
		return skip.skipValidation(value);
	}

	/**
	 * Validates the given {@link Group}'s children against the {@link Group}'s
	 * definition.
	 * 
	 * @param group the group to validate
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing
	 *            children (in case of a choice) or not
	 * @param reference the instance reference
	 * @param context the instance validation context
	 * @param presentIn the child definition this group is present in, if
	 *            applicable
	 */
	private static void validateGroupChildren(Group group, InstanceValidationReporter reporter,
			QName type, List<QName> path, boolean onlyCheckExistingChildren,
			InstanceReference reference, InstanceValidationContext context,
			@Nullable ChildDefinition<?> presentIn) {
		Collection<? extends ChildDefinition<?>> childDefs = DefinitionUtil
				.getAllChildren(group.getDefinition());

		// special case handling - nillable XML element with only attributes ->
		// check only existing children (=attributes)
		if (group instanceof Instance && presentIn != null && presentIn.asProperty() != null) {
			Instance instance = (Instance) group;
			if (presentIn.asProperty().getConstraint(NillableFlag.class).isEnabled()
					&& instance.getValue() == null) {
				// test if all properties present are attributes
				boolean onlyAttributes = true;
				// but there must be an attribute present (otherwise we are not
				// sure this is XML)
				boolean foundAttribute = false;
				for (QName propertyName : group.getPropertyNames()) {
					ChildDefinition<?> childDef = presentIn.asProperty().getPropertyType()
							.getChild(propertyName);
					if (childDef == null || childDef.asProperty() == null || !childDef.asProperty()
							.getConstraint(XmlAttributeFlag.class).isEnabled()) {
						onlyAttributes = false;
						break;
					}
					else {
						foundAttribute = true;
					}
				}
				if (onlyAttributes && foundAttribute) {
					onlyCheckExistingChildren = true;
				}
			}
		}

		validateGroupChildren(group, childDefs, reporter, type, path, onlyCheckExistingChildren,
				reference, context);
	}

	/**
	 * Validates the given {@link Group}'s children against the {@link Group}'s
	 * definition.
	 * 
	 * @param group the group to validate
	 * @param childDefs the pre-determined children to validate (can be all
	 *            children or a subset)
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing
	 *            children (in case of a choice) or not
	 * @param reference the instance reference
	 * @param context the instance validation context
	 */
	private static void validateGroupChildren(Group group,
			Collection<? extends ChildDefinition<?>> childDefs, InstanceValidationReporter reporter,
			QName type, List<QName> path, boolean onlyCheckExistingChildren,
			InstanceReference reference, InstanceValidationContext context) {
		for (ChildDefinition<?> childDef : childDefs) {
			QName name = childDef.getName();
			path.add(name);
			// Cannot use getPropertyNames in case of onlyCheckExistingChildren,
			// because then I get no ChildDefinitions.
			Object[] property = group.getProperty(name);
			if (!onlyCheckExistingChildren || (property != null && property.length > 0)) {
				if (childDef.asGroup() != null) {
					validateGroup(property, childDef.asGroup(), reporter, type, path, reference,
							context);
				}
				else if (childDef.asProperty() != null) {
					validateProperty(property, childDef.asProperty(), reporter, type, path,
							reference, context);
				}
				else
					throw new IllegalStateException("Illegal child type.");
			}
			path.remove(path.size() - 1);
		}
	}

	/**
	 * Validates the given property values against their
	 * {@link PropertyDefinition}.<br>
	 * Then calls
	 * {@link #validateChildren(Object[], ChildDefinition, InstanceValidationReporter, QName, List, boolean, InstanceReference, InstanceValidationContext)}
	 * .
	 * 
	 * @param properties the array of existing properties, may be null
	 * @param propertyDef their definition
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param reference the instance reference
	 * @param context the instance validation context
	 */
	@SuppressWarnings("unchecked")
	private static void validateProperty(Object[] properties, PropertyDefinition propertyDef,
			InstanceValidationReporter reporter, QName type, List<QName> path,
			InstanceReference reference, InstanceValidationContext context) {
		ValidationLocation loc = new ValidationLocation(reference, type,
				new ArrayList<QName>(path));
		for (Entry<Class<PropertyConstraint>, PropertyConstraintValidator> entry : ConstraintValidatorExtension
				.getInstance().getPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validatePropertyConstraint(properties,
						propertyDef
								.getConstraint((Class<? extends PropertyConstraint>) ConstraintUtil
										.getConstraintType(entry.getKey())),
						propertyDef, context, loc);
			} catch (ValidationException vE) {
				reporter.warn(new DefaultInstanceValidationMessage(loc,
						entry.getKey().getSimpleName(), vE.getMessage()));
			}

		validateChildren(properties, propertyDef, reporter, type, path, false, reference, context);
	}

	/**
	 * Validates the given property values against their
	 * {@link GroupPropertyDefinition}.<br>
	 * Then calls
	 * {@link #validateChildren(Object[], ChildDefinition, InstanceValidationReporter, QName, List, boolean, InstanceReference, InstanceValidationContext)}
	 * .
	 * 
	 * @param properties the array of existing properties, may be null
	 * @param groupDef their definition
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param reference the instance reference
	 * @param context the instance validation context
	 */
	private static void validateGroup(Object[] properties, GroupPropertyDefinition groupDef,
			InstanceValidationReporter reporter, QName type, List<QName> path,
			InstanceReference reference, InstanceValidationContext context) {
		for (Entry<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator> entry : ConstraintValidatorExtension
				.getInstance().getGroupPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validateGroupPropertyConstraint(properties,
						groupDef.getConstraint(entry.getKey()), groupDef, context);
			} catch (ValidationException vE) {
				reporter.warn(new DefaultInstanceValidationMessage(reference, type,
						new ArrayList<QName>(path), entry.getKey().getSimpleName(),
						vE.getMessage()));
			}

		// In case of enabled choice flag only check existing children.
		// That only one child exists should get checked above in a validator
		// for the choice flag.
		validateChildren(properties, groupDef, reporter, type, path,
				groupDef.getConstraint(ChoiceFlag.class).isEnabled(), reference, context);
	}

	/**
	 * Validates the given property values (their values - as instances - and/or
	 * group children).
	 * 
	 * @param properties the array of existing properties, may be null
	 * @param childDef their definition
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing
	 *            children (in case of a choice) or not
	 * @param reference the instance reference
	 * @param context the instance validation context
	 */
	private static void validateChildren(Object[] properties, ChildDefinition<?> childDef,
			InstanceValidationReporter reporter, QName type, List<QName> path,
			boolean onlyCheckExistingChildren, InstanceReference reference,
			InstanceValidationContext context) {
		if (properties != null && properties.length > 0) {
			for (Object property : properties) {
				if (property instanceof Instance) {
					validateInstance((Instance) property, reporter, type, path,
							onlyCheckExistingChildren, reference, context, childDef);
				}
				else if (property instanceof Group) {
					validateGroupChildren((Group) property, reporter, type, path,
							onlyCheckExistingChildren, reference, context, childDef);
				}
				else {
					if (childDef.asGroup() != null)
						reporter.warn(new DefaultInstanceValidationMessage(reference, type,
								new ArrayList<QName>(path), "Wrong group",
								"A property is no group"));
					else if (childDef.asProperty() != null) {
						if (!skipValidation(childDef.asProperty().getPropertyType(), property)) {
							// don't skip property

							// wrap value in dummy instance for type validation
							MutableInstance instance = new DefaultInstance(
									childDef.asProperty().getPropertyType(), null);
							instance.setValue(property);
							validateInstance(instance, reporter, type, path,
									onlyCheckExistingChildren, reference, context, childDef);
						}
					}
				}
			}
		}
		else {
			// no property value

			/*
			 * Special case: No property value, but a combination of minimum
			 * cardinality greater than zero and NillableFlag is set. Then there
			 * can be sub-properties that are required.
			 * 
			 * Applicable for XML (simple) types with mandatory attributes.
			 */
			if (childDef.asProperty() != null
					&& childDef.asProperty().getConstraint(Cardinality.class).getMinOccurs() > 0
					&& childDef.asProperty().getConstraint(NillableFlag.class).isEnabled()
					&& childDef.asProperty().getPropertyType().getConstraint(HasValueFlag.class)
							.isEnabled()
					&& !childDef.asProperty().getPropertyType().getChildren().isEmpty()) {
				// collect XML attribute children
				List<ChildDefinition<?>> attributes = new ArrayList<ChildDefinition<?>>();
				for (ChildDefinition<?> child : childDef.asProperty().getPropertyType()
						.getChildren()) {
					if (child.asProperty() != null && child.asProperty()
							.getConstraint(XmlAttributeFlag.class).isEnabled()) {
						attributes.add(child);
					}
				}

				if (!attributes.isEmpty()) {
					// create an empty dummy instance
					Instance instance = new DefaultInstance(childDef.asProperty().getPropertyType(),
							null);
					validateGroupChildren(instance, attributes, reporter, type, path,
							onlyCheckExistingChildren, reference, context);
				}
			}
		}
	}
}
