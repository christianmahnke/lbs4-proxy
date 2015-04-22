JavaScript Replacements
=======================

# Firefox JavaScript errors

## TypeError: document.attachEvent is not a function
This is a Internet Explorer specific JavaScript method.

See: http://stackoverflow.com/questions/13190336/error-typeerror-window-attachevent-is-not-a-function

### Files
* lbsData.js

## TypeError: window.navigate is not a function
This is a Internet Explorer specific JavaScript method. 

See: http://stackoverflow.com/questions/1112093/button-javascript-works-on-ie-but-not-firefox-window-navigate

Replace with:  
> window.location.href=

### Files
* messageDoc.js (only if SCRIPT=1)

## TypeError: window.event is undefined
Internet Explorer uses another event model

See: http://stackoverflow.com/questions/9813445/why-ff-says-that-window-event-is-undefined-call-function-with-added-event-list

There is a related Problem regarding 'event.srcElement'

See also: http://stackoverflow.com/questions/5301643/how-can-i-make-event-srcelement-work-in-firefox-and-what-does-it-mean

### Files
* lbsData.js

