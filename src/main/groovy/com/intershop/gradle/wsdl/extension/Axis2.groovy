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

import org.gradle.util.GUtil

class Axis2 extends BaseAxisConfig {

    final static String DEFAULT_DATABINDING = 'adb'
    final static String DEFAULT_LANGUAGE = 'java'

    /** Language of the generated code. */
    String language;

    /** The name of the service in the case of multiple services. */
    String serviceName;

    /** The name of the port in the presence of multiple ports. */
    String portName;

    /** The Axis data binding: 'adb' (default), 'xmlbeans', 'jaxbri', 'jibx'). */
    String databindingName;

    /** Synchronization Mode. */
    String synchronizationMode;

    /** WSDL version. */
    String wsdlVersion

    /**
     * This option specifies whether to unpack the classes and
     * generate separate classes for the databinders.
     */
    Boolean unpackClasses = Boolean.TRUE

    /**
     * Generate Axis2 service deployment descriptor
     * file (services.xml): true, false (default).
     */
    Boolean generateServiceDeploymentDescriptor;

    Axis2(String name) {
        super(name)
    }

    String getTaskName() {
        "axis2Wsdl2java" + GUtil.toCamelCase(name);
    }

    String getDatabindingName() {
        (databindingName) ? databindingName : DEFAULT_DATABINDING
    }

    String getLanguage() {
        (language) ? language : DEFAULT_LANGUAGE;
    }
}
