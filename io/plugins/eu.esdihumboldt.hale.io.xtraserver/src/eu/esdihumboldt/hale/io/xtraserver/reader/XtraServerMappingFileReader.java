/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

import de.interactive_instruments.xtraserver.config.util.ApplicationSchema;
import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import de.interactive_instruments.xtraserver.config.util.api.XtraServerMapping;
import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentReader;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.NamedEntityType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * TODO Type description
 * 
 * @author zahnen
 */
public class XtraServerMappingFileReader extends AbstractAlignmentReader {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentReader#loadAlignment(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected MutableAlignment loadAlignment(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		progress.begin("Load XtraServer mapping", ProgressIndicator.UNKNOWN);

		final InputStream in = getSource().getInput();

		final TypeIndex schemaspace = getTargetSchema();
		if (schemaspace == null) {
			reporter.error("Load the target schema first!");
			reporter.setSuccess(false);
			return null;
		}
		if (!(schemaspace instanceof SchemaSpace)) {
			throw new IllegalArgumentException(
					"Unknown target schema type: " + schemaspace.getClass());
		}

		final Schema schema = ((SchemaSpace) schemaspace).getSchemas().iterator().next();
		final ApplicationSchema applicationSchema = new ApplicationSchema(schema.getLocation());

		MutableAlignment alignment = null;
		try {

			EntityResolver entityResolver = null;
			if (getServiceProvider() != null) {
				entityResolver = getServiceProvider().getService(EntityResolver.class);
			}
			TypeIndex sourceTypes = getSourceSchema();
			TypeIndex targetTypes = getTargetSchema();
			PathUpdate updater = getPathUpdater();

			alignment = new DefaultAlignment();

			XtraServerMapping xsm = XtraServerMapping.createFromStream(in, applicationSchema);

			NamedEntityType sourceType;
			NamedEntityType targetType;

			for (String featureType : xsm.getFeatureTypeList(false)) {
				FeatureTypeMapping ftm = xsm.getFeatureTypeMapping(featureType, true).get();
				QName targetTypeName = ftm.getQName();

				if (targetTypeName == null) {
					System.out.println("NOT FOUND: " + ftm.getName());
					continue;
				}
				boolean added = false;

				targetType = getNamedEntity(targetTypeName);

				// TODO: won't work for more than one
				for (String t : ftm.getPrimaryTableNames()) {
					QName sourceTableName = getTableQName(t, sourceTypes);

					// System.out.println(sourceTableName.getLocalPart() + " ||| " + t + " ||| "
					// + targetTypeName.toString());
					sourceType = getNamedEntity(sourceTableName, "types");

					// addRetype(alignment, sourceTypes, targetTypes, entityResolver, sourceType,
					// targetType);

					Set<NamedEntityType> joinedTables = new LinkedHashSet<>();
					Set<QName> joinedSourceTypesQN = new LinkedHashSet<>();
					joinedSourceTypesQN.add(sourceTableName);
					Set<JoinCondition> conditions = new LinkedHashSet<>();

					// TODO: reference tables

					List<String> allTableNames = ImmutableList.<String> builder()
							.addAll(ftm.getJoinedTableNames()).addAll(ftm.getReferenceTableNames())
							.build();

					for (String joinTableName : allTableNames) {
						MappingTable joinedTable = ftm.getTable(joinTableName).get();

						if (joinedTable.hasJoinPath()) {

							for (MappingJoin mappingJoin : joinedTable.getJoinPaths()) {
								for (MappingJoin.Condition condition : mappingJoin
										.getJoinConditions()) {
									QName baseTableName = getTableQName(condition.getSourceTable(),
											sourceTypes);
									QName joinedTableName = getTableQName(
											condition.getTargetTable(), sourceTypes);

									if (baseTableName.getLocalPart()
											.equals(joinedTable.getName())) {
										reporter.warn("SELF JOIN: " + condition.toString() + " ["
												+ mappingJoin.toString() + "]");
									}

									joinedTables.add(getNamedEntity(joinedTableName, "types"));
									joinedSourceTypesQN.add(baseTableName);
									joinedSourceTypesQN.add(joinedTableName);

									PropertyEntityDefinition baseProperty = getEntityDefinition(
											baseTableName, new QName(condition.getSourceField()));
									PropertyEntityDefinition joinedProperty = getEntityDefinition(
											joinedTableName, new QName(condition.getTargetField()));

									conditions.add(new JoinCondition(baseProperty, joinedProperty));
								}
							}

						}
					}

					// add self joins for root table
					MappingTable joinedTable = ftm.getTable(t).get();

					if (!joinedTable.getJoinPaths().isEmpty()) {

						for (MappingJoin mappingJoin : joinedTable.getJoinPaths()) {
							for (MappingJoin.Condition condition : mappingJoin
									.getJoinConditions()) {
								QName baseTableName = getTableQName(condition.getSourceTable(),
										sourceTypes);
								QName joinedTableName = getTableQName(condition.getTargetTable(),
										sourceTypes);

								if (!joinedTableName.getLocalPart().equals(joinedTable.getName())) {
									/*
									 * reporter.warn("Valid self join on root table of " +
									 * ftm.getName() + ": " + condition.toString() +
									 * "\ncomplete join: " + mappingJoin.toString() +
									 * "\njoin target: " + mappingJoin.getTarget() +
									 * "\ntable + target: " + joinedTable.getName() + "[" +
									 * joinedTable.getTarget() + "]");
									 * 
									 * joinedTables.add(getNamedEntity(joinedTableName, "types"));
									 * joinedSourceTypesQN.add(baseTableName);
									 * joinedSourceTypesQN.add(joinedTableName);
									 * 
									 * PropertyEntityDefinition baseProperty = getEntityDefinition(
									 * baseTableName, new QName(condition.getSourceField()));
									 * PropertyEntityDefinition joinedProperty =
									 * getEntityDefinition( joinedTableName, new
									 * QName(condition.getTargetField()));
									 * 
									 * conditions.add(new JoinCondition(baseProperty,
									 * joinedProperty));
									 */
								}
								else {
									if (!mappingJoin.isSuppressJoin()) {
										reporter.warn("Self join on root table of " + ftm.getName()
												+ ": " + condition.toString() + "\ncomplete join: "
												+ mappingJoin.toString() + "\njoin target: "
												+ mappingJoin.getTarget() + "\ntable + target: "
												+ joinedTable.getName() + "["
												+ joinedTable.getTarget() + "]");
									}

									Collection<MappingValue> ignoreValues = Collections2
											.filter(ftm.getValues(), new Predicate<MappingValue>() {

												@Override
												public boolean apply(MappingValue value) {
													return value.getTarget()
															.startsWith(mappingJoin.getTarget())
															&& value.getTable()
																	.equals(joinedTable.getName());
												}
											});
									if (!mappingJoin.isSuppressJoin()) {
										for (MappingValue ignoreValue : ignoreValues) {
											reporter.warn("Ignored joined value: "
													+ ignoreValue.getTable() + " "
													+ ignoreValue.getValue() + " "
													+ ignoreValue.getTarget());
										}
									}
									ftm.getValues().removeAll(ignoreValues);
								}
							}
						}

					}

					addJoin(alignment, sourceTypes, targetTypes, entityResolver, sourceType,
							targetType, joinedTables, conditions, joinedSourceTypesQN);

					added = addValues(alignment, ftm.getValues(), joinedSourceTypesQN,
							targetTypeName, entityResolver);
				}

				/*
				 * if (!added) { System.out.println("NOT ADDED: " + ftm.getName()); } else {
				 * System.out.println("ADDED: " + ftm.getName()); }
				 */
			}

		} catch (

		Exception e) {
			reporter.error(new IOMessageImpl(e.getMessage(), e));
			reporter.setSuccess(false);
			return alignment;
		} finally {
			in.close();
		}

