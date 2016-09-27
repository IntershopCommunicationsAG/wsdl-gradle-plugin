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
package com.intershop.gradle.wsdl.tasks.axis2

import com.intershop.gradle.wsdl.extension.WSDLExtension
import com.intershop.gradle.wsdl.tasks.AbstractWSDL2Java
import org.gradle.api.file.FileCollection
import org.gradle.process.internal.JavaExecHandleBuilder

class WSDL2Java extends AbstractWSDL2Java {

    static final String MAIN_CLASS_NAME = 'org.apache.axis2.wsdl.WSDL2Java'

    /**
     * Prepares the JavaExecHandlerBuilder for the task.
     *
     * @return JavaExecHandleBuilder
     */
    JavaExecHandleBuilder prepareExec() {
        JavaExecHandleBuilder javaExec = new JavaExecHandleBuilder(getFileResolver());

        getForkOptions().copyTo(javaExec);

        FileCollection axis2CodegenConfiguration = getProject().getConfigurations().getAt(WSDLExtension.WSDLAXIS2_CONFIGURATION_NAME)

        List<String> args = []

        return javaExec
                .setClasspath(axis2CodegenConfiguration)
                .setMain(MAIN_CLASS_NAME)
                .setArgs(args)
    }

}
