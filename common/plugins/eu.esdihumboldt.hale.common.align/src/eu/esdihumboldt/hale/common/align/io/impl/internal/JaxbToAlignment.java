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
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.annotation.AnnotationExtension;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.io.LoadAlignmentContext;
import eu.esdihumboldt.hale.common.align.io.impl.DefaultEntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractParameterType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType.Base;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AnnotationType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.CellType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ComplexParameterType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.CustomFunctionType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.DocumentationType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ModifierType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ModifierType.DisableFor;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.NamedEntityType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ParameterType;
import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.ElementValue;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Converts an {@link AlignmentType} loaded with JAXB to a
 * {@link MutableAlignment}.
 * 
 * @author Simon Templer
 */
public class JaxbToAlignment
		extends AbstractBaseAlignmentLoader<AlignmentType, CellType, ModifierType> {

	private final TypeIndex targetTypes;
	private final TypeIndex sourceTypes;
	private final IOReporter reporter;
	private final AlignmentType alignment;
	private final PathUpdate updater;
	private final EntityResolver resolver;

	/**
	 * Private constructor for internal use.
	 */
	private JaxbToAlignment() {
		this.alignment = null;
		this.reporter = null;
		this.sourceTypes = null;
		this.targetTypes = null;
		this.updater = null;
		// no custom resolver here, this constructor is used when loading base
		// alignments
		this.resolver = DefaultEntityResolver.getInstance();
	}

	/**
	 * @param alignment the alignment read using JAXB
	 * @param reporter where to report problems to, may be <code>null</code>
	 * @param sourceTypes the source types for resolving source entities
	 * @param targetTypes the target types for resolving target entities
	 * @param updater the path updater to use for base alignments
	 * @param resolver the entity resolver
	 */
	public JaxbToAlignment(AlignmentType alignment, IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes, PathUpdate updater, EntityResolver resolver) {
		this.alignment = alignment;
		this.reporter = reporter;
		this.sourceTypes = sourceTypes;
		this.targetTypes = targetTypes;
		this.updater = updater;
		if (resolver == null) {
			this.resolver = DefaultEntityResolver.getInstance();
		}
		else {
			this.resolver = resolver;
		}
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
	 * @param projectLocation the project location or <code>null</code>
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @throws IOException if adding the base alignment fails
	 */
	public static void addBaseAlignment(MutableAlignment alignment, URI newBase,
			URI projectLocation, TypeIndex sourceTypes, TypeIndex targetTypes, IOReporter reporter)
					throws IOException {
		new JaxbToAlignment().internalAddBaseAlignment(alignment, newBase, projectLocation,
				sourceTypes, targetTypes, reporter);
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

	private static MutableCell convert(CellType cell, LoadAlignmentContext context,
			IOReporter reporter, EntityResolver resolver) {
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
							new ParameterValue(new ElementValue(cpt.getAny(), context)));
				}
				else
					throw new IllegalStateException("Illegal parameter type");
			}
			result.setTransformationParameters(parameters);
		}

		try {
			result.setSource(convertEntities(cell.getSource(), context.getSourceTypes(),
					SchemaSpaceID.SOURCE, resolver));
			result.setTarget(convertEntities(cell.getTarget(), context.getTargetTypes(),
					SchemaSpaceID.TARGET, resolver));
			if (result.getTarget() == null || result.getTarget().isEmpty()) {
				// target is mandatory for cells!
				throw new IllegalStateException("Cannot create cell without target");
			}
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
				AnnotationDescriptor<?> desc = AnnotationExtension.getInstance()
						.get(annot.getType());
				if (desc != null) {
					try {
						Object value = desc.fromDOM(annot.getAny(), null);
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
			 * <li>IllegalStateException - entity could not be resolved, reject
			 * cell</li>
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
		LoadAlignmentContextImpl context = new LoadAlignmentContextImpl();
		context.setSourceTypes(sourceTypes);
		context.setTargetTypes(targetTypes);
		return convert(cell, context, reporter, resolver);
	}

	@Override
	protected Collection<CustomPropertyFunction> getPropertyFunctions(AlignmentType source,
			TypeIndex sourceTypes, TypeIndex targetTypes) {
		LoadAlignmentContextImpl context = new LoadAlignmentContextImpl();
		context.setSourceTypes(sourceTypes);
		context.setTargetTypes(targetTypes);

		Collection<CustomPropertyFunction> result = new ArrayList<>();
		List<CustomFunctionType> functions = source.getCustomFunction();
		if (functions != null) {
			for (CustomFunctionType function : functions) {
				Element elem = function.getAny();
				if (elem != null) {
					CustomPropertyFunction cf = HaleIO.getComplexValue(elem,
							CustomPropertyFunction.class, context);
					if (cf != null) {
						result.add(cf);
					}
				}
			}
		}
		return result;
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

	@Override
	protected TransformationMode getTransformationMode(ModifierType modifier) {
		if (modifier.getTransformation() != null) {
			String name = modifier.getTransformation().getMode().value();
			return TransformationMode.valueOf(name);
		}
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getCellId(java.lang.Object)
	 */
	@Override
	protected String getCellId(CellType cell) {
		return cell.getId();
	}

}
