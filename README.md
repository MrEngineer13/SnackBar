# SnackBar; toast-like alert pattern for Android inspired by the [Google Material Design Spec](http://www.google.com/design/spec/components/snackbars-and-toasts.html)

[![SnackBar on Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SnackBar-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/869)

![Mr.Waffle](https://cloud.githubusercontent.com/assets/1573624/4054112/5f907220-2d88-11e4-9624-dd08fa49ac78.png)

# Deprecated

This library is deprecated in favor of the new [Design Support Library](http://developer.android.com/tools/support-library/features.html#design) which includes a [Snackbar](http://developer.android.com/reference/android/support/design/widget/Snackbar.html). It is due to this development that this library is no longer activly being developed.

# Features

* Set message text and optionally duration
* Shows only one message at a time
* Can have action item (e.g. undo, refresh, etc.)
* Set text color of action items
* Swipe down to dismiss all notifications as per documentation
* Backwards compatible to 2.3.x

#### New Features since 1.0.0
* Set custom background color
* Set custom height
* Set custom typeface

![SnackBar Screenshot via Google](http://material-design.storage.googleapis.com/images/components-toasts-specs-spec_toast_03_1_large_mdpi.png)


[![SnackBar on Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.mrengineer13.snackbar.sample)

# Usage
1. Add SnackBar to your project
###Maven
Just add the following to your `build.gradle`.

    dependencies {
        compile 'com.github.mrengineer13:snackbar:1.2.0'
    }

2. Show a message

#### Build SnackBar in Activity
    new SnackBar.Builder(this)
        .withOnClickListener(this)
        .withMessage("This library is awesome!") // OR
        .withMessageId(messageId)
        .withTypeFace(myAwesomeTypeFace)

        .withActionMessage("Action") // OR
        .withActionMessageId(actionMsgId)

        .withTextColorId(textColorId)
        .withBackGroundColorId(bgColorId)
        .withVisibilityChangeListener(this)
        .withStyle(style)
        .withDuration(duration)
        .show();

#### Build SnackBar in Fragment
    new SnackBar.Builder(getActivity().getApplicationContext(), root)
        .withOnClickListener(this)
        .withMessage("This library is awesome!") // OR
        .withMessageId(messageId)
        .withTypeFace(myAwesomeTypeFace)

        .withActionMessage("Action") // OR
        .withActionMessageId(actionMsgId)

        .withTextColorId(textColorId)
        .withBackGroundColorId(bgColorId)
        .withVisibilityChangeListener(this)
        .withStyle(style)
        .withDuration(duration)
        .show();

## Using this library?

If you're using this library in one of your projects just [send me a tweet](https://twitter.com/MrEngineer13) and I'll add your project to the list.

Icon | Application
------------ | -------------
<img src="https://lh4.ggpht.com/uADrrF0FMReNrt7ap_cI-057Zmsl6awZWhpjA0Eupe-HGou1-FFb1ECeta3ED4N1Mos=w300-rw" width="48" height="48" /> | [Plume]
<img src="https://lh6.ggpht.com/pTT1RebLeNJMH7pm9XgQtDWpm0azxOJ7dFYkZqAMT-QE1oi2OGor3qI1ZgiJze4uYvo=w300-rw" width="48" height="48" /> | [Score It]
<img src="https://lh5.ggpht.com/_r-p6eZOnWIPpu5B-jNHWeHBhT-2UC_OZxRFE-BapvdJLIBA2qrrSrOLm15SZsAC1X0=w300-rw" width="48" height="48" /> | [Lotería Navidad 2014]
<img src="https://lh3.ggpht.com/Mz6YqxKsLfVbjYVHj_3nfUxLe5Yvl9W4KO2sKnwud6hZl5mnGitm55PnILT2jx4Hafv6=w300-rw" width="48" height="48" /> | [Journal]
<img src="https://lh4.ggpht.com/DvzthG-_lJsR7Ny8in8KPtEuNAgzzJSdlvUg2EG8qvXH0Oq5YJFQffWjFNKblx2GVAA=w300" width="48" height="48" /> | [My Garage]
<img src="https://lh6.ggpht.com/hdfIOAe9xYS3NzgTx1_3IfVwCP8UCyxDpXHxbviVMPg3iCEkrZudFZ4iuYQNvOp-aKI=w300" width="48" height="48" /> | [QuoteMe]

# Contribution
## Pull requests are welcome!

Feel free to contribute to SnackBar.

Just create your branch then submit pull request on the dev branch.

If you have a bug to report a feature to request or have other questions, [file an issue](https://github.com/MrEngineer13/SnackBar/issues/new). I'll try to answer as soon as I can.

[Plume]:https://play.google.com/store/apps/details?id=com.levelup.touiteur
[Score It]:https://play.google.com/store/apps/details?id=com.sbgapps.scoreit
[Lotería Navidad 2014]:https://play.google.com/store/apps/details?id=com.moya.garcia.loterianavidad&hl=es
[Journal]:https://play.google.com/store/apps/details?id=com.journey.app
[My Garage]:https://play.google.com/store/apps/details?id=com.moremu.mygarage
[QuoteMe]:https://play.google.com/store/apps/details?id=com.wching.quoteme
