/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Eicher (Avaloq Evolution AG) - block selection mode
 *     Simon Templer - adaptations
 *******************************************************************************/
package eu.esdihumboldt.hale.ui.util.groovy;

import java.util.Arrays;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.ui.util.groovy.internal.AbstractJavaScanner;
import eu.esdihumboldt.hale.ui.util.groovy.internal.GroovyTagScanner;
import eu.esdihumboldt.hale.ui.util.groovy.internal.GroovyUIPlugin;
import eu.esdihumboldt.hale.ui.util.groovy.internal.IJavaPartitions;
import eu.esdihumboldt.hale.ui.util.groovy.internal.JavaCommentScanner;
import eu.esdihumboldt.hale.ui.util.groovy.internal.JavaDocScanner;
import eu.esdihumboldt.hale.ui.util.groovy.internal.SingleTokenJavaScanner;

/**
 * Configuration for a source viewer which shows Groovy code. Based on the Java
 * source viewer configuration and source viewer configuration extensions from
 * Groovy Eclipse. Adapted to not require an editor and without dependencies to
 * the JDT.
 */
public class SimpleGroovySourceViewerConfiguration extends SourceViewerConfiguration {

	/**
	 * The document partitioning.
	 * 
	 * @since 3.0
	 */
	private final String fDocumentPartitioning;

	/**
	 * The Java source code scanner.
	 * 
	 * @since 3.0
	 */
	private AbstractJavaScanner fCodeScanner;

	/**
	 * The Java multi-line comment scanner.
	 * 
	 * @since 3.0
	 */
	private AbstractJavaScanner fMultilineCommentScanner;

	/**
	 * The Java single-line comment scanner.
	 * 
	 * @since 3.0
	 */
	private AbstractJavaScanner fSinglelineCommentScanner;

	/**
	 * The Java string scanner.
	 * 
	 * @since 3.0
	 */
	private AbstractJavaScanner fStringScanner;

	/**
	 * The Javadoc scanner.
	 * 
	 * @since 3.0
	 */
	private AbstractJavaScanner fJavaDocScanner;

	/**
	 * The color manager.
	 * 
	 * @since 3.0
	 */
	private final IColorManager fColorManager;

//	/**
//	 * The double click strategy.
//	 * @since 3.1
//	 */
//	private JavaDoubleClickSelector fJavaDoubleClickSelector;

	private final IPreferenceStore fPreferenceStore;

	/**
	 * Creates a new Groovy source viewer configuration using the given color
	 * manager.
	 * 
	 * @param colorManager the color manager
	 */
	public SimpleGroovySourceViewerConfiguration(IColorManager colorManager) {
		this(colorManager, GroovyUIPlugin.getDefault().getPreferenceStore(), null);// IJavaPartitions.JAVA_PARTITIONING);
	}

	/**
	 * Creates a new Groovy source viewer configuration using the given
	 * preference store, the color manager and the specified document
	 * partitioning.
	 * 
	 * @param colorManager the color manager
	 * @param preferenceStore the preference store, can be read-only
	 * @param partitioning the document partitioning for this configuration, or
	 *            <code>null</code> for the default partitioning
	 * @since 3.0
	 */
	public SimpleGroovySourceViewerConfiguration(IColorManager colorManager,
			IPreferenceStore preferenceStore, String partitioning) {
		super();
		fPreferenceStore = preferenceStore;
		fColorManager = colorManager;
		fDocumentPartitioning = partitioning;
		initializeScanners();
	}

	/**
	 * Returns the Java source code scanner for this configuration.
	 * 
	 * @return the Java source code scanner
	 */
	protected RuleBasedScanner getCodeScanner() {
		return fCodeScanner;
	}

	/**
	 * Returns the Java multi-line comment scanner for this configuration.
	 * 
	 * @return the Java multi-line comment scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getMultilineCommentScanner() {
		return fMultilineCommentScanner;
	}

	/**
	 * Returns the Java single-line comment scanner for this configuration.
	 * 
	 * @return the Java single-line comment scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getSinglelineCommentScanner() {
		return fSinglelineCommentScanner;
	}

	/**
	 * Returns the Java string scanner for this configuration.
	 * 
	 * @return the Java string scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getStringScanner() {
		return fStringScanner;
	}

	/**
	 * Returns the JavaDoc scanner for this configuration.
	 * 
	 * @return the JavaDoc scanner
	 */
	protected RuleBasedScanner getJavaDocScanner() {
		return fJavaDocScanner;
	}

