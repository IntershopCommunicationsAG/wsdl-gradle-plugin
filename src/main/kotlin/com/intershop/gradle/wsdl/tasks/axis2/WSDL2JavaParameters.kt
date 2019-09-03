package com.intershop.gradle.wsdl.tasks.axis2

import org.gradle.api.provider.ListProperty
import org.gradle.workers.WorkParameters

interface WSDL2JavaParameters : WorkParameters {

    val paramList: ListProperty<String>
}