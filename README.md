# Place Finder: Homeaway Android Technical Challenge

# General
The app is a seattle place search using Foursquare API. The app is made of 3 screens, one for viewing search results in a list, on a map and screen which displays the individual details of each search result. The project also uses many cool libraries like Lottie for animation, Retrofit & rxjava for handling API calls, GSON for parsing and google play services for 
maps.

## Technique
1. Created a search activity that parses JSON response from Foursquare API using Retrofit and rxjava libraries and displays in a recyclerview with pagination handled.
2. Search activity also has a Autocomplete TextView for handling search suggestions and udpates the recyclerview
3. Search activity also has a Floating Action Button, upon click event redirects to a map with list of markers with titles 
corresponding to the list displayed on search activity earlier.
4. Every click event on search list redirects to a Details activity with a map in collapsible toolbar displaying the location of place along with other information like rating, hours, contact details, menu, website links, address and whether or not the place is favorited
5. Used shared preferences to persist bookmarking feature
6. Network checks have been handled

## How to run the app
To run the app, download or clone the master branch of the repository. Open in Android Studio and run it.
In case, there is any error with fetching results from Foursquare API (very unlikely), please check Config file to update 
AUTH credentials: CLIENT_ID and CLIENT_SECRET

## Improvements 
1. Redirect to details activity from maps on marker click event and display the information of the specific place
