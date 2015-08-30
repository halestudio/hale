/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * Helper function Completion proposal, created for styling the the display name
 * 
 * @author Sameer Sheikh
 */
public class HelperFunctionCompletionPropasal implements ICompletionProposal,
		ICompletionProposalExtension6 {

	private final ICompletionProposal p;
	private final StyledString fDisplayString;

	/**
	 * @param replacementString replacement string
	 * @param replacementOffset replacement position
	 * @param replacementLength length of the text to be replaced
	 * @param cursorPosition new cursor position relative to replacement
	 *            position
	 */
	public HelperFunctionCompletionPropasal(String replacementString, int replacementOffset,
			int replacementLength, int cursorPosition) {
		this(replacementString, replacementOffset, replacementLength, cursorPosition, null, null,
				null, null);
	}

	/**
	 * @param replacementString replacement string
	 * @param replacementOffset replacement position
	 * @param replacementLength length of the text to be replaced
	 * @param cursorPosition new cursor position relative to replacement
	 *            position
	 * @param image an image
	 * @param displayString display styled string
	 * @param contextInformation context information
	 * @param additionalProposalInfo an additional information to be displayed
	 */
	public HelperFunctionCompletionPropasal(String replacementString, int replacementOffset,
			int replacementLength, int cursorPosition, Image image, StyledString displayString,
			IContextInformation contextInformation, String additionalProposalInfo) {

		fDisplayString = displayString;
		p = new CompletionProposal(replacementString, replacementOffset, replacementLength,
				cursorPosition, image, displayString.getString(), contextInformation,
				additionalProposalInfo);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public Point getSelection(IDocument document) {
		return p.getSelection(document);
	}

	@Override
	public StyledString getStyledDisplayString() {
		return fDisplayString;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public void apply(IDocument document) {
		p.apply(document);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	@Override
	public String getAdditionalProposalInfo() {
		return p.getAdditionalProposalInfo();
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	@Override
	public String getDisplayString() {
		return getStyledDisplayString().toString();
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	@Override
	public Image getImage() {
		return p.getImage();
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	@Override
	public IContextInformation getContextInformation() {
		return p.getContextInformation();
	}

}
