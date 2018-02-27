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

open class Axis2(project: Project, private val confname: String) : AbbstractAxisConfig(project, confname) {

    // properties will analyzed as Boolean
    val asyncProperty = project.objects.property<Boolean>()
    val syncProperty = project.objects.property<Boolean>()
    val serverSideProperty = project.objects.property<Boolean>()
    val serviceDescriptionProperty = project.objects.property<Boolean>()
    val generateAllClassesProperty = project.objects.property<Boolean>()
    val unpackClassesProperty = project.objects.property<Boolean>()
    val serversideInterfaceProperty = project.objects.property<Boolean>()
    val flattenFilesProperty = project.objects.property<Boolean>()
    val unwrapParamsProperty = project.objects.property<Boolean>()
    val xsdconfigProperty = project.objects.property<Boolean>()
    val allPortsProperty = project.objects.property<Boolean>()
    val backwordCompatibleProperty = project.objects.property<Boolean>()
    val suppressPrefixesProperty = project.objects.property<Boolean>()
    val noMessageReceiverProperty = project.objects.property<Boolean>()
    
    // Strings
    val databindingMethodProperty: Property<String> = project.objects.property(String::class.java)
    val wsdlVersionProperty: Property<String> = project.objects.property(String::class.java)
    val serviceNameProperty: Property<String> = project.objects.property(String::class.java)
    val portNameProperty: Property<String> = project.objects.property(String::class.java)

    private val outputDirProperty: DirectoryProperty = project.layout.directoryProperty()

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

        outputDirProperty.set(project.layout.buildDirectory.dir("${WSDLExtension.CODEGEN_OUTPUTPATH}/axis2/${name.replace(' ', '_')}"))
    }

    /**
     * Generate code only for async style. When this option is used the generated
     * stubs will have only the asynchronous invocation methods. Switched off by default.
     */
    val asyncProvider: Provider<Boolean>
        get() = asyncProperty

    var async by asyncProperty

    /**
     * Generate code only for sync style . When this option is used the generated stubs
     * will have only the synchronous invocation methods. Switched off by default.
     * When async is set to true, this takes precedence.
     */
    val syncProvider: Provider<Boolean>
        get() = syncProperty

    var sync by syncProperty

    /**
     * Generates server side code (i.e. skeletons). Default is false.
     */
    val serverSideProvider: Provider<Boolean>
        get() = serverSideProperty

    var serverSide by serverSideProperty

    /**
     * Generates the service descriptor (i.e. server.xml). Default is false.
     * Only valid if serverSide is true, the server side code generation option.
     */
    val serviceDescriptionProvider: Provider<Boolean>
        get() = serviceDescriptionProperty

    var serviceDescription by serviceDescriptionProperty

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
    val databindingMethodProvider: Provider<String>
        get() = databindingMethodProperty

    var databindingMethod by databindingMethodProperty

    /**
     * Generates all the classes. This option is valid only if serverSide otpion is true. If the value is true,
     * the client code (stubs) will also be generated along with the skeleton.
     */
    val generateAllClassesProvider: Provider<Boolean>
        get() = generateAllClassesProperty

    var generateAllClasses by generateAllClassesProperty

    /**
     * Unpack classes. This option specifies whether to unpack the classes and
     * generate separate classes for the databinders.
     */
    val unpackClassesProvider: Provider<Boolean>
        get() = unpackClassesProperty

    var unpackClasses by unpackClassesProperty

    /**
     * Specifies the service name to be code generated. If the service name is not specified,
     * then the first service will be picked.
     */
    val serviceNameProvider: Provider<String>
        get() = serviceNameProperty

    var serviceName by serviceNameProperty

    /**
     * Specifies the port name to be code generated. If the port name is not specified,
     * then the first port (of the selected service) will be picked.
     */
    val portNameProvider: Provider<String>
        get() = portNameProperty

    var portName by portNameProperty

    /**
     * Generate an interface for the service skeleton.
     */
    val serversideInterfaceProvider: Provider<Boolean>
        get() = serversideInterfaceProperty

    var serversideInterface by serversideInterfaceProperty

    /**
     * WSDLExtension Version. Valid Options : 2, 2.0, 1.1
     */
    val wsdlVersionProvider: Provider<String>
        get() = wsdlVersionProperty

    var wsdlVersion by wsdlVersionProperty

    /**
     * Flattens the generated files
     */
    val flattenFilesProvider: Provider<Boolean>
        get() = flattenFilesProperty

    var flattenFiles by flattenFilesProperty

    /**
     * Switch on un-wrapping, if this value is true.
     */
    val unwrapParamsProvider: Provider<Boolean>
        get() = unwrapParamsProperty

    var unwrapParams by unwrapParamsProperty

    /**
     * Use XMLBeans .xsdconfig file if this value is true.
     * This is only valid if  databindingMethod is 'xmlbeans'.
     */
    val xsdconfigProvider: Provider<Boolean>
        get() = xsdconfigProperty

    var xsdconfig by xsdconfigProperty

    /**
     * Generate code for all ports
     */
    val allPortsProvider: Provider<Boolean>
        get() = allPortsProperty

    var allPorts by allPortsProperty

    /**
     * Generate Axis 1.x backword compatible code
     */
    val backwordCompatibleProvider: Provider<Boolean>
        get() = backwordCompatibleProperty

    var backwordCompatible by backwordCompatibleProperty

    /**
     * Suppress namespace prefixes (Optimzation that reduces size of soap request/response)
     */
    val suppressPrefixesProvider: Provider<Boolean>
        get() = suppressPrefixesProperty

    var suppressPrefixes by suppressPrefixesProperty

    /**
     * Don't generate a MessageReceiver in the generated sources
     */
    val noMessageReceiverProvider: Provider<Boolean>
        get() = noMessageReceiverProperty

    var noMessageReceiver by noMessageReceiverProperty

    /**
     * Output directory
     */
    val outputDirProvider: Provider<Directory>
        get() = outputDirProperty

    var outputDir: File
        get() = outputDirProperty.get().asFile
        set(value) = this.outputDirProperty.set(value)

    /**
     * Calculate the task name
     * @return
     */
    fun getTaskName(): String {
        return "axis2Wsdl2java${confname.toCamelCase()}"
    }

    private fun String.toCamelCase() : String {
        return split(" ").joinToString("") { it.capitalize() }
    }
}