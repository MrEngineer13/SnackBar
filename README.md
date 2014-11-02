# SnackBar; toast-like alert pattern for Android inspired by the [Google Material Design Spec](http://www.google.com/design/spec/components/snackbars-and-toasts.html)

[![SnackBar on Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SnackBar-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/869) 

![Mr.Waffle](https://cloud.githubusercontent.com/assets/1573624/4054112/5f907220-2d88-11e4-9624-dd08fa49ac78.png)

# Features

* Set message text and optionally duration
* Shows only one message at a time 
* Can have action item (e.g. undo, refresh, etc.)
* Set text color of action items 
* Swipe down to dismiss all notifications as per documentation
* Backwards compatible to 2.3.x

![SnackBar Screenshot via Google](http://material-design.storage.googleapis.com/images/components-toasts-specs-spec_toast_03_1_large_mdpi.png)


[![SnackBar on Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.mrengineer13.snackbar.sample)

# Usage
##1. Add SnackBar to your project
###Maven
Just add the following to your `build.gradle`.

    dependencies {
        compile 'com.github.mrengineer13:snackbar:0.4.0'
    }

###Eclipse
1. Download ZIP then extract the SnackBar project
1. From Eclipse menu: New > Android Project from Existing Code > SnackBar project > Finish
1. Right click on your project
1. Android > Library: Add > SnackBar
        
## 2. Show a message

#### Create SnackBar
    SnackBar mSnackBar = new SnackBar(this);
    mSnackBar.show("This library is awesome!");

  
## Using this library?

[Score It][1]

If you're using this library in one of your projects just [send me a tweet](https://twitter.com/MrEngineer13) and I'll add your project to the list.
  
# Contribution
## Pull requests are welcome!

Feel free to contribute to SnackBar.

If you've implemented a bug fix or new feature, just create a pull request.

If you have a bug to report a feature to request or have other questions, [file an issue](https://github.com/MrEngineer13/SnackBar/issues/new). I'll try to answer as soon as I can.
    
[1]:https://play.google.com/store/apps/details?id=com.sbgapps.scoreit