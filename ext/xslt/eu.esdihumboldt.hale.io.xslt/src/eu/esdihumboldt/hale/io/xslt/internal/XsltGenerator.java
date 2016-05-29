/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.internal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.AbstractTypeMatcher;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.PathElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xslt.SourceContextProvider;
import eu.esdihumboldt.hale.io.xslt.XslPropertyTransformation;
import eu.esdihumboldt.hale.io.xslt.XslTransformationUtil;
import eu.esdihumboldt.hale.io.xslt.XslTypeTransformation;
import eu.esdihumboldt.hale.io.xslt.XsltConstants;
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext;
import eu.esdihumboldt.hale.io.xslt.extension.XslPropertyTransformationExtension;
import eu.esdihumboldt.hale.io.xslt.extension.XslTypeTransformationExtension;
import eu.esdihumboldt.util.CustomIdentifiers;

/**
 * Generate a XSLT transformation from an {@link Alignment}. Each generation
 * process has to use its own instance.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class XsltGenerator implements XsltConstants {

	private final XsltGenerationContext context = new XsltGenerationContext() {

		private int numIncludes = 0;

		private final Map<String, XslPropertyTransformation> cachedTransformations = new HashMap<String, XslPropertyTransformation>();

		@Override
		public NamespaceContext getNamespaceContext() {
			return prefixes;
		}

		@Override
		public Alignment getAlignment() {
			return alignment;
		}

		@Override
		public XmlIndex getSourceSchema() {
			return sourceSchema;
		}

		@Override
		public XmlIndex getTargetSchema() {
			return targetSchema;
		}

		@Override
		public XslPropertyTransformation getPropertyTransformation(String functionId) {
			XslPropertyTransformation result = cachedTransformations.get(functionId);
			if (result == null) {
				try {
					result = XslPropertyTransformationExtension.getInstance()
							.getTransformation(functionId);
				} catch (Exception e) {
					return null;
				}
				cachedTransformations.put(functionId, result);
			}
			return result;
		}

		@Override
		public Template loadTemplate(Class<?> transformation, ByteSource resource, String id)
				throws ResourceNotFoundException, ParseErrorException, Exception {
			File templateFile = new File(workDir, "_" + transformation.getCanonicalName()
					+ ((id == null) ? ("") : ("_" + id)) + ".xsl");

			synchronized (ve) {
				if (!templateFile.exists()) {
					// copy template to template directory
					InputStream in = resource.openBufferedStream();
					OutputStream out = new FileOutputStream(templateFile);
					try {
						ByteStreams.copy(in, out);
					} finally {
						out.close();
						in.close();
					}
				}
			}

			return ve.getTemplate(templateFile.getName(), "UTF-8");
		}

		@Override
		public Template loadTemplate(final Class<?> transformation) throws Exception {
			return loadTemplate(transformation, new ByteSource() {

				@Override
				public InputStream openStream() throws IOException {
					return transformation
							.getResourceAsStream(transformation.getSimpleName() + ".xsl");
				}
			}, null);
		}

		@Override
		public ByteSink addInclude() {
			String filename = "include_" + (++numIncludes) + ".xsl";
			includes.add(filename);
			File file = new File(workDir, filename);
			return Files.asByteSink(file);
		}

		@Override
		public String reserveTemplateName(String desiredName) {
			synchronized (templateNames) {
				String id = desiredName;
				int num = 1;
				while (!isValidNewId(id)) {
					if (id.startsWith(cellIdentifiers.getPrefix())) {
						desiredName = '_' + desiredName;
						id = desiredName;
					}
					else {
						id = desiredName + '_' + num++;
					}
				}
				templateNames.add(id);
				return id;
			}
		}

		private boolean isValidNewId(String id) {
			if (id.startsWith(cellIdentifiers.getPrefix())) {
				// may not start with the cell identifiers prefix
				return false;
			}
			if (cellIdentifiers.getObject(id) != null) {
				// already used by a cell
				return false;
			}
			// may not be used if already reserved
			return !templateNames.contains(id);
		}

		@Override
		public String getInlineTemplateName(Cell typeCell) {
			return cellIdentifiers.getId(typeCell) + "_inline";
		}

		@Override
		public String getSourceContext(TypeDefinition type) {
			String result = null;

			if (sourceContext != null) {
				result = sourceContext.getSourceContext(type, getNamespaceContext());
			}

			if (result == null) {
				// default is anywhere in the document
				return "/";
			}
			else {
				return result;
			}
		}

	};

	/**
	 * Fixed namespace prefixes. Prefixes mapped to namespaces.
	 */
	private static final Map<String, String> FIXED_PREFIXES = ImmutableMap.of( //
			NS_PREFIX_XSI, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, //
			NS_PREFIX_XS, XMLConstants.W3C_XML_SCHEMA_NS_URI, //
			NS_PREFIX_XSL, NS_URI_XSL, //
			NS_PREFIX_CUSTOM_XSL, NS_CUSTOM_XSL, //
			NS_PREFIX_XSL_DEFINITIONS, NS_XSL_DEFINITIONS);

	/**
	 * The template engine.
	 */
	private final VelocityEngine ve;

	/**
	 * The reporter.
	 */
	private final IOReporter reporter;

	/**
	 * The progress indicator.
	 */
	private final ProgressIndicator progress;

	/**
	 * The alignment.
	 */
	private final Alignment alignment;

	/**
	 * The target XML schema.
	 */
	private final XmlIndex targetSchema;

	/**
	 * The source XML schema.
	 */
	private final XmlIndex sourceSchema;

	/**
	 * The working directory where the templates reside.
	 */
	private final File workDir;

	/**
	 * Collects XSL fragments to include in the main file
	 */
	private final Set<String> includes = new LinkedHashSet<String>();

	/**
	 * Namespace prefixes mapped to namespaces.
	 */
	private final NamespaceContextImpl prefixes;

	/**
	 * The reserved template names.
	 */
	private final Set<String> templateNames = new HashSet<String>();

	/**
	 * The cell identifiers.
	 */
	private final CustomIdentifiers<Cell> cellIdentifiers = new CustomIdentifiers<Cell>(Cell.class,
			true) {

		@Override
		protected boolean isReserved(String id) {
			synchronized (templateNames) {
				// names in this set are reserved
				return templateNames.contains(id);
			}
		}

	};

	/**
	 * The name of the container in the target schema.
	 */
	private final XmlElement targetContainer;

	/**
	 * An optional custom source context provider.
	 */
	private final SourceContextProvider sourceContext;

	private final ProjectInfo projectInfo;

	/**
	 * Create a XSLT generator.
	 * 
	 * @param workDir the working directory where the generator may store
	 *            temporary files, the caller is responsible for cleaning this
	 *            up, e.g. after {@link #write(LocatableOutputSupplier)} was
	 *            called
	 * @param alignment the alignment
	 * @param sourceSchema the source schema
	 * @param targetSchema the target schema
	 * @param reporter the reporter for documenting errors
	 * @param progress the progress indicator for indicating the generation
	 *            progress
	 * @param containerElement the name of the element to serve as document root
	 *            in the target XML file
	 * @param sourceContext an optional custom source context provider
	 * @param projectInfo the project info, if available, may be
	 *            <code>null</code>
	 * @throws Exception if an error occurs initializing the generator
	 */
	public XsltGenerator(File workDir, Alignment alignment, XmlIndex sourceSchema,
			XmlIndex targetSchema, IOReporter reporter, ProgressIndicator progress,
			XmlElement containerElement, SourceContextProvider sourceContext,
			ProjectInfo projectInfo) throws Exception {
		this.reporter = reporter;
		this.progress = progress;
		this.alignment = alignment;
		this.workDir = workDir;
		this.targetContainer = containerElement;
		this.targetSchema = targetSchema;
		this.sourceSchema = sourceSchema;
		this.sourceContext = sourceContext;
		this.projectInfo = projectInfo;

		// initialize the velocity template engine
		Templates.copyTemplates(workDir);
		ve = new VelocityEngine();
//		ve.setProperty("resource.loader", "main, file");
//		ve.setProperty("main.resource.loader.class",
//				eu.esdihumboldt.hale.io.xslt.internal.Templates.class);
		// custom resource loader does not work in OSGi context, so copy
		// templates to template folder
		ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, workDir.getAbsolutePath());
		// custom logger
		ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new AVelocityLogger());
		ve.init();

		// initialize the prefix map
		NamespaceContextImpl prefixes = new NamespaceContextImpl();

		// fixed prefixes
		for (Entry<String, String> entry : FIXED_PREFIXES.entrySet()) {
			prefixes.add(entry.getKey(), entry.getValue());
		}
		// target schema prefixes
		for (Entry<String, String> pair : this.targetSchema.getPrefixes().entrySet()) {
			prefixes.add(pair.getValue(), pair.getKey());
		}
		// source schema prefixes
		for (Entry<String, String> pair : this.sourceSchema.getPrefixes().entrySet()) {
			prefixes.add(pair.getValue(), pair.getKey());
		}

		this.prefixes = prefixes;
	}

	/**
	 * Generate the XSLT transformation and write it to the given target.
	 * 
	 * @param target the target output supplier
	 * @return the report
	 * @throws Exception if a unrecoverable error occurs during the process
	 */
	public IOReport write(LocatableOutputSupplier<? extends OutputStream> target) throws Exception {
		Template root = ve.getTemplate(Templates.ROOT, "UTF-8");

		VelocityContext context = XslTransformationUtil.createStrictVelocityContext();

		// project info
		context.put("info", ProjectXslInfo.getInfo(projectInfo));

		// collects IDs of type cells
		Set<String> typeIds = new HashSet<String>();

		// type cells
		for (Cell typeCell : alignment.getTypeCells()) {
			if (typeCell.getTransformationMode() != TransformationMode.disabled) {
				// ignore disabled cells

				Entity targetEntity = CellUtil.getFirstEntity(typeCell.getTarget());
				if (targetEntity != null) {
					// assign identifiers for type transformations
					String targetName = targetEntity.getDefinition().getDefinition().getName()
							.getLocalPart();
					String id = cellIdentifiers.getId(typeCell, targetName);
					typeIds.add(id);
				}
				else {
					reporter.warn(
							new IOMessageImpl("Ignoring type relation without target type", null));
				}
			}
		}

		// collects IDs of type cells mapped to target element names
		Map<String, QName> targetElements = new HashMap<String, QName>();

		// container
		File container = new File(workDir, "container.xsl");
		progress.setCurrentTask("Generating container");
		generateContainer(typeIds, container, targetElements);

		Set<String> passiveCellIds = new HashSet<String>(typeIds);
		progress.setCurrentTask("Generate type transformations");
		// all active cells templates
		for (Entry<String, QName> entry : targetElements.entrySet()) {
			// generate XSL fragments for type transformations
			String id = entry.getKey();
			QName elementName = entry.getValue();
			Cell typeCell = cellIdentifiers.getObject(id);

			// this is not a passive cell
			passiveCellIds.remove(id);

			XmlElement targetElement = targetSchema.getElements().get(elementName);

			String filename = "_" + id + ".xsl";
			File file = new File(workDir, filename);
			includes.add(filename);

			generateTypeTransformation(id, targetElement, typeCell, file);
		}
		// all passive cell templates
		for (String passiveId : passiveCellIds) {
			Cell typeCell = cellIdentifiers.getObject(passiveId);

			String filename = "_" + passiveId + ".xsl";
			File file = new File(workDir, filename);
			includes.add(filename);

			// XXX dummy target element
			XmlElement targetElement = new XmlElement(new QName(NS_XSL_DEFINITIONS, "dummy"), null,
					null);

			generateTypeTransformation(passiveId, targetElement, typeCell, file);

			// for passive cells no variables should be created
			typeIds.remove(passiveId);
		}

		// namespaces that occur additionally to the fixed namespaces
		Map<String, String> additionalNamespaces = new HashMap<String, String>(prefixes.asMap());
		for (String fixedPrefix : FIXED_PREFIXES.keySet()) {
			additionalNamespaces.remove(fixedPrefix);
		}
		context.put("additionalNamespaces", additionalNamespaces);

		// types cells
		/*
		 * The type identifiers are used as variable name to store the result of
		 * the equally named template.
		 */
		context.put("targets", typeIds);

		// includes
		// TODO check if files to include are actually there?
		context.put("includes", includes);

		OutputStream out = target.getOutput();
		XMLPrettyPrinter printer = new XMLPrettyPrinter(out);
		Future<?> ready = printer.start();
		Writer writer = new OutputStreamWriter(printer, "UTF-8");
		try {
			root.merge(context, writer);
			writer.flush();
		} finally {
			writer.close();
			ready.get();
			out.close();
		}

		reporter.setSuccess(reporter.getErrors().isEmpty());
		return reporter;
	}

	/**
	 * Generate a XSL fragment that is the root of transformed target files and
	 * incorporates the results of type transformation that are store as
	 * temporary documents in XSL variables.
	 * 
	 * @param typeIds the identifiers of the type transformations, they are also
	 *            the names of the variables holding the temporary documents
	 * @param templateFile the file to write the fragment to
	 * @param targetElements an empty map that is populated with variable names
	 *            mapped to target element names
	 * @throws IOException if an error occurs writing the template
	 * @throws XMLStreamException if an error occurs writing XML content to the
	 *             template
	 */
	protected void generateContainer(Set<String> typeIds, File templateFile,
			Map<String, QName> targetElements) throws XMLStreamException, IOException {
		// group typeIds by target type
		Multimap<TypeDefinition, String> groupedResults = HashMultimap.create();
		for (String typeId : typeIds) {
			Cell cell = cellIdentifiers.getObject(typeId);
			if (cell.getTransformationMode() == TransformationMode.active) {
				// only active cells get placed in the container
				Collection<? extends Entity> targetEntities = cell.getTarget().values();
				if (targetEntities.size() == 1) {
					TypeDefinition type = targetEntities.iterator().next().getDefinition()
							.getType();
					groupedResults.put(type, typeId);
				}
				else {
					throw new IllegalStateException(
							"Type cell may only have exactly one target type");
				}
			}
		}

		// generate container and integration of temporary documents
		writeContainerFragment(templateFile, groupedResults, targetElements);
	}

	/**
	 * Write the container fragment.
	 * 
	 * @param templateFile the file to write to
	 * @param groupedResults the result variable names grouped by associated
	 *            target type
	 * @param targetElements an empty map that is populated with variable names
	 *            mapped to target element names
	 * @throws IOException if an error occurs writing the template
	 * @throws XMLStreamException if an error occurs writing XML content to the
	 *             template
	 */
	private void writeContainerFragment(File templateFile,
			Multimap<TypeDefinition, String> groupedResults, Map<String, QName> targetElements)
					throws XMLStreamException, IOException {
		XMLStreamWriter writer = XslTransformationUtil.setupXMLWriter(
				new BufferedOutputStream(new FileOutputStream(templateFile)), prefixes);
		try {
			// write container
			GmlWriterUtil.writeStartElement(writer, targetContainer.getName());
			// generate an eventual required identifier on the container
			GmlWriterUtil.writeRequiredID(writer, targetContainer.getType(), null, false);

			writeContainerIntro(writer, context);

			// cache definition paths
			Map<TypeDefinition, DefinitionPath> paths = new HashMap<TypeDefinition, DefinitionPath>();

			Descent lastDescent = null;
			for (Entry<TypeDefinition, String> entry : groupedResults.entries()) {
				TypeDefinition type = entry.getKey();

				// get stored definition path for the type
				DefinitionPath defPath;
				if (paths.containsKey(type)) {
					// get the stored path, may be null
					defPath = paths.get(type);
				}
				else {
					// determine a valid definition path in the container
					defPath = findMemberAttribute(targetContainer, type);

					// store path (may be null)
					paths.put(type, defPath);
				}
				if (defPath != null) {
					// insert xsl:for-each at the appropriate position in
					// the path
					defPath = pathInsertForEach(defPath, entry.getValue(), targetElements);

					lastDescent = Descent.descend(writer, defPath, lastDescent, false, true);

					// write single target instance from variable
					GmlWriterUtil.writeEmptyElement(writer, new QName(NS_URI_XSL, "copy-of"));
					writer.writeAttribute("select", ".");
				}
				else {
					reporter.warn(new IOMessageImpl(MessageFormat.format(
							"No compatible member attribute for type {0} found in root element {1}, one instance was skipped",
							type.getDisplayName(), targetContainer.getName().getLocalPart()),
							null));
				}
			}
			if (lastDescent != null) {
				lastDescent.close();
			}

			// end container
			writer.writeEndElement();
		} finally {
			writer.close();
		}
	}

	/**
	 * Write additional content into the container before it is populated by the
	 * type relations.
	 * 
	 * @param writer the XML stream writer
	 * @param context the XSLT generation context
	 * @throws XMLStreamException if an error occurs while writing to the
	 *             container
	 * @throws IOException if an error occurs writing to the file
	 */
	@SuppressWarnings("unused")
	protected void writeContainerIntro(XMLStreamWriter writer, XsltGenerationContext context)
			throws XMLStreamException, IOException {
		// override me
	}

	/**
	 * Inserts a <code>xsl:for-each</code> element in the path before the
	 * element that may be repeated. Also removes the last path element.
	 * 
	 * @param path the path where target instances should be written to
	 * @param variable the variable name of the xsl:variable holding the
	 *            instances
	 * @param targetElements an empty map that is populated with variable names
	 *            mapped to target element names
	 * @return the adapted path including the for-each instruction and w/o the
	 *         last path element
	 */
	private DefinitionPath pathInsertForEach(DefinitionPath path, String variable,
			Map<String, QName> targetElements) {
		List<PathElement> elements = new ArrayList<PathElement>(path.getSteps());

		int index = elements.size() - 1;
		PathElement lastNonUniqueElement = null;
		while (lastNonUniqueElement == null && index >= 0) {
			PathElement element = elements.get(index);

			if (!element.isUnique()) {
				lastNonUniqueElement = element;
			}
			else {
				index--;
			}
		}

		if (lastNonUniqueElement == null) {
			// TODO instead some fall-back?
			throw new IllegalStateException("No element in member path repeatable");
		}

		/*
		 * Store last element name for variable, this information is needed for
		 * the type transformation.
		 */
		targetElements.put(variable, elements.get(elements.size() - 1).getName());

		// remove last element
		elements.remove(elements.size() - 1);

		// insert for-each element before last non-unique element

		if (index == elements.size()) {
			/*
			 * There seems to be a problem with compiling the XSLT if the
			 * for-each is the last element in the path. Then insert the whole
			 * variable in once piece.
			 */
			elements.add(index, new XslForEach("$" + variable));
		}
		else {
			// loop for each element in the variable
			elements.add(index, new XslForEach("$" + variable + "/*"));
		}

		return new DefinitionPath(elements);
	}

	/**
	 * Find a matching attribute for the given member type in the given
	 * container type
	 * 
	 * @param container the container element
	 * @param memberType the member type
	 * 
	 * @return the attribute definition or <code>null</code>
	 */
	protected DefinitionPath findMemberAttribute(XmlElement container,
			final TypeDefinition memberType) {
		AbstractTypeMatcher<TypeDefinition> matcher = new AbstractTypeMatcher<TypeDefinition>() {

			@Override
			protected DefinitionPath matchPath(TypeDefinition type, TypeDefinition matchParam,
					DefinitionPath path) {
				if (type.equals(memberType)) {
					return path;
				}

				return null;
			}
		};

		// candidate match
		List<DefinitionPath> candidates = matcher.findCandidates(container.getType(),
				container.getName(), true, memberType);
		if (candidates != null && !candidates.isEmpty()) {
			return candidates.get(0); // TODO notification? FIXME will this
										// work? possible problem: attribute is
										// selected even though better candidate
										// is in other attribute
		}

		return null;
	}

	/**
	 * Generate a XSL fragment for transformation based on the given type
	 * relation.
	 * 
	 * @param templateName name of the XSL template
	 * @param typeCell the type relation
	 * @param targetfile the target file to write the fragment to
	 * @param targetElement the target element to use to hold a transformed
	 *            instance
	 * @throws TransformationException if an unrecoverable error occurs during
	 *             the XSLT transformation generation
	 */
	protected void generateTypeTransformation(String templateName, XmlElement targetElement,
			Cell typeCell, File targetfile) throws TransformationException {
		XslTypeTransformation xslt;
		try {
			xslt = XslTypeTransformationExtension.getInstance()
					.getTransformation(typeCell.getTransformationIdentifier());
		} catch (Exception e) {
			throw new TransformationException(
					"Could not retrieve XSLT transformation generator for cell function", e);
		}

		xslt.setContext(context);

		xslt.generateTemplate(templateName, targetElement, typeCell,
				new FileIOSupplier(targetfile));
	}

}
