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

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * Main extension of the WSDL plugin.
 */
open class WSDLExtension @Inject constructor(objectFactory: ObjectFactory) {

    companion object {
        /**
         * Extension name of plugin.
         */
        const val WSDL_EXTENSION_NAME = "wsdl"

        /**
         * Task group name of WSDL code generation.
         */
        const val WSDL_GROUP_NAME = "WSDL Code Generation"

        /**
         * Configuration name of Axis 1.
         */
        const val WSDL_AXIS1_CONFIGURATION_NAME = "wsdlAxis1"

        /**
         * Configuration name of Axis 2.
         */
        const val WSDL_AXIS2_CONFIGURATION_NAME = "wsdlAxis2"

        /**
         * Folder names for generated java files.
         **/
        const val CODEGEN_OUTPUTPATH = "generated/wsdl2java"
    }

    /**
     * Container for axis1 generation configurations.
     */
    val axis1: NamedDomainObjectContainer<Axis1> = objectFactory.domainObjectContainer(Axis1::class.java)

    /**
     * Container for axis2 generation configurations.
     */
    val axis2: NamedDomainObjectContainer<Axis2> = objectFactory.domainObjectContainer(Axis2::class.java)

}
