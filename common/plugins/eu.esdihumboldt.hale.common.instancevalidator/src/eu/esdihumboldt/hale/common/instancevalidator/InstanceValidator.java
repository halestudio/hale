package eu.esdihumboldt.hale.common.instancevalidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IProgressMonitor;

import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;

import de.cs3d.util.logging.ALogger;

import eu.esdihumboldt.hale.common.instance.extension.validation.ConstraintValidatorExtension;
import eu.esdihumboldt.hale.common.instance.extension.validation.GroupPropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
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
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
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
	public static InstanceValidationReport validateInstances(InstanceCollection instances, IProgressMonitor monitor) {
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
						new ArrayList<QName>(), false, instances.getReference(instance),
						context);
				monitor.worked(1);
			}
		} finally {
			iterator.close();
			trans.end();
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
		InstanceValidationContext context = new InstanceValidationContext();

		// first a special case for Choice-Flag
		// XXX a better way to do this than coding this special case?
		boolean onlyCheckExistingChildren = false;
		if (childDef.asGroup() != null) {
			GroupPropertyConstraintValidator validator = ConstraintValidatorExtension.getInstance()
					.getGroupPropertyConstraintValidators().get(ChoiceFlag.class);
			if (validator != null)
				try {
					validator.validateGroupPropertyConstraint(new Object[] {object},
							childDef.asGroup().getConstraint(ChoiceFlag.class), childDef.asGroup(), context);
				} catch (ValidationException vE) {
					reporter.warn(new DefaultInstanceValidationMessage(null, null, Collections.<QName>emptyList(), ChoiceFlag.class.getSimpleName(), vE.getMessage()));
				}
			onlyCheckExistingChildren = childDef.asGroup().getConstraint(ChoiceFlag.class).isEnabled();
		}
		// then validate the object as if it were a lone property value
		validateChildren(new Object[] {object}, childDef, reporter, null,
				new ArrayList<QName>(), onlyCheckExistingChildren, null, context);

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
		InstanceValidationContext context = new InstanceValidationContext();
		validateInstance(instance, reporter, instance.getDefinition().getName(),
				new ArrayList<QName>(), false, null, context);
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Validates the instances value against existing {@link TypeConstraintValidator}s and
	 * calls {@link #validateGroupChildren(Group, InstanceValidationReporter, QName, List, boolean, InstanceReference, InstanceValidationContext)}.
	 *
	 * @param instance the instance to validate
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing children (in case of a choice) or not
	 * @param reference the instance reference
	 * @param context the instance validation context
	 */
	private static void validateInstance(Instance instance, InstanceValidationReporter reporter,
			QName type, List<QName> path, boolean onlyCheckExistingChildren, InstanceReference reference,
			InstanceValidationContext context) {
		TypeDefinition typeDef = instance.getDefinition();
		for (Entry<Class<TypeConstraint>, TypeConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getTypeConstraintValidators().entrySet())
			try {
				entry.getValue().validateTypeConstraint(instance, typeDef.getConstraint(entry.getKey()), context);
			} catch (ValidationException vE) {
				reporter.warn(new DefaultInstanceValidationMessage(reference, type, new ArrayList<QName>(path), entry.getKey().getSimpleName(), vE.getMessage()));
			}

		validateGroupChildren(instance, reporter, type, path, onlyCheckExistingChildren,
				reference, context);
	}

	/**
	 * Validates the given {@link Group}'s children against the {@link Group}'s definition.
	 *
	 * @param group the group to validate
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing children (in case of a choice) or not
	 * @param reference the instance reference
	 * @param context the instance validation context
	 */
	private static void validateGroupChildren(Group group, InstanceValidationReporter reporter,
			QName type, List<QName> path, boolean onlyCheckExistingChildren, InstanceReference reference,
			InstanceValidationContext context) {
		Collection<? extends ChildDefinition<?>> childDefs = DefinitionUtil.getAllChildren(group.getDefinition());
		validateGroupChildren(group, childDefs, reporter, type, path,
				onlyCheckExistingChildren, reference, context);
	}

	/**
	 * Validates the given {@link Group}'s children against the {@link Group}'s definition.
	 *
	 * @param group the group to validate
	 * @param childDefs the pre-determined children to validate (can be all children or a subset)
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing children (in case of a choice) or not
	 * @param reference the instance reference
	 * @param context the instance validation context
	 */
	private static void validateGroupChildren(Group group, Collection<? extends ChildDefinition<?>> childDefs, 
			InstanceValidationReporter reporter, QName type, List<QName> path,
			boolean onlyCheckExistingChildren, InstanceReference reference,
			InstanceValidationContext context) {
		for (ChildDefinition<?> childDef : childDefs) {
			QName name = childDef.getName();
			path.add(name);
			// Cannot use getPropertyNames in case of onlyCheckExistingChildren,
			// because then I get no ChildDefinitions.
			Object[] property = group.getProperty(name);
			if (!onlyCheckExistingChildren || (property != null && property.length > 0)) {
				if (childDef.asGroup() != null) {
					validateGroup(property, childDef.asGroup(), reporter, type, path,
							reference, context);
				} else if (childDef.asProperty() != null) {
					validateProperty(property, childDef.asProperty(), reporter, type, path,
							reference, context);
				} else
					throw new IllegalStateException("Illegal child type.");
			}
			path.remove(path.size() - 1);
		}
	}

	/**
	 * Validates the given property values against their {@link PropertyDefinition}.<br>
	 * Then calls {@link #validateChildren(Object[], ChildDefinition, InstanceValidationReporter, QName, List, boolean, InstanceReference, InstanceValidationContext)}.
	 *
	 * @param properties the array of existing properties, may be null
	 * @param propertyDef their definition
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param reference the instance reference
	 * @param context the instance validation context
	 */
	private static void validateProperty(Object[] properties, PropertyDefinition propertyDef,
			InstanceValidationReporter reporter, QName type, List<QName> path,
			InstanceReference reference, InstanceValidationContext context) {
		for (Entry<Class<PropertyConstraint>, PropertyConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validatePropertyConstraint(properties,
						propertyDef.getConstraint(entry.getKey()), propertyDef, context);
			} catch (ValidationException vE) {
				reporter.warn(new DefaultInstanceValidationMessage(reference, type, new ArrayList<QName>(path), entry.getKey().getSimpleName(), vE.getMessage()));
			}

		validateChildren(properties, propertyDef, reporter, type, path, false, reference, context);
	}

	/**
	 * Validates the given property values against their {@link GroupPropertyDefinition}.<br>
	 * Then calls {@link #validateChildren(Object[], ChildDefinition, InstanceValidationReporter, QName, List, boolean, InstanceReference, InstanceValidationContext)}.
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
		for (Entry<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator> entry :
				ConstraintValidatorExtension.getInstance().getGroupPropertyConstraintValidators().entrySet())
			try {
				entry.getValue().validateGroupPropertyConstraint(properties,
						groupDef.getConstraint(entry.getKey()), groupDef, context);
			} catch (ValidationException vE) {
				reporter.warn(new DefaultInstanceValidationMessage(reference, type, new ArrayList<QName>(path), entry.getKey().getSimpleName(), vE.getMessage()));
			}

		// In case of enabled choice flag only check existing children.
		// That only one child exists should get checked above in a validator for the choice flag.
		validateChildren(properties, groupDef, reporter, type, path,
				groupDef.getConstraint(ChoiceFlag.class).isEnabled(), reference, context);
	}

	/**
	 * Validates the given property values (their values - as instances - and/or group children).
	 *
	 * @param properties the array of existing properties, may be null
	 * @param childDef their definition
	 * @param reporter the reporter to report to
	 * @param type the top level type
	 * @param path the current property path
	 * @param onlyCheckExistingChildren whether to only validate existing children (in case of a choice) or not
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
							onlyCheckExistingChildren, reference, context);
				} else if (property instanceof Group) {
					validateGroupChildren((Group) property, reporter, type, path,
							onlyCheckExistingChildren, reference, context);
				} else {
					if (childDef.asGroup() != null)
						reporter.warn(new DefaultInstanceValidationMessage(reference, type, new ArrayList<QName>(path), "Wrong group", "A property is no group"));
					else if (childDef.asProperty() != null) {
						MutableInstance instance = new DefaultInstance(childDef.asProperty().getPropertyType(), null);
						instance.setValue(property);
						validateInstance(instance, reporter, type, path,
								onlyCheckExistingChildren, reference, context);
					}
				}
			}
		}
		else {
			// no property value
			
			/*
			 *  Special case:
			 *  No property value, but a combination of minimum cardinality
			 *  greater than zero and NillableFlag is set.
			 *  Then there can be sub-properties that are required.
			 *  
			 *  Applicable for XML (simple) types with mandatory attributes.
			 */
			if (childDef.asProperty() != null
					&& childDef.asProperty().getConstraint(Cardinality.class).getMinOccurs() > 0
					&& childDef.asProperty().getConstraint(NillableFlag.class).isEnabled()
					&& childDef.asProperty().getPropertyType().getConstraint(HasValueFlag.class).isEnabled()
					&& !childDef.asProperty().getPropertyType().getChildren().isEmpty()) {
				// collect XML attribute children
				List<ChildDefinition<?>> attributes = new ArrayList<ChildDefinition<?>>();
				for (ChildDefinition<?> child : childDef.asProperty().getPropertyType().getChildren()) {
					if (child.asProperty() != null && child.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
						attributes.add(child);
					}
				}
				
				if (!attributes.isEmpty()) {
					// create an empty dummy instance
					Instance instance = new DefaultInstance(childDef.asProperty().getPropertyType(), null);
					validateGroupChildren(instance, attributes, reporter, type, path,
							onlyCheckExistingChildren, reference, context);
				}
			}
		}
	}
}
