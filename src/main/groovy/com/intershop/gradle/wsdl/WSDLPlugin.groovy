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
    }
}
