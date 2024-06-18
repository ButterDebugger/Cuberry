# Cuberry
[![](https://jitpack.io/v/ButterDebugger/Cuberry.svg)](https://jitpack.io/#ButterDebugger/Cuberry)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

An all-in-one plugin that lets you customize a server to your liking while also supplying a useful and lightweight set of utilities for other plugins!

### Project Setup
In your build.gradle include:
``` gradle
repositories {
    maven { url = 'https://jitpack.io' }
}

dependencies {
    // Import the whole project as a dependency
    implementation 'com.github.ButterDebugger:Cuberry:<version>'
    // Or just one module, "common" can be replaced with either "paper" or "velocity"
    implementation "com.github.ButterDebugger:Cuberry:common:<version>"
}
```
