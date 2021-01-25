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
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File
import javax.inject.Inject

/**
 * Axis 2 Configuration container.
 *
 * @constructur default constructor with project and configuration name.
 */
open class Axis2 @Inject constructor(name: String, objectFactory: ObjectFactory, layout: ProjectLayout):
    AbstractAxisConfig(name, objectFactory) {

    // properties will analyzed as Boolean
    private val asyncProperty = objectFactory.property(Boolean::class.java)
    private val syncProperty = objectFactory.property(Boolean::class.java)
    private val serverSideProperty = objectFactory.property(Boolean::class.java)
    private val serviceDescriptionProperty = objectFactory.property(Boolean::class.java)
    private val generateAllClassesProperty = objectFactory.property(Boolean::class.java)
    private val unpackClassesProperty = objectFactory.property(Boolean::class.java)
    private val serversideInterfaceProperty = objectFactory.property(Boolean::class.java)
    private val flattenFilesProperty = objectFactory.property(Boolean::class.java)
    private val unwrapParamsProperty = objectFactory.property(Boolean::class.java)
    private val xsdconfigProperty = objectFactory.property(Boolean::class.java)
    private val allPortsProperty = objectFactory.property(Boolean::class.java)
    private val backwordCompatibleProperty = objectFactory.property(Boolean::class.java)
    private val suppressPrefixesProperty = objectFactory.property(Boolean::class.java)
    private val noMessageReceiverProperty = objectFactory.property(Boolean::class.java)
    
    // Strings
    private val databindingMethodProperty: Property<String> = objectFactory.property(String::class.java)
    private val wsdlVersionProperty: Property<String> = objectFactory.property(String::class.java)
    private val serviceNameProperty: Property<String> = objectFactory.property(String::class.java)
    private val portNameProperty: Property<String> = objectFactory.property(String::class.java)

    private val outputDirProperty: DirectoryProperty = objectFactory.directoryProperty()

    init {
        asyncProperty.convention(false)
        syncProperty.convention(false)
        serverSideProperty.convention(false)
        serviceDescriptionProperty.convention(false)
        databindingMethodProperty.convention(Databinding.ADB.binding)
        generateAllClassesProperty.convention(false)
        unpackClassesProperty.convention(false)
        serviceNameProperty.convention("")
        portNameProperty.convention("")
        serversideInterfaceProperty.convention(false)
        wsdlVersionProperty.convention("")
        flattenFilesProperty.convention(false)
        unwrapParamsProperty.convention(false)
        xsdconfigProperty.convention(false)
        allPortsProperty.convention(false)
        backwordCompatibleProperty.convention(false)
        suppressPrefixesProperty.convention(false)
        noMessageReceiverProperty.convention(false)

        outputDirProperty.convention(layout.buildDirectory.dir(
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
    var async : Boolean
        get() = asyncProperty.get()
        set(value) = asyncProperty.set(value)

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
    var sync : Boolean
        get() = syncProperty.get()
        set(value) = syncProperty.set(value)

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
    var serverSide : Boolean
        get() = serverSideProperty.get()
        set(value) = serverSideProperty.set(value)

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
    var serviceDescription : Boolean
        get() = serviceDescriptionProperty.get()
        set(value) = serviceDescriptionProperty.set(value)

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
    var databindingMethod : String
        get() = databindingMethodProperty.get()
        set(value) = databindingMethodProperty.set(value)

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
    var generateAllClasses : Boolean
        get() = generateAllClassesProperty.get()
        set(value) = generateAllClassesProperty.set(value)

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
    var unpackClasses : Boolean
        get() = unpackClassesProperty.get()
        set(value) = unpackClassesProperty.set(value)

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
    var serviceName : String
        get() = serviceNameProperty.get()
        set(value) = serviceNameProperty.set(value)

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
    var portName : String
        get() = portNameProperty.get()
        set(value) = portNameProperty.set(value)

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
    var serversideInterface : Boolean
        get() = serversideInterfaceProperty.get()
        set(value) = serversideInterfaceProperty.set(value)

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
    var wsdlVersion : String
        get() = wsdlVersionProperty.get()
        set(value) = wsdlVersionProperty.set(value)

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
    var flattenFiles : Boolean
        get() = flattenFilesProperty.get()
        set(value) = flattenFilesProperty.set(value)

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
    var unwrapParams : Boolean
        get() = unwrapParamsProperty.get()
        set(value) = unwrapParamsProperty.set(value)

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
    var xsdconfig : Boolean
        get() = xsdconfigProperty.get()
        set(value) = xsdconfigProperty.set(value)

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
    var allPorts : Boolean
        get() = allPortsProperty.get()
        set(value) = allPortsProperty.set(value)

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
    var backwordCompatible : Boolean
        get() = backwordCompatibleProperty.get()
        set(value) = backwordCompatibleProperty.set(value)

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
    var suppressPrefixes : Boolean
        get() = suppressPrefixesProperty.get()
        set(value) = suppressPrefixesProperty.set(value)

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
    var noMessageReceiver : Boolean
        get() = noMessageReceiverProperty.get()
        set(value) = noMessageReceiverProperty.set(value)

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
        return "axis2Wsdl2java${name.toCamelCase()}"
    }

    private fun String.toCamelCase() : String {
        return split(" ").joinToString("") { it.capitalize() }
    }
}
