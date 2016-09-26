[![API](https://img.shields.io/badge/API-22%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=22)
Watch face for Weather App
==========================

API Key
-------
This app requires you to set your own API key from [openweathermap.org](https://www.openweathermap.org), and place it into gradle.properties.

## About WeatherForWatch.
This is a watch face implementation for the Sunshine's weather app. For this, it is necesary to set Google Api Client in the SynAdapter of the Sunshine's code. This information is then sent over the data layer to the watch, which mainly presents to the user the day's low/high temperature, and time.
It receives also weather's condition, enabling to find a .png image, which is displayed in the watch; telling in this way if day is, rainy, cloudy, sunny, etc.

## Tehcnologies used:
- GCM.
- Watch Face Engine.


## License

[Apache 2.0](https://svn.apache.org/viewvc/httpd/httpd/trunk/LICENSE?view=markup)  
Also those added to the gradle configuration file.
