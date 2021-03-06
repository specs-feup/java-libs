/*
 * Copyright 2010 SPeCS Research Group.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.suikasoft.jOptions.gui.panels.option.notimplementedyet;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pt.up.fe.specs.guihelper.FieldType;
import pt.up.fe.specs.guihelper.Base.ListOfSetupDefinitions;
import pt.up.fe.specs.guihelper.Base.SetupDefinition;
import pt.up.fe.specs.guihelper.Base.SetupFieldEnum;
import pt.up.fe.specs.guihelper.BaseTypes.FieldValue;
import pt.up.fe.specs.guihelper.BaseTypes.ListOfSetups;
import pt.up.fe.specs.guihelper.BaseTypes.SetupData;
import pt.up.fe.specs.guihelper.SetupFieldOptions.MultipleSetup;
import pt.up.fe.specs.guihelper.gui.FieldPanel;
import pt.up.fe.specs.guihelper.gui.BasePanels.BaseSetupPanel;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsLogs;

/**
 * 
 * @author Joao Bispo
 */
public class ListOfSetupsPanel extends FieldPanel {

    private static final long serialVersionUID = 1L;

    private JPanel currentOptionsPanel;
    private JPanel choicePanel;

    private JLabel label;
    // private JComboBox<String> elementsBox;
    // private JComboBox<String> choicesBox;
    private JComboBox<String> elementsBox;
    private JComboBox<String> choicesBox;
    private JButton removeButton;
    private JButton addButton;

    private List<String> choicesBoxShadow;
    private ListOfSetupDefinitions setups;

    private List<Integer> elementsBoxShadow;
    private List<SetupData> elementsFiles;
    private List<BaseSetupPanel> elementsOptionPanels;

    // Properties
    private static final String ENUM_NAME_SEPARATOR = "-";

