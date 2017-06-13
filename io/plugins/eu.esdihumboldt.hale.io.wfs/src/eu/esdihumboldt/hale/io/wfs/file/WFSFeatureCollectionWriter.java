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

package eu.esdihumboldt.hale.io.wfs.file;

import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xmlbeans.XmlDateTime;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableURI;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.geometry.GMLConstants;
import eu.esdihumboldt.hale.io.gml.writer.GmlInstanceWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil;
import eu.esdihumboldt.hale.io.wfs.WFSConstants;
import eu.esdihumboldt.hale.io.wfs.WFSVersion;
import eu.esdihumboldt.hale.io.wfs.WFSWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xsd.reader.internal.SubstitutionGroupProperty;

/**
 * Write GML features as a WFS FeatureCollection.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class WFSFeatureCollectionWriter extends GmlInstanceWriter
		implements WFSWriter, WFSConstants {

	/**
	 * Parameter to skip counting instances.
	 */
	public static final String PARAM_SKIP_COUNT = "skipFeatureCount";

	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (getWFSVersion() == null) {
			fail("A WFS version must be provided");
		}
	}

	@Override
	protected XmlElement findDefaultContainter(XmlIndex targetIndex, IOReporter reporter) {
		WFSVersion version = getWFSVersion();
		if (version != null) {
			XmlElement result = createFeatureCollectionElement(version, targetIndex);
			if (result != null) {
				return result;
			}
		}

		throw new IllegalStateException("Unable to write to WFS FeatureCollection");
	}

	private XmlElement createFeatureCollectionElement(WFSVersion version, XmlIndex targetIndex) {
		String wfsNs = version.wfsNamespace;
		XmlElement memberElement = null;
		switch (version) {
		case V1_1_0:
			memberElement = targetIndex.getElements()
					.get(new QName(GMLConstants.NS_GML, "_Feature"));
			break;
		case V2_0_0:
		case V2_0_2:
			for (XmlElement element : targetIndex.getElements().values()) {
				if (element.getName().getLocalPart().equals("AbstractFeature") && element.getName()
						.getNamespaceURI().startsWith(GMLConstants.GML_NAMESPACE_CORE)) {
					memberElement = element;
					break;
				}
			}
			break;
		}

		if (memberElement == null) {
			throw new IllegalStateException(
					"Unable to identify member types for feature collection");
		}

		addValidationSchema(version.wfsNamespace, new LocatableURI(version.schemaLocation), "wfs");

		List<XmlElement> memberElements = SubstitutionGroupProperty
				.collectSubstitutions(memberElement.getName(), memberElement.getType());

		TypeDefinition fcType = WFSSchemaHelper.createFeatureCollectionType(version,
				memberElements);

		return new XmlElement(new QName(wfsNs, "FeatureCollection"), fcType, null);
	}

	@Override
	protected void writeAdditionalElements(XMLStreamWriter writer,
			TypeDefinition containerDefinition, IOReporter reporter) throws XMLStreamException {
		// write additional needed attributes for WFS 2.0

		boolean requiresCount = requiresCount();
		boolean skipCount = getParameter(PARAM_SKIP_COUNT).as(Boolean.class, false);

		if (requiresCount) {
			String countString = null;

			InstanceCollection source = getInstances();

			if (source.hasSize()) {
				// no iteration needed
				countString = String.valueOf(source.size());
			}
			else if (!skipCount) {
				// count features
				int count = 0;
				// need to iterate collection to determine size
				try (ResourceIterator<Instance> it = source.iterator()) {
					while (it.hasNext()) {
						Instance candidate = it.next();
						if (GmlWriterUtil.isFeatureType(candidate.getDefinition())) {
							count++;
						}
					}
				}

				countString = String.valueOf(count);
			}

			// numberMatched
			if (countString != null) {
				writer.writeAttribute("numberMatched", countString);
			}
			else {
				writer.writeAttribute("numberMatched", "unknown");
			}

			// numberReturned
			if (countString != null) {
				writer.writeAttribute("numberReturned", countString);
			}
			else {
				writer.writeAttribute("numberReturned", "0");
			}

			// timestamp
			XmlDateTime result = XmlDateTime.Factory.newInstance();
			result.setDateValue(new Date());
			writer.writeAttribute("timeStamp", result.getStringValue());
		}

		super.writeAdditionalElements(writer, containerDefinition, reporter);
	}

	private boolean requiresCount() {
		WFSVersion version = getWFSVersion();
		if (version != null) {
			switch (version) {
			case V2_0_0:
			case V2_0_2:
				return true;
			case V1_1_0:
			default:
				return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	public void setWFSVersion(WFSVersion version) {
		setParameter(PARAM_WFS_VERSION, Value.of(version.versionString));
	}

	@Override
	public WFSVersion getWFSVersion() {
		String versionString = getParameter(PARAM_WFS_VERSION).as(String.class);
		if (versionString == null) {
			return null;
		}
		else
			return WFSVersion.fromString(versionString, null);
	}

	@Override
	public boolean isPassthrough() {
		if (!super.isPassthrough()) {
			return false;
		}

		boolean requiresCount = requiresCount();
		boolean skipCount = getParameter(PARAM_SKIP_COUNT).as(Boolean.class, false);

		return !requiresCount || skipCount;
	}

}
