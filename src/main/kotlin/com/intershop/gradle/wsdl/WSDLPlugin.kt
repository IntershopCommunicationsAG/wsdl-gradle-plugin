/*
 * Copyright 2017 Intershop Communications AG.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intershop.gradle.wsdl

import com.intershop.gradle.wsdl.extension.WSDLExtension
import com.intershop.gradle.wsdl.extension.WSDLExtension.Companion.WSDL_EXTENSION_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import com.intershop.gradle.wsdl.tasks.axis1.WSDL2Java as axis1WSDL2Java
import com.intershop.gradle.wsdl.tasks.axis2.WSDL2Java as axis2WSDL2Java

/**
 * Plugin Class implementation.
 */
class WSDLPlugin : Plugin<Project> {

    companion object {
        /**
         * Description for main task.
         */
        const val TASKDESCRIPTION = "Generate Java code for Axis 1 and Axis2 WSDL files"
        /**
         * Taskname for main task.
         */
        const val TASKNAME = "wsdl2java"
    }

    /**
     * Applies the extension and calls the
     * task initialization for this plugin.
     *
     * @param project current project
     */
    override fun apply(project: Project) {
        with(project) {
            logger.info("WSDL plugin adds extension {} to {}", WSDL_EXTENSION_NAME, name)
            val extension = extensions.findByType(
                    WSDLExtension::class.java) ?: extensions.create(WSDL_EXTENSION_NAME, WSDLExtension::class.java)

            addWsdlAxis1Configuration(this)
            addWsdlAxis2Configuration(this)

            configureTask(this, extension)
        }
    }

    /*
     * Configure tasks for WSDL code generation
     *
     * @param project       project to configure
     * @param extension     extension of this plugin
     */
    private fun configureTask(project: Project, extension: WSDLExtension) {
        with(project) {
            val wsdlMain = tasks.maybeCreate(TASKNAME).apply {
                description = TASKDESCRIPTION
                group = WSDLExtension.WSDL_GROUP_NAME
            }

            confiureAxsis1Tasks(this, extension, wsdlMain)
            confiureAxsis2Tasks(this, extension, wsdlMain)
        }
    }

    /*
     * Configure tasks for WSDL code generation
     *
     * @param project       project to configure
     * @param extension     extension of this plugin
     * @param mainTask      main wsdl task
     */
    private fun confiureAxsis1Tasks(project: Project, extension: WSDLExtension, mainTask: Task) {
        with(project) {
            extension.axis1.all { axis1 ->
                tasks.maybeCreate(axis1.getTaskName(), axis1WSDL2Java::class.java).apply {
                    group = WSDLExtension.WSDL_GROUP_NAME

                    providePackageName(axis1.packageNameProvider)
                    namespacePackageMappings = axis1.namespacePackageMappings
                    provideGenerateTestcase(axis1.generateTestcaseProvider)
                    provideNamespacePackageMappingFile(axis1.namespacePackageMappingFileProvider)
                    provideArguments(axis1.argumentsProvider)
                    provideOutputDir(axis1.outputDirProvider)
                    provideWsdlFile(axis1.wsdlFileProvider)

                    provideNoImports(axis1.noImportsProvider)
                    provideTimeout(axis1.timeoutProvider)
                    provideNoWrapped(axis1.noWrappedProvider)
                    provideServerSide(axis1.serverSideProvider)
                    provideSkeletonDeploy(axis1.skeletonDeployProvider)
                    provideDeployScope(axis1.deployScopeProvider)
                    provideGenerateAllClasses(axis1.generateAllClassesProvider)
                    provideTypeMappingVersion(axis1.typeMappingVersionProvider)
                    provideFactory(axis1.factoryProvider)
                    provideHelperGen(axis1.helperGenProvider)
                    provideUserName(axis1.userNameProvider)
                    providePassword(axis1.passwordProvider)
                    provideImplementationClassName(axis1.implementationClassNameProvider)
                    provideWrapArrays(axis1.wrapArraysProvider)
                    provideAllowInvalidURL(axis1.allowInvalidURLProvider)
                    provideNsInclude(axis1.nsIncludeProvider)
                    provideNsExclude(axis1.nsExcludeProvider)

                    toolsClasspath.from(project.configurations.findByName(WSDLExtension.WSDL_AXIS1_CONFIGURATION_NAME))
                    wsdlProperties = axis1.wsdlProperties

                    afterEvaluate {
                        plugins.withType(JavaBasePlugin::class.java) {
                            val javaPluginConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
                            val sourceSet = javaPluginConvention.sourceSets.findByName(axis1.sourceSetName)
                            sourceSet?.java?.srcDir(this@apply.outputs)
                        }
                    }

                    mainTask.dependsOn(this)
                }
            }
        }
    }