	/**
	 * Returns the color manager for this configuration.
	 * 
	 * @return the color manager
	 */
	protected IColorManager getColorManager() {
		return fColorManager;
	}

	/**
	 * Initializes the scanners.
	 * 
	 * @since 3.0
	 */
	private void initializeScanners() {
		fCodeScanner = new GroovyTagScanner(getColorManager()); // ,
																// fPreferenceStore);
		fMultilineCommentScanner = new JavaCommentScanner(getColorManager(), fPreferenceStore,
				ColorConstants.JAVA_MULTI_LINE_COMMENT);
		fSinglelineCommentScanner = new JavaCommentScanner(getColorManager(), fPreferenceStore,
				ColorConstants.JAVA_SINGLE_LINE_COMMENT);
		fStringScanner = new SingleTokenJavaScanner(getColorManager(), fPreferenceStore,
				ColorConstants.JAVA_STRING);
		fJavaDocScanner = new JavaDocScanner(getColorManager(), fPreferenceStore);
	}

	/*
	 * @see SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)
	 */
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getJavaDocScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_DOC);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_DOC);

		dr = new DefaultDamagerRepairer(getMultilineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(getSinglelineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_STRING);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_STRING);

		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_CHARACTER);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_CHARACTER);

		return reconciler;
	}

	/*
	 * @see SourceViewerConfiguration#getContentAssistant(ISourceViewer)
	 */
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

//		if (getEditor() != null) {
//
//			ContentAssistant assistant= new ContentAssistant();
//			assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
//
//			assistant.setRestoreCompletionProposalSize(getSettings("completion_proposal_size")); //$NON-NLS-1$
//
//			IContentAssistProcessor javaProcessor= new JavaCompletionProcessor(getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
//			assistant.setContentAssistProcessor(javaProcessor, IDocument.DEFAULT_CONTENT_TYPE);
//
//			ContentAssistProcessor singleLineProcessor= new JavaCompletionProcessor(getEditor(), assistant, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
//			assistant.setContentAssistProcessor(singleLineProcessor, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
//
//			ContentAssistProcessor stringProcessor= new JavaCompletionProcessor(getEditor(), assistant, IJavaPartitions.JAVA_STRING);
//			assistant.setContentAssistProcessor(stringProcessor, IJavaPartitions.JAVA_STRING);
//
//			ContentAssistProcessor multiLineProcessor= new JavaCompletionProcessor(getEditor(), assistant, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
//			assistant.setContentAssistProcessor(multiLineProcessor, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
//
//			ContentAssistProcessor javadocProcessor= new JavadocCompletionProcessor(getEditor(), assistant);
//			assistant.setContentAssistProcessor(javadocProcessor, IJavaPartitions.JAVA_DOC);
//
//			ContentAssistPreference.configure(assistant, fPreferenceStore);
//
//			assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
//			assistant.setInformationControlCreator(new IInformationControlCreator() {
//				public IInformationControl createInformationControl(Shell parent) {
//					return new DefaultInformationControl(parent, JavaPlugin.getAdditionalInfoAffordanceString());
//				}
//			});
//
//			return assistant;
//		}

		return null;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#
	 * getQuickAssistAssistant(org.eclipse.jface.text.source.ISourceViewer)
	 * 
	 * @since 3.2
	 */
	@Override
	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
