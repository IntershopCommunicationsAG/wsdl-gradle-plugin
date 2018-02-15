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

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

open class WSDLExtension (project: Project) {

    companion object {
        // names for the plugin
        const val WSDL_EXTENSION_NAME = "wsdl"
        const val WSDL_GROUP_NAME = "WSDL Code Generation"

        // Dependency configuration names
        const val WSDL_AXIS1_CONFIGURATION_NAME = "wsdlAxis1"
        const val WSDL_AXIS2_CONFIGURATION_NAME = "wsdlAxis2"

        const val CODEGEN_OUTPUTPATH = "generated/wsdl2java"
    }

    /**
     * Container for axis1 generation configurations
     */
    val axis1: NamedDomainObjectContainer<Axis1> = project.container(Axis1::class.java, Axis1Factory(project))

    fun axis1(configureAction: Action<in NamedDomainObjectContainer<Axis1>>) {
        configureAction.execute(axis1)
    }

    /**
     * Container for axis2 generation configurations
     */
    val axis2: NamedDomainObjectContainer<Axis2> = project.container(Axis2::class.java, Axis2Factory(project))

    fun axis2(configureAction: Action<in NamedDomainObjectContainer<Axis2>>) {
        configureAction.execute(axis2)
    }
}
