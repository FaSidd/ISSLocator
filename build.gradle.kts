plugins {
  java
  jacoco
  pmd
  application
}       

application {
  mainClassName = "main.Main"
}

repositories {
	mavenCentral()
}

dependencies {
  testCompile("org.junit.jupiter:junit-jupiter-api:5.2.0")
	testRuntime("org.junit.jupiter:junit-jupiter-engine:5.2.0")
	testRuntime("org.junit.platform:junit-platform-console:1.2.0")
  
  implementation("net.iakovlev:timeshape:2018d.6")
  implementation("org.mockito:mockito-core:2.2.7")
  implementation("org.json:json:20190722")
}
 
sourceSets {
  main {
    java.srcDirs("ISSLocator/src")
  }
  test {
    java.srcDirs("ISSLocator/test")
  }
}

tasks {
    val treatWarningsAsError =
            listOf("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")

    getByName<JavaCompile>("compileJava") {
        options.compilerArgs = treatWarningsAsError
    }

    getByName<JavaCompile>("compileTestJava") {
        options.compilerArgs = treatWarningsAsError
    }
    getByName<JacocoReport>("jacocoTestReport") {
        afterEvaluate {
            setClassDirectories(files(classDirectories.files.map {
                fileTree(it) { exclude("**/main/**") }
            }))
        }
    }
}
val test by tasks.getting(Test::class) {
	useJUnitPlatform {}
}
 
pmd {
    ruleSets = listOf()
    ruleSetFiles = files("../conf/pmd/ruleset.xml")
}                                                
 
defaultTasks("clean", "test", "jacocoTestReport", "pmdMain")
