/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.headless.transform;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.core.report.Reporter;
import eu.esdihumboldt.hale.common.core.report.impl.DefaultReporter;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.InstanceCollectionDecorator;
import eu.esdihumboldt.hale.common.instance.model.ext.helper.InstanceIteratorDecorator;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance collection that collects stats on the read instances.
 * 
 * @author Simon Templer
 */
public class StatsCountInstanceCollection extends InstanceCollectionDecorator {

	/**
	 * Task type identifier.
	 */
	public static final String TASK_TYPE = "eu.esdihumboldt.hale.transform.source";

	private class StatsCountIterator extends InstanceIteratorDecorator {

		private final Reporter<Message> reporter;

		/**
		 * Constructor.
		 * 
		 * @param decoratee the decorated iterator
		 */
		public StatsCountIterator(ResourceIterator<Instance> decoratee) {
			super(decoratee);

			this.reporter = new DefaultReporter<Message>("Load transformation source", TASK_TYPE,
					Message.class, false);
		}

		@Override
		public void close() {
			super.close();

			reporter.setSuccess(true);
			reportHandler.publishReport(reporter);
		}

		@Override
		public Instance next() {
			Instance instance = super.next();

			reporter.stats().at("loadedPerType").at(instance.getDefinition().getName().toString())
					.next();

			return instance;
		}

		@Override
		public void skip() {
			QName typeName = null;
			if (supportsTypePeek()) {
				TypeDefinition type = typePeek();
				if (type != null) {
					typeName = type.getName();
				}
			}

			if (typeName != null) {
				// count skipped per type
				reporter.stats().at("skippedPerType").at(typeName.toString()).next();
			}

			// count skipped
			reporter.stats().at("skipped").next();

			super.skip();
		}

	}

	private final ReportHandler reportHandler;

	/**
	 * Create an instance collection that reports retrieved instances.
	 * 
	 * @param sources the instance collection to decorate
	 * @param reportHandler the report handler
	 */
	public StatsCountInstanceCollection(InstanceCollection sources, ReportHandler reportHandler) {
		super(sources);

		this.reportHandler = reportHandler;
	}

	@Override
	public ResourceIterator<Instance> iterator() {
		return new StatsCountIterator(super.iterator());
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return new StatsCountInstanceCollection(super.select(filter), reportHandler);
	}

}
