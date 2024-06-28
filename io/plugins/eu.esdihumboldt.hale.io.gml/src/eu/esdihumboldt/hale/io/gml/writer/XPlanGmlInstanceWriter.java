/*
 * Copyright (c) 2020 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.gml.writer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.graph.reference.ReferenceGraph;
import eu.esdihumboldt.hale.common.instance.graph.reference.impl.XMLInspector;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.io.gml.writer.internal.DefaultMultipartHandler;
import eu.esdihumboldt.hale.io.gml.writer.internal.MultipartHandler;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.util.Pair;

/**
 * Writes instances to a XPlanGML XPlanAuszug
 * 
 * @author Florian Esser
 */
public class XPlanGmlInstanceWriter extends StreamGmlWriter {

	/**
	 * The identifier of the writer as registered to the I/O provider extension.
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.gml.xplan.writer";

	/**
	 * The base part of all XPlanGML namespace URIs
	 */
	public static final String XPLAN_NS_BASE = "http://www.xplanung.de/xplangml/";

	/**
	 * Name of the parameter to create separate files for each feature type
	 */
	public static final String PARAM_PARTITION_BY_PLAN = "partition.byPlan";

	/**
	 * Default constructor
	 */
	public XPlanGmlInstanceWriter() {
		super(true);
	}

	/**
	 * @see StreamGmlWriter#requiresDefaultContainer()
	 */
	@Override
	protected boolean requiresDefaultContainer() {
		return true; // requires an XPlanAuszug element being present
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter#isFeatureCollection(eu.esdihumboldt.hale.io.xsd.model.XmlElement)
	 */
	@Override
	protected boolean isFeatureCollection(XmlElement el) {
		return el.getName().getLocalPart().contains("XPlanAuszug")
				&& !el.getType().getConstraint(AbstractFlag.class).isEnabled()
				&& hasChild(el.getType(), "featureMember"); //$NON-NLS-1$
	}

	private boolean isPartitionByPlanConfigured() {
		return getParameter(PARAM_PARTITION_BY_PLAN).as(Boolean.class, false);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		init();

		if (isPartitionByPlanConfigured()) {
			partitionByPlan(progress, reporter);
		}
		else {
			return super.execute(progress, reporter);
		}

		return reporter;
	}

	private void partitionByPlan(ProgressIndicator progress, IOReporter reporter)
			throws IOException {

		final Set<TypeDefinition> planTypes = collectPlanTypes(getTargetSchema().getTypes());

		/*
		 * Split instances into plan and non-plan instances. Associate the ID of
		 * a plan with its plan type and the plan instance.
		 */
		final XMLInspector gadget = new XMLInspector();
		final DefaultInstanceCollection nonPlanInstances = new DefaultInstanceCollection();
		final Map<String, TypeDefinition> planIdToPlanTypeMapping = new HashMap<>();
		final Map<String, InstanceCollection> planIdToInstancesMapping = new HashMap<>();
		try (ResourceIterator<Instance> it = getInstances().iterator()) {
			while (it.hasNext()) {
				Instance inst = it.next();
				if (!planTypes.contains(inst.getDefinition())) {
					nonPlanInstances.add(inst);
					continue;
				}

				String planId = gadget.getIdentity(inst);
				planIdToInstancesMapping.put(planId,
						new DefaultInstanceCollection(Arrays.asList(inst)));
				planIdToPlanTypeMapping.put(planId, inst.getDefinition());
			}
		}

		/*
		 * Collect referenced instances for every plan instance
		 */
		for (String planId : planIdToInstancesMapping.keySet()) {
			MultiInstanceCollection mic = new MultiInstanceCollection(
					Arrays.asList(planIdToInstancesMapping.get(planId), nonPlanInstances));
			ReferenceGraph<String> rg = new ReferenceGraph<String>(new XMLInspector(), mic, planId);

			Iterator<InstanceCollection> p = rg.partition(1, reporter);
			while (p.hasNext()) {
				boolean found = false;
				InstanceCollection c = p.next();
				Iterator<Instance> it = c.iterator();
				while (it.hasNext()) {
					Instance i = it.next();
					if (planId.equals(gadget.getIdentity(i))) {
						planIdToInstancesMapping.put(planId, c);
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
		}

		final MultipartHandler handler = new MultipartHandler() {

			@Override
			public String getTargetFilename(InstanceCollection part, URI originalTarget) {
				Path origPath = Paths.get(originalTarget).normalize();
				Pair<String, String> nameAndExt = DefaultMultipartHandler
						.getFileNameAndExtension(origPath.toString());

				String planId = null;
				for (Entry<String, InstanceCollection> mapping : planIdToInstancesMapping
						.entrySet()) {
					if (part == mapping.getValue()) {
						planId = mapping.getKey();
						break;
					}
				}

				if (planId == null) {
					throw new RuntimeException("Plan was not seen before");
				}

				// Replace all characters that are not allowed in XML IDs with
				// an underscore. In addition, the colon (:) is also replaced
				// to make sure that the resulting String can be used safely in
				// a file name.
				String sanitizedPlanId = planId.replaceAll("[^A-Za-z0-9-_.]", "_");
				return String.format("%s%s%s.%s.%s.%s", origPath.getParent().toString(),
						File.separator, nameAndExt.getFirst(),
						planIdToPlanTypeMapping.get(planId).getDisplayName(), sanitizedPlanId,
						nameAndExt.getSecond());
			}
		};

		try {
			writeParts(planIdToInstancesMapping.values().iterator(), handler, progress, reporter);
		} catch (XMLStreamException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private Set<TypeDefinition> collectPlanTypes(Collection<? extends TypeDefinition> types) {
		final Set<TypeDefinition> planTypes = new HashSet<>();

		for (TypeDefinition type : types) {
			QName typeName = type.getName();
			if (typeName.getNamespaceURI().toString().startsWith(XPLAN_NS_BASE)
					&& typeName.getLocalPart().endsWith("_PlanType")) {
				planTypes.add(type);
			}
		}
		return planTypes;
	}
}
