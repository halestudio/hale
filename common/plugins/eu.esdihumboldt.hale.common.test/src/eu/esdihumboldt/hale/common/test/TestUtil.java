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

package eu.esdihumboldt.hale.common.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import de.fhg.igd.osgi.util.OsgiUtils;
import de.fhg.igd.osgi.util.OsgiUtils.Condition;
import de.fhg.igd.osgi.util.OsgiUtilsActivator;
import eu.esdihumboldt.hale.common.align.io.AlignmentIO;
import eu.esdihumboldt.hale.common.align.io.impl.CastorAlignmentIO;
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex;
import eu.esdihumboldt.hale.io.gml.reader.internal.XmlInstanceReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Some static helper methods for tests.
 * 
 * @author Kai Schwierczek
 */
@SuppressWarnings("restriction")
public class TestUtil {

	/**
	 * Loads the specified XML Schema.
	 * 
	 * @param location the URI specifying the location of the schema
	 * @return the loaded schema
	 * @throws IOProviderConfigurationException if the schema reader
	 *             configuration failed
	 * @throws IOException if the schema could not be loaded
	 */
	public static Schema loadSchema(URI location)
			throws IOProviderConfigurationException, IOException {
		DefaultInputSupplier input = new DefaultInputSupplier(location);

		XmlSchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(new DefaultTypeIndex());
		reader.setSource(input);

		reader.validate();
		IOReport report = reader.execute(null);

		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());

		return reader.getSchema();
	}

	/**
	 * Loads the specified alignment. Assumes that its base alignments don't
	 * need a location update.
	 * 
	 * @param location the URI specifying the location of the alignment
	 * @param sourceTypes the source type index
	 * @param targetTypes the target type index
	 * @return the loaded alignment
	 * @throws Exception if the alignment or other resources could not be loaded
	 */
	public static Alignment loadAlignment(final URI location, Schema sourceTypes,
			Schema targetTypes) throws Exception {
		DefaultInputSupplier input = new DefaultInputSupplier(location);

		IOReporter report = new DefaultIOReporter(new Locatable() {

			@Override
			public URI getLocation() {
				return location;
			}
		}, "Load alignment", AlignmentIO.ACTION_LOAD_ALIGNMENT, true);
		Alignment alignment;
		try {
			alignment = CastorAlignmentIO.load(input.getInput(), report, sourceTypes, targetTypes,
					new PathUpdate(null, null));
		} catch (Exception e) {
			alignment = JaxbAlignmentIO.load(input.getInput(), report, sourceTypes, targetTypes,
					new PathUpdate(null, null), null, null);
		}

		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());

		return alignment;
	}

	/**
	 * Loads an instance collection from the specified XML file with the given
	 * source types.
	 * 
	 * @param location the URI specifying the location of the xml instance file
	 * @param types the type index
	 * @return the loaded instance collection
	 * @throws IOException if loading the instance failed
	 * @throws IOProviderConfigurationException if configuring the instance
	 *             reader failed
	 */
	public static InstanceCollection loadInstances(URI location, Schema types)
			throws IOProviderConfigurationException, IOException {
		DefaultInputSupplier input = new DefaultInputSupplier(location);

		XmlInstanceReader instanceReader = new XmlInstanceReader();
		instanceReader.setSource(input);
		instanceReader.setSourceSchema(types);
		IOReport report = instanceReader.execute(null);

		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());

		return instanceReader.getInstances();
	}

	/**
	 * Starts the conversion service.
	 */
	public static void startConversionService() {
		// no longer any action needed
	}

	/**
	 * Starts the instance factory.
	 */
	public static void startInstanceFactory() {
		// no longer any action needed
	}

	/**
	 * Starts the instance factory.
	 */
	public static void startTransformationService() {
		// no longer any action needed
	}

	/**
	 * Uninstalls a bundle with the given symbolic name.
	 * 
	 * @param name the symbolic name of the bundle to uninstall
	 */
	public static void uninstallBundle(String name) {
		BundleContext context = OsgiUtilsActivator.getInstance().getContext();
		for (Bundle bundle : context.getBundles()) {
			if (bundle.getSymbolicName().equals(name)) {
				try {
					bundle.uninstall();
				} catch (BundleException e) {
					throw new IllegalStateException("Failed to uninstall bundle", e);
				}
			}
		}
	}

	/**
	 * Start the given bundles and then check that the given service is
	 * available.
	 * 
	 * XXX HACKHACK
	 * 
	 * @param bundlesToStart the bundles to start
	 * @param serviceToCheck the service to check
	 */
	public static void startService(List<String> bundlesToStart, final Class<?> serviceToCheck) {
		Map<String, Bundle> bundles = new HashMap<String, Bundle>();
		BundleContext context = OsgiUtilsActivator.getInstance().getContext();
		for (Bundle bundle : context.getBundles()) {
			bundles.put(bundle.getSymbolicName(), bundle);
		}

		for (String bundleName : bundlesToStart) {
			startBundle(bundleName, bundles);
		}

		assertTrue("Service " + serviceToCheck.getSimpleName() + " not available",
				OsgiUtils.waitUntil(new Condition() {

					@Override
					public boolean evaluate() {
						return OsgiUtils.getService(serviceToCheck) != null;
					}
				}, 30));
	}

	/**
	 * Start the bundle with the given name.
	 * 
	 * @param bundleName the name of the bundle to start
	 */
	public static void startBundle(String bundleName) {
		BundleContext context = OsgiUtilsActivator.getInstance().getContext();
		for (Bundle bundle : context.getBundles()) {
			if (bundleName.equals(bundle.getSymbolicName())) {
				startBundle(bundle);
				return;
			}
		}

		throw new IllegalStateException("Bundle " + bundleName + " not found");
	}

	private static void startBundle(String bundleName, Map<String, Bundle> bundles) {
		Bundle bundle = bundles.get(bundleName);
		assertNotNull("Bundle not found: " + bundleName, bundle);
		startBundle(bundle);
	}

	private static void startBundle(Bundle bundle) {
		if ((bundle.getState() & Bundle.ACTIVE) != 0)
			return;
		try {
			bundle.start();
		} catch (BundleException be) {
			fail("Could not start bundle " + bundle.getSymbolicName() + ": " + be.toString());
		}
		// without arguments on start a postcondition is that the bundle is
		// ACTIVE
		assertTrue("Bundle state not ACTIVE", (bundle.getState() & Bundle.ACTIVE) != 0);
	}
}
