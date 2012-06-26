package eu.esdihumboldt.hale.common.instancevalidator;

import java.util.Collection;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IProgressMonitor;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.Reporter;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;
import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;
import eu.esdihumboldt.hale.common.instance.extension.validation.ConstraintValidatorExtension;
import eu.esdihumboldt.hale.common.instance.extension.validation.GroupPropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.TypeConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
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
	/**
	 * Validates the given instances using all constraints that are validatable.
	 *
	 * @param instances the instances to validate
	 * @param monitor the progress monitor
	 * @return a report of the validation
	 */
	public static Report<Message> validateInstances(InstanceCollection instances, IProgressMonitor monitor) {
		Reporter<Message> reporter = new DefaultReporter<Message>("Instance validation", Message.class, true);
		reporter.setSuccess(false);
		ResourceIterator<Instance> iterator = instances.iterator();
		try {
			while (iterator.hasNext()) {
				if (monitor.isCanceled())
					return reporter;
				Instance instance = iterator.next();
				validateInstance(instance, reporter, instance.getDefinition().getDisplayName());
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
	 *
	 * @param object the object to validate (i. e. an instance, group or basic value)
	 * @param childDef the child definition of the given object
	 * @return a report of the validation
	 */
	public static Report<Message> validate(Object object, ChildDefinition<?> childDef) {
		Reporter<Message> reporter = new DefaultReporter<Message>("Instance validation", Message.class, true);
		reporter.setSuccess(false);

		String path = childDef.getName().getLocalPart();
		// first a special case for Choice-Flag
		// XXX a better way to do this than coding this special case?
		if (childDef.asGroup() != null) {
			GroupPropertyConstraintValidator validator = ConstraintValidatorExtension.getInstance()
					.getGroupPropertyConstraintValidators().get(ChoiceFlag.class);
			if (validator != null)
				try {
					validator.validateGroupPropertyConstraint(new Object[] {object},
							childDef.asGroup().getConstraint(ChoiceFlag.class), childDef.asGroup());
				} catch (ValidationException vE) {
					reporter.warn(new MessageImpl("Group properties (" + path + ") not valid: " + vE.getMessage(), null));
				}
		}
		// then validate the object as if it were a lone property value
		validateChildren(new Object[] {object}, childDef, reporter, path);

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Validates the given {@link Instance}.
	 *
	 * @param instance the instance to validate
	 * @return a report of the validation
	 */
	public static Report<Message> validate(Instance instance) {
		Reporter<Message> reporter = new DefaultReporter<Message>("Instance validation", Message.class, true);
		reporter.setSuccess(false);
		validateInstance(instance, reporter, instance.getDefinition().getDisplayName());
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Validates the instances value against existing {@link TypeConstraintValidator}s and
	 * calls {@link #validateGroupChildren(Group, Reporter, String)}.
	 *
	 * @param instance the instance to validate
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 */
	private static void validateInstance(Instance instance, Reporter<Message> reporter, String path) {
		TypeDefinition typeDef = instance.getDefinition();
		for (Entry<Class<TypeConstraint>, TypeConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getTypeConstraintValidators().entrySet())
			try {
				entry.getValue().validateTypeConstraint(instance, typeDef.getConstraint(entry.getKey()));
			} catch (ValidationException vE) {
				reporter.warn(new MessageImpl("Instance (" + path + ") not valid: " + vE.getMessage(), null));
			}

		validateGroupChildren(instance, reporter, path);
	}

	/**
	 * Validates the given {@link Group}'s children against the {@link Group}'s definition.
	 *
	 * @param group the group to validate
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 */
	private static void validateGroupChildren(Group group, Reporter<Message> reporter, String path) {
		Collection<? extends ChildDefinition<?>> childDefs;
		if (group.getDefinition() instanceof TypeDefinition)
			childDefs = ((TypeDefinition) group.getDefinition()).getChildren();
		else
			childDefs = group.getDefinition().getDeclaredChildren();
		for (ChildDefinition<?> childDef : childDefs) {
			QName name = childDef.getName();
			if (childDef.asGroup() != null)
				validateGroup(group.getProperty(name), childDef.asGroup(), reporter, path + '.' + name.getLocalPart());
			else if (childDef.asProperty() != null)
				validateProperty(group.getProperty(name), childDef.asProperty(), reporter, path + '.' + name.getLocalPart());
			else
				throw new IllegalStateException("Illegal child type.");
		}
	}

	/**
	 * Validates the given property values against their {@link PropertyDefinition}.<br>
	 * Then calls {@link #validateChildren(Object[], ChildDefinition, Reporter, String)}.
	 *
	 * @param properties the array of existing properties, may be null
	 * @param propertyDef their definition
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 */
	private static void validateProperty(Object[] properties, PropertyDefinition propertyDef,
			Reporter<Message> reporter, String path) {
		for (Entry<Class<PropertyConstraint>, PropertyConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validatePropertyConstraint(properties,
						propertyDef.getConstraint(entry.getKey()), propertyDef);
			} catch (ValidationException vE) {
				reporter.warn(new MessageImpl("Properties (" + path + ") not valid: " + vE.getMessage(), null));
			}

		validateChildren(properties, propertyDef, reporter, path);
	}

	/**
	 * Validates the given property values against their {@link GroupPropertyDefinition}.<br>
	 * Then calls {@link #validateChildren(Object[], ChildDefinition, Reporter, String)}.
	 *
	 * @param properties the array of existing properties, may be null
	 * @param groupDef their definition
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 */
	private static void validateGroup(Object[] properties, GroupPropertyDefinition groupDef,
			Reporter<Message> reporter, String path) {
		for (Entry<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getGroupPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validateGroupPropertyConstraint(properties,
						groupDef.getConstraint(entry.getKey()), groupDef);
			} catch (ValidationException vE) {
				reporter.warn(new MessageImpl("Group properties (" + path + ") not valid: " + vE.getMessage(), null));
			}

		validateChildren(properties, groupDef, reporter, path);
	}

	/**
	 * Validates the given property values (their values - as instances - and/or group children).
	 *
	 * @param properties the array of existing properties, may be null
	 * @param childDef their definition
	 * @param reporter the reporter to report to
	 * @param path the current property path
	 */
	private static void validateChildren(Object[] properties, ChildDefinition<?> childDef, Reporter<Message> reporter, String path) {
		if (properties != null) {
			for (Object property : properties) {
				if (property instanceof Instance)
					validateInstance((Instance) property, reporter, path);
				else if (property instanceof Group)
					validateGroupChildren((Group) property, reporter, path);
				else {
					if (childDef.asGroup() != null) {
						// XXX WHAT NOW?
					} else if (childDef.asProperty() != null) {
						MutableInstance instance = new DefaultInstance(childDef.asProperty().getPropertyType(), null);
						instance.setValue(property);
						validateInstance(instance, reporter, path);
					}
				}
			}
		}
	}
}
