package eu.esdihumboldt.hale.common.instancevalidator;

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
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

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

	private static void validateInstance(Instance instance, Reporter<Message> reporter, String path) {
		TypeDefinition typeDef = instance.getDefinition();
		for (Entry<Class<TypeConstraint>, TypeConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getTypeConstraintValidators().entrySet())
			try {
				entry.getValue().validateTypeConstraint(instance, typeDef.getConstraint(entry.getKey()));
			} catch (ValidationException vE) {
				reporter.warn(new MessageImpl("Instance (" + path + ") not valid: " + vE.getMessage(), null));
			}

		for (ChildDefinition<?> childDef : typeDef.getChildren()) {
			QName name = childDef.getName();
			if (childDef.asGroup() != null)
				validateGroup(instance.getProperty(name), childDef.asGroup(), reporter, path + '.' + name.getLocalPart());
			else if (childDef.asProperty() != null)
				validateProperty(instance.getProperty(name), childDef.asProperty(), reporter, path + '.' + name.getLocalPart());
			else
				throw new IllegalStateException("Illegal child type.");
		}
	}

	private static void validateGroupChildren(Group group, Reporter<Message> reporter, String path) {
		for (ChildDefinition<?> childDef : group.getDefinition().getDeclaredChildren()) {
			QName name = childDef.getName();
			if (childDef.asGroup() != null)
				validateGroup(group.getProperty(name), childDef.asGroup(), reporter, path + '.' + name.getLocalPart());
			else if (childDef.asProperty() != null)
				validateProperty(group.getProperty(name), childDef.asProperty(), reporter, path + '.' + name.getLocalPart());
			else
				throw new IllegalStateException("Illegal child type.");
		}
	}

	private static void validateProperty(Object[] properties, PropertyDefinition propertyDef, Reporter<Message> reporter, String path) {
		for (Entry<Class<PropertyConstraint>, PropertyConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validatePropertyConstraint(properties,
						propertyDef.getConstraint(entry.getKey()), propertyDef);
			} catch (ValidationException vE) {
				reporter.warn(new MessageImpl("Properties (" + path + ") not valid: " + vE.getMessage(), null));
			}

		validateChildren(properties, reporter, path);	
	}

	private static void validateGroup(Object[] properties, GroupPropertyDefinition groupDef, Reporter<Message> reporter, String path) {
		for (Entry<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getGroupPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validateGroupPropertyConstraint(properties,
						groupDef.getConstraint(entry.getKey()), groupDef);
			} catch (ValidationException vE) {
				reporter.warn(new MessageImpl("Group properties (" + path + ") not valid: " + vE.getMessage(), null));
			}

		validateChildren(properties, reporter, path);	
	}

	private static void validateChildren(Object[] properties, Reporter<Message> reporter, String path) {
		if (properties != null) {
			for (Object property : properties) {
				if (property instanceof Instance)
					validateInstance((Instance) property, reporter, path);
				else if (property instanceof Group)
					validateGroupChildren((Group) property, reporter, path);
			}
		}
	}
}
