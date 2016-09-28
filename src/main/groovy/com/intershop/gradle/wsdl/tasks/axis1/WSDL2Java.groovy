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
package com.intershop.gradle.wsdl.tasks.axis1

import com.intershop.gradle.wsdl.extension.WSDLExtension
import com.intershop.gradle.wsdl.tasks.AbstractWSDL2Java
import org.gradle.api.file.FileCollection
import org.gradle.process.internal.JavaExecHandleBuilder

/**
 * Task to execute the WSDL2Java Tool.
 *
 * <pre>
 * Based on WSDL2Java:
 * Usage: java org.apache.axis.wsdl.WSDL2Java [options] WSDL-URI
 *
 * CommandLine-Options for WSDL2Java:
 *
 * -h, --help
 *   print this message and exit
 * -v, --verbose
 *   print informational messages
 * -n, --noImports
 *   only generate code for the immediate WSDL document
 * -O, --timeout <argument>
 *   timeout in seconds (default is 45, specify -1 to disable)
 * -D, --Debug
 *   print debug information
 * -W, --noWrapped
 *   turn off support for "wrapped" document/literal
 * -s, --server-side
 *   emit server-side bindings for web service
 * -S, --skeletonDeploy <argument>
 *   deploy skeleton (true) or implementation (false) in
 *   deploy.wsdd.
 *   Default is false.  Assumes --server-side.
 * -N, --NStoPkg <argument>=<value>
 *   mapping of namespace to package
 * -f, --fileNStoPkg <argument>
 *   file of NStoPkg mappings (default NStoPkg.properties)
 * -p, --package <argument>
 *   override all namespace to package mappings, use this package
 *   name instead
 * -o, --output <argument>
 *   output directory for emitted files
 * -d, --deployScope <argument>
 *   add scope to deploy.xml: "Application", "Request", "Session"
 * -t, --testCase
 *   emit junit testcase class for web service
 * -a, --all
 *   generate code for all elements, even unreferenced ones
 * -T, --typeMappingVersion
 *   indicate 1.1 or 1.2. The default is 1.1 (SOAP 1.1 JAX-RPC  compliant.
 *   1.2 indicates SOAP 1.1 encoded.)
 * -F, --factory <argument>
 *   name of a custom class that implements GeneratorFactory interface
 *   (for extending Java generation functions)
 * -i, --nsInclude <namespace>
 *   namescape to specifically include in the generated code (defaults to
 *   all namespaces unless specifically excluded with the -x option)
 * -x, --nsExclude <namespace>
 *   namespace to specifically exclude from the generated code (defaults to
 *   none excluded until first namespace included with -i option)
 * -p, --property <name>=<value>
 *   name and value of a property for use by the custom GeneratorFactory
 * -H, --helperGen
 *   emits separate Helper classes for meta data
 * -U, --user <argument>
 *   username to access the WSDL-URI
 * -P, --password <argument>
 *   password to access the WSDL-URI
 * -c, --implementationClassName <argument>
 *   use this as the implementation class
 * -w, --wrapArrays
 *   Prefer generating JavaBean classes like "ArrayOfString" for certain schema array patterns (default is to use String [])
 * </pre>
 */
class WSDL2Java extends AbstractWSDL2Java {

    static final String MAIN_CLASS_NAME = 'org.apache.axis.wsdl.WSDL2Java'

    /**
     * Prepares the JavaExecHandlerBuilder for the task.
     *
     * @return JavaExecHandleBuilder
     */
    JavaExecHandleBuilder prepareExec() {

        JavaExecHandleBuilder javaExec = new JavaExecHandleBuilder(getFileResolver());
        getForkOptions().copyTo(javaExec);

        FileCollection axis1CodegenConfiguration = getProject().getConfigurations().getAt(WSDLExtension.WSDLAXIS1_CONFIGURATION_NAME)

        List<String> args = []

        return javaExec
                .setClasspath(axis1CodegenConfiguration)
                .setMain(MAIN_CLASS_NAME)
                .setArgs(args)
    }
}
