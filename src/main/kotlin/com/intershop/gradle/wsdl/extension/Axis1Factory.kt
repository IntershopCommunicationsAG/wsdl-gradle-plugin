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

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project

/**
 * Factory class for Axis 1 configuration extension.
 *
 * @constructor default constructor
 */
class Axis1Factory(private val project: Project) : NamedDomainObjectFactory<Axis1> {
    override fun create(name: String) : Axis1 {
        return Axis1(project, name)
    }
}
