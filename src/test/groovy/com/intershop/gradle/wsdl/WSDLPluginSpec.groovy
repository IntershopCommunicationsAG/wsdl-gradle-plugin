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

import com.intershop.gradle.test.AbstractProjectSpec
import com.intershop.gradle.wsdl.extension.WSDLExtension
import org.gradle.api.Plugin

class WSDLPluginSpec extends AbstractProjectSpec {

    @Override
    Plugin getPlugin() {
        return new WSDLPlugin()
    }

    def 'should add extension named wsdl'() {
        when:
        plugin.apply(project)

        then:
        project.extensions.getByName(WSDLExtension.WSDL_EXTENSION_NAME)
    }

    def 'should add WSDL generate task for each wsdl config'() {
        when:
        plugin.apply(project)

        then:
        project.extensions.getByName(WSDLExtension.WSDL_EXTENSION_NAME).getAxis1Version() == WSDLExtension.AXIS1_DEFAULT_VERSION
        project.extensions.getByName(WSDLExtension.WSDL_EXTENSION_NAME).getAxis2Version() == WSDLExtension.AXIS2_DEFAULT_VERSION

        /**
        when:
        project.extensions.getByName(WSDLExtension.WSDL_EXTENSION_NAME).schemaGen {
            testconfiguration {
            }
        }

        then:
        project.tasks.findByName("jaxbSchemaGenTestconfiguration")

        when:
        project.extensions.getByName(JaxbExtension.JAXB_EXTENSION_NAME).javaGen {
            testconfiguration {
            }
        }

        then:
        project.tasks.findByName("jaxbJavaGenTestconfiguration")
        **/
    }
}
