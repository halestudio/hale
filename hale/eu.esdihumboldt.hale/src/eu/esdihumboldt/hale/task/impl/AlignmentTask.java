/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.task.impl;

import java.util.List;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.alignment.AlignmentServiceListener;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.task.ServiceProvider;

/**
 * A task that listens to the alignment service
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AlignmentTask extends DefaultTask implements AlignmentServiceListener {
	
	/**
	 * The alignment service
	 */
	protected final AlignmentService alignmentService;
	
	/**
	 * The schema service
	 */
	protected final SchemaService schemaService;

	private HaleServiceListener schemaListener;

	/**
	 * Creates an alignment task
	 * 
	 * @param serviceProvider the service provider
	 * @param typeName the type name
	 * @param context the task context
	 */
	public AlignmentTask(ServiceProvider serviceProvider, String typeName, 
			List<? extends Definition> context) {
		super(serviceProvider, typeName, context);
		
		this.alignmentService = serviceProvider.getService(AlignmentService.class);
		this.schemaService = serviceProvider.getService(SchemaService.class);
		
		alignmentService.addListener(this);
		
		schemaService.addListener(schemaListener = new HaleServiceListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
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
	 * @see HaleServiceListener#update(UpdateMessage)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(UpdateMessage message) {
		// override me - but only if it's really necessary, rather use other event notifications
	}

	/**
	 * @see AlignmentServiceListener#alignmentCleared()
	 */
	@Override
	public void alignmentCleared() {
		// override me
	}

	/**
	 * @see AlignmentServiceListener#cellRemoved(ICell)
	 */
	@Override
	public void cellRemoved(ICell cell) {
		// override me
	}

	/**
	 * @see AlignmentServiceListener#cellsAdded(Iterable)
	 */
	@Override
	public void cellsAdded(Iterable<ICell> cells) {
		// override me
	}

	/**
	 * @see AlignmentServiceListener#cellsUpdated(Iterable)
	 */
	@Override
	public void cellsUpdated(Iterable<ICell> cells) {
		// override me
	}

	/**
	 * @see DefaultTask#dispose()
	 */
	@Override
	public void dispose() {
		alignmentService.removeListener(this);
		schemaService.removeListener(schemaListener);
		
		super.dispose();
	}

}
