# RxApp
Learning to use some of the new opensource libraries that can make Android application development fun and productive.
Libraries used in this example:
 * Android view model
 * Kotlin coroutines
 * Hilt
 * Jetpack Navigation
 * Retrofit
 * Android databinding
 * Barista (makes Espresso nice)
 * RESTMock
 
The application presents the contributors of a open source project stored in GitHub.

The application consists of one Activity and two Fragments. The first Fragment provides an input field where the user can enter the name of the open source project by the Square company which she  or he is interested in and a button to start making a request. The second Fragments then shows the list of contributors.

The two Fragments share a singleton view model (scoped to Activity) as their data source. The first Fragment makes a request to the view model and if the network request completes successfully the second Fragment is started.

The caching of the data is handled by shared view model that exposes the data via LiveData.
