# RxApp
Learning to use some of the new opensource libraries that can make Android application development fun and productive.
Libraries used in this example:
 * RxJava & RxAndroid
 * Dagger 2
 * Retrofit 
 * LeakCanary
 * Android databinding
 * Espresso
 
The application presents the contributors of a open source project stored in GitHub.

The application consists of one Activity and two Fragments. The first Fragment provides an input field where the user can enter the name of the open source project by the Square company which she  or he is interested in and a button to start making a request. The second Fragments then shows the list of contributors.

The two Fragments share a singleton model as their data source. The first Fragment makes a request to the model and if the network request completes succesfully the second Fragment is started.

The first Fragment is bit more complicated because it has to take care of subscribing to pending request in case the orientation was changed during the request has not yet completed. In a real life application it's not always possible to make a new request after orientation change and hence we can't lose the response no matter what happens. The second Fragments can just rely on data being already cached by the model.

The cache in the model is implemented by using a AsyncSubject which emits the last value emitted by the source observable. 
