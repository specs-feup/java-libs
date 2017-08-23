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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import pt.up.fe.specs.eclipse.Classpath.ClasspathFiles;
import pt.up.fe.specs.eclipse.Classpath.ClasspathParser;
import pt.up.fe.specs.eclipse.Tasks.TaskExecutor;
import pt.up.fe.specs.eclipse.Tasks.TaskUtils;
import pt.up.fe.specs.eclipse.Utilities.DeployUtils;
import pt.up.fe.specs.eclipse.builder.BuildUtils;
import pt.up.fe.specs.guihelper.BaseTypes.SetupData;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.utilities.ProgressCounter;

/**
 * Builds and deploys Eclipse projects.
 * 
 * @author Joao Bispo
 */
public class EclipseDeployment {

    private static final String BUILD_FILE = "build.xml";

    private static final Map<String, TaskExecutor> tasks = TaskUtils.getTasksByName();

    private static final Map<JarType, Consumer<EclipseDeploymentData>> DEPLOY_BUILDER;
    static {
        DEPLOY_BUILDER = new HashMap<>();
        EclipseDeployment.DEPLOY_BUILDER.put(JarType.RepackJar, EclipseDeployment::buildJarRepack);
        EclipseDeployment.DEPLOY_BUILDER.put(JarType.UseJarInJar, EclipseDeployment::buildJarInJar);
    }

    private final EclipseDeploymentData data;

    public EclipseDeployment(EclipseDeploymentData data) {
        this.data = data;
    }

    public int execute() {

        // Clear temporary folder
        DeployUtils.clearTempFolder();

        // Resolve Ivy
        resolveIvy();

        // Check if case is defined
        if (!EclipseDeployment.DEPLOY_BUILDER.containsKey(data.jarType)) {
            SpecsLogs.msgWarn("Case not defined:" + data.jarType);
        }

        // Build JAR
        EclipseDeployment.DEPLOY_BUILDER
                .getOrDefault(data.jarType, EclipseDeployment::buildJarRepack)
                .accept(data);

        // Execute tasks
        processTasks();

        return 0;
    }

    private void resolveIvy() {
        ClasspathParser parser = ClasspathParser.newFromWorkspace(data.workspaceFolder);

        Collection<String> dependentProjects = parser.getDependentProjects(data.projetName);
        Collection<String> projectsWithIvy = BuildUtils.filterProjectsWithIvy(parser, dependentProjects);

        // Replace fields in template
        String template = SpecsIo.getResource(DeployResource.IVY_RESOLVE_TEMPLATE);

        // System.out.println("IVY RESOLVE 1:\n" + BuildUtils.getResolveTasks(parser, dependentProjects));
        // System.out.println("IVY RESOLVE 2:\n" + BuildUtils.getResolveTask(classpathFiles));
        template = template.replace("<IVY_RESOLVE>", BuildUtils.getResolveTasks(parser, dependentProjects));
        template = template.replace("<USE_IVY>", BuildUtils.getIvyDependency(parser));
        template = template.replace("<IVY_DEPENDENCIES>", BuildUtils.getIvyDepends(projectsWithIvy));
        // System.out.println("BUILD FILE:\n" + template);

        // Save script
        File buildFile = new File(EclipseDeployment.BUILD_FILE);
        SpecsIo.write(buildFile, template);

        // Launch ant
        Project project = new Project();
        project.init();

        ProjectHelper.configureProject(project, buildFile);

        project.addBuildListener(DeployUtils.newStdoutListener());
        project.executeTarget(project.getDefaultTarget());

    }

    /**
     * 
     */
    private void processTasks() {

        ProgressCounter progress = new ProgressCounter(data.tasks.getNumSetups());

        for (SetupData setup : data.tasks.getMapOfSetups()) {
            String setupName = setup.getSetupName();

            System.out.println("Executing task '" + setupName + "' " + progress.next());

            // Get task
            TaskExecutor task = EclipseDeployment.tasks.get(setupName);

            if (task == null) {
                SpecsLogs.msgWarn("Could not find task for setup '" + setupName + "'");
                continue;
            }

            task.execute(setup, data);
        }

    }

    /**
     * 
     */
    /*
    private void deployFtp() {
    String script = "open specsuser:SpecS#12345@specs.fe.up.pt\r\n" + "bin\r\n"
    	+ "cd /home/specsuser/tools/gearman_server\r\n" + "put C:\\temp_output\\deploy\\suika.properties\r\n"
    	+ "ls\r\n" + "exit";
    
    IoUtils.write(new File("ftp_script.txt"), script);
    
    ProcessUtils.run(Arrays.asList("WinSCP.com", "/script=ftp_script.txt"), IoUtils.getWorkingDir()
    	.getAbsolutePath());
    
    }
    */

