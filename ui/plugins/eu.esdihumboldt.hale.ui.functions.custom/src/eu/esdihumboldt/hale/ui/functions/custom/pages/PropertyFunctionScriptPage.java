/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.custom.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.custom.CustomPropertyFunctionType;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionEntity;
import eu.esdihumboldt.hale.common.align.custom.groovy.CustomGroovyTransformation;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.ui.functions.custom.CustomPropertyFunctionWizard;
import eu.esdihumboldt.hale.ui.functions.groovy.GroovyScriptPage;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.InstanceBuilderCompletions;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.PageHelp;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.TypeStructureTray;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.TypeStructureTray.TypeProvider;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.groovy.ast.GroovyAST;
import eu.esdihumboldt.hale.ui.util.source.CompilingSourceViewer;

/**
 * Configuration page for custom property function script.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class PropertyFunctionScriptPage extends GroovyScriptPage<CustomPropertyFunctionWizard>
		implements CustomFunctionWizardPage {

//	private final TestValues testValues;

	/**
	 * Default constructor.
	 */
	public PropertyFunctionScriptPage() {
		super();
		setTitle("Custom function script");
		setDescription("Specify a Groovy script to determine the output value or structure");

//		testValues = new InstanceTestValues();
	}

	@Override
	protected SourceViewerConfiguration createConfiguration() {
		InstanceBuilderCompletions targetCompletions = new InstanceBuilderCompletions(
				definitionImages) {

			@Override
			protected TypeDefinition getTargetType() {
				DefaultCustomPropertyFunctionEntity targetDef = getWizard().getUnfinishedFunction()
						.getTarget();

				if (targetDef.getBindingType() != null) {
					return targetDef.getBindingType();
				}
				else {
					// simple or not specified type
					return null;
				}
			}
		};

		return new SimpleGroovySourceViewerConfiguration(colorManager,
				ImmutableList.of(BINDING_BUILDER, BINDING_TARGET, BINDING_SOURCE_TYPES,
						BINDING_TARGET_TYPE, BINDING_CELL, BINDING_LOG, BINDING_CELL_CONTEXT,
						BINDING_FUNCTION_CONTEXT, BINDING_TRANSFORMATION_CONTEXT,
						CustomGroovyTransformation.BINDING_PARAMS),
				ImmutableList.of(targetCompletions));
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

		if (getWizard().getUnfinishedFunction() == null)
			return false;

		@SuppressWarnings("unused")
		List<DefaultCustomPropertyFunctionEntity> sources = getWizard().getUnfinishedFunction()
				.getSources();

		// TODO create test values and do validation?

//		List<PropertyValue> values = new ArrayList<PropertyValue>();
//		for (EntityDefinition var : getVariables()) {
//			if (var instanceof PropertyEntityDefinition) {
//				PropertyEntityDefinition property = (PropertyEntityDefinition) var;
//				values.add(new PropertyValueImpl(testValues.get(property), property));
//			}
//		}
//
//		Property targetProperty = (Property) CellUtil.getFirstEntity(getWizard()
//				.getUnfinishedCell().getTarget());
//		if (targetProperty == null) {
//			// not yet selected (NewRelationWizard)
//			return false;
//		}
//
//		InstanceBuilder builder = GroovyTransformation
//				.createBuilder(targetProperty.getDefinition());
//
//		Cell cell = getWizard().getUnfinishedCell();
//
//		boolean useInstanceValues = CellUtil.getOptionalParameter(cell,
//				GroovyTransformation.PARAM_INSTANCE_VARIABLES, Value.of(false)).as(Boolean.class);
//
//		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
//				AlignmentService.class);
//		GroovyService gs = HaleUI.getServiceProvider().getService(GroovyService.class);
//		Script script = null;
//		try {
//			Collection<? extends Cell> typeCells = as.getAlignment().getTypeCells(cell);
//			// select one matching type cell, the script has to run for all
//			// matching cells
//			// if there is no matching cell it may produce a npe, which is okay
//			Cell typeCell = null;
//			if (!typeCells.isEmpty()) {
//				typeCell = typeCells.iterator().next();
//			}
//			CellLog log = new CellLog(new DefaultTransformationReporter("dummy", false), cell);
//			ExecutionContext context = new DummyExecutionContext(HaleUI.getServiceProvider());
//			groovy.lang.Binding binding;
//			if (cell.getTransformationIdentifier().equals(GroovyGreedyTransformation.ID)) {
//				binding = GroovyGreedyTransformation.createGroovyBinding(values, null, cell,
//						typeCell, builder, useInstanceValues, log, context);
//			}
//			else {
//				binding = GroovyTransformation.createGroovyBinding(values, null, cell, typeCell,
//						builder, useInstanceValues, log, context);
//			}
//			script = gs.parseScript(document, binding);
//
//			GroovyTransformation.evaluate(script, builder, targetProperty.getDefinition()
//					.getDefinition().getPropertyType(), gs);
//		} catch (final Exception e) {
//			return handleValidationResult(script, e);
//		}
//
//		return handleValidationResult(script, null);

		return true;
	}

	/**
	 * Get the list of source entities configured as variables.
	 * 
	 * @return the source entities configured as variables
	 */
	protected List<EntityDefinition> getVariables() {
		List<DefaultCustomPropertyFunctionEntity> sources = getWizard().getUnfinishedFunction()
				.getSources();
		if (sources != null) {
			List<EntityDefinition> result = new ArrayList<>();
			for (DefaultCustomPropertyFunctionEntity source : sources) {
				result.add(createDummyEntity(source, SchemaSpaceID.SOURCE));
			}
			return result;
		}
		return Collections.emptyList();
	}

	private EntityDefinition createDummyEntity(DefaultCustomPropertyFunctionEntity entity,
			SchemaSpaceID ssid) {
		DefaultTypeDefinition parent = new DefaultTypeDefinition(new QName("dummy"));
		DefaultPropertyDefinition property = new DefaultPropertyDefinition(
				new QName(entity.getName()), parent, createDummyType(entity));
		property.setConstraint(
				Cardinality.get(entity.getMinOccurrence(), entity.getMaxOccurrence()));
		return new PropertyEntityDefinition(parent,
				Collections.singletonList(new ChildContext(property)), ssid, null);
	}

	private TypeDefinition createDummyType(DefaultCustomPropertyFunctionEntity entity) {
		if (entity.getBindingType() != null) {
			return entity.getBindingType();
		}
		else if (entity.getBindingClass() != null) {
			return createBindingDummy(entity.getBindingClass());
		}
		else {
			return createBindingDummy(Object.class);
		}
	}

	private TypeDefinition createBindingDummy(Class<?> clazz) {
		DefaultTypeDefinition type = new DefaultTypeDefinition(new QName(clazz.getName()));
		type.setConstraint(Binding.get(clazz));
		type.setConstraint(HasValueFlag.ENABLED);
		return type;
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

				DefaultCustomPropertyFunction cf = getWizard().getUnfinishedFunction();

				int index = 0;
				for (EntityDefinition variable : getVariables()) {
					DefaultCustomPropertyFunctionEntity source = cf.getSources().get(index);

					if (variable.getDefinition() instanceof PropertyDefinition) {
						PropertyDefinition prop = (PropertyDefinition) variable.getDefinition();

						TypeDefinition propertyType;
						boolean useInstanceValue = CustomGroovyTransformation
								.useInstanceVariableForSource(source);
						if (useInstanceValue) {
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
								new QName(source.getName()), dummy, propertyType);

						// for greedy transformation the property can occur any
						// number of times
						if (source.isEager())
							dummyProp.setConstraint(Cardinality.CC_ANY_NUMBER);
					}

					index++;
				}

				return Collections.singleton(dummy);
			}
		});

		TypeStructureTray.createToolItem(toolbar, this, SchemaSpaceID.TARGET, new TypeProvider() {

			@Override
			public Collection<? extends TypeDefinition> getTypes() {
				DefaultCustomPropertyFunctionEntity target = getWizard().getUnfinishedFunction()
						.getTarget();
				if (target != null) {
					return Collections.singleton(createDummyType(target));
				}
				return Collections.emptyList();
			}
		});
	}

	@Override
	public void apply() {
		DefaultCustomPropertyFunction cf = getWizard().getResultFunction();
		if (cf == null)
			return;

		cf.setFunctionType(CustomPropertyFunctionType.GROOVY);

		List<ParameterValue> script = getConfiguration().get(PARAMETER_SCRIPT);
		if (script != null && !script.isEmpty()) {
			cf.setFunctionDefinition(script.get(0));
		}
		else {
			cf.setFunctionDefinition(Value.NULL);
		}
	}

	@Override
	protected void createContent(Composite page) {
		getConfiguration().clear();
		DefaultCustomPropertyFunction cf = getWizard().getResultFunction();
		if (cf.getFunctionDefinition() != null) {
			ListMultimap<String, ParameterValue> initialValues = ArrayListMultimap.create();
			initialValues.put(PARAMETER_SCRIPT, new ParameterValue(cf.getFunctionDefinition()));
			setParameter(Collections.<FunctionParameterDefinition> emptySet(), initialValues);
		}

		super.createContent(page);
	}

	@Override
	public String getHelpContext() {
		return "eu.esdihumboldt.cst.functions.groovy.script";
	}

}
