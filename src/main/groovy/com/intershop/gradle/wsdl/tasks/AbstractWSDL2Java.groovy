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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaForkOptions
import org.gradle.process.internal.DefaultJavaForkOptions
import org.gradle.process.internal.JavaExecHandleBuilder

import javax.inject.Inject

abstract class AbstractWSDL2Java extends DefaultTask {

    /**
     * This is a shorthand option to map all namespaces in a WSDL document to the same
     * Java package name. This can be useful, but dangerous. You must make sure that you
     * understand the effects of doing this. For instance there may be multiple types
     * with the same name in different namespaces.
     * Only for Axis1: It is an error to use the --NStoPkg switch and --package at the same time.
     */
    @Input
    String packageName

    /**
     * By default, package names are generated from the namespace strings in the WSDL document in a
     * magical manner (typically, if the namespace is of the form "http://x.y.com" or "urn:x.y.com"
     * the corresponding package will be "com.y.x"). If this magic is not what you want, you can provide your
     * own mapping using the this maps argument. For example, if there is a namespace in the WSDL document
     * called "urn:AddressFetcher2", and you want files generated from the objects within this namespace
     * to reside in the package samples.addr, you would provide the following option:
     * <p><blockquote><pre>
     * urn:AddressFetcher2=samples.addr
     * </pre></blockquote></p>
     */
    @Input
    Map<String, String> namespacePackageMapping

    /**
     * Generate a client-side JUnit test case. This test case can stand on its own, but it doesn't
     * really do anything except pass default values (null for objects, 0 or false for primitive types).
     * Like the generated implementation file, the generated test case file could be considered a template
     * that you may fill in.
     */
    @Input
    boolean generateTestcase

    /**
     * If there are a number of namespaces in the WSDL document, listing a mapping for them all could
     * become tedious. To help keep the command line terse, WSDL2Java will also look for mappings in
     * a properties file. By default, this file is named "NStoPkg.properties" and it must reside in
     * the default package (ie., no package). But you can explicitly provide your own file using this option.
     *
     * The entries in this file are of the same form as the arguments to the namespacePackageMapping option.
     * For example, instead of providing the command line option as above, we could provide the same
     * information in a properties file:
     * <p><blockquote><pre>
     * urn\:AddressFetcher2=samples.addr
     * </pre></blockquote></p>
     *
     * (Note that the colon must be escaped in the properties file.)
     *
     * If an entry for a given mapping exists both with namespacePackageMapping and in this properties file,
     * the namespacePackageMapping entry takes precedence.
     */
    @InputFile
    File namespacePackageMappingFile

    /** WSDL file for generation. */
    @InputFile
    File wsdlFile

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

    /**
     * Adds an attribute to the parameter list.
     * @param arguments Argument list.
     * @param value Value
     * @param optionName Parameter name
     *
     */
    private void addAttribute(List<String> arguments, String value, String optionName) {
        if (value) {
            arguments << optionName
            arguments << "${value}" // + ' ' + value
        }
    }

    /**
     * Adds a flag to the argument list.
     * @param arguments Argument list
     * @param value Configured value
     * @param optionName name of the option
     */
    private void addFlag(List<String> arguments, boolean value, String optionName) {
        if (value) {
            arguments << optionName
        }
    }
}