//		if (getEditor() != null) {
//			JavaCorrectionAssistant assistant= new JavaCorrectionAssistant(getEditor());
//			assistant.setRestoreCompletionProposalSize(getSettings("quick_assist_proposal_size")); //$NON-NLS-1$
//			return assistant;
//		}
		return null;
	}

	/*
	 * @see SourceViewerConfiguration#getReconciler(ISourceViewer)
	 */
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {

//		final ITextEditor editor= getEditor();
//		if (editor != null && editor.isEditable()) {
//
//			JavaCompositeReconcilingStrategy strategy= new JavaCompositeReconcilingStrategy(sourceViewer, editor, getConfiguredDocumentPartitioning(sourceViewer));
//			JavaReconciler reconciler= new JavaReconciler(editor, strategy, false);
//			reconciler.setIsAllowedToModifyDocument(false);
//			reconciler.setDelay(500);
//
//			return reconciler;
//		}
		return null;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies
	 * (org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
//		String partitioning= getConfiguredDocumentPartitioning(sourceViewer);
//		if (IJavaPartitions.JAVA_DOC.equals(contentType) || IJavaPartitions.JAVA_MULTI_LINE_COMMENT.equals(contentType))
//			return new IAutoEditStrategy[] { new JavaDocAutoIndentStrategy(partitioning) };
//		else if (IJavaPartitions.JAVA_STRING.equals(contentType))
//			return new IAutoEditStrategy[] { new SmartSemicolonAutoEditStrategy(partitioning), new JavaStringAutoIndentStrategy(partitioning) };
//		else if (IJavaPartitions.JAVA_CHARACTER.equals(contentType) || IDocument.DEFAULT_CONTENT_TYPE.equals(contentType))
//			return new IAutoEditStrategy[] { new SmartSemicolonAutoEditStrategy(partitioning), new JavaAutoIndentStrategy(partitioning, getProject(), sourceViewer) };
//		else
//			return new IAutoEditStrategy[] { new JavaAutoIndentStrategy(partitioning, getProject(), sourceViewer) };
		return new IAutoEditStrategy[] { new DefaultIndentLineAutoEditStrategy() };
	}

	/*
	 * @see SourceViewerConfiguration#getDoubleClickStrategy(ISourceViewer,
	 * String)
	 */
//	@Override
//	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
//		if (IJavaPartitions.JAVA_DOC.equals(contentType))
//			return new JavadocDoubleClickStrategy(getConfiguredDocumentPartitioning(sourceViewer));
//		if (IJavaPartitions.JAVA_SINGLE_LINE_COMMENT.equals(contentType))
//			return new PartitionDoubleClickSelector(getConfiguredDocumentPartitioning(sourceViewer), 0, 0);
//		if (IJavaPartitions.JAVA_MULTI_LINE_COMMENT.equals(contentType))
//			return new PartitionDoubleClickSelector(getConfiguredDocumentPartitioning(sourceViewer), 0, 0);
//		else if (IJavaPartitions.JAVA_STRING.equals(contentType) || IJavaPartitions.JAVA_CHARACTER.equals(contentType))
//			return new PartitionDoubleClickSelector(getConfiguredDocumentPartitioning(sourceViewer), 1, 1);
//		if (fJavaDoubleClickSelector == null) {
//			fJavaDoubleClickSelector= new JavaDoubleClickSelector();
//			fJavaDoubleClickSelector.setSourceVersion(fPreferenceStore.getString(JavaCore.COMPILER_SOURCE));
//		}
//		return fJavaDoubleClickSelector;
//	}

	/*
	 * @see SourceViewerConfiguration#getDefaultPrefixes(ISourceViewer, String)
	 * 
	 * @since 2.0
	 */
	@Override
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { "//", "" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * @see SourceViewerConfiguration#getIndentPrefixes(ISourceViewer, String)
	 */
//	@Override
//	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
// 		IJavaProject project= getProject();
//		final int tabWidth= CodeFormatterUtil.getTabWidth(project);
//		final int indentWidth= CodeFormatterUtil.getIndentWidth(project);
//		boolean allowTabs= tabWidth <= indentWidth;
//
//		String indentMode;
//		if (project == null)
//			indentMode= JavaCore.getOption(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
//		else
//			indentMode= project.getOption(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, true);
//
//		boolean useSpaces= JavaCore.SPACE.equals(indentMode) || DefaultCodeFormatterConstants.MIXED.equals(indentMode);
//
//		Assert.isLegal(allowTabs || useSpaces);
//
//		if (!allowTabs) {
//			char[] spaces= new char[indentWidth];
//			Arrays.fill(spaces, ' ');
//			return new String[] { new String(spaces), "" }; //$NON-NLS-1$
//		} else if  (!useSpaces)
//			return getIndentPrefixesForTab(tabWidth);
//		else
//			return getIndentPrefixesForSpaces(tabWidth);
//	}

	/**
	 * Computes and returns the indent prefixes for space indentation and the
	 * given <code>tabWidth</code>.
	 * 
	 * @param tabWidth the display tab width
	 * @return the indent prefixes
	 * @see #getIndentPrefixes(ISourceViewer, String)
	 * @since 3.3
	 */
	@SuppressWarnings("unused")
	private String[] getIndentPrefixesForSpaces(int tabWidth) {
		String[] indentPrefixes = new String[tabWidth + 2];
		indentPrefixes[0] = getStringWithSpaces(tabWidth);

		for (int i = 0; i < tabWidth; i++) {
			String spaces = getStringWithSpaces(i);
			if (i < tabWidth)
				indentPrefixes[i + 1] = spaces + '\t';
			else
				indentPrefixes[i + 1] = new String(spaces);
		}

		indentPrefixes[tabWidth + 1] = ""; //$NON-NLS-1$

		return indentPrefixes;
	}

	/**
	 * Creates and returns a String with <code>count</code> spaces.
	 * 
	 * @param count the space count
	 * @return the string with the spaces
	 * @since 3.3
	 */
	private String getStringWithSpaces(int count) {
		char[] spaceChars = new char[count];
		Arrays.fill(spaceChars, ' ');
		return new String(spaceChars);
	}

	/*
	 * @see SourceViewerConfiguration#getTabWidth(ISourceViewer)
	 */
	@Override
	public int getTabWidth(ISourceViewer sourceViewer) {
		return 4; // CodeFormatterUtil.getTabWidth(getProject());
	}

	/*
	 * @see SourceViewerConfiguration#getAnnotationHover(ISourceViewer)
	 */
//	@Override
//	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
//		return new HTMLAnnotationHover(false) {
//			@Override
//			protected boolean isIncluded(Annotation annotation) {
//				return isShowInVerticalRuler(annotation);
//			}
//		};
//	}

	/*
	 * @see
	 * SourceViewerConfiguration#getOverviewRulerAnnotationHover(ISourceViewer)
	 * 
	 * @since 3.0
	 */
//	@Override
//	public IAnnotationHover getOverviewRulerAnnotationHover(ISourceViewer sourceViewer) {
//		return new HTMLAnnotationHover(true) {
//			@Override
//			protected boolean isIncluded(Annotation annotation) {
//				return isShowInOverviewRuler(annotation);
//			}
//		};
//	}

	/*
	 * @see
	 * SourceViewerConfiguration#getConfiguredTextHoverStateMasks(ISourceViewer,
	 * String)
	 * 
	 * @since 2.1
	 */
//	@Override
//	public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer, String contentType) {
//		JavaEditorTextHoverDescriptor[] hoverDescs= JavaPlugin.getDefault().getJavaEditorTextHoverDescriptors();
//		int stateMasks[]= new int[hoverDescs.length];
//		int stateMasksLength= 0;
//		for (int i= 0; i < hoverDescs.length; i++) {
//			if (hoverDescs[i].isEnabled()) {
//				int j= 0;
//				int stateMask= hoverDescs[i].getStateMask();
//				while (j < stateMasksLength) {
//					if (stateMasks[j] == stateMask)
//						break;
//					j++;
//				}
//				if (j == stateMasksLength)
//					stateMasks[stateMasksLength++]= stateMask;
//			}
//		}
//		if (stateMasksLength == hoverDescs.length)
//			return stateMasks;
//
//		int[] shortenedStateMasks= new int[stateMasksLength];
//		System.arraycopy(stateMasks, 0, shortenedStateMasks, 0, stateMasksLength);
//		return shortenedStateMasks;
//	}

	/*
	 * @see SourceViewerConfiguration#getTextHover(ISourceViewer, String, int)
	 * 
	 * @since 2.1
	 */
//	@Override
//	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
//		JavaEditorTextHoverDescriptor[] hoverDescs= JavaPlugin.getDefault().getJavaEditorTextHoverDescriptors();
//		int i= 0;
//		while (i < hoverDescs.length) {
//			if (hoverDescs[i].isEnabled() &&  hoverDescs[i].getStateMask() == stateMask)
//				return new JavaEditorTextHoverProxy(hoverDescs[i], getEditor());
//			i++;
//		}
//
//		return null;
//	}

	/*
	 * @see SourceViewerConfiguration#getTextHover(ISourceViewer, String)
	 */
//	@Override
//	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
//		return getTextHover(sourceViewer, contentType,
//				ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK);
//	}

	/*
	 * @see SourceViewerConfiguration#getConfiguredContentTypes(ISourceViewer)
	 */
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, IJavaPartitions.JAVA_DOC,
				IJavaPartitions.JAVA_MULTI_LINE_COMMENT, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT,
				IJavaPartitions.JAVA_STRING, IJavaPartitions.JAVA_CHARACTER };
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#
	 * getConfiguredDocumentPartitioning
	 * (org.eclipse.jface.text.source.ISourceViewer)
	 * 
	 * @since 3.0
	 */
	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		if (fDocumentPartitioning != null)
			return fDocumentPartitioning;
		return super.getConfiguredDocumentPartitioning(sourceViewer);
	}

	/*
	 * @see SourceViewerConfiguration#getContentFormatter(ISourceViewer)
	 */
