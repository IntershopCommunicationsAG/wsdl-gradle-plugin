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

import org.gradle.api.Project

/**
 * <p>This is the extension object for the Intershop WSDL plugin.</p>
 */
class WSDLExtension {

    /**
     * Default versions of Axis 1
     */
    final static String AXIS1_DEFAULT_VERSION = '2.2.11'

    /**
     * Default versions of Axis 2
     */
    final static String AXIS2_DEFAULT_VERSION = '2.2.11'

    /**
     * Extension name
     */
    final static String WSDL_EXTENSION_NAME = 'wsdl'

    /**
     * Dependency configuration name
     */
    final static String WSDL_CONFIGURATION_NAME = 'wsdl'

    /**
     * Task group name
     */
    final static String WSDL_TASK_GROUP = 'wsdl code generation'

    /**
     * Default source set name
     */
    final static String DEFAULT_SOURCESET_NAME = 'main'

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

    }

    /**
     * Version of axis1, default is 2.2.11
     */
    String axis1Version

    /**
     * Version of axis2, default is 2.2.11
     */
    String axis2Version
}
