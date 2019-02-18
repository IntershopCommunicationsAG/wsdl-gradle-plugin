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
package com.intershop.gradle.wsdl.extension

import com.intershop.gradle.wsdl.utils.Databinding
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File

/**
 * Axis 2 Configuration container.
 *
 * @constructur default constructor with project and configuration name.
 */
open class Axis2(project: Project, private val confname: String) : AbstractAxisConfig(project, confname) {

    // properties will analyzed as Boolean
    private val asyncProperty = project.objects.property<Boolean>()
    private val syncProperty = project.objects.property<Boolean>()
    private val serverSideProperty = project.objects.property<Boolean>()
    private val serviceDescriptionProperty = project.objects.property<Boolean>()
    private val generateAllClassesProperty = project.objects.property<Boolean>()
    private val unpackClassesProperty = project.objects.property<Boolean>()
    private val serversideInterfaceProperty = project.objects.property<Boolean>()
    private val flattenFilesProperty = project.objects.property<Boolean>()
    private val unwrapParamsProperty = project.objects.property<Boolean>()
    private val xsdconfigProperty = project.objects.property<Boolean>()
    private val allPortsProperty = project.objects.property<Boolean>()
    private val backwordCompatibleProperty = project.objects.property<Boolean>()
    private val suppressPrefixesProperty = project.objects.property<Boolean>()
    private val noMessageReceiverProperty = project.objects.property<Boolean>()
    
    // Strings
    private val databindingMethodProperty: Property<String> = project.objects.property(String::class.java)
    private val wsdlVersionProperty: Property<String> = project.objects.property(String::class.java)
    private val serviceNameProperty: Property<String> = project.objects.property(String::class.java)
    private val portNameProperty: Property<String> = project.objects.property(String::class.java)

    private val outputDirProperty: DirectoryProperty = project.objects.directoryProperty()

    init {
        asyncProperty.set(false)
        syncProperty.set(false)
        serverSideProperty.set(false)
        serviceDescriptionProperty.set(false)
        databindingMethodProperty.set(Databinding.ADB.binding)
        generateAllClassesProperty.set(false)
        unpackClassesProperty.set(false)
        serviceNameProperty.set("")
        portNameProperty.set("")
        serversideInterfaceProperty.set(false)
        wsdlVersionProperty.set("")
        flattenFilesProperty.set(false)
        unwrapParamsProperty.set(false)
        xsdconfigProperty.set(false)
        allPortsProperty.set(false)
        backwordCompatibleProperty.set(false)
        suppressPrefixesProperty.set(false)
        noMessageReceiverProperty.set(false)

        outputDirProperty.set(project.layout.buildDirectory.dir(
                "${WSDLExtension.CODEGEN_OUTPUTPATH}/axis2/${name.replace(' ', '_')}"
        ))
    }

    /**
     * Provider for async property.
     */
    val asyncProvider: Provider<Boolean>
        get() = asyncProperty

    /**
     * Generate code only for async style. When this option is used the generated
     * stubs will have only the asynchronous invocation methods. Switched off by default.
     *
     * @property async
     */
    var async by asyncProperty

    /**
     * Provider for sync property.
     */
    val syncProvider: Provider<Boolean>
        get() = syncProperty

    /**
     * Generate code only for sync style . When this option is used the generated stubs
     * will have only the synchronous invocation methods. Switched off by default.
     * When async is set to true, this takes precedence.
     *
     * @property sync
     */
    var sync by syncProperty

    /**
     * Provider for serverSide property.
     */
    val serverSideProvider: Provider<Boolean>
        get() = serverSideProperty

    /**
     * Generates server side code (i.e. skeletons). Default is false.
     *
     * @property serverSide
     */
    var serverSide by serverSideProperty

    /**
     * Provider for serviceDescription property.
     */
    val serviceDescriptionProvider: Provider<Boolean>
        get() = serviceDescriptionProperty

    /**
     * Generates the service descriptor (i.e. server.xml). Default is false.
     * Only valid if serverSide is true, the server side code generation option.
     *
     * @property serviceDescription
     */
    var serviceDescription by serviceDescriptionProperty

    /**
     * Provider for databindingMethod property.
     */
    val databindingMethodProvider: Provider<String>
        get() = databindingMethodProperty

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
     *  @property databindingMethod
     */
    var databindingMethod by databindingMethodProperty

    /**
     * Provider for generateAllClasses property.
     */
    val generateAllClassesProvider: Provider<Boolean>
        get() = generateAllClassesProperty