//	@Override
//	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
//		final MultiPassContentFormatter formatter= new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);
//		formatter.setMasterStrategy(new JavaFormattingStrategy());
//		return formatter;
//	}

	/*
	 * @see
	 * SourceViewerConfiguration#getInformationControlCreator(ISourceViewer)
	 * 
	 * @since 2.0
	 */
	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {

			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, false);
			}
		};
	}

	/**
	 * Returns the information presenter control creator. The creator is a
	 * factory creating the presenter controls for the given source viewer. This
	 * implementation always returns a creator for
	 * <code>DefaultInformationControl</code> instances.
	 * 
	 * @param sourceViewer the source viewer to be configured by this
	 *            configuration
	 * @return an information control creator
	 * @since 2.1
	 */
	@SuppressWarnings("unused")
	private IInformationControlCreator getInformationPresenterControlCreator(
			ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {

			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, true);
			}
		};
	}

	/**
	 * Returns the outline presenter control creator. The creator is a factory
	 * creating outline presenter controls for the given source viewer. This
	 * implementation always returns a creator for
	 * <code>JavaOutlineInformationControl</code> instances.
	 * 
	 * @param sourceViewer the source viewer to be configured by this
	 *            configuration
	 * @param commandId the ID of the command that opens this control
	 * @return an information control creator
	 * @since 2.1
	 */
