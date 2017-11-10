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

import com.intershop.gradle.test.AbstractIntegrationSpec

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Axis1IntegrationSpec extends AbstractIntegrationSpec {

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
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis1Wsdl2javaAddressBook').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

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
                        namespacePackageMapping = [
                            'http://soapinterop.org/':'samples.echo',
                            'http://soapinterop.org/xsd':'samples.echo'
                        ]
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
                        namespacePackageMapping = [
                            'urn:xmltoday-delayed-quotes':'samples.jms.stub.xmltoday_delayed_quotes',
                            'urn:xmltoday-delayed-quotes':'samples.jms.stub.xmltoday_delayed_quotes'
                        ]
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
                javaOptions.jvmArgs += ["-XX:-UseSerialGC"]  
                
                javaOptions.systemProperty 'http.proxyHost', 'test.host.com'
                javaOptions.systemProperty 'http.proxyPort', '8081'
                javaOptions.systemProperty 'https.proxyHost', 'test.host.com' 
                javaOptions.systemProperty 'https.proxyPort', '4081'
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

        when:
        List<String> args = ['compileJava']
        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':axis1Wsdl2javaWsrpservice').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        where:
        gradleVersion << supportedGradleVersions
    }
}