    /**
     * Builds a JAR with additional library JARs inside. Uses a custom class loader.
     */
    private static void buildJarInJar(EclipseDeploymentData data) {
        ClasspathParser parser = ClasspathParser.newFromWorkspace(data.workspaceFolder);

        ClasspathFiles classpathFiles = parser.getClasspath(data.projetName);

        Collection<String> dependentProjects = parser.getDependentProjects(data.projetName);
        Collection<String> projectsWithIvy = BuildUtils.filterProjectsWithIvy(parser, dependentProjects);
        Collection<String> ivyFolders = projectsWithIvy.stream()
                .map(ivyProject -> BuildUtils.getIvyJarFoldername(parser.getClasspath(ivyProject).getProjectFolder()))
                .collect(Collectors.toList());

        String fileset = DeployUtils.buildFileset(parser, data.projetName, ivyFolders, false);

        String jarList = DeployUtils.buildJarList(classpathFiles, ivyFolders);

        // Replace fields in template
        String template = SpecsIo.getResource(DeployResource.DEPLOY_JAR_IN_JAR_TEMPLATE);

        // Output JAR
        File outputJar = DeployUtils.getOutputJar(data.nameOfOutputJar);

        template = template.replace("<OUTPUT_JAR_FILE>", outputJar.getAbsolutePath());
        template = template.replace("<MAIN_CLASS>", data.mainClass);
        template = template.replace("<JAR_LIST>", jarList);
        template = template.replace("<FILESET>", fileset);

        // System.out.println("IVY RESOLVE 1:\n" + BuildUtils.getResolveTasks(parser, dependentProjects));
        // System.out.println("IVY RESOLVE 2:\n" + BuildUtils.getResolveTask(classpathFiles));
        template = template.replace("<IVY_RESOLVE>", BuildUtils.getResolveTasks(parser, dependentProjects));
        template = template.replace("<USE_IVY>", BuildUtils.getIvyDependency(parser));
        template = template.replace("<IVY_DEPENDENCIES>", BuildUtils.getIvyDepends(projectsWithIvy));
        template = template.replace("<DELETE_IVY>", DeployUtils.getDeleteIvyFolders(ivyFolders));
        // System.out.println("BUILD FILE:\n" + template);
        // Save script
        File buildFile = new File(EclipseDeployment.BUILD_FILE);
        SpecsIo.write(buildFile, template);

        // Run script
        // ProcessUtils.run(Arrays.asList("ant", "build.xml"), IoUtils.getWorkingDir().getPath());
        // Launch ant
        Project project = new Project();
        project.init();

        ProjectHelper.configureProject(project, buildFile);

        project.addBuildListener(DeployUtils.newStdoutListener());
        project.executeTarget(project.getDefaultTarget());
        // System.out.println("OUTPUT JAR:" + outputJar.getAbsolutePath());
        // Check if jar file exists
        if (!outputJar.isFile()) {
            throw new RuntimeException("Could not create output JAR '" + outputJar.getAbsolutePath() + "'");
        }

    }

    /**
     * Creates a single JAR with additional library JARs extracted into the file.
     */
    private static void buildJarRepack(EclipseDeploymentData data) {
        ClasspathParser parser = ClasspathParser.newFromWorkspace(data.workspaceFolder);

        ClasspathFiles classpathFiles = parser.getClasspath(data.projetName);

        Collection<String> dependentProjects = parser.getDependentProjects(data.projetName);
        Collection<String> projectsWithIvy = BuildUtils.filterProjectsWithIvy(parser, dependentProjects);
        Collection<String> ivyFolders = projectsWithIvy.stream()
                .map(ivyProject -> BuildUtils.getIvyJarFoldername(parser.getClasspath(ivyProject).getProjectFolder()))
                .collect(Collectors.toList());

        String fileset = DeployUtils.buildFileset(parser, data.projetName, ivyFolders, true);
        String jarList = DeployUtils.buildJarList(classpathFiles, ivyFolders);

        // Replace fields in template
        String template = SpecsIo.getResource(DeployResource.DEPLOY_REPACK_TEMPLATE);

        // Output JAR
        File outputJar = DeployUtils.getOutputJar(data.nameOfOutputJar);

        template = template.replace("<OUTPUT_JAR_FILE>", outputJar.getAbsolutePath());
        template = template.replace("<MAIN_CLASS>", data.mainClass);
        template = template.replace("<JAR_LIST>", jarList);
        template = template.replace("<FILESET>", fileset);

        // System.out.println("IVY RESOLVE 1:\n" + BuildUtils.getResolveTasks(parser, dependentProjects));
        // System.out.println("IVY RESOLVE 2:\n" + BuildUtils.getResolveTask(classpathFiles));
        template = template.replace("<IVY_RESOLVE>", BuildUtils.getResolveTasks(parser, dependentProjects));
        template = template.replace("<USE_IVY>", BuildUtils.getIvyDependency(parser));
        template = template.replace("<IVY_DEPENDENCIES>", BuildUtils.getIvyDepends(projectsWithIvy));
        template = template.replace("<DELETE_IVY>", DeployUtils.getDeleteIvyFolders(ivyFolders));
        // System.out.println("BUILD FILE:\n" + template);
        // Save script
        File buildFile = new File(EclipseDeployment.BUILD_FILE);
        SpecsIo.write(buildFile, template);

        // Run script
        // ProcessUtils.run(Arrays.asList("ant", "build.xml"), IoUtils.getWorkingDir().getPath());
        // Launch ant
        Project project = new Project();
        project.init();

        ProjectHelper.configureProject(project, buildFile);

        project.addBuildListener(DeployUtils.newStdoutListener());
        project.executeTarget(project.getDefaultTarget());
        // System.out.println("OUTPUT JAR:" + outputJar.getAbsolutePath());
        // Check if jar file exists
        if (!outputJar.isFile()) {
            throw new RuntimeException("Could not create output JAR '" + outputJar.getAbsolutePath() + "'");
        }

    }
}