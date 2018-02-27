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
package com.intershop.gradle.wsdl.tasks

import com.intershop.gradle.wsdl.extension.data.NamespacePackageMapping
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.process.JavaForkOptions
import java.io.File

inline fun <reified T> ObjectFactory.property(): Property<T> = property(T::class.java)

abstract class AbstractWSDL2Java : DefaultTask() {

    companion object {
        /**
         * Adds an attribute to the parameter list.
         * @param arguments Argument list.
         * @param value Value
         * @param optionName Parameter name
         *
         */
        fun addAttribute(arguments: MutableList<String>, value: String, optionName: String) {
            if (value.isNotBlank()) {
                arguments.add(optionName)
                arguments.add(value)
            }
        }

        /**
         * Adds a flag to the argument list.
         * @param arguments Argument list
         * @param value Configured value
         * @param optionName name of the option
         */
        fun addFlag(arguments: MutableList<String>, value: Boolean, optionName: String) {
            if (value) {
                arguments.add(optionName)
            }
        }
    }

    /**
     * This is a shorthand option to map all namespaces in a WSDLExtension document to the same
     * Java package name. This can be useful, but dangerous. You must make sure that you
     * understand the effects of doing this. For instance there may be multiple types
     * with the same name in different namespaces.
     * Only for Axis1: It is an error to use the --NStoPkg switch and --package at the same time.
     */
    private val packageNameProperty: Property<String> = project.objects.property(String::class.java)

    @get:Input
    var packageName: String
        get() = packageNameProperty.getOrElse("")
        set(value) = packageNameProperty.set(value)

    fun providePackageName(packageName: Provider<String?>) = packageNameProperty.set(packageName)


    /**
     * By default, package names are generated from the namespace strings in the WSDLExtension document in a
     * magical manner (typically, if the namespace is of the form "http://x.y.com" or "urn:x.y.com"
     * the corresponding package will be "com.y.x"). If this magic is not what you want, you can provide your
     * own mapping using the this maps argument. For example, if there is a namespace in the WSDLExtension document
     * called "urn:AddressFetcher2", and you want files generated from the objects within this namespace
     * to reside in the package samples.addr, you would provide the following option:
     * <p><blockquote><pre>
     * urn:AddressFetcher2=samples.addr
     * </pre></blockquote></p>
     */
    @Internal
    var namespacePackageMappings: NamedDomainObjectContainer<NamespacePackageMapping>? = null

    @get:Input
    val namespacePackageMappingList: List<String>
        get() {
            val mappings: MutableList<String> = mutableListOf()

            namespacePackageMappings?.forEach {
                mappings.add("${it.namespace}=${it.packageName}")
            }

            return mappings.toList()
        }

    /**
     * Generate a client-side JUnit test case. This test case can stand on its own, but it doesn't
     * really do anything except pass default values (null for objects, 0 or false for primitive types).
     * Like the generated implementation file, the generated test case file could be considered a template
     * that you may fill in.
     */
    private val generateTestcaseProperty = project.objects.property<Boolean>()

    @get:Input
    var generateTestcase: Boolean
        get() = generateTestcaseProperty.getOrElse(false)
        set(value)  = generateTestcaseProperty.set(value)

    fun provideGenerateTestcase(generateTestcase: Provider<Boolean>) = generateTestcaseProperty.set(generateTestcase)

    /**
     * If there are a number of namespaces in the WSDLExtension document, listing a mapping for them all could
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
    private val namespacePackageMappingFileProperty: RegularFileProperty = this.newInputFile()

    @get:Optional
    @get:InputFile
    var namespacePackageMappingFile: File?
        get() = namespacePackageMappingFileProperty.orNull?.asFile
        set(value) {
            if(value != null) {
                namespacePackageMappingFileProperty.set(value)
            }
        }

    fun provideNamespacePackageMappingFile(namespacePackageMappingFile: Provider<RegularFile>)
            = namespacePackageMappingFileProperty.set(namespacePackageMappingFile)

    /**
     * Additional parameters for WSDL Command Line Client
     */
    private val argumentsProperty: ListProperty<String> = project.objects.listProperty(String::class.java)

    @get:Input
    var args: List<String>
        get() = argumentsProperty.get()
        set(value) = argumentsProperty.set(value)

    fun args(arg: String) {
        argumentsProperty.add(arg)
    }

    fun provideArguments(arguments: Provider<List<String>>) = argumentsProperty.set(arguments)

    /**
     * Output directory for generated sources
     */
    private val outputDirProperty: DirectoryProperty = this.newOutputDirectory()

    @get:OutputDirectory
    var outputDir: File
        get() = outputDirProperty.get().asFile
        set(value) = outputDirProperty.set(value)

    fun provideOutputDir(outputDir: Provider<Directory>) = outputDirProperty.set(outputDir)

    /**
     * Input wsdl file
     */
    private val wsdlFileProperty: RegularFileProperty = this.newInputFile()

    @get:InputFile
    var wsdlFile: File
        get() = wsdlFileProperty.get().asFile
        set(value) = wsdlFileProperty.set(value)

    fun provideWsdlFile(wsdlFile: Provider<RegularFile>) = wsdlFileProperty.set(wsdlFile)

    /**
     * Java fork options for the Java task.
     */
    protected var internalForkOptionsAction: Action<in JavaForkOptions>? = null

    fun forkOptions(forkOptionsAction: Action<in JavaForkOptions>) {
        internalForkOptionsAction = forkOptionsAction
    }

}