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

package eu.esdihumboldt.hale.rcp.wizards.functions.core.inspire.geographicname;

import org.apache.batik.svggen.font.table.NameTable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Caret;

import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizardPage;

/**
 * The WizardPage for the {@link GeographicNameFunctionWizard}
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public class GeographicNamePage extends AbstractSingleComposedCellWizardPage {

	private Text nameSpellingText;
	private StyledText nameSpellingScript;
	private StyledText nameSpellingTransliteration;
	private Text namePronounciationSounds;
	private StyledText namePronounciationIPA;
	private StyledText nameLanguageText;	
	private StyledText nameSourceText;
	private Combo nameStatusCombo;
	private Combo nameNativenessCombo;
	private Combo nameGenderCombo;
	private Combo nameNumberCombo;
	
	private String text;
	private String script;
	private String transliteration;
	private String ipa;
	private String language;
	private String sourceOfName;
	private String nameStatus;
	private String nativeness;
	private String gender;
	private String number;
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * @param script the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * @return the transliteration
	 */
	public String getTransliteration() {
		return transliteration;
	}

	/**
	 * @param transliteration the transliteration to set
	 */
	public void setTransliteration(String transliteration) {
		this.transliteration = transliteration;
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
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public GeographicNamePage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);

	}

	/**
	 * @param pageName
	 */
	public GeographicNamePage(String pageName) {
		super(pageName);

	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);

		setPageComplete(true);
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));

		
		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.setFont(parent.getFont());

		this.createSpellingGroup(composite);
		this.createPronounciationGroup(composite);
		this.createOptionalAttributes(composite);

		setErrorMessage(null); // should not initially have error message
		super.setControl(composite);

	}

	private void createOptionalAttributes(Composite parent) {

		final Composite configurationComposite = new Composite(parent, SWT.NONE);
		GridData configurationLayoutData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		configurationLayoutData.grabExcessHorizontalSpace = true;
		// configurationLayoutData.grabExcessVerticalSpace = true;
		configurationLayoutData.verticalIndent = 3;
		configurationComposite.setLayoutData(configurationLayoutData);
		configurationComposite.setSize(configurationComposite.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));

		GridLayout pronounciationLayout = new GridLayout();
		pronounciationLayout.numColumns = 2;
		pronounciationLayout.makeColumnsEqualWidth = false;
		pronounciationLayout.marginWidth = 0;
		pronounciationLayout.marginHeight = 0;
		configurationComposite.setLayout(pronounciationLayout);

		// Language
		final Label nameLanguageLabel = new Label(configurationComposite,
				SWT.NONE);
		nameLanguageLabel.setText("Language");
		
		this.nameLanguageText = new StyledText(configurationComposite, SWT.BORDER);
		this.nameLanguageText.setLayoutData(configurationLayoutData);
		String languageCode = null;
		if(getLanguage()!=null && !getLanguage().equals("")){
			languageCode = getLanguage();
			this.nameLanguageText.setCaretOffset(languageCode.length());
			
			}
		else {
		languageCode = " <enter ISO language code>";
		}
		this.nameLanguageText.setText(languageCode);
		
		this.nameLanguageText.setCaretOffset(languageCode.length());
		this.nameLanguageText.setEnabled(true);
		this.nameLanguageText.setTabs(0);
		this.nameLanguageText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e){
				//set a new iso value for the language
				setLanguage(nameLanguageText.getText());
				
			}

			
		});
		//Add a key listener to enable the switch between the input text fields.
		this.nameLanguageText.addKeyListener(new KeyListener(){
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.TAB){
					nameSourceText.setFocus();
					
					
				}
				
			}
		});

		// Source of Name
		final Label nameSourceLabel = new Label(configurationComposite,
				SWT.NONE);
		nameSourceLabel.setText("Source of Name");
		this.nameSourceText = new StyledText(configurationComposite, SWT.BORDER);
		this.nameSourceText.setLayoutData(configurationLayoutData);
		String nameSource = null;
		if(getSourceOfName()!=null && !getSourceOfName().equals("")){
			nameSource = getSourceOfName();
			
			
			}
		else {
			nameSource = " <enter source if known>";
		}
		
		
		this.nameSourceText.setText(nameSource);
		this.nameSourceText.setCaretOffset(nameSource.length());
		this.nameSourceText.setEnabled(true);
		this.nameSourceText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e){
				//set a new iso value for the language
				setSourceOfName(nameSourceText.getText());
				
				
			}
		});
		this.nameSourceText.setTabs(0);
		//Add a key listener to enable the switch between the input text fields.
		this.nameSourceText.addKeyListener(new KeyListener(){
			
			@Override
			public void keyReleased(KeyEvent e) {
				//DO nothing
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.TAB){
					nameSpellingScript.setFocus();
				}
				
			}
		});

		// Name Status
		final Label nameStatusLabel = new Label(configurationComposite,
				SWT.NONE);
		nameStatusLabel.setText("Name Status");
		this.nameStatusCombo = new Combo(configurationComposite, SWT.READ_ONLY
				| SWT.DROP_DOWN);
		this.nameStatusCombo.setLayoutData(configurationLayoutData);
		this.nameStatusCombo.setItems(new String[] { "Official",
				"Standardised", "Historical", "Other" });
		this.nameStatusCombo.select(0);
		this.nameStatusCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// TODO implement
			}

		});

		// Nativeness
		final Label nativenessLabel = new Label(configurationComposite,
				SWT.NONE);
		nativenessLabel.setText("Nativeness");
		this.nameNativenessCombo = new Combo(configurationComposite,
				SWT.READ_ONLY | SWT.DROP_DOWN);
		this.nameNativenessCombo.setLayoutData(configurationLayoutData);
		this.nameNativenessCombo
				.setItems(new String[] { "Endonym", "Exonym", });
		this.nameNativenessCombo.select(0);
		this.nameNativenessCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// TODO implement
			}

		});

		// Gramatical Gender
		final Label genderLabel = new Label(configurationComposite, SWT.NONE);
		genderLabel.setText("Grammatical Gender");
		this.nameGenderCombo = new Combo(configurationComposite, SWT.READ_ONLY
				| SWT.DROP_DOWN);
		this.nameGenderCombo.setLayoutData(configurationLayoutData);
		this.nameGenderCombo.setItems(new String[] { "M", "F", "N" });
		this.nameGenderCombo.select(0);
		this.nameGenderCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// TODO implement
			}

		});

		// Gramatical Number
		final Label numberLabel = new Label(configurationComposite, SWT.NONE);
		numberLabel.setText("Grammatical Number");
		this.nameNumberCombo = new Combo(configurationComposite, SWT.READ_ONLY
				| SWT.DROP_DOWN);
		this.nameNumberCombo.setLayoutData(configurationLayoutData);
		this.nameNumberCombo.setItems(new String[] { "Singular", "Plural" });
		this.nameNumberCombo.select(0);
		this.nameNumberCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// TODO implement
			}

		});
	}

	private void createPronounciationGroup(Composite parent) {
		// define Pronounciatiation Group composite
		Group configurationGroup = new Group(parent, SWT.NONE);
		configurationGroup.setText("Pronounciation");
		configurationGroup.setLayout(new GridLayout());
		GridData configurationAreaGD = new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		configurationAreaGD.grabExcessHorizontalSpace = true;
		configurationAreaGD.grabExcessVerticalSpace = true;
		configurationGroup.setLayoutData(configurationAreaGD);
		configurationGroup.setSize(configurationGroup.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		configurationGroup.setFont(parent.getFont());

		final Composite configurationComposite = new Composite(
				configurationGroup, SWT.NONE);
		GridData configurationLayoutData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		configurationLayoutData.grabExcessHorizontalSpace = true;
		configurationComposite.setLayoutData(configurationLayoutData);

		GridLayout pronounciationLayout = new GridLayout();
		pronounciationLayout.numColumns = 2;
		pronounciationLayout.makeColumnsEqualWidth = false;
		pronounciationLayout.marginWidth = 0;
		pronounciationLayout.marginHeight = 0;
		configurationComposite.setLayout(pronounciationLayout);

		// Sounds like
		final Label namePronounciationTextLabel = new Label(
				configurationComposite, SWT.NONE);
		namePronounciationTextLabel.setText("Sounds like... ");
		this.namePronounciationSounds = new Text(configurationComposite,
				SWT.BORDER);
		this.namePronounciationSounds.setLayoutData(configurationLayoutData);
		this.namePronounciationSounds.setEnabled(false);

		// IPA
		final Label namePronounciatiationIPALabel = new Label(
				configurationComposite, SWT.NONE);
		namePronounciatiationIPALabel.setText("IPA");
		this.namePronounciationIPA = new StyledText(configurationComposite,
				SWT.BORDER);
		this.namePronounciationIPA.setLayoutData(configurationLayoutData);
		this.namePronounciationIPA.setEnabled(true);
		this.namePronounciationIPA.setTabs(0);
		String ipa = "";
		if(getIpa()!=null && !getIpa().equals("")){
			ipa = getIpa();
			
		}
		this.namePronounciationIPA.setText(ipa);
		this.namePronounciationIPA.setCaretOffset(ipa.length());
		this.namePronounciationIPA.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				setIpa(namePronounciationIPA.getText());
				
			}
			
		});
		//Add a key listener to enable the switch between the input text fields.
		this.namePronounciationIPA.addKeyListener(new KeyListener(){
			
			@Override
			public void keyReleased(KeyEvent e) {
				//DO nothing
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.TAB){
					nameLanguageText.setFocus();
				}
				
			}
		});

	}

	private void createSpellingGroup(Composite parent) {

		// define Spelling Group composite
		Group configurationGroup = new Group(parent, SWT.NONE);
		configurationGroup.setText("Spelling");
		configurationGroup.setLayout(new GridLayout());
		GridData configurationAreaGD = new GridData(
				GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		configurationAreaGD.grabExcessHorizontalSpace = true;
		configurationAreaGD.grabExcessVerticalSpace = true;
		configurationGroup.setLayoutData(configurationAreaGD);
		configurationGroup.setSize(configurationGroup.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		configurationGroup.setFont(parent.getFont());

		final Composite configurationComposite = new Composite(
				configurationGroup, SWT.NONE);
		GridData configurationLayoutData = new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		configurationLayoutData.grabExcessHorizontalSpace = true;
		configurationComposite.setLayoutData(configurationLayoutData);

		GridLayout spellingLayout = new GridLayout();
		spellingLayout.numColumns = 2;
		spellingLayout.makeColumnsEqualWidth = false;
		spellingLayout.marginWidth = 0;
		spellingLayout.marginHeight = 0;
		configurationComposite.setLayout(spellingLayout);

		// Text
		final Label nameSpellingTextLabel = new Label(configurationComposite,
				SWT.NONE);
		nameSpellingTextLabel.setText("Text");
		this.nameSpellingText = new Text(configurationComposite, SWT.BORDER);
		this.nameSpellingText.setLayoutData(configurationLayoutData);
		this.nameSpellingText.setText(getParent().getFirstSourceItem().getName().getLocalPart());
		setText(this.nameSpellingText.getText());
		this.nameSpellingText.setEnabled(false);

		// Script
		final Label nameSpellingScriptLabel = new Label(configurationComposite,
				SWT.NONE);
		nameSpellingScriptLabel.setText("Script");
		this.nameSpellingScript = new StyledText(configurationComposite,
				SWT.BORDER);
		this.nameSpellingScript.setLayoutData(configurationLayoutData);
		this.nameSpellingScript.setEnabled(true);
		this.nameSpellingScript.setTabs(0);
		String script = "";
		if(getScript()!=null && !getScript().equals("")){
			script = getScript();
			
			
		}
		this.nameSpellingScript.setText(script);
		this.nameSpellingScript.setCaretOffset(script.length());
		
		this.nameSpellingScript.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				setScript(nameSpellingScript.getText());
				
			}
		});
		//Add a key listener to enable the switch between the input text fields.
		this.nameSpellingScript.addKeyListener(new KeyListener(){
			
			@Override
			public void keyReleased(KeyEvent e) {
				//DO nothing
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.TAB){
					nameSpellingTransliteration.setFocus();
				}
				
			}
		});

		// Transliteration
		final Label nameSpellingTransliterationLabel = new Label(
				configurationComposite, SWT.NONE);
		nameSpellingTransliterationLabel.setText("Transliteration");
		this.nameSpellingTransliteration = new StyledText(
				configurationComposite, SWT.BORDER);
		this.nameSpellingTransliteration.setLayoutData(configurationLayoutData);
		this.nameSpellingTransliteration.setEnabled(true);
		this.nameSpellingTransliteration.setTabs(0);
		String transliteration = "";
		if(getTransliteration()!=null && !getTransliteration().equals("")){
			transliteration = getTransliteration();
			
			
		}
		this.nameSpellingTransliteration.setText(transliteration);
		this.nameSpellingTransliteration.setCaretOffset(transliteration.length());
		this.nameSpellingTransliteration.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				setTransliteration(nameSpellingTransliteration.getText());
				
			}
		});
		//Add a key listener to enable the switch between the input text fields.
		this.nameSpellingTransliteration.addKeyListener(new KeyListener(){
			
			@Override
			public void keyReleased(KeyEvent e) {
				//DO nothing
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.TAB){
					namePronounciationIPA.setFocus();
				}
				
			}
		});

	}

}
