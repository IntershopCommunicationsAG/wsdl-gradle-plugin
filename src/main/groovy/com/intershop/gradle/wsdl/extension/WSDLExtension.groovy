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

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
/**
 * <p>This is the extension object for the Intershop WSDL plugin.</p>
 */
class WSDLExtension {

    /**
     * Default versions of Axis 1
     */
    final static String AXIS1_DEFAULT_VERSION = '1.5.1'

    /**
     * Default versions of Axis 2
     */
    final static String AXIS2_DEFAULT_VERSION = '1.7.3'

    /**
     * Extension name
     */
    final static String WSDL_EXTENSION_NAME = 'wsdl'

    /**
     * Dependency configuration name axis1
     */
    final static String WSDLAXIS1_CONFIGURATION_NAME = 'wsdlAxis1'

    /**
     * Dependency configuration name axis2
     */
    final static String WSDLAXIS2_CONFIGURATION_NAME = 'wsdlAxis2'

    /**
     * Default output path
     */
    final static String CODEGEN_DEFAULT_OUTPUTPATH = 'generated/wsdl2java'

    /**
     * Task group name
     */
    final static String WSDL_TASK_GROUP = 'wsdl code generation'

    /**
     * Default source set name
     */
    final static String DEFAULT_SOURCESET_NAME = 'main'

    /**
     * Version of axis1, default is 1.4
     */
    String axis1Version

    /**
     * Version of axis2, default is 1.7.3
     */
    String axis2Version

    /**
     * Container for axis1 generation configurations
     */
    final NamedDomainObjectContainer<Axis1> axis1

    /**
     * Container for axis2 generation configurations
     */
    final NamedDomainObjectContainer<Axis2> axis2

    private Project project

    /**
     * Initialize the extension.
     *
     * @param project
     */
    public WSDLExtension(Project project) {

        this.project = project

        if(! axis1Version) {
            axis1Version = AXIS1_DEFAULT_VERSION
        }

        if(! axis2Version) {
            axis2Version = AXIS2_DEFAULT_VERSION
        }

        axis1 = project.container(Axis1)
        axis2 = project.container(Axis2)
    }

    /**
     * Closure with the configuration of axis1 code generation configurations
     * @param closure with axis1 code generation configurations
     */
    void axis1(Closure c) {
        axis1.configure(c)
    }

    /**
     * Closure with the configuration of axis2 code generation configurations
     * @param closure with axis2 code generation configurations
     */
    void axis2(Closure c) {
        axis2.configure(c)
    }
}