    /*
     * Configure tasks for WSDL code generation
     *
     * @param project       project to configure
     * @param extension     extension of this plugin
     * @param mainTask      main wsdl task
     */
    private fun confiureAxsis2Tasks(project: Project, extension: WSDLExtension, mainTask: Task) {
        with(project) {
            extension.axis2.all { axis2 ->
                tasks.maybeCreate(axis2.getTaskName(), axis2WSDL2Java::class.java).apply {
                    group = WSDLExtension.WSDL_GROUP_NAME

                    providePackageName(axis2.packageNameProvider)
                    namespacePackageMappings = axis2.namespacePackageMappings
                    provideGenerateTestcase(axis2.generateTestcaseProvider)
                    provideNamespacePackageMappingFile(axis2.namespacePackageMappingFileProvider)
                    provideArguments(axis2.argumentsProvider)
                    provideOutputDir(axis2.outputDirProvider)
                    provideWsdlFile(axis2.wsdlFileProvider)

                    provideAsync(axis2.asyncProvider)
                    provideSync(axis2.syncProvider)
                    provideServerSide(axis2.serverSideProvider)
                    provideServiceDescription(axis2.serviceDescriptionProvider)
                    provideDatabindingMethod(axis2.databindingMethodProvider)
                    provideGenerateAllClasses(axis2.generateAllClassesProvider)
                    provideUnpackClasses(axis2.unpackClassesProvider)
                    provideServiceName(axis2.serviceNameProvider)
                    providePortName(axis2.portNameProvider)
                    provideServersideInterface(axis2.serversideInterfaceProvider)
                    provideWsdlVersion(axis2.wsdlVersionProvider)
                    provideFlattenFiles(axis2.flattenFilesProvider)
                    provideUnwrapParams(axis2.unwrapParamsProvider)
                    provideXsdconfig(axis2.xsdconfigProvider)
                    provideAllPorts(axis2.allPortsProvider)
                    provideBackwordCompatible(axis2.backwordCompatibleProvider)
                    provideSuppressPrefixes(axis2.suppressPrefixesProvider)
                    provideNoMessageReceiver(axis2.noMessageReceiverProvider)

                    toolsClasspath.from(project.configurations.findByName(WSDLExtension.WSDL_AXIS2_CONFIGURATION_NAME))

                    afterEvaluate {
                        plugins.withType(JavaBasePlugin::class.java) {
                            val javaPluginConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
                            val sourceSet = javaPluginConvention.sourceSets.findByName(axis2.sourceSetName)
                            sourceSet?.java?.srcDir(this@apply.outputs)
                        }
                    }

                    mainTask.dependsOn(this)
                }
            }
        }
    }

    /*
     * Adds the dependencies for the AXIS1 code generation. It is possible to override this.
     *
     * @param project
     */
    private fun addWsdlAxis1Configuration(project: Project) {
        val configuration = project.configurations.maybeCreate(WSDLExtension.WSDL_AXIS1_CONFIGURATION_NAME)
        configuration.setVisible(false)
                .setTransitive(true)
                .setDescription("Configuration for Axis code generator")
                .defaultDependencies {
                    val dependencyHandler = project.dependencies

                    it.add(dependencyHandler.create("axis:axis-wsdl4j:1.5.1"))
                    it.add(dependencyHandler.create("commons-discovery:commons-discovery:0.5"))
                    it.add(dependencyHandler.create("javax.activation:activation:1.1.1"))
                    it.add(dependencyHandler.create("javax.mail:mail:1.4.7"))
                    it.add(dependencyHandler.create("commons-logging:commons-logging:1.2"))

                    it.add(dependencyHandler.create("org.apache.axis:axis:1.4"))
                    it.add(dependencyHandler.create("org.apache.axis:axis-jaxrpc:1.4"))
                }
    }

    /*
     * Adds the dependencies for the AXIS2 code generation. It is possible to override this.
     *
     * @param project
     */
    private fun addWsdlAxis2Configuration(project: Project) {
        val configuration = project.configurations.maybeCreate(WSDLExtension.WSDL_AXIS2_CONFIGURATION_NAME)
        configuration.setVisible(false)
                .setTransitive(true)
                .setDescription("Configuration for Axis 2 code generator")
                .defaultDependencies {
                    val dependencyHandler = project.dependencies

                    it.add(dependencyHandler.create("org.apache.axis2:axis2-kernel:1.7.7"))
                    it.add(dependencyHandler.create("org.apache.axis2:axis2-codegen:1.7.7"))
                    it.add(dependencyHandler.create("org.apache.axis2:axis2-adb:1.7.7"))
                    it.add(dependencyHandler.create("org.apache.axis2:axis2-adb-codegen:1.7.7"))
                    it.add(dependencyHandler.create("org.apache.axis2:axis2-jaxbri:1.7.7"))
                    it.add(dependencyHandler.create("com.sun.xml.ws:jaxws-tools:2.2.10"))
                    it.add(dependencyHandler.create("wsdl4j:wsdl4j:1.6.3"))
                    it.add(dependencyHandler.create("commons-logging:commons-logging:1.2"))
                    it.add(dependencyHandler.create("org.apache.neethi:neethi:3.0.3"))
                    it.add(dependencyHandler.create("org.apache.ws.commons.axiom:axiom-api:1.2.20"))
                    it.add(dependencyHandler.create("org.apache.ws.commons.axiom:axiom-impl:1.2.20"))
                    it.add(dependencyHandler.create("org.apache.woden:woden-core:1.0M10"))
                    it.add(dependencyHandler.create("org.apache.ws.xmlschema:xmlschema-core:2.2.1"))
                    it.add(dependencyHandler.create("com.sun.xml.bind:jaxb-impl:2.2.6"))
                    it.add(dependencyHandler.create("com.sun.xml.bind:jaxb-xjc:2.2.6"))
                    it.add(dependencyHandler.create("javax.xml.soap:javax.xml.soap-api:1.4.0"))
                }
    }
}
