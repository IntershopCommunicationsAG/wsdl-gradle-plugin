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
import groovy.lang.Closure
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import java.io.File
import kotlin.reflect.KProperty

operator fun <T> Property<T>.setValue(receiver: Any?, property: KProperty<*>, value: T) = set(value)
operator fun <T> Property<T>.getValue(receiver: Any?, property: KProperty<*>): T = get()

inline fun <reified T> ObjectFactory.property(): Property<T> = property(T::class.java)

operator fun org.gradle.api.file.RegularFileProperty.setValue(receiver: Any?, property: KProperty<*>, value: File) = set(value)
operator fun org.gradle.api.file.RegularFileProperty.getValue(receiver: Any?, property: KProperty<*>): File = get().asFile

abstract class AbbstractAxisConfig(val project: Project, private val confname: String) : Named {

    override fun getName() : String {
        return confname
    }

    private val sourceSetNameProperty: Property<String> = project.objects.property(String::class.java)
    private val packageNameProperty: Property<String?> = project.objects.property(String::class.java)

    private val namespacePackageMappingFileProperty: RegularFileProperty = project.layout.fileProperty()
    private val wsdlFileProperty: RegularFileProperty = project.layout.fileProperty()

    private val argumentsProperty: ListProperty<String> = project.objects.listProperty(String::class.java)

    // will be analyzed as Boolean
    private val generateTestcaseProperty = project.objects.property<Boolean>()

    val namespacePackageMappingsContainer: NamedDomainObjectContainer<NamespacePackageMapping> = project.container(NamespacePackageMapping::class.java)

    init {
        sourceSetNameProperty.set(SourceSet.MAIN_SOURCE_SET_NAME)
        generateTestcaseProperty.set(false)
    }

    /**
     * This is a shorthand option to map all namespaces in a WSDLExtension document to the same
     * Java package name. This can be useful, but dangerous. You must make sure that you
     * understand the effects of doing this. For instance there may be multiple types
     * with the same name in different namespaces.
     * Only for Axis1: It is an error to use the --NStoPkg switch and --package at the same time.
     */
    val packageNameProvider: Provider<String?>
        get() = packageNameProperty

    var packageName by packageNameProperty

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
    fun namespacePackageMappings(c: Closure<NamespacePackageMapping>) {
        namespacePackageMappingsContainer.configure(c)
    }

    /**
     * Generate a client-side JUnit test case. This test case can stand on its own, but it doesn't
     * really do anything except pass default values (null for objects, 0 or false for primitive types).
     * Like the generated implementation file, the generated test case file could be considered a template
     * that you may fill in.
     */
    val generateTestcaseProvider: Provider<Boolean>
        get() = generateTestcaseProperty

    var generateTestcase by generateTestcaseProperty

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
    val namespacePackageMappingFileProvider: Provider<RegularFile>
        get() = namespacePackageMappingFileProperty

    var namespacePackageMappingFile by namespacePackageMappingFileProperty

    /**
     * Additional arguments
     */
    val argumentsProvider: Provider<List<String>>
        get() = argumentsProperty

    var args: List<String>
        get() = argumentsProperty.get()
        set(value) = this.argumentsProperty.set(value)

    fun addArg(argument: String) {
        argumentsProperty.add(argument)
    }

    fun addArgs(args: List<String>) {
        for(arg in args) {
            argumentsProperty.add(arg)
        }
    }

    /**
     * WSDLExtension file for processing.
     */
    val wsdlFileProvider: Provider<RegularFile>
        get() = wsdlFileProperty

    var wsdlFile by wsdlFileProperty

    /**
     * SourceSet for Java files
     */
    val sourceSetNameProvider: Provider<String?>
        get() = sourceSetNameProperty

    var sourceSetName by sourceSetNameProperty
}