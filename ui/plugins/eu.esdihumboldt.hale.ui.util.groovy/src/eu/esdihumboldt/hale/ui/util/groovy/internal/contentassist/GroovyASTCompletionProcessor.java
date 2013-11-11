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

package eu.esdihumboldt.hale.ui.util.groovy.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.ui.util.groovy.GroovyCompletionProposals;
import eu.esdihumboldt.hale.ui.util.groovy.compile.GroovyAST;
import eu.esdihumboldt.hale.ui.util.source.CompilingSourceViewer;

/**
 * Groovy AST based completion processor that delegates the actual computation
 * to {@link GroovyCompletionProposals}.
 * 
 * @author Simon Templer
 */
public class GroovyASTCompletionProcessor implements IContentAssistProcessor {

	private static final ALogger log = ALoggerFactory.getLogger(GroovyASTCompletionProcessor.class);

	private final List<? extends GroovyCompletionProposals> proposals;

	/**
	 * Constructor.
	 * 
	 * @param proposals the Groovy completion proposal computers this object
	 *            delegates to
	 */
	public GroovyASTCompletionProcessor(Iterable<? extends GroovyCompletionProposals> proposals) {
		super();
		this.proposals = (proposals != null) ? (ImmutableList.copyOf(proposals)) : (null);
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(final ITextViewer viewer,
			final int offset) {
		if (proposals == null || proposals.isEmpty()) {
			return null;
		}

		if (viewer instanceof CompilingSourceViewer<?>) {

			CompilingSourceViewer<?> csv = (CompilingSourceViewer<?>) viewer;
			try {
				Object compiled = csv.getCompiled().get();
				if (compiled instanceof GroovyAST) {
					// the Groovy AST
					GroovyAST ast = (GroovyAST) compiled;
					// the line (0-based)
					int line = csv.getDocument().getLineOfOffset(offset);
					// the line column (0-based)
					int column = offset - csv.getDocument().getLineOffset(line);
					// locations in Groovy AST are 1-based
					line++;
					column++;

					List<ICompletionProposal> list = new ArrayList<>();

					// add proposals of individual computers
					for (GroovyCompletionProposals proposal : proposals) {
						Iterable<? extends ICompletionProposal> computed = proposal
								.computeProposals(ast, line, column, offset);
						if (computed != null) {
							Iterables.addAll(list, computed);
						}
					}

					return list.toArray(new ICompletionProposal[list.size()]);
				}
			} catch (Exception e) {
				log.warn("Failed to get AST to compute completion proposals", e);
			}
		}

		return null;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}
