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

import com.intershop.gradle.wsdl.extension.data.NamespacePackageMapping
import com.intershop.gradle.wsdl.utils.getValue
import com.intershop.gradle.wsdl.utils.property
import com.intershop.gradle.wsdl.utils.setValue
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import java.io.File
import javax.inject.Inject

/**
 * Class with configuration for Axis 1 and 2.
 *
 * @constructur default constructor with project and a configuration name.
 */
abstract class AbstractAxisConfig(val name: String) {

    /**
     * Inject service of ObjectFactory (See "Service injection" in Gradle documentation.
     */
    @get:Inject
    abstract val objectFactory: ObjectFactory

    private val sourceSetNameProperty: Property<String> = objectFactory.property(String::class.java)
    private val packageNameProperty: Property<String?> = objectFactory.property(String::class.java)

    private val namespacePackageMappingFileProperty: RegularFileProperty = objectFactory.fileProperty()
    private val wsdlFileProperty: RegularFileProperty = objectFactory.fileProperty()

    private val argumentsProperty: ListProperty<String> = objectFactory.listProperty(String::class.java)

    // will be analyzed as Boolean
    private val generateTestcaseProperty = objectFactory.property<Boolean>()

    init {
        sourceSetNameProperty.set(SourceSet.MAIN_SOURCE_SET_NAME)
        generateTestcaseProperty.set(false)
    }

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
    val namespacePackageMappings: NamedDomainObjectContainer<NamespacePackageMapping> = objectFactory.domainObjectContainer(NamespacePackageMapping::class.java)

    /**
     * Provider for packageName.
     */
    val packageNameProvider: Provider<String?>
        get() = packageNameProperty

    /**
     * This is a shorthand option to map all namespaces in a WSDLExtension document to the same
     * Java package name. This can be useful, but dangerous. You must make sure that you
     * understand the effects of doing this. For instance there may be multiple types
     * with the same name in different namespaces.
     * Only for Axis1: It is an error to use the --NStoPkg switch and --package at the same time.
     *
     * @property packageName
     */
    var packageName by packageNameProperty

    /**
     * Provider for generateTestcase.
     */
    val generateTestcaseProvider: Provider<Boolean>
        get() = generateTestcaseProperty

    /**
     * Generate a client-side JUnit test case. This test case can stand on its own, but it doesn't
     * really do anything except pass default values (null for objects, 0 or false for primitive types).
     * Like the generated implementation file, the generated test case file could be considered a template
     * that you may fill in.
     *
     * @property generateTestcase
     */
    var generateTestcase by generateTestcaseProperty

    /**
     * Provider for namespacePackageMappingFile.
     */
    val namespacePackageMappingFileProvider: Provider<RegularFile>
        get() = namespacePackageMappingFileProperty

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
     *
     * @property namespacePackageMappingFile
     */
    var namespacePackageMappingFile: File
        get() = namespacePackageMappingFileProperty.get().asFile
        set(value) = namespacePackageMappingFileProperty.set(value)

    /**
     * Provider for additional arguments.
     */
    val argumentsProvider: Provider<List<String>>
        get() = argumentsProperty

    /**
     * Additional arguments for WSDL code configuration.
     *
     * @property args
     */
    var args: List<String>
        get() = argumentsProperty.get()
        set(value) = this.argumentsProperty.set(value)

    /**
     * Add argument to list of additional arguments.
     *
     * @property argument
     */
    fun addArg(argument: String) {
        argumentsProperty.add(argument)
    }

    /**
     * Add a list of arguments to list of additional arguments.
     *
     * @property args
     */
    fun addArgs(args: List<String>) {
        for(arg in args) {
            argumentsProperty.add(arg)
        }
    }

    /**
     * Provider for wsdlFile property.
     */
    val wsdlFileProvider: Provider<RegularFile>
        get() = wsdlFileProperty

    /**
     * WSDLExtension file for processing.
     */
    var wsdlFile: File
        get() = wsdlFileProperty.get().asFile
        set(value) = wsdlFileProperty.set(value)

    /**
     * Provider for source set name property.
     */
    val sourceSetNameProvider: Provider<String?>
        get() = sourceSetNameProperty

    /**
     * SourceSet name for Java files.
     */
    var sourceSetName by sourceSetNameProperty
}
