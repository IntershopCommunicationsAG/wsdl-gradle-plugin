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

import com.intershop.gradle.wsdl.extension.WSDLExtension
import com.intershop.gradle.wsdl.tasks.AbstractWSDL2Java
import com.intershop.gradle.wsdl.tasks.axis2.WSDL2JavaRunner
import com.intershop.gradle.wsdl.utils.Databinding
import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaForkOptions
import org.gradle.workers.ForkMode
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

open class WSDL2Java @Inject constructor(private val workerExecutor: WorkerExecutor) : AbstractWSDL2Java() {

    /**
     * Generate code only for async style. When this option is used the generated
     * stubs will have only the asynchronous invocation methods. Switched off by default.
     */
    private val asyncProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var async: Boolean
        get() = asyncProperty.getOrElse("false").toBoolean()
        set(value) = asyncProperty.set(value.toString())

    fun provideAsync(async: Provider<String>) = asyncProperty.set(async)

    /**
     * Generate code only for sync style . When this option is used the generated stubs
     * will have only the synchronous invocation methods. Switched off by default.
     * When async is set to true, this takes precedence.
     */
    private val syncProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var sync: Boolean
        get() = syncProperty.getOrElse("false").toBoolean()
        set(value) = syncProperty.set(value.toString())

    fun provideSync(sync: Provider<String>) = syncProperty.set(sync)

    /**
     * Generates server side code (i.e. skeletons). Default is false.
     */
    private val serverSideProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var serverSide: Boolean
        get() = serverSideProperty.getOrElse("false").toBoolean()
        set(value) = serverSideProperty.set(value.toString())

    fun provideServerSide(serverSide: Provider<String>) = serverSideProperty.set(serverSide)
    /**
     * Generates the service descriptor (i.e. server.xml). Default is false.
     * Only valid if serverSide is true, the server side code generation option.
     */
    private val serviceDescriptionProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var serviceDescription: Boolean
        get() = serviceDescriptionProperty.getOrElse("false").toBoolean()
        set(value) = serviceDescriptionProperty.set(value.toString())

    fun provideServiceDescription(serviceDescription: Provider<String>) = serviceDescriptionProperty.set(serviceDescription)

    /**
     * Specifies the Databinding framework.
     * Valid values are
     *  - xmlbeans -> XMLBEANS,
     *  - adb      -> ADB,
     *  - jaxbri   -> JAXBRI
     *  - jibx     -> JIBX, and
     *  - none     -> NONE.
     *  Default is adb.
     */
    private val databindingMethodProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var databindingMethod: String
        get() = databindingMethodProperty.getOrElse(Databinding.ADB.binding)
        set(value) = databindingMethodProperty.set(value)

    fun provideDatabindingMethod(databindingMethod: Provider<String>) = databindingMethodProperty.set(databindingMethod)

    /**
     * Generates all the classes. This option is valid only if serverSide otpion is true. If the value is true,
     * the client code (stubs) will also be generated along with the skeleton.
     */
    private val generateAllClassesProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var generateAllClasses: Boolean
        get() = generateAllClassesProperty.getOrElse("false").toBoolean()
        set(value) = generateAllClassesProperty.set(value.toString())

    fun provideGenerateAllClasses(generateAllClasses: Provider<String>) = generateAllClassesProperty.set(generateAllClasses)

    /**
     * Unpack classes. This option specifies whether to unpack the classes and
     * generate separate classes for the databinders.
     */
    private val unpackClassesProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var unpackClasses: Boolean
        get() = unpackClassesProperty.getOrElse("false").toBoolean()
        set(value) = unpackClassesProperty.set(value.toString())

    fun provideUnpackClasses(unpackClasses: Provider<String>) = unpackClassesProperty.set(unpackClasses)

    /**
     * Specifies the service name to be code generated. If the service name is not specified,
     * then the first service will be picked.
     */
    private val serviceNameProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var serviceName: String
        get() = serviceNameProperty.getOrElse("")
        set(value) = serviceNameProperty.set(value)

    fun provideServiceName(serviceName: Provider<String>) = serviceNameProperty.set(serviceName)

    /**
     * Specifies the port name to be code generated. If the port name is not specified,
     * then the first port (of the selected service) will be picked.
     */
    private val portNameProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var portName: String
        get() = portNameProperty.getOrElse("")
        set(value) = portNameProperty.set(value)

    fun providePortName(portName: Provider<String>) = portNameProperty.set(portName)