//	private IInformationControlCreator getOutlinePresenterControlCreator(ISourceViewer sourceViewer, final String commandId) {
//		return new IInformationControlCreator() {
//			public IInformationControl createInformationControl(Shell parent) {
//				int shellStyle= SWT.RESIZE;
//				int treeStyle= SWT.V_SCROLL | SWT.H_SCROLL;
//				return new JavaOutlineInformationControl(parent, shellStyle, treeStyle, commandId);
//			}
//		};
//	}

//	private IInformationControlCreator getHierarchyPresenterControlCreator() {
//		return new IInformationControlCreator() {
//			public IInformationControl createInformationControl(Shell parent) {
//				int shellStyle= SWT.RESIZE;
//				int treeStyle= SWT.V_SCROLL | SWT.H_SCROLL;
//				return new HierarchyInformationControl(parent, shellStyle, treeStyle);
//			}
//		};
//	}

	/*
	 * @see SourceViewerConfiguration#getInformationPresenter(ISourceViewer)
	 * 
	 * @since 2.0
	 */
//	@Override
//	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
//		InformationPresenter presenter= new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));
//		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
//
//		// Register information provider
//		IInformationProvider provider= new JavaInformationProvider(getEditor());
//		String[] contentTypes= getConfiguredContentTypes(sourceViewer);
//		for (int i= 0; i < contentTypes.length; i++)
//			presenter.setInformationProvider(provider, contentTypes[i]);
//
//		// sizes: see org.eclipse.jface.text.TextViewer.TEXT_HOVER_*_CHARS
//		presenter.setSizeConstraints(100, 12, true, true);
//		return presenter;
//	}

	/**
	 * Returns the outline presenter which will determine and shown information
	 * requested for the current cursor position.
	 * 
	 * @param sourceViewer the source viewer to be configured by this
	 *            configuration
	 * @param doCodeResolve a boolean which specifies whether code resolve
	 *            should be used to compute the Java element
	 * @return an information presenter
	 * @since 2.1
	 */
