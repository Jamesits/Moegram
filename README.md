# Moegram

## **This project is no longer maintained!**
I'm sorry for this, but with the slow release of Telegram source code, I don't have any more patience to continue working on this project.

For an alternative of this, try [Plus Messenger](https://play.google.com/store/apps/details?id=org.telegram.plus) or [Lagatgram](https://play.google.com/store/apps/details?id=org.ilwt.lagatgram). 

--------------

This project adds an option to display Google's Emoji Font and keeps minimal changes to other part of the application.

[Download Moegram from Google Play](https://play.google.com/store/apps/details?id=me.swineson.moegram.messenger)

[Visit releases page](https://github.com/Jamesits/Moegram/releases)

Moegram channel: [https://telegram.me/moegram](https://telegram.me/moegram)

## Thanks

 * [kaoyusu/XposedMod_TelegramWithAndroidEmoji](https://github.com/kaoyusu/XposedMod_TelegramWithAndroidEmoji)
 * [com.amulyakhare.textdrawable.TextDrawable](https://github.com/amulyakhare/TextDrawable)
 * [Noto Sans CJK - Google Noto Fonts](https://www.google.com/get/noto/help/cjk/)

--------------

## Build Instructions

These instructions apply to both this repository (Moegram) and official Telegram for Android sources.

DO NOT use Windows -- you will end up in make error 87.

### Dependencies

 * Android SDK
 * Android NDK
 * Android Studio

When on an OS X with [Homebrew](http://brew.sh/) and [Homebrew Cask](http://caskroom.io/), it's easy to install them:
```shell
brew install android-sdk android-ndk
brew cask install android-studio
```
**DO NOT** let Android SDK or Android Studio to install NDK on OS X, or you will end up in a mess of file permissions and build errors. **DO** install them using Homebrew or any package manager.

### Create Project

 1. `git clone` Telegram repository to some path;
 2. Launch Android Studio, select "Import" from welcome page and select that path;
 3. Config code signing keys if needed;
 4. If Android Studio needs a Gradle sync, sync it;
 5. Android Studio will ask if you want to install missing dependencies (maybe once or twice). Install as instructed.

### Build NDK Libraries

 1. Run Gradle sync, it will be automatically built

### Customize APK

#### Fill API keys
Find `project_root/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java.sample`, copy it to `BuildVars.java` in the same folder, and fill all the API keys. That file is self-explainary.

Note:
 * If you need a debug version, set `DEBUG_VERSION` to be `true`. This will enable various debugging and logging functions.
 * Visit [https://github.com/DrKLO/Telegram/blob/master/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java](https://github.com/DrKLO/Telegram/blob/master/TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java) for the most recent value of `BUILD_VERSION`. Telegram servers use this value to determine if certain type of messages are supported and send a fallback value if not. A wrong value may cause fatals since message types change day by day.
 * For `HOCKEY_APP_HASH` and `HOCKEY_APP_HASH_DEBUG`, you can use any random 32 digit HEX number (for example, the MD5 of any sentence you like) in string format if you don't want to use Hockey. A wrong length will cause fatal error.
 * Do not change `GCM_SENDER_ID`, or GCM will be completely unusable.
 * `SMS_HASH`

#### Fill `google-service.json`
 1. Go to https://console.firebase.google.com/u/0/
 2. Create a new project
 3. Click on the android icon on the project homepage hero
 4. Fill in application package name
 5. Download `google-service.json`
 6. Put it under `./TMessagesProj`

#### Edit Package Identifier and Version
Open `project_root/TMessagesProj/build.gradle`, edit `defaultConfig`.

#### Edit Name
Edit all `string.xml` in `project_root/TMessagesProj/src/main/res/values*`, modify the following line:
```xml
<string name="AppName">some_name</string>
```
Note: it may be faster using Translation Editor of Android Studio.

### Build APK

 1. Select Build -> Generate Signed APK… from Android Studio.

Once you see a log entry like `APK(s) generated successfully`, find your APK under `project_root/TMessagesProj/TMessagesProj-your_flavor_name-release.apk`.

--------------

## Telegram messenger for Android

[Telegram](https://telegram.org) is a messaging app with a focus on speed and security. It’s superfast, simple and free.
This repo contains the official source code for [Telegram App for Android](https://play.google.com/store/apps/details?id=org.telegram.messenger).

## Creating your Telegram Application

We welcome all developers to use our API and source code to create applications on our platform.
There are several things we require from **all developers** for the moment.

1. [**Obtain your own api_id**](https://core.telegram.org/api/obtaining_api_id) for your application.
2. Please **do not** use the name Telegram for your app — or make sure your users understand that it is unofficial.
3. Kindly **do not** use our standard logo (white paper plane in a blue circle) as your app's logo.
3. Please study our [**security guidelines**](https://core.telegram.org/mtproto/security_guidelines) and take good care of your users' data and privacy.
4. Please remember to publish **your** code too in order to comply with the licences.

### API, Protocol documentation

Telegram API manuals: https://core.telegram.org/api

MTproto protocol manuals: https://core.telegram.org/mtproto

### Usage

**Beware of using the dev branch and uploading it to any markets, in many cases it not will work as expected**.

First of all, take a look at **src/main/java/org/telegram/messenger/BuildVars.java** and fill it with correct values.
Import the root folder into your IDE (tested on Android Studio), then run project.

### Localization

We moved all translations to https://translations.telegram.org/en/android/. Please use it.
