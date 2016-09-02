Watch face for Weather App
==========================

API KEY
-------
This app requires you to set your own API key from [openweathermap.org](openweathermap.org), and place it into gradle.properties.

## About WeatherForWatch.
This is an implementation for android watch face. For this, is necesary to set Google Api Client in the SynAdapter of the Sunshine's weather app. This information is then sent over the data layer to the watch, which mainly presents to the user the day's low/high temperature and time.
It receives at the same time the type of weather, so it can pull for a .png image, which is displayed in the watch; telling in this way if day is, rainy, cloudy, sunny, etc.

## Tehcnologies used:
- GCM.
- Watch Face Engine.