    /**
     * Generates all the classes. This option is valid only if serverSide otpion is true. If the value is true,
     * the client code (stubs) will also be generated along with the skeleton.
     *
     * @property generateAllClasses
     */
    var generateAllClasses by generateAllClassesProperty

    /**
     * Provider for unpackClasses property.
     */
    val unpackClassesProvider: Provider<Boolean>
        get() = unpackClassesProperty

    /**
     * Unpack classes. This option specifies whether to unpack the classes and
     * generate separate classes for the databinders.
     *
     * @property unpackClasses
     */
    var unpackClasses by unpackClassesProperty

    /**
     * Provider for serviceName property.
     */
    val serviceNameProvider: Provider<String>
        get() = serviceNameProperty

    /**
     * Specifies the service name to be code generated. If the service name is not specified,
     * then the first service will be picked.
     *
     * @property serviceName
     */
    var serviceName by serviceNameProperty

    /**
     * Provider for portName property.
     */
    val portNameProvider: Provider<String>
        get() = portNameProperty

    /**
     * Specifies the port name to be code generated. If the port name is not specified,
     * then the first port (of the selected service) will be picked.
     *
     * @property portName
     */
    var portName by portNameProperty

    /**
     * Provider for serversideInterface property.
     */
    val serversideInterfaceProvider: Provider<Boolean>
        get() = serversideInterfaceProperty

    /**
     * Generate an interface for the service skeleton.
     *
     * @property serversideInterface
     */
    var serversideInterface by serversideInterfaceProperty

    /**
     * Provider for wsdlVersion property.
     */
    val wsdlVersionProvider: Provider<String>
        get() = wsdlVersionProperty

    /**
     * WSDLExtension Version. Valid Options : 2, 2.0, 1.1
     *
     * @property wsdlVersion
     */
    var wsdlVersion by wsdlVersionProperty

    /**
     * Provider for flattenFiles property.
     */
    val flattenFilesProvider: Provider<Boolean>
        get() = flattenFilesProperty

    /**
     * Flattens the generated files.
     *
     * @property flattenFiles
     */
    var flattenFiles by flattenFilesProperty

    /**
     * Provider for unwrapParams property.
     */
    val unwrapParamsProvider: Provider<Boolean>
        get() = unwrapParamsProperty

    /**
     * Switch on un-wrapping, if this value is true.
     *
     * @property unwrapParams
     */
    var unwrapParams by unwrapParamsProperty

    /**
     * Provider for xsdconfig property.
     */
    val xsdconfigProvider: Provider<Boolean>
        get() = xsdconfigProperty

    /**
     * Use XMLBeans .xsdconfig file if this value is true.
     * This is only valid if  databindingMethod is 'xmlbeans'.
     *
     * @property xsdconfig
     */
    var xsdconfig by xsdconfigProperty

    /**
     * Provider for allPorts property.
     */
    val allPortsProvider: Provider<Boolean>
        get() = allPortsProperty

    /**
     * Generate code for all ports.
     *
     * @property allPorts
     */
    var allPorts by allPortsProperty

    /**
     * Provider for backwordCompatible property.
     */
    val backwordCompatibleProvider: Provider<Boolean>
        get() = backwordCompatibleProperty

    /**
     * Generate Axis 1.x backword compatible code.
     *
     * @property backwordCompatible
     */
    var backwordCompatible by backwordCompatibleProperty

    /**
     * Provider for suppressPrefixes property.
     */
    val suppressPrefixesProvider: Provider<Boolean>
        get() = suppressPrefixesProperty

    /**
     * Suppress namespace prefixes (Optimzation that reduces size of soap request/response).
     *
     * @property suppressPrefixes
     */
    var suppressPrefixes by suppressPrefixesProperty

    /**
     * Provider for noMessageReceiver property.
     */
    val noMessageReceiverProvider: Provider<Boolean>
        get() = noMessageReceiverProperty

    /**
     * Don't generate a MessageReceiver in the generated sources.
     *
     * @property noMessageReceiver
     */
    var noMessageReceiver by noMessageReceiverProperty

    /**
     * Provider for outputDir property.
     */
    val outputDirProvider: Provider<Directory>
        get() = outputDirProperty

    /**
     * Output directory for the generated code.
     *
     * @return file for output directory.
     */
    var outputDir: File
        get() = outputDirProperty.get().asFile
        set(value) = this.outputDirProperty.set(value)

    /**
     * Calculate the task name for the task.
     * @return task name for configuration
     */
    fun getTaskName(): String {
        return "axis2Wsdl2java${confname.toCamelCase()}"
    }

    private fun String.toCamelCase() : String {
        return split(" ").joinToString("") { it.capitalize() }
    }
}
