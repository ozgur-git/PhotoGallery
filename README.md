# General Info
This is an android native application from the book The Big Nerd Ranch Guide Android Programming,3rd Edition. The project's main targets are
* create and open an HTTPURL connection (somewhat similar to XMLHttpRequest), make queries using Flickr REST APIs flickr.photos.search and flickr.photos.getRecent
* receive and parse the inputstream in JSON format asynchronously.
* download, cache and present the recent  or the searched thumbnailed photos to the user with RecylerView in gridlist asynchronously.
* add functionality so that users can click on thumbnails an display the content in WebView.
* add an alarm using background services to poll recent photos periodically and show n notification bar if any.
* use an ordered broadcast to stop polling when the actvivity is foreground and start polling when the activity is not visible.

I added the following to the standard code described in the book
* parse JSON responses using GSON library instead of the JSONObject library.
* add page number parameter to the Flicker API query so that the user can see the next page when hit bottom while scrolling the gridlist.
* dynamically adjusting grid columns number wih respect to screen size and orientation.
* add and LRUCache so that it downloads and caches previous and next 10 photos to achieve smooth scrolling performance.
* add progress bar.
 
