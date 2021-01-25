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

class Axis1IntegrationSpec extends AbstractIntegrationGroovySpec {

    def 'Test simple code generation'() {
        given:
        copyResources('axis1/addressbook/AddressBook.wsdl', 'staticfiles/wsdl/AddressBook.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis1 {
                    addressBook {
                        wsdlFile = file('staticfiles/wsdl/AddressBook.wsdl')
                    }
                }
            }
            
            repositories {
                jcenter()
            }

            dependencies {
                compile 'org.apache.axis:axis:1.4'
                compile 'org.apache.axis:axis-jaxrpc:1.4'
                compile 'javax.xml:jaxrpc-api:1.1'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-d', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']

        def result1 = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result1.task(':axis1Wsdl2javaAddressBook').outcome == SUCCESS
        result1.task(':compileJava').outcome == SUCCESS

        when:
        def result2 = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result2.task(':axis1Wsdl2javaAddressBook').outcome == UP_TO_DATE
        result2.task(':compileJava').outcome == UP_TO_DATE

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test with namespace mapping'() {
        copyResources('axis1/echo-sample/InteropTest.wsdl', 'staticfiles/wsdl/InteropTest.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis1 {
                    echo {
                        wsdlFile = file('staticfiles/wsdl/InteropTest.wsdl')
                        namespacePackageMappings {
                            sample1 {
                                namespace = 'http://soapinterop.org/'
                                packageName = 'samples.echo'
                            }
                            sample2 {
                                namespace = 'http://soapinterop.org/xsd'
                                packageName = 'samples.echo'                           
                            }
                        }
                    }
                }
            }
            
            repositories {
                jcenter()
            }

            dependencies {
                compile 'org.apache.axis:axis:1.4'
                compile 'org.apache.axis:axis-jaxrpc:1.4'
                compile 'javax.xml:jaxrpc-api:1.1'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis1Wsdl2javaEcho').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS
        (new File(testProjectDir, 'build/generated/wsdl2java/axis1/echo/samples/echo/InteropTestPortType.java')).exists()

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test with generated server code'() {
        copyResources('axis1/jaxrpc-sample/Address.wsdl', 'staticfiles/wsdl/Address.wsdl')
        copyResources('axis1/jaxrpc-sample/HelloWorld.wsdl', 'staticfiles/wsdl/HelloWorld.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis1 {
                    address {
                        wsdlFile = file('staticfiles/wsdl/Address.wsdl')
                        serverSide = true
                    }
                    hello {
                        wsdlFile = file('staticfiles/wsdl/HelloWorld.wsdl')
                    }
                }
            }
            
            repositories {
                jcenter()
            }

            dependencies {
                compile 'org.apache.axis:axis:1.4'
                compile 'org.apache.axis:axis-jaxrpc:1.4'
                compile 'javax.xml:jaxrpc-api:1.1'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis1Wsdl2javaAddress').outcome == SUCCESS
        result.task(':axis1Wsdl2javaHello').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS
        (new File(testProjectDir, 'build/generated/wsdl2java/axis1/address/samples/jaxrpc/address/deploy.wsdd')).exists()

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test with extended configuration'() {
        copyResources('axis1/jms-sample/GetQuote.wsdl', 'staticfiles/wsdl/GetQuote.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis1 {
                    jms {
                        wsdlFile = file('staticfiles/wsdl/GetQuote.wsdl')
                        typeMappingVersion = '1.1'
                        deployScope = 'Session'
                        allowInvalidURL = true
                        namespacePackageMappings {
                            sample1 {
                                namespace = 'urn:xmltoday-delayed-quotes'
                                packageName = 'samples.jms.stub.xmltoday_delayed_quotes'
                            }
                            sample2 {
                                namespace = 'urn:xmltoday-delayed-quotes'
                                packageName = 'samples.jms.stub.xmltoday_delayed_quotes'                     
                            }
                        }
                    }
                }
            }
            
            repositories {
                jcenter()
            }

            dependencies {
                compile 'org.apache.axis:axis:1.4'
                compile 'org.apache.axis:axis-jaxrpc:1.4'
                compile 'javax.xml:jaxrpc-api:1.1'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis1Wsdl2javaJms').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test simple code generation with javaOptions'() {
        given:
        copyResources('axis1/addressbook/AddressBook.wsdl', 'staticfiles/wsdl/AddressBook.wsdl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis1 {
                    addressBook {
                        wsdlFile = file('staticfiles/wsdl/AddressBook.wsdl')
                    }
                }
            }
            
            tasks.withType(com.intershop.gradle.wsdl.tasks.axis1.WSDL2Java) {
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
                compile 'org.apache.axis:axis:1.4'
                compile 'org.apache.axis:axis-jaxrpc:1.4'
                compile 'javax.xml:jaxrpc-api:1.1'
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis1Wsdl2javaAddressBook').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS
        result.output.contains('-Dhttp.proxyHost=test.host.com')
        result.output.contains('-Dhttp.proxyPort=8081')
        result.output.contains('-Dhttps.proxyHost=test.host.com')
        result.output.contains('-Dhttps.proxyPort=4081')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test code generation wsrp service'() {
        given:
        copyResources('axis1/wsrpservice/NStoPkg.properties' ,'staticfiles/wsdl/NStoPkg.properties')
        copyResources('axis1/wsrpservice/wsrp_service.wsdl' ,'staticfiles/wsdl/wsrp_service.wsdl')
        copyResources('axis1/wsrpservice/wsrp_v1_bindings.wsdl' ,'staticfiles/wsdl/wsrp_v1_bindings.wsdl')
        copyResources('axis1/wsrpservice/wsrp_v1_interfaces.wsdl' ,'staticfiles/wsdl/wsrp_v1_interfaces.wsdl')
        copyResources('axis1/wsrpservice/wsrp_v1_types.xsd' ,'staticfiles/wsdl/wsrp_v1_types.xsd')
        copyResources('axis1/wsrpservice/wsrp_v1_types_original.xsd' ,'staticfiles/wsdl/wsrp_v1_types_original.xsd')
        copyResources('axis1/wsrpservice/xml.xsd' ,'staticfiles/wsdl/xml.xsd')


        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.wsdl'
            }
            
            wsdl {
                axis1 {
                    wsrpservice {
                        wsdlFile = file('staticfiles/wsdl/wsrp_service.wsdl')
                        namespacePackageMappingFile = file('staticfiles/wsdl/NStoPkg.properties')
                        wrapArrays = true
                        noWrapped = true
                    }
                }
            }

            repositories {
                jcenter()
            }
            
            dependencies {
                compile 'org.apache.axis:axis:1.4'
                compile 'org.apache.axis:axis-jaxrpc:1.4'
                compile 'javax.xml:jaxrpc-api:1.1'
            }
        """.stripIndent()

        File resultBindDir = new File(testProjectDir, 'build/generated/wsdl2java/axis1/wsrpservice/oasis/names/tc/wsrp/v1/bind')

        when:
        List<String> args = ['compileJava']
        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis1Wsdl2javaWsrpservice').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS
        resultBindDir.exists()
        resultBindDir.listFiles().length == 4

        where:
        gradleVersion << supportedGradleVersions
    }
}