    public ListOfSetupsPanel(SetupFieldEnum enumOption, String labelName, MultipleSetup setup) {
	// Initiallize objects
	// id = enumOption;
	// masterFile = null;
	label = new JLabel(labelName + ":");
	removeButton = new JButton("X");
	addButton = new JButton("Add");

	initChoices(setup);
	initElements();

	// Add actions
	addButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent evt) {
		addButtonActionPerformed(evt);
	    }
	});

	removeButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent evt) {
		removeButtonActionPerformed(evt);
	    }

	});

	elementsBox.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		elementComboBoxActionPerformed(e);
	    }

	});

	// Build choice panel
	choicePanel = buildChoicePanel();

	currentOptionsPanel = null;

	// setLayout(new BorderLayout(5, 5));
	// add(choicePanel, BorderLayout.PAGE_START);
	LayoutManager layout = new BoxLayout(this, BoxLayout.Y_AXIS);
	setLayout(layout);
	add(choicePanel);

    }

    private void initChoices(MultipleSetup setupList) {
	// setups = new ArrayList<SingleSetupEnum>();
	setups = setupList.getSetups();
	// System.out.println("Setups:"+setups);
	// setups.addAll(setupList.getSetups());

	// choicesBox = new JComboBox<String>();
	choicesBox = new JComboBox<>();
	choicesBoxShadow = new ArrayList<>();

	// for(SingleSetupEnum setup : setups) {
	// for(String setupName : setups.getNameList()) {
	for (SetupDefinition setup : setups.getSetupKeysList()) {
	    // String setupName = ((Enum)setup).name();
	    String setupName = setup.getSetupName();
	    choicesBox.addItem(setupName);
	    choicesBoxShadow.add(setupName);
	}

    }

    private void initElements() {
	elementsBoxShadow = new ArrayList<>();
	// elementsBox = new JComboBox<String>();
	elementsBox = new JComboBox<>();
	elementsFiles = new ArrayList<>();
	elementsOptionPanels = new ArrayList<>();
    }

    private JPanel buildChoicePanel() {
	JPanel panel = new JPanel();

	panel.add(label);
	panel.add(elementsBox);
	panel.add(removeButton);
	panel.add(choicesBox);
	panel.add(addButton);

	panel.setLayout(new FlowLayout(FlowLayout.LEFT));

	return panel;
    }

    /**
     * Adds the option from the avaliable list to selected list.
     * 
     * @param evt
     */
    private void addButtonActionPerformed(ActionEvent evt) {
	// Determine what element is selected
	int choice = choicesBox.getSelectedIndex();
	if (choice == -1) {
	    return;
	}

	addElement(choice);
    }

    /**
     * Removes the option from the selected list to the available list.
     * 
     * @param evt
     */
    private void removeButtonActionPerformed(ActionEvent evt) {
	// Determine index of selected element to remove
	int indexToRemove = elementsBox.getSelectedIndex();
	if (indexToRemove == -1) {
	    return;
	}

	removeElement(indexToRemove);
    }

    /**
     * Updates the options panel.
     * 
     * @param e
     */
    private void elementComboBoxActionPerformed(ActionEvent e) {
	updateSetupOptions();
    }

    @Override
    public FieldType getType() {
	return FieldType.setupList;
    }

    /**
     * Adds an element to the elements list, from the choices list.
     * 
     * @return the index of the added element
     */
    public int addElement(int choice) {
	// Add index to elements
	elementsBoxShadow.add(choice);
	// Get setup options and create option file for element
	SetupDefinition setupKeys = setups.getSetupKeysList().get(choice);

	elementsFiles.add(SetupData.create(setupKeys));

	BaseSetupPanel newPanel = new BaseSetupPanel(setupKeys, identationLevel + 1);

	if (!setupKeys.getSetupKeys().isEmpty()) {
	    // newPanel.add(new javax.swing.JSeparator(), 0);
	    // newPanel.add(new javax.swing.JSeparator());
	}

	elementsOptionPanels.add(newPanel);

	// Refresh
	updateElementsComboBox();

	int elementIndex = elementsBoxShadow.size() - 1;
	// Select last item
	elementsBox.setSelectedIndex(elementIndex);
	// Update vision of setup options - not needed, when we select, automatically updates
	// updateSetupOptions();

	return elementIndex;
    }

    /**
     * Loads several elements from an AppValue.
     * 
     * @param choice
     */
    @Override
    // public void updatePanel(FieldValue value) {
    public void updatePanel(Object value) {
	// Clear previous values
	clearElements();

	ListOfSetups maps = (ListOfSetups) value;

	for (SetupData key : maps.getMapOfSetups()) {
	    // String enumName = BaseUtils.decodeMapOfSetupsKey(key);
	    // extractEnumNameFromListName(key);
	    // loadElement(enumName, maps.get(key));
	    loadElement(key);
	}

    }

    /**
     * Loads a single element from a file
     * 
     * @param aClass
     * @param aFile
     */
    // private void loadElement(String enumName, SetupData table) {
    private void loadElement(SetupData table) {
	// Build name
	String enumName = table.getSetupName();

	int setupIndex = choicesBoxShadow.indexOf(enumName);

	if (setupIndex == -1) {
	    SpecsLogs.getLogger().warning("Could not find enum '" + enumName + "'. Available enums:" + setups);
	    return;
	}

	// Create element
	int elementsIndex = addElement(setupIndex);

	// Set option file
	elementsFiles.set(elementsIndex, table);
	// Load values in the file
	elementsOptionPanels.get(elementsIndex).loadValues(table);

    }

    private void updateElementsComboBox() {
	// Build list of strings to present
	elementsBox.removeAllItems();
	for (int i = 0; i < elementsBoxShadow.size(); i++) {
	    // Get choice name
	    int choice = elementsBoxShadow.get(i);
	    // SingleSetupEnum setup = setups.get(choice);
	    // String setupName = setups.getNameList().get(choice);
	    String setupName = setups.getSetupKeysList().get(choice).getSetupName();

	    // String boxString = (i+1)+ " - "+((Enum)setup).name();
	    // String boxString = createListName((i+1), ((Enum)setup).name());

	    // String boxString = BaseUtils.encodeMapOfSetupsKey(((Enum)setup).name(), i+1);
	    // String boxString = BaseUtils.encodeMapOfSetupsKey(setupName, i+1);
	    String boxString = buildSetupString(setupName, i + 1);
	    elementsBox.addItem(boxString);
	}
    }

    private void updateSetupOptions() {
	if (currentOptionsPanel != null) {
	    remove(currentOptionsPanel);
	    currentOptionsPanel = null;
	}

	// Determine what item is selected in the elements combo
	int index = elementsBox.getSelectedIndex();

	if (index != -1) {
	    currentOptionsPanel = elementsOptionPanels.get(index);
	    add(currentOptionsPanel);
	    currentOptionsPanel.revalidate();
	}

	// TODO: Is it repaint necessary here, or revalidate on panel solves it?
	repaint();
	// System.out.println("SetupPanel Repainted");
    }

    /**
     * Removes an element from the elements list.
     * 
     * @return
     */
    public void removeElement(int index) {
	// Check if the index is valid
	if (elementsBox.getItemCount() <= index) {
	    SpecsLogs.getLogger().warning(
		    "Given index ('" + index + "')is too big. Elements size: " + elementsBox.getItemCount());
	    return;
	}

	// Remove shadow index, AppOptionFile and panel
	elementsBoxShadow.remove(index);
	elementsFiles.remove(index);
	elementsOptionPanels.remove(index);

	// Refresh
	updateElementsComboBox();

	// Calculate new index of selected element and select it
	int newIndex = calculateIndexAfterRemoval(index);
	if (newIndex != -1) {
	    elementsBox.setSelectedIndex(newIndex);
	}
    }

    private int calculateIndexAfterRemoval(int index) {
	int numElements = elementsBox.getItemCount();

	// If there are no elements, return -1
	if (numElements == 0) {
	    return -1;
	}

	// If there are enough elements, the index is the same
	if (numElements > index) {
	    return index;
	}

	// If size is the same as index, it means that we removed the last element
	// Return the index of the current last element
	if (numElements == index) {
	    return index - 1;
	}

	SpecsLogs.getLogger().warning("Invalid index '" + index + "' for list with '" + numElements + "' elements.");
	return -1;
    }

    // public Map<String, SetupData> getPackedValues() {
    public ListOfSetups getPackedValues() {

	List<SetupData> listOfSetups = new ArrayList<>();

	// For each selected panel, add the corresponding options table to the return list
	for (int i = 0; i < elementsOptionPanels.size(); i++) {
	    listOfSetups.add(elementsOptionPanels.get(i).getMapWithValues());
	}

	return new ListOfSetups(listOfSetups);
    }

    private void clearElements() {
	elementsBox.removeAllItems();

	elementsBoxShadow = new ArrayList<>();
	elementsFiles = new ArrayList<>();
	elementsOptionPanels = new ArrayList<>();
    }

    @Override
    public FieldValue getOption() {
	return FieldValue.create(getPackedValues(), getType());
    }

    private static String buildSetupString(String enumName, int index) {
	return index + ENUM_NAME_SEPARATOR + enumName;
    }

    @Override
    public JLabel getLabel() {
	return label;
    }

    @Override
    public Collection<FieldPanel> getPanels() {
	return elementsOptionPanels.stream()
		.map(setupPanel -> setupPanel.getPanels().values())
		.reduce(new ArrayList<>(), SpecsCollections::add);
    }
}
