# Cuberry
[![](https://jitpack.io/v/ButterDebugger/Cuberry.svg)](https://jitpack.io/#ButterDebugger/Cuberry)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

An all-in-one plugin that lets you customize a server to your liking while also supplying a useful and lightweight set of utilities for other plugins!

## Features

All of Cuberry's features are disabled by default and can be enabled in the config

This help Cuberry be as lightweight as possible and ensures that you only use the features you actually need

- Chat
    - Formattable chat
    - Global chat (requires additional setup)
    - Message filters
    - Mentioning
- Custom system messages
    - First time joins
    - Joining & leaving
    - Banned & whitelist join attempt
    - Private MOTD
- Commands
    - Action (aka /me)
    - Back
    - EnderChest
    - Fly
    - Gamemode (including shorted commands e.g. /gmc /gmsp /gms /gma)
    - Heal
    - Home
    - InvSee
    - ItemStack (easily modify an items components e.g. name or lore)
    - Launch
    - Message (replaces Minecraft Vanilla's whisper command)
    - Mutechat
    - Report
    - Respawn (Forces a player on the death screen to respawn)
    - Rules
    - Skipday (Slowly transitions from night to day)
    - Spawn
    - Speed
    - Sudo
    - Teleport
    - Top
    - Tpa
    - Warp
    - Whois
- Formattable text in books and signs
- Custom player names
- Idle players
    - Disable item pickup
    - Change player name
- Resource pack handling
    - Global and per world resource packs
- World specific whitelist

## Dependencies

All of Cuberry's dependencies are optional and will automatically be hooked if present

- Placeholder API
- ProtoWeaver (Required for Global chat)

## Contribute

Feel free to contribute to this project and make it better, contributions are greatly appreciated.

If you are looking to add a new feature to the language, please first search for existing issues, or create a new one to discuss the feature and get feedback first.

## Using the API in your plugin 

In your build.gradle include:

``` gradle
repositories {
    maven { url = 'https://jitpack.io' }
}

dependencies {
    // Import the platform specific Api (e.g. "paper", "velocity", "common")
    implementation "com.github.ButterDebugger.Cuberry:<module>:<version>"
}
```
