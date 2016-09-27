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
package com.intershop.gradle.wsdl

import com.intershop.gradle.wsdl.extension.WSDLExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * <p>This plugin will apply the WSDL plugin.</p>
 *
 * <p>It generates code with Axis 1 and Axis 2</p>
 */
class WSDLPlugin implements Plugin<Project> {

    /**
     * Name of the extension
     */
    final static String EXTENSION_NAME = 'wsdl'

    private WSDLExtension extension

    void apply(Project project) {
        project.logger.info("Applying ${EXTENSION_NAME} plugin to project: ${project.name}")

        this.extension = project.extensions.findByType(WSDLExtension) ?: project.extensions.create(EXTENSION_NAME, WSDLExtension, project)

        addAxis1Configuration(project, extension)
        addAxis2Configuration(project, extension)


    }



    /**
     * Adds the dependencies for the axis 1 code generation. It is possible to override this.
     *
     * @param project
     * @param extension
     */
    private void addAxis1Configuration(final Project project, WSDLExtension extension) {
        final Configuration configuration =
                project.getConfigurations().findByName(WSDLExtension.WSDLAXIS1_CONFIGURATION_NAME) ?:
                        project.getConfigurations().create(WSDLExtension.WSDLAXIS1_CONFIGURATION_NAME)

        configuration
                .setVisible(false)
                .setTransitive(false)
                .setDescription("WSDL Axis1 configuration is used for code generation")
                .defaultDependencies { dependencies ->
            DependencyHandler dependencyHandler = project.getDependencies()

            dependencies.add(dependencyHandler.create('axis:axis-wsdl4j:1.5.1'))
            dependencies.add(dependencyHandler.create('commons-discovery:commons-discovery:0.5'))
            dependencies.add(dependencyHandler.create('javax.activation:activation:1.1.1'))
            dependencies.add(dependencyHandler.create('javax.mail:mail:1.4.7'))

            dependencies.add(dependencyHandler.create('org.apache.axis:axis:' +  extension.getAxis1Version()))
            dependencies.add(dependencyHandler.create('org.apache.axis:axis-jaxrpc:' +  extension.getAxis1Version()))
        }
    }

    /**
     * Adds the dependencies for the axis 2 code generation. It is possible to override this.
     *
     * @param project
     * @param extension
     */
    private void addAxis2Configuration(final Project project, WSDLExtension extension) {
        final Configuration configuration =
                project.getConfigurations().findByName(WSDLExtension.WSDLAXIS2_CONFIGURATION_NAME) ?:
                        project.getConfigurations().create(WSDLExtension.WSDLAXIS2_CONFIGURATION_NAME)

        configuration
                .setVisible(false)
                .setTransitive(false)
                .setDescription("WSDL Axis2 configuration is used for code generation")
                .defaultDependencies { dependencies ->
            DependencyHandler dependencyHandler = project.getDependencies()
            dependencies.add(dependencyHandler.create('org.apache.axis2:axis2-codegen:' + extension.getAxis2Version()))
        }
    }
}