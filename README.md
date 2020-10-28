# CYK Algorithm

<p align="left">
    <a href="https://opensource.org/licenses/MIT"><img alt="License" src="https://img.shields.io/github/license/deryeger/cyk-algorithm?style=for-the-badge"></a>
    <a href="https://travis-ci.com/deryeger/cyk-algorithm"><img alt="Build" src="https://img.shields.io/travis/com/deryeger/cyk-algorithm?style=for-the-badge"></a>
    <a href="https://bintray.com/deryeger/maven/cyk-algorithm"><img alt="Download" src="https://img.shields.io/bintray/dt/deryeger/maven/cyk-algorithm?style=for-the-badge"></a>
</p>

> Kotlin Multiplatform implementation of the CYK algorithm.

<p><img src="/src/main/resources/cyk-algorithm-logo.png" height="128" width="128"></p>

## Installation

#### build.gradle.kts

```
repositories {
    maven(url = "https://dl.bintray.com/deryeger/maven")
}

dependencies {
    implementation("eu.yeger:cyk-algorithm:0.2.0")
}
```

## Usage

This library contains two variants of the algorithm.
Both versions can be used by providing a grammar to the included parser or by programmatically generating the required model.
While the first version only computes the end result, `runningCYK` computes a list containing every step of the algorithm.

```
cyk("hello world") {
    grammar("S") {
        """
            S -> A B
            A -> hello
            B -> world
        """.trimIndent()
    }
}.getOrElse { error(it) }


runningCYK("hello world") {
    grammar("S") {
        """
            S -> A B
            A -> hello
            B -> world
        """.trimIndent()
    }
}.getOrElse { error(it) }
```


## Example

An example project using this library can be found [here](https://github.com/DerYeger/cyk-visualizer).

## Credits

Logo by [Magdalena Jirku](https://www.deviantart.com/keshyx).
