/*
 * This file is part of the gradle-release plugin.
 *
 * (c) Eric Berry
 * (c) ResearchGate GmbH
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package net.researchgate.release

import net.researchgate.release.tasks.CreateReleaseTag
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class TagNameTests extends Specification {

    Project project

    CreateReleaseTag createReleaseTagTask;

    def testDir = new File("build/tmp/test/${getClass().simpleName}")

    def setup() {
        project = ProjectBuilder.builder().withName("ReleasePluginTest").withProjectDir(testDir).build()
        project.version = '1.1'
        project.apply plugin: ReleasePlugin
        project.release.scmAdapters = [TestAdapter]

        createReleaseTagTask = project.task('createReleaseTagTask', type: CreateReleaseTag)
    }

    def 'when no includeProjectNameInTag then tag name is version'() {
        expect:
        createReleaseTagTask.tagName() == '1.1'
    }

    def 'when includeProjectNameInTag then tag name starts from project name'() {
        given:
        project.release {
            includeProjectNameInTag = true
        }
        expect:
        createReleaseTagTask.tagName() == "$project.name-$project.version" as String
    }

    def 'when tagPrefix not blank then it added to tag ignoring project name'() {
        given:
        project.release {
            includeProjectNameInTag = includeProjectName
            tagPrefix = 'PREF'
        }
        expect:
        createReleaseTagTask.tagName() == 'PREF-1.1'
        where:
        includeProjectName << [true, false]
    }

    def 'when tagTemplate not blank then it is used as tag name and all other options are ignored'() {
        given:
        project.release {
            tagTemplate = '$version'
            tagPrefix = tagPrefixSetting
            includeProjectNameInTag = includeProjectName
        }
        expect:
        createReleaseTagTask.tagName() == '1.1'
        where:
        includeProjectName << [true, false]
        tagPrefixSetting << ['PREF', null]
    }

    def 'when tagTemplate not blank then it is used as tag name'() {
        given:
        project.release {
            tagTemplate = 'PREF-$name-$version'
        }
        expect:
        createReleaseTagTask.tagName() == 'PREF-ReleasePluginTest-1.1'
    }
}