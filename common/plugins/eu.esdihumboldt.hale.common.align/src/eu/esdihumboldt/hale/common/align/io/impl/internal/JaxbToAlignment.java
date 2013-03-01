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

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.annotation.AnnotationExtension;
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractEntityType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractParameterType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType.Base;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AnnotationType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.CellType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ComplexParameterType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ConditionType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.DocumentationType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ModifierType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ModifierType.DisableFor;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.NamedEntityType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ParameterType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.QNameType;
import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.ElementValue;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.io.PathUpdate;

/**
 * Converts an {@link AlignmentType} loaded with JAXB to a
 * {@link MutableAlignment}.
 * 
 * @author Simon Templer
 */
public class JaxbToAlignment extends
		AbstractBaseAlignmentLoader<AlignmentType, CellType, ModifierType> {

	private final TypeIndex targetTypes;
	private final TypeIndex sourceTypes;
	private final IOReporter reporter;
	private final AlignmentType alignment;
	private final PathUpdate updater;

	/**
	 * Private constructor for internal use.
	 */
	private JaxbToAlignment() {
		this.alignment = null;
		this.reporter = null;
		this.sourceTypes = null;
		this.targetTypes = null;
		this.updater = null;
	}

	/**
	 * @param alignment the alignment read using JAXB
	 * @param reporter where to report problems to, may be <code>null</code>
	 * @param sourceTypes the source types for resolving source entities
	 * @param targetTypes the target types for resolving target entities
	 * @param updater the path updater to use for base alignments
	 */
	public JaxbToAlignment(AlignmentType alignment, IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes, PathUpdate updater) {
		this.alignment = alignment;
		this.reporter = reporter;
		this.sourceTypes = sourceTypes;
		this.targetTypes = targetTypes;
		this.updater = updater;
	}

	/**
	 * Load a {@link AlignmentType} from an input stream. The stream is closed
	 * at the end.
	 * 
	 * @param in the input stream
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @return the alignment
	 * @throws JAXBException if reading the alignment failed
	 */
	public static AlignmentType load(InputStream in, IOReporter reporter) throws JAXBException {
		JAXBContext jc;
		JAXBElement<AlignmentType> root = null;
		jc = JAXBContext.newInstance(JaxbAlignmentIO.ALIGNMENT_CONTEXT,
				AlignmentType.class.getClassLoader());
		Unmarshaller u = jc.createUnmarshaller();

		// it will debug problems while unmarshalling
		u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

		try {
			root = u.unmarshal(new StreamSource(in), AlignmentType.class);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}

		return root.getValue();
	}

	/**
	 * Adds the given base alignment to the given alignment.
	 * 
	 * @param alignment the alignment to add a base alignment to
	 * @param newBase URI of the new base alignment
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @throws IOException if adding the base alignment fails
	 */
	public static void addBaseAlignment(MutableAlignment alignment, URI newBase,
			TypeIndex sourceTypes, TypeIndex targetTypes, IOReporter reporter) throws IOException {
		new JaxbToAlignment().internalAddBaseAlignment(alignment, newBase, sourceTypes,
				targetTypes, reporter);
	}

	/**
	 * Create the converted alignment.
	 * 
	 * @return the resolved alignment
	 * @throws IOException if a base alignment couldn't be loaded
	 */
	public MutableAlignment convert() throws IOException {
		return super.createAlignment(alignment, sourceTypes, targetTypes, updater, reporter);
	}

	private MutableCell convert(CellType cell, TypeIndex sourceTypes, TypeIndex targetTypes,
			IOReporter reporter) {
		DefaultCell result = new DefaultCell();

		result.setTransformationIdentifier(cell.getRelation());

		if (!cell.getAbstractParameter().isEmpty()) {
			ListMultimap<String, ParameterValue> parameters = ArrayListMultimap.create();
			for (JAXBElement<? extends AbstractParameterType> param : cell.getAbstractParameter()) {
				AbstractParameterType apt = param.getValue();
				if (apt instanceof ParameterType) {
					// treat string parameters or null parameters
					ParameterType pt = (ParameterType) apt;
					parameters.put(pt.getName(),
							new ParameterValue(pt.getType(), Value.of(pt.getValue())));
				}
				else if (apt instanceof ComplexParameterType) {
					// complex parameters
					ComplexParameterType cpt = (ComplexParameterType) apt;
					parameters.put(cpt.getName(),
							new ParameterValue(new ElementValue(cpt.getAny())));
				}
				else
					throw new IllegalStateException("Illegal parameter type");
			}
			result.setTransformationParameters(parameters);
		}

		try {
			result.setSource(convertEntities(cell.getSource(), sourceTypes, SchemaSpaceID.SOURCE));
			result.setTarget(convertEntities(cell.getTarget(), targetTypes, SchemaSpaceID.TARGET));
		} catch (Exception e) {
			if (reporter != null) {
				reporter.error(new IOMessageImpl("Could not create cell", e));
			}
			return null;
		}

		// annotations & documentation
		for (Object element : cell.getDocumentationOrAnnotation()) {
			if (element instanceof AnnotationType) {
				// add annotation to the cell
				AnnotationType annot = (AnnotationType) element;

				// but first load it from the DOM
				AnnotationDescriptor<?> desc = AnnotationExtension.getInstance().get(
						annot.getType());
				if (desc != null) {
					try {
						Object value = desc.fromDOM(annot.getAny());
						result.addAnnotation(annot.getType(), value);
					} catch (Exception e) {
						if (reporter != null) {
							reporter.error(new IOMessageImpl("Error loading cell annotation", e));
						}
						else
							throw new IllegalStateException("Error loading cell annotation", e);
					}
				}
				else
					reporter.error(new IOMessageImpl(
							"Cell annotation of type {0} unknown, cannot load the annotation object",
							null, -1, -1, annot.getType()));
			}
			else if (element instanceof DocumentationType) {
				// add documentation to the cell
				DocumentationType doc = (DocumentationType) element;
				result.getDocumentation().put(doc.getType(), doc.getValue());
			}
		}

		result.setId(cell.getId());

		// a default value is assured for priority
		String priorityStr = cell.getPriority().value();
		Priority priority = Priority.fromValue(priorityStr);
		if (priority != null) {
			result.setPriority(priority);
		}
		else {
			// TODO check if it makes sense to do something. Default value is
			// used.
			throw new IllegalArgumentException();
		}
		return result;
	}

	private ListMultimap<String, ? extends Entity> convertEntities(
			List<NamedEntityType> namedEntities, TypeIndex types, SchemaSpaceID schemaSpace) {
		if (namedEntities == null || namedEntities.isEmpty()) {
			return null;
		}

		ListMultimap<String, Entity> result = ArrayListMultimap.create();

		for (NamedEntityType namedEntity : namedEntities) {
			result.put(namedEntity.getName(),
					convert(namedEntity.getAbstractEntity().getValue(), types, schemaSpace));
		}

		return result;
	}

	private Entity convert(AbstractEntityType entity, TypeIndex types, SchemaSpaceID schemaSpace) {
		// must first check for PropertyType as it inherits from ClassType
		if (entity instanceof PropertyType) {
			return convert((PropertyType) entity, types, schemaSpace);
		}
		if (entity instanceof ClassType) {
			return convert((ClassType) entity, types, schemaSpace);
		}
		throw new IllegalArgumentException("Illegal type of entity");
	}

	private Type convert(ClassType classType, TypeIndex types, SchemaSpaceID schemaSpace) {
		TypeDefinition typeDef = types.getType(asName(classType.getType()));

		Filter filter = getTypeFilter(classType);

		TypeEntityDefinition tef = new TypeEntityDefinition(typeDef, schemaSpace, filter);

		return new DefaultType(tef);
	}

	private Filter getTypeFilter(ClassType classType) {
		if (classType.getType() != null && classType.getType().getCondition() != null) {
			return FilterDefinitionManager.getInstance().from(
					classType.getType().getCondition().getLang(),
					classType.getType().getCondition().getValue());
		}
		return null;
	}

	private Entity convert(PropertyType property, TypeIndex types, SchemaSpaceID schemaSpace) {
		TypeDefinition typeDef = types.getType(asName(property.getType()));

		Filter filter = getTypeFilter(property);

		List<ChildContext> path = new ArrayList<ChildContext>();

		DefinitionGroup parent = typeDef;
		for (ChildContextType childContext : property.getChild()) {
			if (parent == null) {
				throw new IllegalStateException(
						"Could not resolve property entity definition: child not present");
			}

			Pair<ChildDefinition<?>, List<ChildDefinition<?>>> childs = PropertyBean.findChild(
					parent, asName(childContext));

			ChildDefinition<?> child = childs.getFirst();

			// if the child is still null throw an exception
			if (child == null) {
				throw new IllegalStateException(
						"Could not resolve property entity definition: child not found");
			}

			if (childs.getSecond() != null) {
				for (ChildDefinition<?> pathElems : childs.getSecond()) {
					path.add(new ChildContext(contextName(childContext.getContext()),
							contextIndex(childContext.getIndex()), createCondition(childContext
									.getCondition()), pathElems));
				}
			}

			path.add(new ChildContext(contextName(childContext.getContext()),
					contextIndex(childContext.getIndex()), createCondition(childContext
							.getCondition()), child));

			if (child instanceof DefinitionGroup) {
				parent = (DefinitionGroup) child;
			}
			else if (child.asProperty() != null) {
				parent = child.asProperty().getPropertyType();
			}
			else {
				parent = null;
			}
		}

		PropertyEntityDefinition ped = new PropertyEntityDefinition(typeDef, path, schemaSpace,
				filter);
		return new DefaultProperty(ped);
	}

	/**
	 * Create a condition.
	 * 
	 * @param conditionFilter the condition filter
	 * @return the condition or <code>null</code>
	 */
	private Condition createCondition(ConditionType conditionFilter) {
		if (conditionFilter == null)
			return null;

		Filter filter = FilterDefinitionManager.getInstance().from(conditionFilter.getLang(),
				conditionFilter.getValue());
		if (filter != null) {
			return new Condition(filter);
		}
		return null;
	}

	private Integer contextName(BigInteger name) {
		if (name == null)
			return null;

//		return Integer.valueOf(name);
		return name.intValue();
	}

	private Integer contextIndex(BigInteger index) {
		if (index == null)
			return null;

		return index.intValue();
	}

	private QName asName(QNameType qname) {
		if (qname.getNs() == null || qname.getNs().isEmpty()) {
			return new QName(qname.getName());
		}
		return new QName(qname.getNs(), qname.getName());
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#loadAlignment(java.io.InputStream,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected AlignmentType loadAlignment(InputStream in, IOReporter reporter) throws IOException {
		try {
			return load(in, reporter);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getBases(java.lang.Object)
	 */
	@Override
	protected Map<String, URI> getBases(AlignmentType alignment) {
		Map<String, URI> baseMap = new HashMap<String, URI>();
		for (Base base : alignment.getBase())
			baseMap.put(base.getPrefix(), URI.create(base.getLocation()));
		return baseMap;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getCells(java.lang.Object)
	 */
	@Override
	protected Collection<CellType> getCells(AlignmentType alignment) {
		List<CellType> cells = new ArrayList<CellType>();
		for (Object cellOrModifier : alignment.getCellOrModifier())
			if (cellOrModifier instanceof CellType)
				cells.add((CellType) cellOrModifier);
		return cells;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#createCell(java.lang.Object,
	 *      eu.esdihumboldt.hale.common.schema.model.TypeIndex,
	 *      eu.esdihumboldt.hale.common.schema.model.TypeIndex,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected MutableCell createCell(CellType cell, TypeIndex sourceTypes, TypeIndex targetTypes,
			IOReporter reporter) {
		return convert(cell, sourceTypes, targetTypes, reporter);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getModifiers(java.lang.Object)
	 */
	@Override
	protected Collection<ModifierType> getModifiers(AlignmentType alignment) {
		List<ModifierType> modifiers = new ArrayList<ModifierType>();
		for (Object cellOrModifier : alignment.getCellOrModifier())
			if (cellOrModifier instanceof ModifierType)
				modifiers.add((ModifierType) cellOrModifier);
		return modifiers;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getModifiedCell(java.lang.Object)
	 */
	@Override
	protected String getModifiedCell(ModifierType modifier) {
		return modifier.getCell();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getDisabledForList(java.lang.Object)
	 */
	@Override
	protected Collection<String> getDisabledForList(ModifierType modifier) {
		return Collections2.transform(modifier.getDisableFor(), new Function<DisableFor, String>() {

			/**
			 * @see com.google.common.base.Function#apply(java.lang.Object)
			 */
			@Override
			public String apply(DisableFor input) {
				return input.getParent();
			}
		});
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getCellId(java.lang.Object)
	 */
	@Override
	protected String getCellId(CellType cell) {
		return cell.getId();
	}

}