    /**
     * Generate an interface for the service skeleton.
     */
    private val serversideInterfaceProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var serversideInterface: Boolean
        get() = serversideInterfaceProperty.getOrElse("false").toBoolean()
        set(value) = serversideInterfaceProperty.set(value.toString())

    fun provideServersideInterface(serversideInterface: Provider<String>) = serversideInterfaceProperty.set(serversideInterface)

    /**
     * WSDLExtension Version. Valid Options : 2, 2.0, 1.1
     */
    private val wsdlVersionProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var wsdlVersion: String
        get() = wsdlVersionProperty.getOrElse("")
        set(value) = wsdlVersionProperty.set(value)

    fun provideWsdlVersion(wsdlVersion: Provider<String>) = wsdlVersionProperty.set(wsdlVersion)

    /**
     * Flattens the generated files
     */
    private val flattenFilesProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var flattenFiles: Boolean
        get() = flattenFilesProperty.getOrElse("false").toBoolean()
        set(value) = flattenFilesProperty.set(value.toString())

    fun provideFlattenFiles(flattenFiles: Provider<String>) = flattenFilesProperty.set(flattenFiles)

    /**
     * Switch on un-wrapping, if this value is true.
     */
    private val unwrapParamsProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var unwrapParams: Boolean
        get() = unwrapParamsProperty.getOrElse("false").toBoolean()
        set(value) = unwrapParamsProperty.set(value.toString())

    fun provideUnwrapParams(unwrapParams: Provider<String>) {
        unwrapParamsProperty.set(unwrapParams)
    }

    /**
     * Use XMLBeans .xsdconfig file if this value is true.
     * This is only valid if  databindingMethod is 'xmlbeans'.
     */
    private val xsdconfigProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var xsdconfig: Boolean
        get() = xsdconfigProperty.getOrElse("false").toBoolean()
        set(value) = xsdconfigProperty.set(value.toString())

    fun provideXsdconfig(xsdconfig: Provider<String>) = xsdconfigProperty.set(xsdconfig)

    /**
     * Generate code for all ports
     */
    private val allPortsProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var allPorts: Boolean
        get() = allPortsProperty.getOrElse("false").toBoolean()
        set(value) = allPortsProperty.set(value.toString())

    fun provideAllPorts(allPorts: Provider<String>) = allPortsProperty.set(allPorts)

    /**
     * Generate Axis 1.x backword compatible code
     */
    private val backwordCompatibleProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var backwordCompatible: Boolean
        get() = backwordCompatibleProperty.getOrElse("false").toBoolean()
        set(value) = backwordCompatibleProperty.set(value.toString())

    fun provideBackwordCompatible(backwordCompatible: Provider<String>) = backwordCompatibleProperty.set(backwordCompatible)

    /**
     * Suppress namespace prefixes (Optimzation that reduces size of soap request/response)
     */
    private val suppressPrefixesProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var suppressPrefixes: Boolean
        get() = suppressPrefixesProperty.getOrElse("false").toBoolean()
        set(value) = suppressPrefixesProperty.set(value.toString())

    fun provideSuppressPrefixes(suppressPrefixes: Provider<String>) = suppressPrefixesProperty.set(suppressPrefixes)

    /**
     * Don't generate a MessageReceiver in the generated sources
     */
    private val noMessageReceiverProperty: Property<String> = project.objects.property(String::class.java)

    @get:Optional
    @get:Input
    var noMessageReceiver: Boolean
        get() = noMessageReceiverProperty.getOrElse("false").toBoolean()
        set(value) = noMessageReceiverProperty.set(value.toString())

    fun provideNoMessageReceiver(noMessageReceiver: Provider<String>) = noMessageReceiverProperty.set(noMessageReceiver)

    @get:InputFiles
    private val toolsclasspathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        // find files of original JASPER and Eclipse compiler
        returnFiles.from(project.configurations.findByName(WSDLExtension.WSDL_AXIS2_CONFIGURATION_NAME))
        returnFiles
    }

    @TaskAction
    fun run() {
        // start runner
        workerExecutor.submit(WSDL2JavaRunner::class.java, {
            it.displayName = "WSDL2Java Axis2 code generation runner."
            it.setParams(calculateParameters())
            it.classpath(toolsclasspathfiles)
            it.isolationMode = IsolationMode.CLASSLOADER
            it.forkMode = ForkMode.AUTO
            if(internalForkOptionsAction != null) {
                project.logger.debug("Add configured JavaForkOptions to WSDL2Java Axis2 code generation runner.")
                (internalForkOptionsAction as Action<in JavaForkOptions>).execute(it.forkOptions)
            }
        })

        workerExecutor.await()
    }

    fun calculateParameters() : List<String> {
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