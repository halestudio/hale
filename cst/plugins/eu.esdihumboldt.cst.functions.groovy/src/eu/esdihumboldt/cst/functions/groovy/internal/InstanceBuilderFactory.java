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

package eu.esdihumboldt.cst.functions.groovy.internal;

import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationSchemas;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Factory for instance builders.
 * 
 * @author Simon Templer
 */
public class InstanceBuilderFactory {

	private final ExecutionContext executionContext;
	private final TransformationLog log;

	/**
	 * @param executionContext
	 * @param log
	 */
	public InstanceBuilderFactory(ExecutionContext executionContext, TransformationLog log) {
		this.executionContext = executionContext;
		this.log = log;
	}

	public InstanceBuilder call() {
		TransformationSchemas service = executionContext.getService(TransformationSchemas.class);
		if (service == null) {
			throw new IllegalStateException("Transformation schemas service not available");
		}

		SchemaSpace schema = service.getSchemas(SchemaSpaceID.TARGET);

		InstanceBuilder builder = new InstanceBuilder(false);
		builder.setTypes(schema);
		return builder;
	}
//	@Override
//	public Object invokeMethod(String name, Object args) {
//		List<?> argList = InvokerHelper.asList(args);
//		String namespace = null;
//		if (!argList.isEmpty() && argList.get(0) != null) {
//			namespace = argList.get(0).toString();
//		}
//		
//		TypeDefinition type = findType(name, namespace);
//		
//		if (type != null) {
//			return new InstanceBuilder(false)
//		}
//	}

}
