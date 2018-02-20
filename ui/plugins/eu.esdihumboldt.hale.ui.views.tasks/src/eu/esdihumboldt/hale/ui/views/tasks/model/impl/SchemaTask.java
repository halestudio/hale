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

package eu.esdihumboldt.hale.ui.views.tasks.model.impl;

import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.ui.service.HaleServiceListener;
import eu.esdihumboldt.hale.ui.service.UpdateMessage;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;

/**
 * Task based on a schema
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SchemaTask extends DefaultTask {
	
	/**
	 * The schema service
	 */
	protected final SchemaService schemaService;

	private HaleServiceListener schemaListener;
	
	/**
	 * Create a task for a schema element
	 * 
	 * @param serviceProvider the service provider
	 * @param typeName the type name
	 * @param element the schema element
	 */
	public SchemaTask(ServiceProvider serviceProvider, String typeName,
			SchemaElement element) {
		this(serviceProvider, typeName, Collections.singletonList(element));
	}

	/**
	 * @see DefaultTask#DefaultTask(ServiceProvider, String, List)
	 */
	public SchemaTask(ServiceProvider serviceProvider, String typeName,
			List<? extends Definition> context) {
		super(serviceProvider, typeName, context);
		
		this.schemaService = serviceProvider.getService(SchemaService.class);
		
		schemaService.addListener(schemaListener = new HaleServiceListener() {
			
			@Override
			public void update(@SuppressWarnings("rawtypes") UpdateMessage message) {
				// check if main context is still available in schema
				String contextId = getMainContext().getIdentifier();
				
				Definition type = schemaService.getDefinition(contextId);
				
				if (type == null) {
					invalidate();
				}
			}
		});
	}

	/**
	 * @see BaseTask#dispose()
	 */
	@Override
	public void dispose() {
		schemaService.removeListener(schemaListener);
		
		super.dispose();
	}

}
