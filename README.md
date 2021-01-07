# Trash Locator

**Trash Locator** is an app that uses Maps SDK find nearest trash around you from geojson files provided by the opendata from your city.

Sadly the only opendata *trash datasets* files I've found so far are from 3 cities: 
* **Santa Cruz de Tenerife**
* **Cáceres**
* **Washington DC**

#### Features 
* Search nearest trash cans datasets from your actual location (50m distance)
* Display the trash cans of a certain location
* Show stats of how many trash cans has a certain location
* Change the UI to Light or Dark-Gray theme
* Let you choice what language do you prefer
* Change the UI to light or dark theme

## Things I have taken care of
* Restore always the location when changing theme or language
* If user search 6 locations, then every time he press "Back button" will return to the last location
* Listen to GPS enable/disable
* And design, that's always a must!

# Have you built tests for this application?
* Yes I did, indeed I wanted to make sure everything worked as expected and now application is stable. I've coded UI tests, Integration Tests and Unit tests!

# Do you think this application is useful for any person?
* No, I don't think so, we see a trash can on every corner, doesn't make sense for the user to install this app. Anyways that I wanted to learn about Google Maps and learn testing and this seemed a funny application and indeed I enjoyed coding it 😄!

# How can you improve this app?
* Definitely a downside of this app is that the files are on client side and there's no API for such information, but it could be improved by just hosting files on my own server and request the data✌️! On that case I wouldn't have to make user update an app just because I found some dataset file, this would definitely improve A LOT!

* Change to other type of information less usual to find on every corner!!
