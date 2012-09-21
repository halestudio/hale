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

package eu.esdihumboldt.hale.ui.functions.inspire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.inspire.GeographicalNameFunction;
import eu.esdihumboldt.cst.functions.inspire.SpellingType;
import eu.esdihumboldt.cst.functions.inspire.data.GrammaticalGenderValue;
import eu.esdihumboldt.cst.functions.inspire.data.GrammaticalNumberValue;
import eu.esdihumboldt.cst.functions.inspire.data.NameStatusValue;
import eu.esdihumboldt.cst.functions.inspire.data.NativenessValue;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.functions.inspire.internal.GeographicalNamePageConstants;
import eu.esdihumboldt.hale.ui.functions.inspire.internal.Messages;

/**
 * Wizard page for the inspire geographical name function
 * 
 * @author Anna Pitaev
 * @author Kevin Mais
 */
public class GeographicalNamePage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>>
		implements GeographicalNameFunction, ParameterPage, GeographicalNamePageConstants {

	private ComboViewer nameSpellingText;
	private Text nameSpellingScript;
	private Text nameSpellingTransliteration;
	private Text namePronounciationSounds;
	private Text namePronounciationIPA;
	private Text nameLanguageText;
	private Text nameSourceText;
	private Combo nameStatusCombo;
	private Combo nameNativenessCombo;
	private Combo nameGenderCombo;
	private Combo nameNumberCombo;

	private ArrayList<SpellingType> spellings;
	private SpellingType activeSpelling;

	private String sound;
	private String ipa;
	private String language;
	private String sourceOfName;
	private String nameStatus;
	private String nativeness;
	private String gender;
	private String number;
	private Composite parent;
	private Composite composite;
	private Composite page;
	private List<String> scripts;
	private List<String> trans;

	/**
	 * Default Constructor
	 */
	public GeographicalNamePage() {
		super("geographic", Messages.GeographicNameFunctionWizard_2, null);
		this.spellings = new ArrayList<SpellingType>();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		// selected source(s) could've changed!
		page = composite;
		createContent(parent);
		if (page != null) {
			page.dispose();
		}
		parent.layout();
	}

	/**
	 * @see ParameterPage#setParameter(Set, ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, String> initialValues) {
		if (initialValues != null && initialValues.size() != 0) {
			// set the initial values if they exist
			gender = initialValues.get(PROPERTY_GRAMMA_GENDER).get(0);
			number = initialValues.get(PROPERTY_GRAMMA_NUMBER).get(0);
			language = initialValues.get(PROPERTY_LANGUAGE).get(0);
			nameStatus = initialValues.get(PROPERTY_NAMESTATUS).get(0);
			nativeness = initialValues.get(PROPERTY_NATIVENESS).get(0);
			ipa = initialValues.get(PROPERTY_PRONUNCIATIONIPA).get(0);
			try {
				sound = initialValues.get(PROPERTY_PRONUNCIATIONSOUNDLINK).get(0);
			} catch (Exception e) {
				sound = "";
			}
			sourceOfName = initialValues.get(PROPERTY_SOURCEOFNAME).get(0);

			// script and transliteration can have more than one value, so set
			// lists for them
			scripts = initialValues.get(PROPERTY_SCRIPT);
			trans = initialValues.get(PROPERTY_TRANSLITERATION);
		}
	}

	/**
	 * @see ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, String> getConfiguration() {

		// if one configuration element is null all are null because the page
		// isn't built yet
		// so return a multimap with no configuration and update it after the
		// page was built
		if (namePronounciationIPA == null) {
			return ArrayListMultimap.create();
		}

		ListMultimap<String, String> configuration = ArrayListMultimap.create(10, 10);

		if (spellings != null && spellings.size() != 0) {
			for (SpellingType sp : spellings) {
				String script = sp.getScript();
				String trans = sp.getTransliteration();

				configuration.put(PROPERTY_SCRIPT, script);
				configuration.put(PROPERTY_TRANSLITERATION, trans);
			}
		}

		configuration.put(PROPERTY_PRONUNCIATIONSOUNDLINK, namePronounciationSounds.getText());
		configuration.put(PROPERTY_PRONUNCIATIONIPA, namePronounciationIPA.getText());
		configuration.put(PROPERTY_LANGUAGE, nameLanguageText.getText());
		configuration.put(PROPERTY_SOURCEOFNAME, nameSourceText.getText());
		configuration.put(PROPERTY_NAMESTATUS, nameStatusCombo.getText());
		configuration.put(PROPERTY_NATIVENESS, nameNativenessCombo.getText());
		configuration.put(PROPERTY_GRAMMA_GENDER, nameGenderCombo.getText());
		configuration.put(PROPERTY_GRAMMA_NUMBER, nameNumberCombo.getText());

		return configuration;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		super.initializeDialogUnits(parent);

		this.parent = parent;

		setPageComplete(true);
		composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());

		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.setFont(parent.getFont());

		createSpellingGroup(composite);
		createPronounciationGroup(composite);
		createOptionalAttributes(composite);

		setErrorMessage(null); // should not initially have error message
		super.setControl(composite);
	}

	private void createOptionalAttributes(Composite parent) {

		final Composite configurationComposite = new Composite(parent, SWT.NONE);
		GridData configurationLayoutData = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL);
		configurationLayoutData.grabExcessHorizontalSpace = true;
		// configurationLayoutData.grabExcessVerticalSpace = true;
		configurationLayoutData.verticalIndent = 3;
		configurationComposite.setLayoutData(configurationLayoutData);
		configurationComposite
				.setSize(configurationComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		GridLayout pronounciationLayout = new GridLayout();
		pronounciationLayout.numColumns = 2;
		pronounciationLayout.makeColumnsEqualWidth = false;
		pronounciationLayout.marginWidth = 0;
		pronounciationLayout.marginHeight = 0;
		configurationComposite.setLayout(pronounciationLayout);

		// Language
		final Label nameLanguageLabel = new Label(configurationComposite, SWT.NONE);
		nameLanguageLabel.setText(LANGUAGE_LABEL_TEXT);

		this.nameLanguageText = new Text(configurationComposite, SWT.BORDER | SWT.SINGLE);
		this.nameLanguageText.setLayoutData(configurationLayoutData);
		String languageCode = null;
		if (getLanguage() != null && !getLanguage().equals("")) { //$NON-NLS-1$
			languageCode = getLanguage();
		}
		else {
			languageCode = ISO_CODE_ENG;
		}
		this.nameLanguageText.setText(languageCode);
		setLanguage(languageCode);

		this.nameLanguageText.setEnabled(true);
		this.nameLanguageText.setTabs(0);
		this.nameLanguageText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				language = nameLanguageText.getText();
			}

		});

		// Source of Name
		final Label nameSourceLabel = new Label(configurationComposite, SWT.NONE);
		nameSourceLabel.setText(SOURCE_OF_NAME_LABEL_TEXT);
		this.nameSourceText = new Text(configurationComposite, SWT.BORDER | SWT.SINGLE);
		this.nameSourceText.setLayoutData(configurationLayoutData);
		String nameSource = null;
		if (getSourceOfName() != null
				&& !getSourceOfName().equals("") && !getSourceOfName().equals("unknown")) { //$NON-NLS-1$ //$NON-NLS-2$
			nameSource = getSourceOfName();

		}
		else {
			nameSource = SOURCE_OF_NAME_PROMT;
		}

		this.nameSourceText.setText(nameSource);
		setSourceOfName(nameSource);
		this.nameSourceText.setEnabled(true);
		this.nameSourceText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				// if source of name defined by user
				if (!nameSourceText.getText().equals(SOURCE_OF_NAME_PROMT)) {
					setSourceOfName(nameSourceText.getText());
				}
				// FIXME replace with constant from new commons release
				else {
					setSourceOfName("unknown"); //$NON-NLS-1$
				}

			}
		});
		this.nameSourceText.setTabs(0);

		// Name Status
		final Label nameStatusLabel = new Label(configurationComposite, SWT.NONE);
		nameStatusLabel.setText(NAME_STATUS_LABEL_TEXT);
		this.nameStatusCombo = new Combo(configurationComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.nameStatusCombo.setLayoutData(configurationLayoutData);
		String[] statusItems = new String[] { NameStatusValue.official.name(),
				NameStatusValue.standardised.name(), NameStatusValue.historical.name(),
				NameStatusValue.other.name() };

		this.nameStatusCombo.setItems(statusItems);
		int index = 0;
		if (getNameStatus() != null) {
			String status = getNameStatus();
			for (int i = 0; i < statusItems.length; i++) {
				if (status.equals(statusItems[i])) {
					index = i;
					break;

				}
			}
		}

		this.nameStatusCombo.select(index);
		setNameStatus(nameStatusCombo.getItem(index));

		this.nameStatusCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				setNameStatus(nameStatusCombo.getItem(nameStatusCombo.getSelectionIndex()));
			}

		});

		// Nativeness
		final Label nativenessLabel = new Label(configurationComposite, SWT.NONE);
		nativenessLabel.setText(NATIVENESS_LABEL_TEXT);
		this.nameNativenessCombo = new Combo(configurationComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.nameNativenessCombo.setLayoutData(configurationLayoutData);

		String[] nativenessItems = new String[] { NativenessValue.endonym.name(),
				NativenessValue.exonym.name() };
		this.nameNativenessCombo.setItems(nativenessItems);
		int nativenessIndex = 0;
		if (getNativeness() != null) {
			String nativeness = getNativeness();
			for (int i = 0; i < nativenessItems.length; i++) {
				if (nativeness.equals(nativenessItems[i])) {
					nativenessIndex = i;
					break;
				}

			}

		}

		this.nameNativenessCombo.select(nativenessIndex);
		setNativeness(nameNativenessCombo.getItem(nativenessIndex));
		this.nameNativenessCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				setNativeness(nameNativenessCombo.getItem(nameNativenessCombo.getSelectionIndex()));
			}

		});

		// Grammatical Gender
		final Label genderLabel = new Label(configurationComposite, SWT.NONE);
		genderLabel.setText(GRAMMATICAL_GENDER_LABEL_TEXT);
		this.nameGenderCombo = new Combo(configurationComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.nameGenderCombo.setLayoutData(configurationLayoutData);
		String[] genderItems = new String[] { GENDER_NULL_VAUE_MENU_ENTRY,
				GrammaticalGenderValue.feminine.name(), GrammaticalGenderValue.masculine.name(),
				GrammaticalGenderValue.common.name() };
		this.nameGenderCombo.setItems(genderItems);
		int genderIndex = 0;
		if (getGender() != null) {
			String gender = getGender();
			for (int i = 0; i < genderItems.length; i++) {
				if (gender.equals(genderItems[i])) {
					genderIndex = i;
					break;
				}

			}
		}
		this.nameGenderCombo.select(genderIndex);
		setGender(nameGenderCombo.getItem(genderIndex));
		this.nameGenderCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				setGender(nameGenderCombo.getItem(nameGenderCombo.getSelectionIndex()));
			}

		});

		// Grammatical Number
		final Label numberLabel = new Label(configurationComposite, SWT.NONE);
		numberLabel.setText(GRAMMATICAL_NUMBER_LABEL_TEXT);
		this.nameNumberCombo = new Combo(configurationComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.nameNumberCombo.setLayoutData(configurationLayoutData);
		String[] numberItems = new String[] { NUMBER_NULL_VALUE_MENU_ENTRY,
				GrammaticalNumberValue.singular.name(), GrammaticalNumberValue.dual.name(),
				GrammaticalNumberValue.plural.name() };
		this.nameNumberCombo.setItems(numberItems);
		// set default selection
		int numberIndex = 0;
		if (getNumber() != null) {
			String number = getNumber();
			for (int i = 0; i < numberItems.length; i++) {
				if (number.equals(numberItems[i])) {
					numberIndex = i;
					break;
				}
			}

		}
		this.nameNumberCombo.select(numberIndex);
		setNumber(nameNumberCombo.getItem(numberIndex));
		this.nameNumberCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				setNumber(nameNumberCombo.getItem(nameNumberCombo.getSelectionIndex()));
			}

		});
	}

	private void createPronounciationGroup(Composite parent) {
		// define Pronounciatiation Group composite
		Group configurationGroup = new Group(parent, SWT.NONE);
		configurationGroup.setText(PRONOUNCIATION_GRPOUP_TEXT);
		configurationGroup.setLayout(new GridLayout());
		GridData configurationAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);
		configurationAreaGD.grabExcessHorizontalSpace = true;
		configurationAreaGD.grabExcessVerticalSpace = true;
		configurationGroup.setLayoutData(configurationAreaGD);
		configurationGroup.setSize(configurationGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		configurationGroup.setFont(parent.getFont());

		final Composite configurationComposite = new Composite(configurationGroup, SWT.NONE);
		GridData configurationLayoutData = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL);
		configurationLayoutData.grabExcessHorizontalSpace = true;
		configurationComposite.setLayoutData(configurationLayoutData);

		GridLayout pronounciationLayout = new GridLayout();
		pronounciationLayout.numColumns = 2;
		pronounciationLayout.makeColumnsEqualWidth = false;
		pronounciationLayout.marginWidth = 0;
		pronounciationLayout.marginHeight = 0;
		configurationComposite.setLayout(pronounciationLayout);

		// Soundlink
		final Label namePronounciationTextLabel = new Label(configurationComposite, SWT.NONE);
		namePronounciationTextLabel.setText(SOUNDLINK_LABEL_TEXT);

		this.namePronounciationSounds = new Text(configurationComposite, SWT.BORDER);
		this.namePronounciationSounds.setLayoutData(configurationLayoutData);
		this.namePronounciationSounds.setEnabled(true);
		String sound = "";
		if (getSound() != null && !getSound().equals("")) {
			sound = getSound();
		}
		this.namePronounciationSounds.setText(sound);
		setSound(sound);
		this.namePronounciationSounds.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setSound(namePronounciationSounds.getText());

			}
		});

		// IPA
		final Label namePronounciatiationIPALabel = new Label(configurationComposite, SWT.NONE);
		namePronounciatiationIPALabel.setText(IPA_LABEL_TEXT);
		this.namePronounciationIPA = new Text(configurationComposite, SWT.BORDER | SWT.SINGLE);
		this.namePronounciationIPA.setLayoutData(configurationLayoutData);
		this.namePronounciationIPA.setEnabled(true);
		this.namePronounciationIPA.setTabs(0);
		String ipa = ""; //$NON-NLS-1$
		if (getIpa() != null && !getIpa().equals("")) { //$NON-NLS-1$
			ipa = getIpa();
		}
		this.namePronounciationIPA.setText(ipa);
		setIpa(ipa);
		this.namePronounciationIPA.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setIpa(namePronounciationIPA.getText());

			}

		});
	}

	private void createSpellingGroup(Composite parent) {
		// define Spelling Group composite
		Group configurationGroup = new Group(parent, SWT.NONE);
		configurationGroup.setText(SPELLING_GROUP_TEXT);
		configurationGroup.setLayout(new GridLayout());
		GridData configurationAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);
		configurationAreaGD.grabExcessHorizontalSpace = true;
		configurationAreaGD.grabExcessVerticalSpace = true;
		configurationGroup.setLayoutData(configurationAreaGD);
		configurationGroup.setSize(configurationGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		configurationGroup.setFont(parent.getFont());

		final Composite configurationComposite = new Composite(configurationGroup, SWT.NONE);
		GridData configurationLayoutData = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL);
		configurationLayoutData.grabExcessHorizontalSpace = true;
		configurationComposite.setLayoutData(configurationLayoutData);

		GridLayout spellingLayout = new GridLayout();
		spellingLayout.numColumns = 2;
		spellingLayout.makeColumnsEqualWidth = false;
		spellingLayout.marginWidth = 0;
		spellingLayout.marginHeight = 0;
		configurationComposite.setLayout(spellingLayout);

		// init spelling types if it doesn't exists
		// or get the known information about the cell to be edited
		if (getSpellings() == null || getSpellings().size() == 0) {
			spellings = new ArrayList<SpellingType>();
			ListMultimap<String, ? extends Entity> source = getWizard().getUnfinishedCell()
					.getSource();
			if (source != null) {
				for (Entity item : source.values()) {
					int i = 0;
					Definition<?> entity = item.getDefinition().getDefinition();
					if (entity instanceof PropertyDefinition) {
						SpellingType sp = new SpellingType((PropertyDefinition) entity);
						// set the same script value if you had a value before
						if (scripts != null && i < scripts.size()) {
							sp.setScript(scripts.get(i));
						}
						else {
							// else set the default value
							sp.setScript(ISO_CODE_ENG);
						}
						// set the same transliteration value if you had a value
						// before
						if (trans != null && i < trans.size()) {
							sp.setTransliteration(trans.get(i));
						}
						else {
							// else set the default value
							sp.setTransliteration("");
						}
						spellings.add(sp);
					}
					i++;
				}
			}
		}
		else {
			// after initialization of the spellings
			ArrayList<PropertyDefinition> temp = new ArrayList<PropertyDefinition>();
			ArrayList<SpellingType> templist = getSpellings();
			// we have to create a new spellings list because a live
			// modification of the combo box input would occur an error
			spellings = new ArrayList<SpellingType>();
			for (int i = 0; i < templist.size(); i++) {
				temp.add(templist.get(i).getProperty());
				if (scripts != null && trans != null && i < scripts.size()
						&& scripts.get(i) != null && i < trans.size() && trans.get(i) != null) {
					templist.get(i).setScript(scripts.get(i));
					templist.get(i).setTransliteration(trans.get(i));
				}
			}

			for (Entity item : getWizard().getUnfinishedCell().getSource().values()) {
				Definition<?> entity = item.getDefinition().getDefinition();
				if (entity instanceof PropertyDefinition) {
					PropertyDefinition propDef = (PropertyDefinition) entity;
					for (SpellingType st : templist) {
						// if the spelling already exists just add the full
						// spelling configuration (text, script,
						// transliteration) to the new spellings list
						if (propDef.equals(st.getProperty())) {
							spellings.add(st);
						}
					}
					// if the spelling didn't exist before add a new spelling
					// with default values
					if (!temp.contains(propDef)) {
						SpellingType sp = new SpellingType(propDef);
						sp.setScript(ISO_CODE_ENG);
						sp.setTransliteration("");
						spellings.add(sp);
					}
				}
			}

		}
		// Text
		final Label nameSpellingTextLabel = new Label(configurationComposite, SWT.NONE);
		nameSpellingTextLabel.setText(SPELLING_TEXT_LABEL_TEXT);
		this.nameSpellingText = new ComboViewer(configurationComposite, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		this.nameSpellingText.getControl().setLayoutData(configurationLayoutData);
		this.nameSpellingText.setContentProvider(ArrayContentProvider.getInstance());
		this.nameSpellingText.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof SpellingType) {
					return ((SpellingType) element).getProperty().getName().getLocalPart();
				}
				return super.getText(element);
			}

		});
		this.nameSpellingText.setInput(spellings);
		// default set selection to the first element on the list
		if (!spellings.isEmpty()) {
			this.activeSpelling = spellings.iterator().next();
			this.nameSpellingText.setSelection(new StructuredSelection(activeSpelling));
		}
		// set active spelling
		nameSpellingText.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()
						&& event.getSelection() instanceof IStructuredSelection) {
					SpellingType selected = (SpellingType) ((IStructuredSelection) event
							.getSelection()).getFirstElement();

					String script = ISO_CODE_ENG;
					String transliteration = ""; //$NON-NLS-1$
					activeSpelling = selected;
					if (activeSpelling.getScript() != null
							&& !activeSpelling.getScript().equals("")) //$NON-NLS-1$
						script = activeSpelling.getScript();
					if (activeSpelling.getTransliteration() != null
							&& !activeSpelling.getTransliteration().equals("")) //$NON-NLS-1$
						transliteration = activeSpelling.getTransliteration();
					nameSpellingScript.setText(script);
					nameSpellingTransliteration.setText(transliteration);
				}
			}
		});

		// Script
		final Label nameSpellingScriptLabel = new Label(configurationComposite, SWT.NONE);
		nameSpellingScriptLabel.setText(SCRIPT_LABEL_TEXT);
		this.nameSpellingScript = new Text(configurationComposite, SWT.BORDER | SWT.SINGLE);
		this.nameSpellingScript.setLayoutData(configurationLayoutData);
		this.nameSpellingScript.setEnabled(true);
		this.nameSpellingScript.setTabs(0);
		String script = "eng"; //$NON-NLS-1$
		// read script from the active spelling
		if (activeSpelling != null && activeSpelling.getScript() != null)
			script = activeSpelling.getScript();
		// set default value for script
		this.nameSpellingScript.setText(script);
		this.nameSpellingScript.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				activeSpelling.setScript(nameSpellingScript.getText());
			}
		});

		// Transliteration
		final Label nameSpellingTransliterationLabel = new Label(configurationComposite, SWT.NONE);
		nameSpellingTransliterationLabel.setText(TRANSLITERATION_LABEL_TEXT);
		this.nameSpellingTransliteration = new Text(configurationComposite, SWT.BORDER | SWT.SINGLE);
		this.nameSpellingTransliteration.setLayoutData(configurationLayoutData);
		this.nameSpellingTransliteration.setEnabled(true);
		this.nameSpellingTransliteration.setTabs(0);
		// read script from the active spelling
		String transliteration = ""; //$NON-NLS-1$
		if (activeSpelling != null && activeSpelling.getTransliteration() != null)
			transliteration = activeSpelling.getTransliteration();
		// set default value for transliteration
		this.nameSpellingTransliteration.setText(transliteration);
		this.nameSpellingTransliteration.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				activeSpelling.setTransliteration(nameSpellingTransliteration.getText());
			}
		});
	}

	/**
	 * Returns a spelling assigned to source attribute name
	 * 
	 * @param property the assigned source property definition
	 * @return the spelling assigned to property
	 */
	protected SpellingType getActiveSpelling(PropertyDefinition property) {
		// 1. check if the spelling object already exists
		Iterator<SpellingType> iterator = getSpellings().iterator();
		SpellingType spelling;
		SpellingType aSpelling = null;
		while (iterator.hasNext()) {

			spelling = iterator.next();
			if (spelling.getProperty().equals(property)) {
				aSpelling = spelling;
				break;
			}

		}
		// if active spelling does not exist
		if (aSpelling == null) {
			aSpelling = new SpellingType(property);
			this.spellings.add(aSpelling);
		}
		return aSpelling;
	}

	/**
	 * @return the ipa
	 */
	public String getIpa() {
		return ipa;
	}

	/**
	 * @param ipa the ipa to set
	 */
	public void setIpa(String ipa) {
		this.ipa = ipa;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the sourceOfName
	 */
	public String getSourceOfName() {
		if (sourceOfName == null || sourceOfName.equals(SOURCE_OF_NAME_PROMT)) {
			return "unknown"; //$NON-NLS-1$
		}

		return sourceOfName;
	}

	/**
	 * @param sourceOfName the sourceOfName to set
	 */
	public void setSourceOfName(String sourceOfName) {
		this.sourceOfName = sourceOfName;
	}

	/**
	 * @return the nameStatus
	 */
	public String getNameStatus() {
		return nameStatus;
	}

	/**
	 * @param nameStatus the nameStatus to set
	 */
	public void setNameStatus(String nameStatus) {
		this.nameStatus = nameStatus;
	}

	/**
	 * @return the nativeness
	 */
	public String getNativeness() {
		return nativeness;
	}

	/**
	 * @param nativeness the nativeness to set
	 */
	public void setNativeness(String nativeness) {
		this.nativeness = nativeness;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the sound
	 */
	public String getSound() {
		return sound;
	}

	/**
	 * @param sound the sound to set
	 */
	public void setSound(String sound) {
		this.sound = sound;
	}

	/**
	 * @return the spellings
	 */
	public ArrayList<SpellingType> getSpellings() {
		return spellings;
	}

	/**
	 * @param spellings the spellings to set
	 */
	public void setSpellings(ArrayList<SpellingType> spellings) {
		this.spellings = spellings;
	}

}
