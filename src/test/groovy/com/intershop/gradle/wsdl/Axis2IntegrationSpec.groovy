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

import com.intershop.gradle.test.AbstractIntegrationGroovySpec

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class Axis2IntegrationSpec extends AbstractIntegrationGroovySpec {

    def 'Test code generation faulthandling'() {
        given:
        copyResources('axis2/faulthandling/bank.wsdl', 'staticfiles/wsdl/bank.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis2 {
                    bank {
                        wsdlFile = file('staticfiles/wsdl/bank.wsdl')
                        serverSide = true
                        serviceDescription = true
                        packageName = 'sample.bank.service'
                    }
                }
            }

            repositories {
                jcenter()
            }
            
            dependencies {
                compile 'org.apache.axis2:axis2:1.7.7'
                compile 'org.apache.axis2:axis2-adb:1.7.7'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']
        def result1 = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result1.task(':axis2Wsdl2javaBank').outcome == SUCCESS
        result1.task(':compileJava').outcome == SUCCESS

        when:
        def result2 = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result2.task(':axis2Wsdl2javaBank').outcome == UP_TO_DATE
        result2.task(':compileJava').outcome == UP_TO_DATE

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test code generation mtom'() {
        given:
        copyResources('axis2/mtom/MTOMSample.wsdl', 'staticfiles/wsdl/MTOMSample.wsdl')
        copyResources('axis2/mtom/xmime.xsd', 'staticfiles/wsdl/xmime.xsd')
        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis2 {
                    mtom {
                        wsdlFile = file('staticfiles/wsdl/MTOMSample.wsdl')
                        serverSide = true
                        allPorts = true
                        serviceDescription = true
                        packageName = 'sample.mtom.service'
                    }
                }
            }

            repositories {
                jcenter()
            }
            
            dependencies {
                compile 'org.apache.axis2:axis2:1.7.7'
                compile 'org.apache.axis2:axis2-adb:1.7.7'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']
        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis2Wsdl2javaMtom').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test code generation qstartadb'() {
        given:
        copyResources('axis2/quickstartadb/StockQuoteService.wsdl', 'staticfiles/wsdl/StockQuoteService.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis2 {
                    qstartadb {
                        wsdlFile = file('staticfiles/wsdl/StockQuoteService.wsdl')
                        sync = true
                        serverSide = true
                        serviceDescription = true
                        serversideInterface = true
                        allPorts = true
                        packageName = 'samples.quickstart.service.adb'
                        namespacePackageMappings {
                            quickstart {
                                namespace = 'http://quickstart.samples/xsd'
                                packageName = 'samples.quickstart.service.adb.xsd'
                            }
                        }
                        databindingMethod = 'adb'
                    }
                }
            }

            repositories {
                jcenter()
            }
            
            dependencies {
                compile 'org.apache.axis2:axis2:1.7.7'
                compile 'org.apache.axis2:axis2-adb:1.7.7'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']
        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis2Wsdl2javaQstartadb').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS
        (new File(testProjectDir, 'build/generated/wsdl2java/axis2/qstartadb/resources/services.xml')).exists()
        (new File(testProjectDir, 'build/generated/wsdl2java/axis2/qstartadb/src/samples/quickstart/service/adb/StockQuoteServiceSkeleton.java')).exists()

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test code generation qstartjibx'() {
        given:
        copyResources('axis2/quickstartadb/StockQuoteService.wsdl', 'staticfiles/wsdl/StockQuoteService.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis2 {
                    qstartjibx {
                        wsdlFile = file('staticfiles/wsdl/StockQuoteService.wsdl')
                        sync = true
                        serverSide = true
                        unwrapParams = true
                        serviceDescription = true
                        serversideInterface = true
                        allPorts = true
                        packageName = 'samples.quickstart.service.jibx'
                        namespacePackageMappings {
                            quickstart {
                                namespace = 'http://quickstart.samples/xsd'
                                packageName = 'samples.quickstart.service.jibx.xsd'
                            }
                        }
                        databindingMethod = 'jibx'
                    }
                }
            }

            repositories {
                jcenter()
            }
            
            configurations {
                wsdlAxis2.extendsFrom(compile)
            }
            
            dependencies {
                compile 'org.apache.axis2:axis2:1.7.3'
                compile 'org.apache.axis2:axis2-kernel:1.7.3'
                compile 'org.apache.axis2:axis2-jibx:1.7.3'
                compile 'org.jibx:jibx-run:1.2'
                compile 'org.jibx:jibx-bind:1.2'
                
                wsdlAxis2 'org.apache.axis2:axis2-codegen:1.7.3'
                wsdlAxis2 'wsdl4j:wsdl4j:1.6.3'
                wsdlAxis2 'commons-logging:commons-logging:1.2'
                wsdlAxis2 'org.apache.neethi:neethi:3.0.3'
                wsdlAxis2 'org.apache.ws.commons.axiom:axiom-api:1.2.20'
                wsdlAxis2 'org.apache.ws.commons.axiom:axiom-impl:1.2.20'
                wsdlAxis2 'org.apache.woden:woden-core:1.0M10'
                wsdlAxis2 'org.apache.ws.xmlschema:xmlschema-core:2.2.1'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']
        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis2Wsdl2javaQstartjibx').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS
        (new File(testProjectDir, 'build/generated/wsdl2java/axis2/qstartjibx/resources/services.xml')).exists()
        (new File(testProjectDir, 'build/generated/wsdl2java/axis2/qstartjibx/src/samples/quickstart/service/jibx/StockQuoteServiceSkeleton.java')).exists()

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test code generation databinding'() {
        given:
        copyResources('axis2/databinding/StockQuoteService.wsdl', 'staticfiles/wsdl/StockQuoteService.wsdl')
        copyResources('axis2/databinding/StockQuote.xsd', 'staticfiles/wsdl/StockQuote.xsd')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis2 {
                    databinding {
                        wsdlFile = file('staticfiles/wsdl/StockQuoteService.wsdl')
                        serverSide = true
                        allPorts = true
                        serviceDescription = true
                        packageName = 'samples.databinding'
                        databindingMethod = 'none'
                    }
                }
            }

            repositories {
                jcenter()
            }
            
            dependencies {
                compile 'org.apache.axis2:axis2:1.7.7'
                compile 'org.apache.axis2:axis2-kernel:1.7.7'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']
        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis2Wsdl2javaDatabinding').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test code generation faulthandling with javaOptions'() {
        given:
        copyResources('axis2/faulthandling/bank.wsdl', 'staticfiles/wsdl/bank.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis2 {
                    bank {
                        wsdlFile = file('staticfiles/wsdl/bank.wsdl')
                        serverSide = true
                        serviceDescription = true
                        packageName = 'sample.bank.service'
                    }
                }
            }

            tasks.withType(com.intershop.gradle.wsdl.tasks.axis2.WSDL2Java) {
                forkOptions { JavaForkOptions options ->
                    options.setMaxHeapSize('64m')
                    options.jvmArgs += ["-XX:-UseSerialGC"]  
                    options.systemProperty('http.proxyHost', 'test.host.com')
                    options.systemProperty('http.proxyPort', '8081')
                    options.systemProperty('https.proxyHost', 'test.host.com')
                    options.systemProperty('https.proxyPort', '4081')
                }
            }

            repositories {
                jcenter()
            }
            
            dependencies {
                compile 'org.apache.axis2:axis2:1.7.3'
                compile 'org.apache.axis2:axis2-adb:1.7.3'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']
        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis2Wsdl2javaBank').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test code generation add number service'() {
        given:
        copyResources('axis2/addnumberservice/AddNumbersService.wsdl', 'staticfiles/wsdl/AddNumbersService.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis2 {
                    numberservice {
                        wsdlFile = file('staticfiles/wsdl/AddNumbersService.wsdl')
                        databindingMethod = 'jaxbri'
                        wsdlVersion = '1.1'
                    }
                }
            }

            repositories {
                jcenter()
            }
            
            dependencies {
                compile 'org.apache.axis2:axis2:1.7.7'
                compile 'org.apache.axis2:axis2-jaxbri:1.7.7'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']
        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis2Wsdl2javaNumberservice').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        where:
        gradleVersion << supportedGradleVersions
    }
}
