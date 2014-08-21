# Snackbar; toast-like alert pattern for Android

# Features

1. Set message text and optionally duration
2. Shows only one message at a time 
3. Can have action item (e.g. undo, refresh, etc.)
4. Swipe down to dismiss all notifications as per documentation
5. Backwards compatible to 2.3.x

![Snackbar Screenshot via Google](http://material-design.storage.googleapis.com/images/components-toasts-specs-spec_toast_03_1_large_mdpi.png)
  

# Usage
##1. Add *snackbar* module as a dependency to your project
        
##2. Show a message


    Snackbar mSnackBar = new SnackBar(this);
    mSnackBar.show("This library is awesome!");
        
# Credit:

1. [Google](http://www.google.com/design/spec/components/snackbars-and-toasts.html) for the idea

<!-- ## Developers-->

<!-- 1. [MrEngineer](https://github.com/MrEngineer13) -->