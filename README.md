# SmallWorld Android

Android codebase for SmallWorld, whose project overview and screenshots can be
found at [the smallworld base repo.](https://github.com/germaindudek/smallworld)

## Prerequisites

- Android device or emulator running Android 12 (Snow Cone) or later
- Android Studio
- Mapbox Account

## Getting Started

1. Clone this repository
2. Open Android Studio and import the project:
    - Click on "Open"
    - Navigate to the location where you cloned the repository and select the `smallworld-android`
      directory
    - If a dialog pops up saying "Trust and Open Project 'smallworld-android'?", go ahead and
      click "Trust Project"
3. Set up Mapbox
    - Create a Mapbox account if you haven't already
      at [account.mapbox.com/auth/signup](https://account.mapbox.com/auth/signup/)
    - Go to [account.mapbox.com](https://account.mapbox.com/)
    - Configure your public token
        1. On your mapbox account page and under "Access Tokens", copy your *default public token*
        2. Go back to Android studio and open the *Project Window* tool window by clicking it in the
           left tool window bar or by pressing `⌘1`
        3. Click on the dropdown at the top left corner of the tool window and select the *Project*
           view from the list. This will show you the file hierarchy of the whole project
        4. Right-click on the `smallworld-android` folder and select *New > File* and name
           it `keys.properties`
        5. Fill it in as follows, making sure to replace `insert_public_token` with the *default
           public token* you copied in step 1: `MAPBOX_ACCESS_TOKEN=insert_public_token`
    - Configure secret token
        1. From your accounts [tokens page](https://account.mapbox.com/access-tokens/) of your
           mapbox account
        2. Select *Create a Token*
        3. From the token creation page, give your token a name and make sure the box next to th *
           Downloads:Read* scope is checked
        4. Click the Create token button at the bottom of the page to create your token
        5. The token you've created is a secret token, which means you will only have one
           opportunity to copy it somewhere secure
        6. Find or create a gradle.properties file in your Gradle user home folder. The folder can
           be found at `«USER_HOME»/.gradle`. Once you have found or created the file, its path
           should be `«USER_HOME»/.gradle/gradle.properties`
        7. Add this line to the file, making sure to replace `insert_secret_token` with the token
           you copied in step 5: `MAPBOX_DOWNLOADS_TOKEN=insert_secret_token`
4. In Android Studio, select your device from the device dropdown in the toolbar, and then click the
   green triangle button or press `^R` to run the app

## Technology Stack

- Android Compose for UI
- Mapbox for maps
- Kotlin Flows for UI state management
- Kotlin Coroutines for concurrency
- Dagger Hilt for dependency injection
- Retrofit for networking
- Android Jetpack components for lifecycle management and navigation
- Google Play Services for location services
- Timber for logging
- LeakCanary for memory leak monitoring

## Package Structure

```
-- com.example.smallworld
|   |-- data            # Data layer: repositories, data sources
|   |   |-- feature-name
|   |   |   |-- dto
|   |   |   |-- enums
|   |   |   |-- model 
|   |-- di              # Hilt Modules
|   |-- ui              # View layer: composables, viewmodels, navigation graphs
|   |   |-- components  # Reused Composables
|   |   |-- flows       # Actual screens organised by flow
|   |   |-- theme       # App theme classes
|   |-- util            # Globally used classes/functions, extension functions
```

## Architecture Overview

Smallworld uses the MVVM architectural pattern, a tried and tested, modern pattern that's used
widely across the industry, is in-line with Google's recommendations, and much of the Android
ecosystem is built with it in mind.

#### Model Layer

The model/data layer of the app is the link between the viewmodels and the data sources. The model
layer is quite simple in Smallworld as it's online-only, so the only data source is the API.

Communication to the API is done through one of two Retrofit services wherein all functions are
implemented as suspending functions conforming to the codebases' strict use of coroutines. The first
retrofit service is `SmallWorldAuthApi` which handles all auth-related api requests i.e. signing
in/out, validating usernames/passwords, and refreshing tokens. The second is `SmallWorldApi`, which
handles requests relating to the main business logic of the application: searching for users,
getting a users' profile, sending/receiving requests, getting locations, etc. Separating those two
services has two advantages: it makes our application more modular and it allows us to configure
them differently. `SmallWorldApi` is mediated by `AuthOkhttpInterceptor`, which automatically
authenticates all requests with the users' access token and refreshes the token if
necessary. `SmallWorldAuthApi` is not mediated by `AuthOkhttpInterceptor` as the requests within it
happen before the user authenticates themself and thus they don't require an access token, nor would
one be available.

The codebase employs two types of data holders: the DTO's, that is the objects returned via JSON
from the API, and the domain models. Most DTO's have a corresponding domain model, and the DTO data
classes are suffixed with `Dto` whilst the domain models are not suffixed (eg `ProfileDto`
and `Profile`). Repositories map DTO's to domain models when fetching data from the api and passing
it on to the upper layers.

Separating the models not only allows us to map values and types from the DTO into representations
in the domain model that might be more useful in the upper layers, but it also protects the
applicaition code from breaking changes in the shape of the DTO. By ensuring that there is a single
point in the code where DTO's are mapped in the domain models (the repository), we can account for
any changes in the DTO by adapting the mapping function from the DTO to the domain model without
having to modify any of the code in the upper layers.

There is one repository for each path of the API.

#### ViewModel Layer

ViewModels contain all the business logic . User events are propagated from the view to the
viewmodel through the viewmodel's methods, the viewmodel interacts with the data layer via the
repositories, and updates it's state holders.

The viewmodels use exclusively `StateFlow`s to hold state, and for passing one-time events like a
notification to navigate to a different screen to the view layer `SharedFlow`s are used. In certain
cases where the consumption of the event is required and using a `SharedFlow` doesn't guarantee it
because flow collection might happen *after* event dispatch, `Channel`s are used. All asynchronous
operations happening in viewmodels use kotlin coroutines and teh structured concurrency it enables.
If the viewmodel depends on a this party class which uses a different style of concurrency than
coroutines like Promises or callbacks, wrapper classes are used to translate those styles to
coroutines. See `LocationProvider` in `ui.flows.map` for an example. This keeps the code in the
viewmodels consistent, clean, and easy to read.

#### View Layer

The view layer is implemented in Jetpack Compose, Android's new way of implementing user interfaces.
Jetpack compose offers a lot of advantages over the old xml inflated views:

- Promotes component reuse, it is much easier to make custom view components in compose than it was
  in the traditional View system
- Often faster to implement
- Completely declarative thus less error prone
- All kotlin, promoting consistency throughout the application and allowing for the use of all the
  features kotlin has on offer (coroutines, collection methods)
- Higher cohesion, rather than having your code divided between a fragment/activity and an xml file,
  all your UI code is in one composable function

#### Snackbars

The whole UI tree is wrapped in a composable called `SnackBarContainer` which is responsible for
displaying snackbars. As snackbars are used widely throughout the app to show error messages or
notify the user of an event, rather than implement a snackbar in each of the screens that use one,
it is implemented at the top level and lower level components send messages to it through
the `SnackBarMessageBus`. The `SnackBarMessageBus` is a simple class with a single `SharedFlow` to
which messages are sent, and the `SnackBarComnponent` observes this flow and displays the
corresponding text in a snackbar.

#### Navigation

Navigation is implemented with navigation component for compose, and the root graph can be found in
the app's root composable `SmallWorldApp`.

Every screen implements it's own navigation node builder function as an extension
on `NavGraphBuilder`. For example, `SignInScreen` has a corresponding `SignInNavigation` file which
implements the following:

```kotlin
private const val signInRoute = "sign_in"

fun NavController.navigateToSignIn() = navigate(signInRoute)

fun NavGraphBuilder.signInScreen(onBackClick: () -> Unit, onSignInSuccess: () -> Unit) =
    composable(signInRoute) {
        val viewModel: SignInViewModel = hiltViewModel()
        LaunchedEffect(viewModel) {
            viewModel.onSignInSuccess.collect { onSignInSuccess() }
        }
        SignInScreen(viewModel = viewModel, onBackClick = onBackClick)
    }
```

Every screen must implement the following (with the exception of the root screen which is never
navigated to):

- A `private const val` string literal used as the route.
- Ann extension function on `NavController` to allow it to be navigated to from other points on the
  graph, since the route is made private. This adds some safety to our navigation code and makes it
  easy to see the navigation options with code completion in our IDEs.
- An extension function on NavGraphBuilder that builds the screens navigation node and adds it to
  the `NavGraph`. The `NavController` is not passed down to this function, so it does not contain
  any calls to `.navigate()`. Instead, the navigation code is called in the root nav graph, and
  callback functions are passed down to this extension function. The name of the parameter for the
  callback corresponds to the *event that triggers the navigation* and not *the screen that is being
  navigated to*. For example, above, the parameter name for the callback is `onSignInSuccess` and
  not `onNavigateToHome`. This makes it so that the calling of the function in the `NavGraphBuilder`
  scope of the root nav graph shows the screen that is being added, the events which trigger
  navigation from that screen, and which screen is navigated to in response to those events, which
  makes it trivial to see the paths that a user can take through the application just by looking at
  the builder code:

```kotlin
landingScreen(
    onSignInButtonClick = navController::navigateToSignIn,
    onSignUpButtonClick = navController::navigateToSignUpGraph
)
signInScreen(
    onSignInSuccess = { navController.navigateToHome { popUpToLandingSCreenInclusive() } },
    onBackClick = { navController.popBackStack() }
)
```

Overall, this means all the navigation code is decoupled from the component itself and kept in one
place, the root navigation graph, and the extension functions in the `SomeScreenNavigation` files.

## Licence

Smallworld is licenced under the Creative Commons Attribution-NonCommercial-ShareAlike (CC BY-NC-SA)
4.0 International License. To view a copy of this licence,
visit https://creativecommons.org/licenses/by-nc-sa/4.0/x