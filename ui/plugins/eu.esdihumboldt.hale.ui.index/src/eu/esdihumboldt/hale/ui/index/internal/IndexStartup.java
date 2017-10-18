package eu.esdihumboldt.hale.ui.index.internal;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.index.InstanceIndexUpdateService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Early startup plugin to add an {@link InstanceIndexUpdateService} listener
 * to the {@link AlignmentService}.
 * 
 * @author Florian Esser
 */
public class IndexStartup implements IStartup {
	
	@Override
	public void earlyStartup() {
		final InstanceIndexUpdateService indexUpdater = PlatformUI.getWorkbench().getService(InstanceIndexUpdateService.class);
		final AlignmentService alignmentService = PlatformUI.getWorkbench().getService(AlignmentService.class);
		alignmentService.addListener(indexUpdater);
	}

}
