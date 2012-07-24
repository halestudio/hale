package eu.esdihumboldt.hale.common.instancevalidator;

import java.util.Collection;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IProgressMonitor;

import eu.esdihumboldt.hale.common.instance.extension.validation.ConstraintValidatorExtension;
import eu.esdihumboldt.hale.common.instance.extension.validation.GroupPropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.TypeConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationReport;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationReporter;
import eu.esdihumboldt.hale.common.instancevalidator.report.impl.DefaultInstanceValidationMessage;
import eu.esdihumboldt.hale.common.instancevalidator.report.impl.DefaultInstanceValidationReporter;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;

/**
 * Validator for instances using constraints.
 *
 * @author Kai Schwierczek
 */
public class InstanceValidator {
	// XXX Data views show only warnings, if something will be changed to errors
	// they need an update, too.

	/**
	 * Validates the given instances using all constraints that are validatable.
	 *
	 * @param instances the instances to validate
	 * @param monitor the progress monitor
	 * @return a report of the validation
	 */
	public static InstanceValidationReport validateInstances(InstanceCollection instances, IProgressMonitor monitor) {
		monitor.beginTask("Instance validation",
				instances.hasSize() ? instances.size() : IProgressMonitor.UNKNOWN);

		InstanceValidationReporter reporter = new DefaultInstanceValidationReporter(true);
		reporter.setSuccess(false);
		ResourceIterator<Instance> iterator = instances.iterator();
		try {
			while (iterator.hasNext()) {
				if (monitor.isCanceled())
					return reporter;
				Instance instance = iterator.next();
				validateInstance(instance, reporter, instance.getDefinition().getDisplayName(), false, instances.getReference(instance));
				monitor.worked(1);
			}
		} finally {
			iterator.close();
		}
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Validates the given object.
	 * The created reports messages do not have an {@link InstanceReference} set.
	 *
	 * @param object the object to validate (i. e. an instance, group or basic value)
	 * @param childDef the child definition of the given object
	 * @return a report of the validation
	 */
	public static InstanceValidationReporter validate(Object object, ChildDefinition<?> childDef) {
		InstanceValidationReporter reporter = new DefaultInstanceValidationReporter(false);
		reporter.setSuccess(false);

		String path = childDef.getName().getLocalPart();
		// first a special case for Choice-Flag
		// XXX a better way to do this than coding this special case?
		boolean onlyCheckExistingChildren = false;
		if (childDef.asGroup() != null) {
			GroupPropertyConstraintValidator validator = ConstraintValidatorExtension.getInstance()
					.getGroupPropertyConstraintValidators().get(ChoiceFlag.class);
			if (validator != null)
				try {
					validator.validateGroupPropertyConstraint(new Object[] {object},
							childDef.asGroup().getConstraint(ChoiceFlag.class), childDef.asGroup());
				} catch (ValidationException vE) {
					reporter.warn(new DefaultInstanceValidationMessage(null, "Group properties (" + path + ") not valid: " + vE.getMessage()));
				}
			onlyCheckExistingChildren = childDef.asGroup().getConstraint(ChoiceFlag.class).isEnabled();
		}
		// then validate the object as if it were a lone property value
		validateChildren(new Object[] {object}, childDef, reporter, path, onlyCheckExistingChildren, null);

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Validates the given {@link Instance}.
	 * The created reports messages do not have an {@link InstanceReference} set.
	 *
	 * @param instance the instance to validate
	 * @return a report of the validation
	 */
	public static InstanceValidationReporter validate(Instance instance) {
		InstanceValidationReporter reporter = new DefaultInstanceValidationReporter(false);
		reporter.setSuccess(false);
		validateInstance(instance, reporter, instance.getDefinition().getDisplayName(), false, null);
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Validates the instances value against existing {@link TypeConstraintValidator}s and
	 * calls {@link #validateGroupChildren(Group, InstanceValidationReporter, String, boolean, InstanceReference)}.
	 *
	 * @param instance the instance to validate
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing children (in case of a choice) or not
	 * @param reference the instance reference
	 */
	private static void validateInstance(Instance instance, InstanceValidationReporter reporter,
			String path, boolean onlyCheckExistingChildren, InstanceReference reference) {
		TypeDefinition typeDef = instance.getDefinition();
		for (Entry<Class<TypeConstraint>, TypeConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getTypeConstraintValidators().entrySet())
			try {
				entry.getValue().validateTypeConstraint(instance, typeDef.getConstraint(entry.getKey()));
			} catch (ValidationException vE) {
				reporter.warn(new DefaultInstanceValidationMessage(reference, "Instance (" + path + ") not valid: " + vE.getMessage()));
			}

		validateGroupChildren(instance, reporter, path, onlyCheckExistingChildren, reference);
	}

	/**
	 * Validates the given {@link Group}'s children against the {@link Group}'s definition.
	 *
	 * @param group the group to validate
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing children (in case of a choice) or not
	 * @param reference the instance reference
	 */
	private static void validateGroupChildren(Group group, InstanceValidationReporter reporter,
			String path, boolean onlyCheckExistingChildren, InstanceReference reference) {
		Collection<? extends ChildDefinition<?>> childDefs;
		if (group.getDefinition() instanceof TypeDefinition)
			childDefs = ((TypeDefinition) group.getDefinition()).getChildren();
		else
			childDefs = group.getDefinition().getDeclaredChildren();
		for (ChildDefinition<?> childDef : childDefs) {
			QName name = childDef.getName();
			// Cannot use getPropertyNames in case of onlyCheckExistingChildren,
			// because then I got no ChildDefinitions.
			if (!onlyCheckExistingChildren || group.getProperty(name) != null) {
				if (childDef.asGroup() != null)
					validateGroup(group.getProperty(name), childDef.asGroup(), reporter, path + '.' + name.getLocalPart(), reference);
				else if (childDef.asProperty() != null)
					validateProperty(group.getProperty(name), childDef.asProperty(), reporter, path + '.' + name.getLocalPart(), reference);
				else
					throw new IllegalStateException("Illegal child type.");
			}
		}
	}

	/**
	 * Validates the given property values against their {@link PropertyDefinition}.<br>
	 * Then calls {@link #validateChildren(Object[], ChildDefinition, InstanceValidationReporter, String, boolean, InstanceReference)}.
	 *
	 * @param properties the array of existing properties, may be null
	 * @param propertyDef their definition
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 * @param reference the instance reference
	 */
	private static void validateProperty(Object[] properties, PropertyDefinition propertyDef,
			InstanceValidationReporter reporter, String path, InstanceReference reference) {
		for (Entry<Class<PropertyConstraint>, PropertyConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validatePropertyConstraint(properties,
						propertyDef.getConstraint(entry.getKey()), propertyDef);
			} catch (ValidationException vE) {
				reporter.warn(new DefaultInstanceValidationMessage(reference, "Properties (" + path + ") not valid: " + vE.getMessage()));
			}

		validateChildren(properties, propertyDef, reporter, path, false, reference);
	}

	/**
	 * Validates the given property values against their {@link GroupPropertyDefinition}.<br>
	 * Then calls {@link #validateChildren(Object[], ChildDefinition, InstanceValidationReporter, String, boolean, InstanceReference)}.
	 *
	 * @param properties the array of existing properties, may be null
	 * @param groupDef their definition
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 * @param reference the instance reference
	 */
	private static void validateGroup(Object[] properties, GroupPropertyDefinition groupDef,
			InstanceValidationReporter reporter, String path, InstanceReference reference) {
		for (Entry<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getGroupPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validateGroupPropertyConstraint(properties,
						groupDef.getConstraint(entry.getKey()), groupDef);
			} catch (ValidationException vE) {
				reporter.warn(new DefaultInstanceValidationMessage(reference, "Group properties (" + path + ") not valid: " + vE.getMessage()));
			}

		// In case of enabled choice flag only check existing children.
		// That only one child exists should get checked above in a validator for the choice flag.
		validateChildren(properties, groupDef, reporter, path,
				groupDef.getConstraint(ChoiceFlag.class).isEnabled(), reference);
	}

	/**
	 * Validates the given property values (their values - as instances - and/or group children).
	 *
	 * @param properties the array of existing properties, may be null
	 * @param childDef their definition
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing children (in case of a choice) or not
	 * @param reference the instance reference
	 */
	private static void validateChildren(Object[] properties, ChildDefinition<?> childDef,
			InstanceValidationReporter reporter, String path, boolean onlyCheckExistingChildren, InstanceReference reference) {
		if (properties != null) {
			for (Object property : properties) {
				if (property instanceof Instance) {
					validateInstance((Instance) property, reporter, path,
							onlyCheckExistingChildren, reference);
				} else if (property instanceof Group) {
					validateGroupChildren((Group) property, reporter, path,
							onlyCheckExistingChildren, reference);
				} else {
					if (childDef.asGroup() != null)
						reporter.warn(new DefaultInstanceValidationMessage(reference, "Group properties (" + path + ") not valid: a property is no group."));
					else if (childDef.asProperty() != null) {
						MutableInstance instance = new DefaultInstance(childDef.asProperty().getPropertyType(), null);
						instance.setValue(property);
						validateInstance(instance, reporter, path, onlyCheckExistingChildren, reference);
					}
				}
			}
		}
	}
}
