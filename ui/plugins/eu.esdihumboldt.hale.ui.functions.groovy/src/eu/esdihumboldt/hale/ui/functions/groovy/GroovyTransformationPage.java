/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.groovy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.GroovyGreedyTransformation;
import eu.esdihumboldt.cst.functions.groovy.GroovyTransformation;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionsService;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.CellLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.DefaultTransformationReporter;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.HelperFunctionsCompletions;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.InstanceBuilderCompletions;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.PageFunctions;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.PageHelp;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.TypeStructureTray;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.TypeStructureTray.TypeProvider;
import eu.esdihumboldt.hale.ui.scripting.groovy.InstanceTestValues;
import eu.esdihumboldt.hale.ui.scripting.groovy.TestValues;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.groovy.ast.GroovyAST;
import eu.esdihumboldt.hale.ui.util.source.CompilingSourceViewer;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import groovy.lang.Script;

/**
 * Configuration page for the Groovy property transformation script.
 * 
 * @author Simon Templer
 */
public class GroovyTransformationPage
		extends GroovyScriptPage<AbstractGenericFunctionWizard<?, ?>> {

	private final TestValues testValues;

	/**
	 * Default constructor.
	 */
	public GroovyTransformationPage() {
		super();
		setTitle("Property transformation script");
		setDescription("Specify a Groovy script to determine the target property value");

		testValues = new InstanceTestValues();
	}

	@Override
	protected SourceViewerConfiguration createConfiguration() {
		InstanceBuilderCompletions targetCompletions = new InstanceBuilderCompletions(
				definitionImages) {

			@Override
			protected TypeDefinition getTargetType() {
				Property targetProperty = (Property) CellUtil
						.getFirstEntity(getWizard().getUnfinishedCell().getTarget());
				if (targetProperty != null) {
					return targetProperty.getDefinition().getDefinition().getPropertyType();
				}
				return null;
			}
		};

		HelperFunctionsCompletions functionCompletions = new HelperFunctionsCompletions(
				HaleUI.getServiceProvider().getService(HelperFunctionsService.class));

		return new SimpleGroovySourceViewerConfiguration(colorManager,
				ImmutableList.of(BINDING_BUILDER, BINDING_TARGET, BINDING_SOURCE_TYPES,
						BINDING_TARGET_TYPE, BINDING_CELL, BINDING_LOG, BINDING_CELL_CONTEXT,
						BINDING_FUNCTION_CONTEXT, BINDING_TRANSFORMATION_CONTEXT,
						BINDING_HELPER_FUNCTIONS),
				ImmutableList.of(targetCompletions, functionCompletions));
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		// variables and settings may have changed
		forceValidation();
	}

	@Override
	protected boolean validate(String document) {
		super.validate(document);

		List<PropertyValue> values = new ArrayList<PropertyValue>();
		for (EntityDefinition var : getVariables()) {
			if (var instanceof PropertyEntityDefinition) {
				PropertyEntityDefinition property = (PropertyEntityDefinition) var;
				values.add(new PropertyValueImpl(testValues.get(property), property));
			}
		}

		Property targetProperty = (Property) CellUtil
				.getFirstEntity(getWizard().getUnfinishedCell().getTarget());
		if (targetProperty == null) {
			// not yet selected (NewRelationWizard)
			return false;
		}

		InstanceBuilder builder = GroovyTransformation
				.createBuilder(targetProperty.getDefinition());

		Cell cell = getWizard().getUnfinishedCell();

		boolean useInstanceValues = CellUtil.getOptionalParameter(cell,
				GroovyTransformation.PARAM_INSTANCE_VARIABLES, Value.of(false)).as(Boolean.class);

		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		GroovyService gs = HaleUI.getServiceProvider().getService(GroovyService.class);
		Script script = null;
		try {
			Collection<? extends Cell> typeCells = as.getAlignment().getTypeCells(cell);
			// select one matching type cell, the script has to run for all
			// matching cells
			// if there is no matching cell it may produce a npe, which is okay
			Cell typeCell = null;
			if (!typeCells.isEmpty()) {
				typeCell = typeCells.iterator().next();
			}
			CellLog log = new CellLog(new DefaultTransformationReporter("dummy", false), cell);
			ExecutionContext context = new DummyExecutionContext(HaleUI.getServiceProvider());
			groovy.lang.Binding binding;
			if (cell.getTransformationIdentifier().equals(GroovyGreedyTransformation.ID)) {
				binding = GroovyGreedyTransformation.createGroovyBinding(values, null, cell,
						typeCell, builder, useInstanceValues, log, context,
						targetProperty.getDefinition().getDefinition().getPropertyType());
			}
			else {
				binding = GroovyTransformation.createGroovyBinding(values, null, cell, typeCell,
						builder, useInstanceValues, log, context,
						targetProperty.getDefinition().getDefinition().getPropertyType());
			}
			script = gs.parseScript(document, binding);

			GroovyTransformation.evaluate(script, builder,
					targetProperty.getDefinition().getDefinition().getPropertyType(), gs, log);
		} catch (NoResultException e) {
			// continue
		} catch (final Exception e) {
			return handleValidationResult(script, e);
		}

		return handleValidationResult(script, null);
	}

	/**
	 * Get the list of source entities configured as variables.
	 * 
	 * @return the source entities configured as variables
	 */
	protected List<EntityDefinition> getVariables() {
		ListMultimap<String, ? extends Entity> source = getWizard().getUnfinishedCell().getSource();
		if (source != null) {
			List<EntityDefinition> result = new ArrayList<>();
			for (Entity entity : source.get(GroovyConstants.ENTITY_VARIABLE)) {
				result.add(entity.getDefinition());
			}
			return result;
		}
		return Collections.emptyList();
	}

	/**
	 * Get the variable name for an entity definition.
	 * 
	 * @param variable the variable
	 * @return the name to use as variable name
	 */
	protected String getVariableName(EntityDefinition variable) {
		if (variable.getPropertyPath() != null && !variable.getPropertyPath().isEmpty()) {
			List<String> names = new ArrayList<String>();
			for (ChildContext context : variable.getPropertyPath()) {
				names.add(context.getChild().getName().getLocalPart());
			}
			String longName = Joiner.on('_').join(names);
			return longName;
		}
		else
			return variable.getDefinition().getDisplayName();
	}

	@Override
	protected void addActions(ToolBar toolbar, CompilingSourceViewer<GroovyAST> viewer) {
		super.addActions(toolbar, viewer);

		PageHelp.createToolItem(toolbar, this);

		TypeStructureTray.createToolItem(toolbar, this, SchemaSpaceID.SOURCE, new TypeProvider() {

			@Override
			public Collection<? extends TypeDefinition> getTypes() {
				// create a dummy type with the variables as children
				DefaultTypeDefinition dummy = new DefaultTypeDefinition(
						TypeStructureTray.VARIABLES_TYPE_NAME);

				Cell cell = getWizard().getUnfinishedCell();

				boolean useInstanceValues = CellUtil.getOptionalParameter(cell,
						GroovyTransformation.PARAM_INSTANCE_VARIABLES, Value.of(false))
						.as(Boolean.class);

				for (EntityDefinition variable : getVariables()) {
					if (variable.getDefinition() instanceof PropertyDefinition) {
						PropertyDefinition prop = (PropertyDefinition) variable.getDefinition();

						TypeDefinition propertyType;
						if (useInstanceValues) {
							// use instance type
							propertyType = prop.getPropertyType();
						}
						else {
							// use dummy type with only the
							// binding/HasValueFlag copied
							DefaultTypeDefinition crippledType = new DefaultTypeDefinition(
									prop.getPropertyType().getName());
							crippledType.setConstraint(
									prop.getPropertyType().getConstraint(Binding.class));
							crippledType.setConstraint(
									prop.getPropertyType().getConstraint(HasValueFlag.class));
							propertyType = crippledType;
						}

						DefaultPropertyDefinition dummyProp = new DefaultPropertyDefinition(
								new QName(getVariableName(variable)), dummy, propertyType);

						// for greedy transformation the property can occur any
						// number of times
						if (cell.getTransformationIdentifier()
								.equals(GroovyGreedyTransformation.ID))
							dummyProp.setConstraint(Cardinality.CC_ANY_NUMBER);
					}
				}

				return Collections.singleton(dummy);
			}
		});

		TypeStructureTray.createToolItem(toolbar, this, SchemaSpaceID.TARGET, new TypeProvider() {

			@Override
			public Collection<? extends TypeDefinition> getTypes() {
				Property targetProperty = (Property) CellUtil
						.getFirstEntity(getWizard().getUnfinishedCell().getTarget());
				if (targetProperty != null) {
					return Collections.singleton(
							targetProperty.getDefinition().getDefinition().getPropertyType());
				}
				return Collections.emptyList();
			}
		});

		PageFunctions.createToolItem(toolbar, this);
	}

	@Override
	public String getHelpContext() {
		return "eu.esdihumboldt.cst.functions.groovy.script";
	}

}
