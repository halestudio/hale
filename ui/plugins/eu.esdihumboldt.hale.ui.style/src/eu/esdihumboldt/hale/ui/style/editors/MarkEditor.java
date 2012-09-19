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
package eu.esdihumboldt.hale.ui.style.editors;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.geotools.styling.Mark;
import org.geotools.styling.StyleBuilder;

import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Editor for {@link Mark}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class MarkEditor implements Editor<Mark> {

	/**
	 * Mark names for {@link Mark} creation
	 */
	private static final String[] MARKS = new String[] { StyleBuilder.MARK_X,
			StyleBuilder.MARK_ARROW, StyleBuilder.MARK_CIRCLE, StyleBuilder.MARK_CROSS,
			StyleBuilder.MARK_SQUARE, StyleBuilder.MARK_STAR, StyleBuilder.MARK_TRIANGLE };

	private static final StyleBuilder styleBuilder = new StyleBuilder();

	private final Composite page;

	private final StrokeEditor strokeEditor;

	private final FillEditor fillEditor;

	private final ComboViewer markView;

	private boolean changed = false;

	/**
	 * Creates a {@link Mark} editor
	 * 
	 * @param parent the parent composite
	 * @param mark the initial mark
	 */
	public MarkEditor(Composite parent, Mark mark) {
		if (mark == null) {
			mark = styleBuilder.createMark(StyleBuilder.MARK_SQUARE);
		}

		page = new Composite(parent, SWT.NONE);

		RowLayout layout = new RowLayout(SWT.VERTICAL);
		page.setLayout(layout);

		// mark
		Composite markComp = new Composite(page, SWT.NONE);
		markComp.setLayout(new GridLayout(2, false));

		Label markLabel = new Label(markComp, SWT.NONE);
		markLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		markLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		markLabel.setText(Messages.MarkEditor_MarkLabel);

		Combo markCombo = new Combo(markComp, SWT.READ_ONLY);
		markCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		markView = new ComboViewer(markCombo);
		markView.add(MARKS);
		markView.setSelection(new StructuredSelection(mark.getWellKnownName().toString()));
		markView.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				changed = true;
			}
		});

		// stroke
		Label strokeLabel = new Label(page, SWT.NONE);
		strokeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		strokeLabel.setText(Messages.MarkEditor_StrokeLabel);
		strokeEditor = new StrokeEditor(page, mark.getStroke());

		// fill
		Label fillLabel = new Label(page, SWT.NONE);
		fillLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		fillLabel.setText(Messages.MarkEditor_FillLabel);
		fillEditor = new FillEditor(page, mark.getFill());
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return page;
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public Mark getValue() {
		String markName = (String) ((IStructuredSelection) markView.getSelection())
				.getFirstElement();

		return styleBuilder.createMark(markName, fillEditor.getValue(), strokeEditor.getValue());
	}

	/**
	 * @see Editor#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return changed || strokeEditor.isChanged() || fillEditor.isChanged();
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(Mark mark) {
		if (mark == null) {
			mark = styleBuilder.createMark(StyleBuilder.MARK_SQUARE);
		}

		markView.setSelection(new StructuredSelection(mark.getWellKnownName().toString()));
		strokeEditor.setValue(mark.getStroke());
		fillEditor.setValue(mark.getFill());
	}

}
