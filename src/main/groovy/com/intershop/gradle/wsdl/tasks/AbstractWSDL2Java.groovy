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
package com.intershop.gradle.wsdl.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaForkOptions
import org.gradle.process.internal.DefaultJavaForkOptions
import org.gradle.process.internal.JavaExecHandleBuilder

import javax.inject.Inject

abstract class AbstractWSDL2Java extends DefaultTask {

    /** WSDL file for generation. */
    @InputFile
    File wsdlFile

    /** The directory to generate the parser source files into. */
    @OutputDirectory
    File outputDirectory

    /**
     * Java fork options for the Java task.
     */
    JavaForkOptions forkOptions

    /**
     * Task action of the SonarQube runner
     */
    @TaskAction
    public void run() {

        JavaExecHandleBuilder exechandler = prepareExec()
        if (exechandler) {
            exechandler.build().start().waitForFinish().assertNormalExitValue()
        }
    }

    /**
     * Prepares the JavaExecHandlerBuilder for the task.
     *
     * @return JavaExecHandleBuilder
     */
    abstract JavaExecHandleBuilder prepareExec()

    /**
     * Set Java fork options.
     *
     * @return JavaForkOptions
     */
    public JavaForkOptions getForkOptions() {
        if (forkOptions == null) {
            forkOptions = new DefaultJavaForkOptions(getFileResolver());
        }

        return forkOptions;
    }

    @Inject
    protected FileResolver getFileResolver() {
        throw new UnsupportedOperationException();
    }
}
