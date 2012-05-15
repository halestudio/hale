/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.doc.user.examples.internal.extension;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;

/**
 * Represents a declared example project
 * @author Simon Templer
 */
public class ExampleProject implements Identifiable {
	
	private final String id;
	
	private final ProjectInfo info;
	
	private final String bundleName;
	
	private final String location;
	
	/**
	 * Create an example project from a configuration element. 
	 * @param id the project identifier
	 * @param conf the configuration element
	 * @throws URISyntaxException if the project location can't be resolved to
	 *   a valid URI
	 * @throws IOException if reading the project information fails
	 * @throws IOProviderConfigurationException if the project reader wasn't
	 *   configured correctly
	 */
	public ExampleProject(String id, IConfigurationElement conf) throws URISyntaxException, IOProviderConfigurationException, IOException {
		super();
		
		this.id = id;
		
		// determine location
		bundleName = conf.getDeclaringExtension().getContributor().getName();
		Bundle bundle = Platform.getBundle(bundleName);
		
		this.location = conf.getAttribute("location");
		URL url = bundle.getResource(location);
		LocatableInputSupplier<InputStream> in = new DefaultInputSupplier(url.toURI());
		
		// load project info
		ProjectReader reader = HaleIO.findIOProvider(ProjectReader.class, in, location);
		reader.setSource(in);
		reader.execute(null);
		
		this.info = reader.getProject();
	}

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get the example project info
	 * @return the project info
	 */
	public ProjectInfo getInfo() {
		return info;
	}

	/**
	 * Get the name of the bundle the example project is contained in.
	 * @return the name of the bundle containing the project
	 */
	public String getBundleName() {
		return bundleName;
	}

	/**
	 * Get the location of the project in its bundle.
	 * @return the bundle location as path inside the bundle that contains it
	 */
	public String getLocation() {
		return location;
	}

}
