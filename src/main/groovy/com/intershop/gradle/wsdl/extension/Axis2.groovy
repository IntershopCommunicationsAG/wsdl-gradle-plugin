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
package com.intershop.gradle.wsdl.extension

import com.intershop.gradle.wsdl.utils.Databinding
import org.gradle.util.GUtil

class Axis2 extends BaseAxisConfig {

    /**
     * Generate code only for async style. When this option is used the generated
     * stubs will have only the asynchronous invocation methods. Switched off by default.
     */
    boolean async = false

    /**
     * Generate code only for sync style . When this option is used the generated stubs
     * will have only the synchronous invocation methods. Switched off by default.
     * When async is set to true, this takes precedence.
     */
    boolean sync = false

    /**
     * Generates server side code (i.e. skeletons). Default is false.
     */
    boolean serverSide = false

    /**
     * Generates the service descriptor (i.e. server.xml). Default is false.
     * Only valid if serverSide is true, the server side code generation option.
     */
    boolean serviceDescription = false

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
    String databindingMethod = Databinding.ADB.toString()

    /**
     * Generates all the classes. This option is valid only if serverSide otpion is true. If the value is true,
     * the client code (stubs) will also be generated along with the skeleton.
     */
    boolean generateAllClasses = false

    /**
     * Unpack classes. This option specifies whether to unpack the classes and
     * generate separate classes for the databinders.
     */
    boolean unpackClasses = false

    /**
     * Specifies the service name to be code generated. If the service name is not specified,
     * then the first service will be picked.
     */
    String serviceName

    /**
     * Specifies the port name to be code generated. If the port name is not specified,
     * then the first port (of the selected service) will be picked.
     */
    String portName

    /**
     * Generate an interface for the service skeleton.
     */
    boolean serversideInterface	= false

    /**
     * WSDL Version. Valid Options : 2, 2.0, 1.1
     */
    String wsdlVersion

    /**
     * Flattens the generated files
     */
    boolean flattenFiles = false

    /**
     * Switch on un-wrapping, if this value is true.
     */
    boolean unwrapParams = false

    /**
     * Use XMLBeans .xsdconfig file if this value is true.
     * This is only valid if  databindingMethod is 'xmlbeans'.
     */
    boolean xsdconfig = false

    /**
     * Generate code for all ports
     */
    boolean allPorts = false

    /**
     * Generate Axis 1.x backword compatible code
     */
    boolean backwordCompatible = false

    /**
     * Suppress namespace prefixes (Optimzation that reduces size of soap request/response)
     */
    boolean suppressPrefixes = false

    /**
     * Don't generate a MessageReceiver in the generated sources
     */
    boolean noMessageReceiver = false

    /**
     * Output file location. This is where the files would be copied once the code generation is done.
     */
    File outputDir

    /**
     * Name of the source set for generated Java code
     * default value is 'main'
     */
    String sourceSetName

    String getSourceSetName() {
        if(! this.sourceSetName) {
            return WSDLExtension.DEFAULT_SOURCESET_NAME
        } else {
            return this.sourceSetName
        }
    }

    /**
     * Additional ars for xjc
     */
    List<String> args = []

    void arg(String parameter) {
        args.add(parameter)
    }

    Axis2(String name) {
        super(name)
    }

    String getTaskName() {
        "axis2Wsdl2java" + GUtil.toCamelCase(name)
    }
}
