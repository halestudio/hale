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

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.util.introspection.Info;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.AbstractTypeMatcher;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.util.CustomIdentifiers;

/**
 * Generate a XSLT transformation from an {@link Alignment}. Each generation
 * process has to use its own instance.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class XsltGenerator {

	/**
	 * Fixed prefix for the XSLT namespace.
	 */
	public static final String NS_PREFIX_XSL = "xsl";

	/**
	 * Fixed prefix for the XML Schema Instance namespace.
	 */
	public static final String NS_PREFIX_XSI = "xsi";

	/**
	 * Fixed prefix for the XML Schema namespace.
	 */
	public static final String NS_PREFIX_XS = "xs";

	/**
	 * Namespace URI for XSLT.
	 */
	public static final String NS_URI_XSL = "http://www.w3.org/1999/XSL/Transform";

	/**
	 * Fixed namespace prefixes. Prefixes mapped to namespaces.
	 */
	private static final Map<String, String> FIXED_PREFIXES = ImmutableMap.of( //
			NS_PREFIX_XSI, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, //
			NS_PREFIX_XS, XMLConstants.W3C_XML_SCHEMA_NS_URI, //
			NS_PREFIX_XSL, NS_URI_XSL);

	/**
	 * The prefix for generated namespace prefixes.
	 */
	private static final String DEFAULT_NS_PREFIX = "ns";

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
	 * The working directory where the templates reside.
	 */
	private final File workDir;

	/**
	 * Namespace prefixes mapped to namespaces.
	 */
	private final BiMap<String, String> prefixes;

	/**
	 * The default event cartridge.
	 */
	private final EventCartridge eventCartridge = new EventCartridge();

	/**
	 * The cell identifiers.
	 */
	private final CustomIdentifiers<Cell> cellIdentifiers = new CustomIdentifiers<Cell>(Cell.class,
			true);

	/**
	 * The name of the container in the target schema.
	 */
	private QName targetContainer;

	/**
	 * Create a XSLT generator.
	 * 
	 * @param workDir the working directory where the generator may store
	 *            temporary files, the caller is responsible for cleaning this
	 *            up, e.g. after {@link #write(LocatableOutputSupplier)} was
	 *            called
	 * @param alignment the alignment
	 * @param targetSchema the target schema
	 * @param reporter the reporter for documenting errors
	 * @param progress the progress indicator for indicating the generation
	 *            progress
	 * @param targetContainer the name of the element to serve as document root
	 *            in the target XML file
	 * @throws Exception if an error occurs initializing the generator
	 */
	public XsltGenerator(File workDir, Alignment alignment, SchemaSpace targetSchema,
			IOReporter reporter, ProgressIndicator progress, QName targetContainer)
			throws Exception {
		this.reporter = reporter;
		this.progress = progress;
		this.alignment = alignment;
		this.workDir = workDir;
		this.targetContainer = targetContainer;

		XmlIndex index = StreamGmlWriter.getXMLIndex(targetSchema);
		if (index == null) {
			throw new IllegalArgumentException("Target schema contains no XML schema");
		}
		this.targetSchema = index;

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
		Map<String, String> prefixes = new HashMap<String, String>(FIXED_PREFIXES);
		for (Entry<String, String> pair : this.targetSchema.getPrefixes().entrySet()) {
			String ns = pair.getKey();
			String prefix = pair.getValue();

			if (!prefixes.containsValue(ns)) {
				// namespace not yet added
				int i = 1;
				while (prefix == null || prefix.isEmpty() || prefixes.containsKey(prefix)) {
					// find an alternate prefix
					prefix = DEFAULT_NS_PREFIX + i++;
				}
				prefixes.put(prefix, ns);
			}
		}
		this.prefixes = ImmutableBiMap.copyOf(prefixes);

		// initialize default event cartridge
		eventCartridge.addInvalidReferenceEventHandler(new InvalidReferenceEventHandler() {

			private void report(Info info, String reference) {
				throw new ParseErrorException("Error while merging template - invalid reference: "
						+ reference, info, reference);
			}

			@Override
			public boolean invalidSetMethod(Context context, String leftreference,
					String rightreference, Info info) {
				report(info, leftreference + "." + rightreference);
				return false;
			}

			@Override
			public Object invalidMethod(Context context, String reference, Object object,
					String method, Info info) {
				report(info, reference);
				return null;
			}

			@Override
			public Object invalidGetMethod(Context context, String reference, Object object,
					String property, Info info) {
				report(info, reference);
				return null;
			}
		});
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

		VelocityContext context = createContext();
		// collects XSL fragments to include in the main file
		Set<String> includes = new HashSet<String>();
		// collects IDs of type cells
		Set<String> typeIds = new HashSet<String>();

		// type cells
		for (Cell typeCell : alignment.getTypeCells()) {
			Entity targetEntity = CellUtil.getFirstEntity(typeCell.getTarget());
			if (targetEntity != null) {
				String targetName = targetEntity.getDefinition().getDefinition().getName()
						.getLocalPart();
				String id = cellIdentifiers.getId(typeCell, targetName);
				String filename = "_" + id + ".xsl";

				File file = new File(workDir, filename);
				generateTypeTransformation(id, typeCell, file);

				includes.add(filename);
				typeIds.add(id);
			}
			else {
				reporter.warn(new IOMessageImpl("Ignoring type relation without target type", null));
			}
		}

		// container
		File container = new File(workDir, "container.xsl");
		generateContainer(typeIds, container);

		// namespaces that occur additionally to the fixed namespaces
		Map<String, String> additionalNamespaces = new HashMap<String, String>(prefixes);
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
		context.put("includes", includes);

		OutputStream out = target.getOutput();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		try {
			root.merge(context, writer);
			writer.flush();
		} finally {
			writer.close();
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
	 */
	protected void generateContainer(Set<String> typeIds, File templateFile) {
		// TODO determine container element and type

		// TODO group typeIds by target type

		// TODO find definition paths where target types fit in

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
		List<DefinitionPath> candidates = matcher.findCandidates(container, containerName, true,
				memberType);
		if (candidates != null && !candidates.isEmpty()) {
			return candidates.get(0); // TODO notification? FIXME will this
										// work? possible problem: attribute is
										// selected even though better candidate
										// is in other attribute
		}

		return null;

		// TODO generate container and integration of temporary documents
	}

	/**
	 * Generate a XSL fragment for transformation based on the given type
	 * relation.
	 * 
	 * @param templateName name of the XSL template
	 * @param typeCell the type relation
	 * @param targetfile the target file to write the fragment to
	 */
	protected void generateTypeTransformation(String templateName, Cell typeCell, File targetfile) {
		// TODO Auto-generated method stub

	}

	/**
	 * Create a new {@link VelocityContext}.
	 * 
	 * @return the context configured with the default event cartridge
	 */
	protected VelocityContext createContext() {
		VelocityContext context = new VelocityContext();
		context.attachEventCartridge(eventCartridge);
		return context;
	}

}
