/*
 * Copyright 2015 Intershop Communications AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.intershop.gradle.wsdl

import com.intershop.gradle.wsdl.extension.Axis1
import com.intershop.gradle.wsdl.extension.Axis2
import com.intershop.gradle.wsdl.extension.WSDLExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

/**
 * <p>This plugin will apply the WSDL plugin.</p>
 *
 * <p>It generates code with Axis 1 and Axis 2</p>
 */
class WSDLPlugin implements Plugin<Project> {

    /**
     * Name of the extension
     */
    final static String EXTENSION_NAME = 'wsdl'

    private WSDLExtension extension

    void apply(Project project) {
        project.logger.info("Applying ${EXTENSION_NAME} plugin to project: ${project.name}")

        this.extension = project.extensions.findByType(WSDLExtension) ?: project.extensions.create(EXTENSION_NAME, WSDLExtension, project)

        addAxis1Configuration(project)
        addAxis2Configuration(project)

        Task wsdl2javaTask = project.getTasks().findByName('wsdl2java')
        if(! wsdl2javaTask) {
            wsdl2javaTask = project.getTasks().create('wsdl2java')
            wsdl2javaTask.group = WSDLExtension.WSDL_TASK_GROUP
            wsdl2javaTask.description = 'WSDL to Java code generation tasks'
        }

        configureAxis1Tasks(project, wsdl2javaTask)
        configureAxis2Tasks(project, wsdl2javaTask)
    }

    /**
     * Create and configure tasks for Axis 1 code generation
     * @param project
     * @param mainTask wsdl2Java
     */
    private void configureAxis1Tasks(Project project, Task mainTask) {
        extension.getAxis1().all { Axis1 axis1Config ->
            com.intershop.gradle.wsdl.tasks.axis1.WSDL2Java task = project.getTasks().create(axis1Config.getTaskName(), com.intershop.gradle.wsdl.tasks.axis1.WSDL2Java.class )
            task.group = WSDLExtension.WSDL_TASK_GROUP
            task.description = "Create java files file for ${axis1Config.name} of ${project.name}"

            task.conventionMapping.outputDirectory = {
                axis1Config.getOutputDir() ?: new File(project.getBuildDir(),
                        "${WSDLExtension.CODEGEN_DEFAULT_OUTPUTPATH}/${axis1Config.getName().replace(' ', '_')}")
            }

            task.conventionMapping.wsdlFile = { axis1Config.getWsdlFile() }

            task.conventionMapping.noImports = { axis1Config.getNoImports() }
            task.conventionMapping.timeout = { axis1Config.getTimeout() }
            task.conventionMapping.noWrapped = { axis1Config.getNoWrapped() }
            task.conventionMapping.serverSide = { axis1Config.getServerSide() }
            task.conventionMapping.skeletonDeploy = { axis1Config.getSkeletonDeploy() }
            task.conventionMapping.deployScope = { axis1Config.getDeployScope() }
            task.conventionMapping.generateAllClasses = { axis1Config.getGenerateAllClasses() }
            task.conventionMapping.typeMappingVersion = { axis1Config.getTypeMappingVersion() }
            task.conventionMapping.factory = { axis1Config.getFactory() }
            task.conventionMapping.helperGen = { axis1Config.getHelperGen() }
            task.conventionMapping.userName = { axis1Config.getUserName() }
            task.conventionMapping.password = { axis1Config.getPassword() }
            task.conventionMapping.implementationClassName = { axis1Config.getImplementationClassName() }
            task.conventionMapping.wrapArrays = { axis1Config.getWrapArrays() }
            task.conventionMapping.packageName = { axis1Config.getPackageName() }
            task.conventionMapping.namespacePackageMapping = { axis1Config.getNamespacePackageMapping() }
            task.conventionMapping.generateTestcase = { axis1Config.getGenerateTestcase() }
            task.conventionMapping.namespacePackageMappingFile = { axis1Config.getNamespacePackageMappingFile() }
            task.conventionMapping.forkOptions = { extension.getAxis1ForkOptions() }
            task.conventionMapping.addArgs = { axis1Config.getArgs() }

            // identify sourceset configuration and add output to sourceset
            project.afterEvaluate {
                if (axis1Config.getSourceSetName() && project.plugins.hasPlugin(JavaBasePlugin) && ! project.convention.getPlugin(JavaPluginConvention.class).sourceSets.isEmpty()) {
                    SourceSet sourceSet = project.convention.getPlugin(JavaPluginConvention.class).sourceSets.findByName(axis1Config.getSourceSetName())
                    if(sourceSet != null) {
                        if(! sourceSet.java.srcDirs.contains(task.getOutputDirectory())) {
                            sourceSet.java.srcDir(task.getOutputDirectory())
                        }
                        project.tasks.getByName(sourceSet.compileJavaTaskName).dependsOn(task)
                    }
                }
            }

            mainTask.dependsOn task
        }
    }

