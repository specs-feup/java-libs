/*
 * Copyright 2013 SPeCS Research Group.
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

package pt.up.fe.specs.eclipse;

import java.io.File;
import java.util.List;

import pt.up.fe.specs.eclipse.Tasks.TaskUtils;
import pt.up.fe.specs.guihelper.FieldType;
import pt.up.fe.specs.guihelper.SetupAccess;
import pt.up.fe.specs.guihelper.Base.ListOfSetupDefinitions;
import pt.up.fe.specs.guihelper.Base.SetupFieldEnum;
import pt.up.fe.specs.guihelper.BaseTypes.FieldValue;
import pt.up.fe.specs.guihelper.BaseTypes.ListOfSetups;
import pt.up.fe.specs.guihelper.BaseTypes.SetupData;
import pt.up.fe.specs.guihelper.SetupFieldOptions.DefaultValue;
import pt.up.fe.specs.guihelper.SetupFieldOptions.MultipleChoice;
import pt.up.fe.specs.guihelper.SetupFieldOptions.MultipleSetup;
import pt.up.fe.specs.util.SpecsFactory;
import pt.up.fe.specs.util.utilities.StringList;

/**
 * Setup definition for program EclipseDeployment.
 *
 * @author Joao Bispo
 */
public enum EclipseDeploymentSetup implements SetupFieldEnum, MultipleSetup, MultipleChoice, DefaultValue {

    WorkspaceFolder(FieldType.string),
    ProjectName(FieldType.string),
    NameOfOutputJar(FieldType.string),
    ClassWithMain(FieldType.string),
    OutputJarType(FieldType.multipleChoice),
    Tasks(FieldType.setupList);

    public static EclipseDeploymentData newData(SetupData setupData) {
        SetupAccess setup = new SetupAccess(setupData);

        File workspaceFolder = setup.getFolderV2(null, WorkspaceFolder, true);

        String projetName = setup.getString(ProjectName);
        String nameOfOutputJar = setup.getString(NameOfOutputJar);
        String mainClass = setup.getString(ClassWithMain);

        JarType jarType = setup.getEnum(OutputJarType, JarType.class);

        ListOfSetups tasks = setup.getListOfSetups(Tasks);

        return new EclipseDeploymentData(workspaceFolder, projetName, nameOfOutputJar, mainClass, jarType, tasks);
    }

    private EclipseDeploymentSetup(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public FieldType getType() {
        return fieldType;
    }

    @Override
    public String getSetupName() {
        return "EclipseDeployment";
    }

    /**
     * INSTANCE VARIABLES
     */
    private final FieldType fieldType;

    /*
     * (non-Javadoc)
     *
     * @see pt.up.fe.specs.guihelper.SetupFieldOptions.MultipleSetup#getSetups()
     */
    @Override
    public ListOfSetupDefinitions getSetups() {
        /*
         * List<Class<? extends SetupFieldEnum>> setups =
         * FactoryUtils.newArrayList();
         * 
         * setups.addAll(TaskUtils.getTasks().keySet()); //
         * setups.add(FtpSetup.class); // setups.add(SftpSetup.class);
         * 
         * return ListOfSetupDefinitions.newInstance(setups);
         */
        return getTasksDefinitions();
    }

    public static ListOfSetupDefinitions getTasksDefinitions() {
        List<Class<? extends SetupFieldEnum>> setups = SpecsFactory.newArrayList();

        setups.addAll(TaskUtils.getTasks().keySet());
        // setups.add(FtpSetup.class);
        // setups.add(SftpSetup.class);

        return ListOfSetupDefinitions.newInstance(setups);
    }

    @Override
    public StringList getChoices() {
        if (this == OutputJarType) {
            return new StringList(JarType.class);
        }

        return null;
    }

    @Override
    public FieldValue getDefaultValue() {
        if (this == OutputJarType) {
            return FieldValue.create(JarType.RepackJar.name(), OutputJarType);
        }

        return null;
    }
}
