/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Michel Ishizuka <cqw10305@nifty.com> - Bug 113266 [syntax highlighting] javadoc tag names including period is not highlighting correctly
 *******************************************************************************/
package eu.esdihumboldt.hale.ui.util.groovy.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;

import eu.esdihumboldt.hale.ui.util.groovy.IColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.internal.CombinedWordRule.WordMatcher;

/**
 * Java comment scanner.
 */
public class JavaCommentScanner extends AbstractJavaScanner {

	private static class AtJavaIdentifierDetector implements IWordDetector {

		@Override
		public boolean isWordStart(char c) {
			return c == '@' || Character.isJavaIdentifierStart(c);
		}

		@Override
		public boolean isWordPart(char c) {
			return c == '.' || Character.isJavaIdentifierPart(c);
		}
	}

	private final String fDefaultTokenProperty;
	private final String[] fTokenProperties;

	/**
	 * Initialize with the given arguments.
	 * 
	 * @param manager Color manager
	 * @param store Preference store
	 * @param defaultTokenProperty Default token property
	 * 
	 * @since 3.0
	 */
	public JavaCommentScanner(IColorManager manager, IPreferenceStore store,
			String defaultTokenProperty) {
		this(manager, store, defaultTokenProperty, new String[] { defaultTokenProperty });
	}

	/**
	 * Initialize with the given arguments.
	 * 
	 * @param manager Color manager
	 * @param store Preference store
	 * @param defaultTokenProperty Default token property
	 * @param tokenProperties Token properties
	 * 
	 * @since 3.0
	 */
	public JavaCommentScanner(IColorManager manager, IPreferenceStore store,
			String defaultTokenProperty, String[] tokenProperties) {
		super(manager, store);

		fDefaultTokenProperty = defaultTokenProperty;
		fTokenProperties = tokenProperties;

		initialize();
	}

	/*
	 * @see AbstractJavaScanner#createRules()
	 */
	@Override
	protected List<IRule> createRules() {
		List<IRule> list = new ArrayList<IRule>();
		Token defaultToken = getToken(fDefaultTokenProperty);

		List<WordMatcher> matchers = createMatchers();
		if (matchers.size() > 0) {
			CombinedWordRule combinedWordRule = new CombinedWordRule(
					new AtJavaIdentifierDetector(), defaultToken);
			for (int i = 0, n = matchers.size(); i < n; i++)
				combinedWordRule.addWordMatcher(matchers.get(i));
			list.add(combinedWordRule);
		}

		setDefaultReturnToken(defaultToken);

		return list;
	}

	/**
	 * Creates a list of word matchers.
	 * 
	 * @return the list of word matchers
	 */
	protected List<WordMatcher> createMatchers() {
		List<WordMatcher> list = new ArrayList<WordMatcher>();

		// No support for Task Tags.

		return list;
	}

	/*
	 * @see
	 * org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#getTokenProperties()
	 */
	@Override
	protected String[] getTokenProperties() {
		return fTokenProperties;
	}

}