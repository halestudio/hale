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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.locationtech.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.InspireDatasetFeedWriter;
import eu.esdihumboldt.hale.io.gml.writer.InspireInstanceWriter;
import eu.esdihumboldt.hale.io.wfs.WFSVersion;
import eu.esdihumboldt.util.Pair;

/**
 * Concrete class for writing a WFS 2.0 FeatureCollection. Needed so HALE can
 * determine without setting a parameter if an appropriate container can be
 * found.
 * 
 * @author Simon Templer
 */
public class WFS2FCWriter extends WFSFeatureCollectionWriter {

	private final Set<TypeDefinition> types = new HashSet<>();
	private final Multiset<CRSDefinition> crss = HashMultiset.create();

	/**
	 * Constructor.
	 */
	public WFS2FCWriter() {
		super();
		setWFSVersion(WFSVersion.V2_0_0);

		addSupportedParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_CREATE_FEED);

		for (String param : InspireDatasetFeedWriter.getAdditionalParams()) {
			addSupportedParameter(param);
		}
	}

	@SuppressWarnings("restriction")
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		super.execute(progress, reporter);

		// run feed writer if applicable
		if (getParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_CREATE_FEED).as(
				Boolean.class, false)) {
			InspireDatasetFeedWriter feedWriter = new InspireDatasetFeedWriter();
			for (String copyParam : InspireDatasetFeedWriter.getAdditionalParams()) {
				feedWriter.setParameter(copyParam, getParameter(copyParam));
			}
			feedWriter.setOccurringTypes(types);
			feedWriter.setOccurringCRSs(crss);

			feedWriter.setParameter(PARAM_TARGET, Value.of(InspireInstanceWriter
					.getDatasetFeedTarget(getTarget().getLocation()).toString()));

			reporter.importMessages(feedWriter.execute(progress));
			// Ignore success of feedWriter-report, since main success criteria
			// is successfully writing the data.
		}

		return reporter;
	}

	@SuppressWarnings("restriction")
	@Override
	protected void writeMember(Instance instance, TypeDefinition type, IOReporter report)
			throws XMLStreamException {
		// collect written types in case a dataset feed will be written
		types.add(type);
		super.writeMember(instance, type, report);
	}

	@Override
	protected Pair<Geometry, CRSDefinition> extractGeometry(Object value, boolean allowConvert,
			IOReporter report) {
		// collect used CRS definitions
		Pair<Geometry, CRSDefinition> result = super.extractGeometry(value, allowConvert, report);
		if (result != null) {
			crss.add(result.getSecond());
		}
		return result;
	}

}
