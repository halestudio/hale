///*
// * Copyright (c) 2012 Data Harmonisation Panel
// * 
// * All rights reserved. This program and the accompanying materials are made
// * available under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation, either version 3 of the License,
// * or (at your option) any later version.
// * 
// * You should have received a copy of the GNU Lesser General Public License
// * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
// * 
// * Contributors:
// *     HUMBOLDT EU Integrated Project #030962
// *     Data Harmonisation Panel <http://www.dhpanel.eu>
// */
//
//package eu.esdihumboldt.hale.ui.views.tasks.model.impl;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//
//import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
//import eu.esdihumboldt.hale.common.schema.model.Definition;
//import eu.esdihumboldt.hale.common.schema.model.Schema;
//import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
//import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
//import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
//import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;
//import eu.esdihumboldt.hale.ui.views.tasks.model.TaskType;
//
///**
// * Task based on a schema
// *
// * @author Simon Templer
// * @partner 01 / Fraunhofer Institute for Computer Graphics Research
// */
//public class SchemaTask extends DefaultTask<Definition> {
//
//	/**
//	 * The schema service
//	 */
//	protected final SchemaService schemaService;
//
//	private SchemaServiceListener schemaListener;
//
//	/**
//	 * Create a task for a schema element
//	 * 
//	 * @param serviceProvider the service provider
//	 * @param typeName the type name
//	 * @param element the schema element
//	 */
//	public SchemaTask(ServiceProvider serviceProvider, TaskType taskType, TypeDefinition element) {
//		this(serviceProvider, taskType, Collections.singletonList(element));
//	}
//
//	/**
//	 * @see DefaultTask#DefaultTask(ServiceProvider, String, List)
//	 */
//	public SchemaTask(ServiceProvider serviceProvider, TaskType taskType,
//			List<? extends Definition> context) {
//		super(serviceProvider, taskType, context);
//
//		this.schemaService = serviceProvider.getService(SchemaService.class);
//
//		schemaService.addSchemaServiceListener(schemaListener = new SchemaServiceListener() {
//
////			@Override
////			public void update(@SuppressWarnings("rawtypes") UpdateMessage message) {
////				// check if main context is still available in schema
////				String contextId = getMainContext().getIdentifier();
////
////				Definition type = schemaService.getDefinition(contextId);
////
////				if (type == null) {
////					invalidate();
////				}
////			}
//
//			@Override
//			public void schemaAdded(SchemaSpaceID spaceID, Schema schema) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void schemasCleared(SchemaSpaceID spaceID) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void mappableTypesChanged(SchemaSpaceID spaceID,
//					Collection<? extends TypeDefinition> types) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//	}
//
//	/**
//	 * @see BaseTask#dispose()
//	 */
//	@Override
//	public void dispose() {
//		schemaService.removeSchemaServiceListener(schemaListener);
//
//		super.dispose();
//	}
//
//}