//	public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer, boolean doCodeResolve) {
//		InformationPresenter presenter;
//		if (doCodeResolve)
//			presenter= new InformationPresenter(getOutlinePresenterControlCreator(sourceViewer, IJavaEditorActionDefinitionIds.OPEN_STRUCTURE));
//		else
//			presenter= new InformationPresenter(getOutlinePresenterControlCreator(sourceViewer, IJavaEditorActionDefinitionIds.SHOW_OUTLINE));
//		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
//		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
//		IInformationProvider provider= new JavaElementProvider(getEditor(), doCodeResolve);
//		presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_DOC);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_STRING);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_CHARACTER);
//		presenter.setSizeConstraints(50, 20, true, false);
//		return presenter;
//	}

	/**
	 * Returns the settings for the given section.
	 * 
	 * @param sectionName the section name
	 * @return the settings
	 * @since 3.0
	 */
//	private IDialogSettings getSettings(String sectionName) {
//		IDialogSettings settings= JavaPlugin.getDefault().getDialogSettings().getSection(sectionName);
//		if (settings == null)
//			settings= JavaPlugin.getDefault().getDialogSettings().addNewSection(sectionName);
//
//		return settings;
//	}

	/**
	 * Returns the hierarchy presenter which will determine and shown type
	 * hierarchy information requested for the current cursor position.
	 * 
	 * @param sourceViewer the source viewer to be configured by this
	 *            configuration
	 * @param doCodeResolve a boolean which specifies whether code resolve
	 *            should be used to compute the Java element
	 * @return an information presenter
	 * @since 3.0
	 */
//	public IInformationPresenter getHierarchyPresenter(ISourceViewer sourceViewer, boolean doCodeResolve) {
//
//		// Do not create hierarchy presenter if there's no CU.
//		if (getEditor() != null && getEditor().getEditorInput() != null && JavaUI.getEditorInputJavaElement(getEditor().getEditorInput()) == null)
//			return null;
//
//		InformationPresenter presenter= new InformationPresenter(getHierarchyPresenterControlCreator());
//		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
//		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
//		IInformationProvider provider= new JavaElementProvider(getEditor(), doCodeResolve);
//		presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_DOC);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_STRING);
//		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_CHARACTER);
//		presenter.setSizeConstraints(50, 20, true, false);
//		return presenter;
//	}

	/**
	 * Determines whether the preference change encoded by the given event
	 * changes the behavior of one of its contained components.
	 * 
	 * @param event the event to be investigated
	 * @return <code>true</code> if event causes a behavioral change
	 * @since 3.0
	 */
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return fCodeScanner.affectsBehavior(event)
				|| fMultilineCommentScanner.affectsBehavior(event)
				|| fSinglelineCommentScanner.affectsBehavior(event)
				|| fStringScanner.affectsBehavior(event) || fJavaDocScanner.affectsBehavior(event);
	}

	/**
	 * Adapts the behavior of the contained components to the change encoded in
	 * the given event.
	 * 
	 * @param event the event to which to adapt
	 * @since 3.0
	 */
	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
//		Assert.isTrue(isNewSetup());
		if (fCodeScanner.affectsBehavior(event))
			fCodeScanner.adaptToPreferenceChange(event);
		if (fMultilineCommentScanner.affectsBehavior(event))
			fMultilineCommentScanner.adaptToPreferenceChange(event);
		if (fSinglelineCommentScanner.affectsBehavior(event))
			fSinglelineCommentScanner.adaptToPreferenceChange(event);
		if (fStringScanner.affectsBehavior(event))
			fStringScanner.adaptToPreferenceChange(event);
		if (fJavaDocScanner.affectsBehavior(event))
			fJavaDocScanner.adaptToPreferenceChange(event);
//		if (fJavaDoubleClickSelector != null && JavaCore.COMPILER_SOURCE.equals(event.getProperty()))
//			if (event.getNewValue() instanceof String)
//				fJavaDoubleClickSelector.setSourceVersion((String) event.getNewValue());
	}

}
