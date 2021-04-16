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
package com.intershop.gradle.wsdl.tasks.axis2

import com.intershop.gradle.wsdl.tasks.AbstractWSDL2Java
import com.intershop.gradle.wsdl.utils.Databinding
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.process.JavaForkOptions
import org.gradle.process.internal.DefaultJavaDebugOptions
import org.gradle.process.internal.DefaultJavaForkOptions
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * This task generates the source code from existing WSDL with axis2 files with a specific configuration.
 *
 * @constructor constructor initialize a WSDL2Java task with a worker executtor.
 */
abstract class WSDL2Java
    @Inject constructor(objectFactory: ObjectFactory,
                        @Internal val execOps: ExecOperations) : AbstractWSDL2Java(objectFactory) {

    private val asyncProperty = objectFactory.property(Boolean::class.java)

    /**
     * Generate code only for async style. When this option is used the generated
     * stubs will have only the asynchronous invocation methods.
     *
     * @property async default value is false
     */
    @get:Input
    var async: Boolean
        get() = asyncProperty.getOrElse(false)
        set(value) = asyncProperty.set(value)

    /**
     * Add provider for async.
     */
    fun provideAsync(async: Provider<Boolean>) = asyncProperty.set(async)

    private val syncProperty = objectFactory.property(Boolean::class.java)

    /**
     * Generate code only for sync style . When this option is used the generated stubs
     * will have only the synchronous invocation methods.
     * When async is set to true, this takes precedence.
     *
     * @property sync default value is false
     */
    @get:Input
    var sync: Boolean
        get() = syncProperty.getOrElse(false)
        set(value) = syncProperty.set(value)

    /**
     * Add provider for sync.
     */
    fun provideSync(sync: Provider<Boolean>) = syncProperty.set(sync)

    private val serverSideProperty = objectFactory.property(Boolean::class.java)

    /**
     * Generates server side code (i.e. skeletons).
     *
     * @property serverSide default value is false
     */
    @get:Input
    var serverSide: Boolean
        get() = serverSideProperty.getOrElse(false)
        set(value) = serverSideProperty.set(value)

    /**
     * Add provider for serverSide.
     */
    fun provideServerSide(serverSide: Provider<Boolean>) = serverSideProperty.set(serverSide)

    private val serviceDescriptionProperty = objectFactory.property(Boolean::class.java)

    /**
     * Generates the service descriptor (i.e. server.xml). Default is false.
     * Only valid if serverSide is true, the server side code generation option.
     *
     * @property serviceDescription default value is false
     */
    @get:Input
    var serviceDescription: Boolean
        get() = serviceDescriptionProperty.getOrElse(false)
        set(value) = serviceDescriptionProperty.set(value)

    /**
     * Add provider for serviceDescription.
     */
    fun provideServiceDescription(serviceDescription: Provider<Boolean>) =
            serviceDescriptionProperty.set(serviceDescription)

    private val databindingMethodProperty = objectFactory.property(String::class.java)

    /**
     * Specifies the Databinding framework.
     * Valid values are
     *  - xmlbeans -> XMLBEANS,
     *  - adb      -> ADB,
     *  - jaxbri   -> JAXBRI
     *  - jibx     -> JIBX, and
     *  - none     -> NONE.
     *  Default is adb.
     *
     *  @property databindingMethod default value is 'adb'
     */
    @get:Optional
    @get:Input
    var databindingMethod: String
        get() = databindingMethodProperty.getOrElse(Databinding.ADB.binding)
        set(value) = databindingMethodProperty.set(value)

    /**
     * Add provider for databindingMethod.
     */
    fun provideDatabindingMethod(databindingMethod: Provider<String>) = databindingMethodProperty.set(databindingMethod)

    private val generateAllClassesProperty = objectFactory.property(Boolean::class.java)

    /**
     * Generates all the classes. This option is valid only if serverSide otpion is true. If the value is true,
     * the client code (stubs) will also be generated along with the skeleton.
     *
     * @property generateAllClasses default value is false
     */
    @get:Input
    var generateAllClasses: Boolean
        get() = generateAllClassesProperty.getOrElse(false)
        set(value) = generateAllClassesProperty.set(value)

    /**
     * Add provider for generateAllClasses.
     */
    fun provideGenerateAllClasses(generateAllClasses: Provider<Boolean>) =
            generateAllClassesProperty.set(generateAllClasses)

    private val unpackClassesProperty = objectFactory.property(Boolean::class.java)

    /**
     * Unpack classes. This option specifies whether to unpack the classes and
     * generate separate classes for the databinders.
     *
     * @property unpackClasses default value is false
     */
    @get:Input
    var unpackClasses: Boolean
        get() = unpackClassesProperty.getOrElse(false)
        set(value) = unpackClassesProperty.set(value)

    /**
     * Add provider for unpackClasses.
     */
    fun provideUnpackClasses(unpackClasses: Provider<Boolean>) = unpackClassesProperty.set(unpackClasses)

    private val serviceNameProperty = objectFactory.property(String::class.java)

    /**
     * Specifies the service name to be code generated. If the service name is not specified,
     * then the first service will be picked.
     *
     * @property serviceName default value is ""
     */
    @get:Optional
    @get:Input
    var serviceName: String
        get() = serviceNameProperty.getOrElse("")
        set(value) = serviceNameProperty.set(value)

    /**
     * Add provider for serviceName.
     */
    fun provideServiceName(serviceName: Provider<String>) = serviceNameProperty.set(serviceName)

    private val portNameProperty = objectFactory.property(String::class.java)

    /**
     * Specifies the port name to be code generated. If the port name is not specified,
     * then the first port (of the selected service) will be picked.
     *
     * @property portName default value is ""
     */
    @get:Optional
    @get:Input
    var portName: String
        get() = portNameProperty.getOrElse("")
        set(value) = portNameProperty.set(value)

    /**
     * Add provider for portName.
     */
    fun providePortName(portName: Provider<String>) = portNameProperty.set(portName)

    private val serversideInterfaceProperty = objectFactory.property(Boolean::class.java)

    /**
     * Generate an interface for the service skeleton.
     *
     * @property serversideInterface default value is false
     */
    @get:Input
    var serversideInterface: Boolean
        get() = serversideInterfaceProperty.getOrElse(false)
        set(value) = serversideInterfaceProperty.set(value)

    /**
     * Add provider for serversideInterface.
     */
    fun provideServersideInterface(serversideInterface: Provider<Boolean>) =
            serversideInterfaceProperty.set(serversideInterface)

    private val wsdlVersionProperty = objectFactory.property(String::class.java)

    /**
     * WSDLExtension Version. Valid Options : 2, 2.0, 1.1
     *
     * @property wsdlVersion default value is ""
     */
    @get:Optional
    @get:Input
    var wsdlVersion: String
        get() = wsdlVersionProperty.getOrElse("")
        set(value) = wsdlVersionProperty.set(value)

    /**
     * Add provider for wsdlVersion.
     */
    fun provideWsdlVersion(wsdlVersion: Provider<String>) = wsdlVersionProperty.set(wsdlVersion)

    private val flattenFilesProperty = objectFactory.property(Boolean::class.java)

    /**
     * Flattens the generated files if the value of the property is true.
     *
     * @property flattenFiles default value is false
     */
    @get:Input
    var flattenFiles: Boolean
        get() = flattenFilesProperty.getOrElse(false)
        set(value) = flattenFilesProperty.set(value)

    /**
     * Add provider for flattenFiles.
     */
    fun provideFlattenFiles(flattenFiles: Provider<Boolean>) = flattenFilesProperty.set(flattenFiles)

    private val unwrapParamsProperty = objectFactory.property(Boolean::class.java)

    /**
     * Switch on un-wrapping, if this value is true.
     *
     * @property unwrapParams default value is false
     */
    @get:Input
    var unwrapParams: Boolean
        get() = unwrapParamsProperty.getOrElse(false)
        set(value) = unwrapParamsProperty.set(value)

    /**
     * Add provider for unwrapParams.
     */
    fun provideUnwrapParams(unwrapParams: Provider<Boolean>) = unwrapParamsProperty.set(unwrapParams)

    private val xsdconfigProperty = objectFactory.property(Boolean::class.java)

    /**
     * Use XMLBeans .xsdconfig file if this value is true.
     * This is only valid if  databindingMethod is 'xmlbeans'.
     *
     * @property xsdconfig default value is false
     */
    @get:Input
    var xsdconfig: Boolean
        get() = xsdconfigProperty.getOrElse(false)
        set(value) = xsdconfigProperty.set(value)

    /**
     * Add provider for xsdconfig.
     */
    fun provideXsdconfig(xsdconfig: Provider<Boolean>) = xsdconfigProperty.set(xsdconfig)

    private val allPortsProperty = objectFactory.property(Boolean::class.java)

    /**
     * Generate code for all ports.
     *
     * @property allPorts default value is false
     */
    @get:Input
    var allPorts: Boolean
        get() = allPortsProperty.getOrElse(false)
        set(value) = allPortsProperty.set(value)

    /**
     * Add provider for allPorts.
     */
    fun provideAllPorts(allPorts: Provider<Boolean>) = allPortsProperty.set(allPorts)

    private val backwordCompatibleProperty = objectFactory.property(Boolean::class.java)

    /**
     * Generate Axis 1.x backword compatible code.
     *
     * @property backwordCompatible default value is false
     */
    @get:Input
    var backwordCompatible: Boolean
        get() = backwordCompatibleProperty.getOrElse(false)
        set(value) = backwordCompatibleProperty.set(value)

    /**
     * Add provider for backwordCompatible.
     */
    fun provideBackwordCompatible(backwordCompatible: Provider<Boolean>) =
            backwordCompatibleProperty.set(backwordCompatible)

    private val suppressPrefixesProperty = objectFactory.property(Boolean::class.java)

    /**
     * Suppress namespace prefixes (Optimzation that reduces size of soap request/response).
     *
     * @property suppressPrefixes default value is false
     */
    @get:Input
    var suppressPrefixes: Boolean
        get() = suppressPrefixesProperty.getOrElse(false)
        set(value) = suppressPrefixesProperty.set(value)

    /**
     * Add provider for suppressPrefixes.
     */
    fun provideSuppressPrefixes(suppressPrefixes: Provider<Boolean>) = suppressPrefixesProperty.set(suppressPrefixes)

    private val noMessageReceiverProperty = objectFactory.property(Boolean::class.java)

    /**
     * Don't generate a MessageReceiver in the generated sources.
     *
     * @property noMessageReceiver default value is false
     */
    @get:Input
    var noMessageReceiver: Boolean
        get() = noMessageReceiverProperty.getOrElse(false)
        set(value) = noMessageReceiverProperty.set(value)

    /**
     * Add provider for noMessageReceiver.
     */
    fun provideNoMessageReceiver(noMessageReceiver: Provider<Boolean>) =
            noMessageReceiverProperty.set(noMessageReceiver)

    /**
     * Classpath for Axis 2 files stored in a configuration.
     *
     * @property toolsClasspath file collection of libraries
     */
    @get:Classpath
    val toolsClasspath : ConfigurableFileCollection = project.files()

    private var javaOptions: JavaForkOptions = DefaultJavaForkOptions(
        (project as ProjectInternal).fileResolver,
        (project as ProjectInternal).services.get(FileCollectionFactory::class.java),
        DefaultJavaDebugOptions()
    )

    /**
     * This is the task action and generates Java source files.
     */
    @TaskAction
    fun run() {
        if (internalForkOptionsAction != null) {
            project.logger.debug("Add configured JavaForkOptions to WSDL2Java Axis2 code generation runner.")
            (internalForkOptionsAction)?.execute(javaOptions)
        }

        val result = execOps.javaexec {
            it.classpath = toolsClasspath
            it.mainClass.set("org.apache.axis2.wsdl.WSDL2Code")
            it.args = calculateParameters()
            javaOptions.copyTo(it)
            it.executable = "java"
        }
        if (result.exitValue != 0) {
            throw GradleException("Execution for ${this.name} failed!")
        }
    }

    private fun calculateParameters() : List<String> {
        val parameters: MutableList<String> = mutableListOf()

        addAttribute(parameters, wsdlFile.absolutePath, "-uri")
        addAttribute(parameters, "java", "--language")
        addAttribute(parameters, packageName, "--package")

        addFlag(parameters, async, "--async")
        if(! async) {
            addFlag(parameters, sync, "--sync")
        }
        if(async && sync) {
            project.logger.warn("Generate code only for async style, because 'async' was set to true")
        }

        addFlag(parameters, generateTestcase, "--test-case")
        addFlag(parameters, serverSide, "--server-side")
        addFlag(parameters, serviceDescription, "--service-description")
        addAttribute(parameters, databindingMethod, "--databinding-method")
        addFlag(parameters, generateAllClasses,"--generate-all")
        addFlag(parameters, unpackClasses, "--unpack-classes")
        addAttribute(parameters, serviceName, "--service-name")
        addAttribute(parameters, portName, "--port-name")

        val namespacePackageMappingAttr = StringBuilder()

        namespacePackageMappingList.forEach {
            namespacePackageMappingAttr.append(it).append(',')
        }

        if(namespacePackageMappingAttr.isNotBlank()) {
            namespacePackageMappingAttr.removeSuffix(",")
        }
        addAttribute(parameters, namespacePackageMappingAttr.toString(), "--namespace2package")

        addFlag(parameters, serversideInterface, "--serverside-interface")
        addAttribute(parameters, wsdlVersion, "--wsdl-version")


        addAttribute(parameters, "src" , "--source-folder")
        addAttribute(parameters, "resources", "--resource-folder")

        addAttribute(parameters, outputDir.absolutePath, "--output")

        outputDir.mkdirs()

        if (namespacePackageMappingFile != null) {
            val filePath: String = namespacePackageMappingFile?.absolutePath ?: ""
            addAttribute(parameters, filePath, "--external-mapping")
        }

        addFlag(parameters, flattenFiles, "--flatten-files")
        addFlag(parameters, unwrapParams, "--unwrap-params")

        if(databindingMethod == Databinding.XMLBEANS.toString()) {
            addFlag(parameters, xsdconfig, "-xsdconfig")
        }

        addFlag(parameters, allPorts, "--all-ports")
        addFlag(parameters, true, "--over-ride")
        addFlag(parameters, backwordCompatible, "--backword-compatible")
        addFlag(parameters, suppressPrefixes, "--suppress-prefixes")
        addFlag(parameters, true, "--noBuildXML")
        addFlag(parameters, true, "--noWSDL")
        addFlag(parameters, noMessageReceiver, "--noMessageReceiver")

        return parameters
    }
}