    /**
     * Create and configure tasks for Axis 2 code generation
     * @param project
     * @param mainTask wsdl2Java
     */
    private void configureAxis2Tasks(Project project, Task mainTask) {
        extension.getAxis2().all { Axis2 axis2Config ->
            com.intershop.gradle.wsdl.tasks.axis2.WSDL2Java task = project.getTasks().create(axis2Config.getTaskName(), com.intershop.gradle.wsdl.tasks.axis2.WSDL2Java.class )
            task.group = WSDLExtension.WSDL_TASK_GROUP
            task.description = "Create java files file for ${axis2Config.name} of ${project.name}"

            task.conventionMapping.srcOutputDirectory = {
                axis2Config.getSrcOutputDir() ?: new File(project.getBuildDir(),
                        "${WSDLExtension.CODEGEN_DEFAULT_OUTPUTPATH}/${axis2Config.getName().replace(' ', '_')}/src")
            }
            task.conventionMapping.resourcesOutputDirectory = {
                axis2Config.getResourceOutputDir() ?: new File(project.getBuildDir(),
                        "${WSDLExtension.CODEGEN_DEFAULT_OUTPUTPATH}/${axis2Config.getName().replace(' ', '_')}/resources")
            }

            task.conventionMapping.wsdlFile = { axis2Config.getWsdlFile() }

            task.conventionMapping.async = { axis2Config.getAsync() }
            task.conventionMapping.sync = { axis2Config.getSync() }
            task.conventionMapping.serverSide = { axis2Config.getServerSide() }
            task.conventionMapping.serviceDescription = { axis2Config.getServiceDescription() }
            task.conventionMapping.databindingMethod = { axis2Config.getDatabindingMethod() }
            task.conventionMapping.generateAllClasses = { axis2Config.getGenerateAllClasses() }
            task.conventionMapping.unpackClasses = { axis2Config.getUnpackClasses() }
            task.conventionMapping.serviceName = { axis2Config.getServiceName() }
            task.conventionMapping.portName = { axis2Config.getPortName() }
            task.conventionMapping.serversideInterface = { axis2Config.getServersideInterface() }
            task.conventionMapping.wsdlVersion = { axis2Config.getWsdlVersion() }
            task.conventionMapping.flattenFiles = { axis2Config.getFlattenFiles() }
            task.conventionMapping.unwrapParams = { axis2Config.getUnwrapParams() }
            task.conventionMapping.xsdconfig = { axis2Config.getXsdconfig() }
            task.conventionMapping.allPorts = { axis2Config.getAllPorts() }
            task.conventionMapping.backwordCompatible = { axis2Config.getBackwordCompatible() }
            task.conventionMapping.suppressPrefixes = { axis2Config.getSuppressPrefixes() }
            task.conventionMapping.noMessageReceiver = { axis2Config.getNoMessageReceiver() }
            task.conventionMapping.packageName = { axis2Config.getPackageName() }
            task.conventionMapping.namespacePackageMapping = { axis2Config.getNamespacePackageMapping() }
            task.conventionMapping.generateTestcase = { axis2Config.getGenerateTestcase() }
            task.conventionMapping.namespacePackageMappingFile = { axis2Config.getNamespacePackageMappingFile() }
            task.conventionMapping.forkOptions = { extension.getAxis2ForkOptions() }
            task.conventionMapping.addArgs = { axis2Config.getArgs() }

            // identify sourceset configuration and add output to sourceset
            project.afterEvaluate {
                if (axis2Config.getSourceSetName() && project.plugins.hasPlugin(JavaBasePlugin) && ! project.convention.getPlugin(JavaPluginConvention.class).sourceSets.isEmpty()) {
                    SourceSet sourceSet = project.convention.getPlugin(JavaPluginConvention.class).sourceSets.findByName(axis2Config.getSourceSetName())
                    if(sourceSet != null) {
                        if(! sourceSet.java.srcDirs.contains(task.getSrcOutputDirectory())) {
                            sourceSet.java.srcDir(task.getSrcOutputDirectory())
                        }
                        project.tasks.getByName(sourceSet.compileJavaTaskName).dependsOn(task)
                        if(! sourceSet.resources.contains(task.getResourcesOutputDirectory())) {
                            sourceSet.resources.srcDir(task.getResourcesOutputDirectory())
                        }
                    }
                }
            }

            mainTask.dependsOn task
        }
    }

    /**
     * Adds the dependencies for the axis 1 code generation. It is possible to override this.
     *
     * @param project
     * @param extension
     */
    private void addAxis1Configuration(final Project project) {
        final Configuration configuration =
                project.getConfigurations().findByName(WSDLExtension.WSDLAXIS1_CONFIGURATION_NAME) ?:
                        project.getConfigurations().create(WSDLExtension.WSDLAXIS1_CONFIGURATION_NAME)

        configuration
                .setVisible(false)
                .setTransitive(false)
                .setDescription("WSDL Axis1 configuration is used for code generation")
                .defaultDependencies { dependencies ->
            DependencyHandler dependencyHandler = project.getDependencies()

            dependencies.add(dependencyHandler.create('axis:axis-wsdl4j:1.5.1'))
            dependencies.add(dependencyHandler.create('commons-discovery:commons-discovery:0.5'))
            dependencies.add(dependencyHandler.create('javax.activation:activation:1.1.1'))
            dependencies.add(dependencyHandler.create('javax.mail:mail:1.4.7'))

            dependencies.add(dependencyHandler.create('org.apache.axis:axis:' +  extension.getAxis1Version()))
            dependencies.add(dependencyHandler.create('org.apache.axis:axis-jaxrpc:' +  extension.getAxis1Version()))
        }
    }

    /**
     * Adds the dependencies for the axis 2 code generation. It is possible to override this.
     *
     * @param project
     * @param extension
     */
    private void addAxis2Configuration(final Project project) {
        final Configuration configuration =
                project.getConfigurations().findByName(WSDLExtension.WSDLAXIS2_CONFIGURATION_NAME) ?:
                        project.getConfigurations().create(WSDLExtension.WSDLAXIS2_CONFIGURATION_NAME)

        configuration
                .setVisible(false)
                .setTransitive(false)
                .setDescription("WSDL Axis2 configuration is used for code generation")
                .defaultDependencies { dependencies ->
            DependencyHandler dependencyHandler = project.getDependencies()
            dependencies.add(dependencyHandler.create('org.apache.axis2:axis2-codegen:' + extension.getAxis2Version()))
        }
    }
}