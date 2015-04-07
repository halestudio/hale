/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceUtil;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.test.TestUtil;

/**
 * Base class for transformation tests.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationTest {

	/**
	 * Wait for needed services.
	 */
	@BeforeClass
	public static void waitForService() {
		TestUtil.startConversionService();
		TestUtil.startInstanceFactory();
	}

	/**
	 * Execute the transformation on a transformation example and test if the
	 * transformation result conforms to the expected target instances provided
	 * by the example.
	 * 
	 * @param example the transformation example
	 * 
	 * @throws Exception if any exception (mostly IO) occurs, loading the
	 *             transformation example or executing the transformation
	 */
	protected void testTransform(TransformationExample example) throws Exception {
		List<Instance> transformedData = transformData(example);
		test(example.getTargetInstances(), transformedData);
	}

	/**
	 * Compares the two given collections for equality. Order of occurrence
	 * doesn't matter for this implementation.
	 * 
	 * @param targetData the expected data
	 * @param transformedData the transformed data to test
	 */
	protected void test(InstanceCollection targetData, List<Instance> transformedData) {
		ResourceIterator<Instance> targetIter = targetData.iterator();
		// make sure we can remove instances from the list...
		transformedData = new LinkedList<Instance>(transformedData);

		int targetInstanceCount = 0;
		int transformedInstanceCount = transformedData.size();
		try {
			while (targetIter.hasNext()) {
				Instance targetInstance = targetIter.next();
				targetInstanceCount++;

				// if transformed data is empty simply continue
				// will fail equals at the end
				if (transformedData.isEmpty())
					continue;

				String error = InstanceUtil.checkInstance(targetInstance, transformedData);
				assertTrue(error, error == null);
			}
		} finally {
			targetIter.close();
		}
		if (targetInstanceCount != transformedInstanceCount) {
			StringBuilder sb = new StringBuilder();
			sb.append("Instance count does not match between target instances: \n");
			ResourceIterator<Instance> targetIterator = targetData.iterator();
			while (targetIterator.hasNext()) {
				Instance targetInstance = targetIterator.next();
				sb.append(InstanceUtil.instanceToString(targetInstance));
			}
			sb.append("\n and transformed: \n");
			for (Instance transformedInstance : transformedData) {
				sb.append(InstanceUtil.instanceToString(transformedInstance));
			}
			String message = sb.toString();
			assertEquals(message, targetInstanceCount, transformedInstanceCount);
		}

	}

	/**
	 * Executes the transformation on the example source data.
	 * 
	 * @param example the transformation example
	 * @return the transformed instances
	 * @throws Exception if an error occurs during the transformation
	 */
	protected abstract List<Instance> transformData(TransformationExample example) throws Exception;

}
