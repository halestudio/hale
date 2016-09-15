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

package eu.esdihumboldt.hale.ui.service.internal;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.service.FunctionService;
import eu.esdihumboldt.hale.common.align.service.TransformationFunctionService;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationSchemas;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;
import eu.esdihumboldt.hale.ui.compatibility.extension.impl.CompatibilityServiceImpl;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.internal.AlignmentServiceImpl;
import eu.esdihumboldt.hale.ui.service.align.internal.AlignmentServiceUndoSupport;
import eu.esdihumboldt.hale.ui.service.align.resolver.UserFallbackEntityResolver;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.internal.EntityDefinitionServiceImpl;
import eu.esdihumboldt.hale.ui.service.entity.internal.EntityDefinitionServiceUndoSupport;
import eu.esdihumboldt.hale.ui.service.geometry.ProjectGeometrySchemaService;
import eu.esdihumboldt.hale.ui.service.groovy.internal.PreferencesGroovyService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.internal.orient.OrientInstanceService;
import eu.esdihumboldt.hale.ui.service.instance.sample.InstanceSampleService;
import eu.esdihumboldt.hale.ui.service.instance.sample.InstanceViewService;
import eu.esdihumboldt.hale.ui.service.instance.sample.internal.InstanceSampleServiceImpl;
import eu.esdihumboldt.hale.ui.service.instance.sample.internal.InstanceViewServiceImpl;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService;
import eu.esdihumboldt.hale.ui.service.instance.validation.internal.InstanceValidationServiceImpl;
import eu.esdihumboldt.hale.ui.service.population.internal.PopulationServiceImpl;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.RecentProjectsService;
import eu.esdihumboldt.hale.ui.service.project.RecentResources;
import eu.esdihumboldt.hale.ui.service.project.internal.ProjectServiceImpl;
import eu.esdihumboldt.hale.ui.service.project.internal.RecentProjectsServiceImpl;
import eu.esdihumboldt.hale.ui.service.project.internal.resources.RecentResourcesService;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.service.report.internal.ReportServiceImpl;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.internal.SchemaServiceImpl;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesService;
import eu.esdihumboldt.hale.ui.service.values.internal.OccurringValuesServiceImpl;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;

/**
 * Factory for HALE services
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class HaleServiceFactory extends AbstractServiceFactory {

	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator,
	 *      IServiceLocator)
	 */
	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface,
			IServiceLocator parentLocator, IServiceLocator locator) {
		if (ReportService.class.equals(serviceInterface)) {
			return new ReportServiceImpl();
		}

		if (InstanceService.class.equals(serviceInterface)) {
			return OrientInstanceService.getInstance(locator.getService(SchemaService.class),
					locator.getService(ProjectService.class),
					locator.getService(AlignmentService.class),
					locator.getService(GroovyService.class));
		}

		if (OccurringValuesService.class.equals(serviceInterface)) {
			return new OccurringValuesServiceImpl(locator.getService(InstanceService.class),
					locator.getService(ProjectService.class));
		}

		if (CompatibilityService.class.equals(serviceInterface)) {
			return new CompatibilityServiceImpl();

		}

		if (AlignmentService.class.equals(serviceInterface)) {
			return new AlignmentServiceUndoSupport(
					new AlignmentServiceImpl(locator.getService(ProjectService.class)));
		}

		if (ProjectService.class.equals(serviceInterface)) {
			return new ProjectServiceImpl();
		}

		if (ProjectInfoService.class.equals(serviceInterface)) {
			return locator.getService(ProjectService.class);
		}

		if (RecentProjectsService.class.equals(serviceInterface)) {
			return new RecentProjectsServiceImpl();
		}

		if (SchemaService.class.equals(serviceInterface)) {
			return new SchemaServiceImpl(locator.getService(ProjectService.class));
		}

		if (TransformationSchemas.class.equals(serviceInterface)) {
			return locator.getService(SchemaService.class);
		}

		if (EntityDefinitionService.class.equals(serviceInterface)) {
			return new EntityDefinitionServiceUndoSupport(
					new EntityDefinitionServiceImpl(locator.getService(AlignmentService.class),
							locator.getService(ProjectService.class)));
		}

		if (InstanceSampleService.class.equals(serviceInterface)) {
			return new InstanceSampleServiceImpl();
		}

		if (GeometrySchemaService.class.equals(serviceInterface)) {
			return new ProjectGeometrySchemaService(locator.getService(ProjectService.class));
		}

		if (InstanceValidationService.class.equals(serviceInterface))
			return new InstanceValidationServiceImpl(locator.getService(InstanceService.class),
					locator.getService(ReportService.class));

		if (PopulationService.class.equals(serviceInterface)) {
			return new PopulationServiceImpl(locator.getService(InstanceService.class));
		}

		if (RecentResources.class.equals(serviceInterface)) {
			return new RecentResourcesService(locator.getService(ProjectService.class));
		}

		if (InstanceViewService.class.equals(serviceInterface)) {
			return new InstanceViewServiceImpl(locator.getService(ProjectService.class));
		}

		if (EntityResolver.class.equals(serviceInterface)) {
			return new UserFallbackEntityResolver();
		}

		if (GroovyService.class.equals(serviceInterface)) {
			return new PreferencesGroovyService(locator.getService(ProjectService.class),
					locator.getService(AlignmentService.class));
		}

		if (FunctionService.class.equals(serviceInterface)) {
			return new HaleFunctionService(locator.getService(AlignmentService.class));
		}

		if (TransformationFunctionService.class.equals(serviceInterface)) {
			return new HaleTransformationFunctionService(
					locator.getService(AlignmentService.class));
		}

		return null;
	}

}
