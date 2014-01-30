/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.util.source;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import eu.esdihumboldt.hale.ui.util.internal.UIUtilitiesPlugin;

/**
 * Panel with a {@link CompilingSourceViewer} and a {@link ToolBar}.
 * 
 * @param <C> type of the compilation result, if applicable
 * 
 * @author Simon Templer
 */
public class SourceViewerPanel<C> {

	private final Composite page;

	private final CompilingSourceViewer<C> viewer;

	private final ToolBar toolbar;

	/**
	 * Create panel featuring a {@link ValidatingSourceViewer} and a
	 * {@link ToolBar}.
	 * 
	 * @param parent the parent composite
	 * @param verticalRuler the source viewer vertical ruler
	 * @param overviewRuler the source viewer overview ruler, may be
	 *            <code>null</code>
	 * @param validator the document validator
	 * @param compiler the document compiler, may be <code>null</code>
	 */
	public SourceViewerPanel(Composite parent, IVerticalRuler verticalRuler,
			IOverviewRuler overviewRuler, SourceValidator validator, SourceCompiler<C> compiler) {
		this(parent, verticalRuler, overviewRuler, overviewRuler != null, SWT.DEFAULT, SWT.DEFAULT,
				validator, compiler);
	}

	/**
	 * Create panel featuring a {@link ValidatingSourceViewer} and a
	 * {@link ToolBar}.
	 * 
	 * @param parent the parent composite
	 * @param verticalRuler the source viewer vertical ruler
	 * @param overviewRuler the source viewer overview ruler, may be
	 *            <code>null</code>
	 * @param showAnnotationsOverview if the overview ruler should be visible
	 * @param viewerWidthHint the width hint for the source viewer
	 * @param viewerHeightHint the height hint for the source viewer
	 * @param validator the document validator
	 * @param compiler the document compiler, may be <code>null</code>
	 */
	public SourceViewerPanel(Composite parent, IVerticalRuler verticalRuler,
			IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int viewerWidthHint,
			int viewerHeightHint, final SourceValidator validator, final SourceCompiler<C> compiler) {
		super();

		page = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(page);

		// create source viewer
		viewer = new CompilingSourceViewer<C>(page, verticalRuler, overviewRuler,
				showAnnotationsOverview, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL,
				validator, compiler);
		GridDataFactory.fillDefaults().grab(true, true).hint(viewerWidthHint, viewerHeightHint)
				.applyTo(viewer.getControl());

		// create toolbar
		toolbar = new ToolBar(page, SWT.VERTICAL | SWT.WRAP | SWT.FLAT);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(toolbar);

		addToolItems(toolbar);
	}

	/**
	 * Add tool items to the tool bar. The default implementation adds a tool
	 * item for en- and disabling the automatic validation.
	 * 
	 * @param toolbar the tool bar to use as parent for the {@link ToolItem}s
	 */
	protected void addToolItems(ToolBar toolbar) {
		/*
		 * Validation tool item
		 */
		final ToolItem validation = new ToolItem(toolbar, SWT.CHECK);
		final Image validImage = UIUtilitiesPlugin.imageDescriptorFromPlugin(
				UIUtilitiesPlugin.PLUGIN_ID, "icons/valid_auto.png").createImage();
		validation.setImage(validImage);
		validation.setToolTipText("Automatic validation");

		// initial selection
		validation.setSelection(getViewer().isValidationEnabled());

		// synchronize with viewer state
		getViewer().addPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (ValidatingSourceViewer.PROPERTY_VALIDATION_ENABLED.equals(event.getProperty())) {
					validation.setSelection((Boolean) event.getNewValue());
				}
			}
		});

		// update viewer state
		validation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = ((ToolItem) e.widget).getSelection();
				getViewer().setValidationEnabled(enabled);
			}
		});

		// make sure to dispose the image
		validation.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				validImage.dispose();
			}
		});
	}

	/**
	 * @return the viewer
	 */
	public CompilingSourceViewer<C> getViewer() {
		return viewer;
	}

	/**
	 * @return the toolbar
	 */
	public ToolBar getToolbar() {
		return toolbar;
	}

	/**
	 * @return the panel control
	 */
	public Control getControl() {
		return page;
	}
}