		progress.end();
		reporter.setSuccess(true);

		return alignment;
	}

	private boolean addValues(MutableAlignment alignment, List<MappingValue> mappingValues,
			Set<QName> sourceTableNames, QName targetTypeName, EntityResolver entityResolver) {
		TypeIndex sourceTypes = getSourceSchema();
		TypeIndex targetTypes = getTargetSchema();
		String oidPrefix = "'urn:adv:oid:' || $T$.";
		boolean added = false;

		for (MappingValue mappingValue : mappingValues) {
			try {
				QName sourceTableName = Iterables.find(sourceTableNames, new Predicate<QName>() {

					@Override
					public boolean apply(QName sourceTableName2) {
						return mappingValue.getTable().equals(sourceTableName2.getLocalPart());
					}
				});

				// if (mappingValue.getTable().equals(sourceTableName.getLocalPart())) {
				if (mappingValue.getValueType().equals("value")) {
					List<QName> targetPropertyNames = mappingValue.getTargetQNameList();

					if (targetPropertyNames == null || targetPropertyNames.isEmpty()) {
						System.out.println("NOT FOUND: " + mappingValue.getTarget());
						continue;
					}

					NamedEntityType sourceProperty = getNamedEntity(sourceTableName,
							new QName(mappingValue.getValue()));
					NamedEntityType targetProperty = getNamedEntity(targetTypeName,
							targetPropertyNames);

					// System.out.println(sourceTableName.getLocalPart() + " ||| "
					// + mappingValue.getValue() + " ||| " + targetPropertyNames.toString());

					addRename(alignment, sourceTypes, targetTypes, entityResolver, sourceProperty,
							targetProperty);

					added = true;
				}
				else if (mappingValue.getValueType().equals("constant")) {
					List<QName> targetPropertyNames = mappingValue.getTargetQNameList();

					if (targetPropertyNames == null || targetPropertyNames.isEmpty()) {
						System.out.println("NOT FOUND: " + mappingValue.getTarget());
						continue;
					}

					NamedEntityType targetProperty = getNamedEntity(targetTypeName,
							targetPropertyNames);

					// System.out.println(sourceTableName.getLocalPart() + " ||| "
					// + mappingValue.getValue() + " ||| " + targetPropertyNames.toString());

					String bindValue = null;

					if (mappingValue.getTarget().contains("/@")) {
						String bindTarget = mappingValue.getTarget().substring(0,
								mappingValue.getTarget().lastIndexOf('/'));

						try {
							MappingValue bv = Iterables.find(mappingValues,
									new Predicate<MappingValue>() {

										@Override
										public boolean apply(MappingValue value) {
											return bindTarget.equals(value.getTarget())
													&& mappingValue.getTable()
															.equals(value.getTable());
										}
									});
							if (bv.getValueType().equals("value")) {
								bindValue = bv.getValue();
							}
							else if (bv.getValueType().equals("expression")) {
								bindValue = bv.getValue().substring(oidPrefix.length());
							}
						} catch (NoSuchElementException e) {
							// ignore
						}
					}

					if (bindValue != null) {
						NamedEntityType sourceProperty = getNamedEntity(sourceTableName,
								new QName(bindValue), "anchor");

						// System.out.println(sourceTableName.getLocalPart() + " ||| " + bindValue);

						addAssignBound(alignment, sourceTypes, targetTypes, entityResolver,
								mappingValue.getValue(), sourceProperty, targetProperty);

						added = true;
					}
					else {

						addAssign(alignment, sourceTypes, targetTypes, entityResolver,
								mappingValue.getValue(), targetProperty);

						added = true;
					}
				}
				else if (mappingValue.getValueType().equals("expression")) {

					if (mappingValue.getValue().startsWith(oidPrefix)) {
						List<QName> targetPropertyNames = mappingValue.getTargetQNameList();

						if (targetPropertyNames == null || targetPropertyNames.isEmpty()) {
							System.out.println("NOT FOUND: " + mappingValue.getTarget());
							continue;
						}

						String value = mappingValue.getValue().substring(oidPrefix.length());
						String pattern = "urn:adv:oid:{" + value + "}";

						NamedEntityType sourceProperty = getNamedEntity(sourceTableName,
								new QName(value), "var");
						NamedEntityType targetProperty = getNamedEntity(targetTypeName,
								targetPropertyNames);

						// System.out.println(sourceTableName.getLocalPart() + " ||| " + value
						// + " ||| " + targetPropertyNames.toString());

						addFormattedString(alignment, sourceTypes, targetTypes, entityResolver,
								sourceProperty, targetProperty, pattern);

						added = true;
					}
				}
				// }
			} catch (NoSuchElementException e) {
				continue;
			}
		}

		return added;
	}

	private QName getTableQName(String name, TypeIndex sourceTypes) {
		for (TypeDefinition td : sourceTypes.getTypes()) {
			if (td.getName().getLocalPart().equals(name)) {
				return td.getName();
			}
		}
		return null;
	}

	private void addRetype(MutableAlignment alignment, TypeIndex sourceTypes, TypeIndex targetTypes,
			EntityResolver entityResolver, NamedEntityType sourceType, NamedEntityType targetType) {
		DefaultCell cell = new DefaultCell();

		cell.setTransformationIdentifier("eu.esdihumboldt.hale.align.retype");

		List<NamedEntityType> sourceEntities = new ArrayList<>();
		sourceEntities.add(sourceType);

		List<NamedEntityType> targetEntities = new ArrayList<>();
		targetEntities.add(targetType);

		cell.setSource(
				convertEntities(sourceEntities, sourceTypes, SchemaSpaceID.SOURCE, entityResolver));
		cell.setTarget(
				convertEntities(targetEntities, targetTypes, SchemaSpaceID.TARGET, entityResolver));

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put("ignoreNamespaces", new ParameterValue("false"));
		parameters.put("structuralRename", new ParameterValue("false"));
		cell.setTransformationParameters(parameters);

		alignment.addCell(cell);
	}

	private void addJoin(MutableAlignment alignment, TypeIndex sourceTypes, TypeIndex targetTypes,
			EntityResolver entityResolver, NamedEntityType sourceType, NamedEntityType targetType,
			Set<NamedEntityType> joinedSourceTypes, Set<JoinCondition> joinConditions,
			Set<QName> joinedSourceTypesQN) {
		DefaultCell cell = new DefaultCell();

		cell.setTransformationIdentifier("eu.esdihumboldt.hale.align.join");

		List<NamedEntityType> sourceEntities = new ArrayList<>();
		// sourceEntities.add(sourceType);
		for (QName joinedTableName : joinedSourceTypesQN) {
			sourceEntities.add(getNamedEntity(joinedTableName, "types"));
		}

		List<NamedEntityType> targetEntities = new ArrayList<>();
		targetEntities.add(targetType);

		cell.setSource(
				convertEntities(sourceEntities, sourceTypes, SchemaSpaceID.SOURCE, entityResolver));
		cell.setTarget(
				convertEntities(targetEntities, targetTypes, SchemaSpaceID.TARGET, entityResolver));

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();

		List<TypeEntityDefinition> typeDefinitions = new ArrayList<>();
		for (QName joinedSourceType : joinedSourceTypesQN) {
			typeDefinitions.add(new TypeEntityDefinition(
					new DefaultTypeDefinition(joinedSourceType), SchemaSpaceID.SOURCE, null));
		}
		JoinParameter joinParameter = new JoinParameter(typeDefinitions, joinConditions);
		final String validation = joinParameter.validate();
		if (validation != null) {
			throw new IllegalArgumentException("Join parameter invalid: " + validation);
		}

		parameters.put("join", new ParameterValue(Value.complex(joinParameter)));
		cell.setTransformationParameters(parameters);

		alignment.addCell(cell);
	}

	private void addRename(MutableAlignment alignment, TypeIndex sourceTypes, TypeIndex targetTypes,
			EntityResolver entityResolver, NamedEntityType sourceType, NamedEntityType targetType) {
		DefaultCell cell = new DefaultCell();

		cell.setTransformationIdentifier("eu.esdihumboldt.hale.align.rename");

		List<NamedEntityType> sourceEntities = new ArrayList<>();
		sourceEntities.add(sourceType);

		List<NamedEntityType> targetEntities = new ArrayList<>();
		targetEntities.add(targetType);

		cell.setSource(
				convertEntities(sourceEntities, sourceTypes, SchemaSpaceID.SOURCE, entityResolver));
		cell.setTarget(
				convertEntities(targetEntities, targetTypes, SchemaSpaceID.TARGET, entityResolver));

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put("ignoreNamespaces", new ParameterValue("false"));
		parameters.put("structuralRename", new ParameterValue("false"));
		cell.setTransformationParameters(parameters);

		alignment.addCell(cell);
	}

	private void addFormattedString(MutableAlignment alignment, TypeIndex sourceTypes,
			TypeIndex targetTypes, EntityResolver entityResolver, NamedEntityType sourceType,
			NamedEntityType targetType, String pattern) {
		DefaultCell cell = new DefaultCell();

		cell.setTransformationIdentifier("eu.esdihumboldt.hale.align.formattedstring");

		List<NamedEntityType> sourceEntities = new ArrayList<>();
		sourceEntities.add(sourceType);

		List<NamedEntityType> targetEntities = new ArrayList<>();
		targetEntities.add(targetType);

		cell.setSource(
				convertEntities(sourceEntities, sourceTypes, SchemaSpaceID.SOURCE, entityResolver));
		cell.setTarget(
				convertEntities(targetEntities, targetTypes, SchemaSpaceID.TARGET, entityResolver));

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put("pattern", new ParameterValue(pattern));
		cell.setTransformationParameters(parameters);

		alignment.addCell(cell);
	}

	private void addAssign(MutableAlignment alignment, TypeIndex sourceTypes, TypeIndex targetTypes,
			EntityResolver entityResolver, String value, NamedEntityType targetType) {
		DefaultCell cell = new DefaultCell();

		cell.setTransformationIdentifier("eu.esdihumboldt.hale.align.assign");

		List<NamedEntityType> targetEntities = new ArrayList<>();
		targetEntities.add(targetType);

		cell.setTarget(
				convertEntities(targetEntities, targetTypes, SchemaSpaceID.TARGET, entityResolver));

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put("value", new ParameterValue(value));
		cell.setTransformationParameters(parameters);

		alignment.addCell(cell);
	}

	private void addAssignBound(MutableAlignment alignment, TypeIndex sourceTypes,
			TypeIndex targetTypes, EntityResolver entityResolver, String value,
			NamedEntityType sourceType, NamedEntityType targetType) {
		DefaultCell cell = new DefaultCell();

		cell.setTransformationIdentifier("eu.esdihumboldt.hale.align.assign.bound");

		List<NamedEntityType> sourceEntities = new ArrayList<>();
		sourceEntities.add(sourceType);

		List<NamedEntityType> targetEntities = new ArrayList<>();
		targetEntities.add(targetType);

		cell.setSource(
				convertEntities(sourceEntities, sourceTypes, SchemaSpaceID.SOURCE, entityResolver));
		cell.setTarget(
				convertEntities(targetEntities, targetTypes, SchemaSpaceID.TARGET, entityResolver));

		ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
		parameters.put("value", new ParameterValue(value));
		cell.setTransformationParameters(parameters);

		alignment.addCell(cell);
	}

	private static ListMultimap<String, ? extends Entity> convertEntities(
			List<NamedEntityType> namedEntities, TypeIndex types, SchemaSpaceID schemaSpace,
			EntityResolver resolver) {
		if (namedEntities == null || namedEntities.isEmpty()) {
			return null;
		}

		ListMultimap<String, Entity> result = ArrayListMultimap.create();

		for (NamedEntityType namedEntity : namedEntities) {
			/**
			 * Resolve entity.
			 * 
			 * Possible results:
			 * <ul>
			 * <li>non-null entity - entity could be resolved</li>
			 * <li>null entity - entity could not be resolved, continue</li>
			 * <li>IllegalStateException - entity could not be resolved, reject cell</li>
			 * </ul>
			 */
			Entity entity = resolver.resolve(namedEntity.getAbstractEntity().getValue(), types,
					schemaSpace);

			if (entity != null) {
				result.put(namedEntity.getName(), entity);
			}
		}

		return result;
	}

	private NamedEntityType getNamedEntity(QName qname) {
		return getNamedEntity(qname, "");
	}

	private NamedEntityType getNamedEntity(QName qname, String name) {
		ClassType.Type sourceQNT = new ClassType.Type();
		sourceQNT.setName(qname.getLocalPart());
		sourceQNT.setNs(qname.getNamespaceURI());
		ClassType sourceQN = new ClassType();
		sourceQN.setType(sourceQNT);
		NamedEntityType sourceType = new NamedEntityType();
		if (!name.isEmpty())
			sourceType.setName(name);
		sourceType.setAbstractEntity(new JAXBElement(new QName("type"), ClassType.class, sourceQN));

		return sourceType;
	}

	private NamedEntityType getNamedEntity(QName qname, QName property) {
		return getNamedEntity(qname, property, "");
	}

	private NamedEntityType getNamedEntity(QName qname, List<QName> properties) {
		return getNamedEntity(qname, properties, "");
	}

	private NamedEntityType getNamedEntity(QName qname, QName property, String name) {
		return getNamedEntity(qname, Lists.newArrayList(property), name);
	}

	private NamedEntityType getNamedEntity(QName qname, List<QName> properties, String name) {
		PropertyType.Type sourceQNT = new PropertyType.Type();
		sourceQNT.setName(qname.getLocalPart());
		sourceQNT.setNs(qname.getNamespaceURI());

		PropertyType sourceQN = new PropertyType();
		sourceQN.setType(sourceQNT);

		for (QName p : properties) {
			ChildContextType sourceP = new ChildContextType();
			sourceP.setName(p.getLocalPart());
			sourceP.setNs(p.getNamespaceURI());
			sourceQN.getChild().add(sourceP);
		}

		NamedEntityType sourceType = new NamedEntityType();
		if (!name.isEmpty())
			sourceType.setName(name);
		sourceType.setAbstractEntity(
				new JAXBElement(new QName("type"), PropertyType.class, sourceQN));

		return sourceType;
	}

	private PropertyEntityDefinition getEntityDefinition(QName qname, QName property) {
		TypeDefinition typeDefinition = new DefaultTypeDefinition(qname);

		ChildDefinition child = new DefaultPropertyDefinition(property, typeDefinition,
				typeDefinition);

		typeDefinition.addChild(child);

		List<ChildContext> path = new ArrayList<>();
		path.add(new ChildContext(child));

		return new PropertyEntityDefinition(typeDefinition, path, SchemaSpaceID.SOURCE, null);
	}

	/*
	 * ClassType.Type sourceQNT = new ClassType.Type(); sourceQNT.setName("o44001");
	 * sourceQNT.setNs("jdbc:postgresql:xtra_atkis:ATKISBDLM"); ClassType sourceQN =
	 * new ClassType(); sourceQN.setType(sourceQNT); NamedEntityType sourceType =
	 * new NamedEntityType(); sourceType.setAbstractEntity( new JAXBElement(new
	 * QName("type"), ClassType.class, sourceQN));
	 * 
	 * 
	 * ClassType.Type targetQNT = new ClassType.Type();
	 * targetQNT.setName("AX_FliessgewaesserType");
	 * targetQNT.setNs("http://www.adv-online.de/namespaces/adv/gid/6.0"); ClassType
	 * targetQN = new ClassType(); targetQN.setType(targetQNT); NamedEntityType
	 * targetType = new NamedEntityType(); targetType.setAbstractEntity( new
	 * JAXBElement(new QName("type"), ClassType.class, targetQN));
	 */

}
