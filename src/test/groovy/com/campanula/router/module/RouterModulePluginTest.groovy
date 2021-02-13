/*
 * This Groovy source file was generated by the Gradle 'init' task.
 */
package com.campanula.router.module

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * A simple unit test for the 'com.campanula.find.include.greeting' plugin.
 */
public class RouterModulePluginTest extends Specification {
    def "plugin registers task"() {
        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.plugins.apply("com.campanula.find.include.greeting")

        then:
        project.tasks.findByName("greeting") != null
    }
}