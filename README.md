# Dope Player
Dope Player is a weekly curated playlist service with a record based interface for Spotify Premium users.

![Dope Player Gif](https://media.giphy.com/media/3og0IKrs6fJEStvnBm/giphy.gif)

## Installation

To begin working with Dope Player, just open the project in [Android Studio](https://developer.android.com/studio/index.html). You will also need to register set up a spotify developer account as denoted in the [Spotify Android SDK Tutorial](https://developer.spotify.com/technologies/spotify-android-sdk/tutorial/). To stream Spotify audio on Android, you need a Spotify Premium account. This is true for testing and development as well.

You will also need to create a java class called `SpotifyConstants.java` in the folder `/app/src/main/java/com/example/jamesb/dopeplayer/` with information from your developer account follwing this format
```java
package com.example.jamesb.dopeplayer;

public class SpotifyConstants {
    public static final String cID = "yourcID";
    public static final String cSecret = "yourcSecret";
    public static final String cRedirectURI = "yourcRedirectURI";
}
```

## Some Implementation Details
The record slider is based on milosmns's [Circular Slider](https://github.com/milosmns/circular-slider-android). 
When touched, it checks to see if it was touched in the square highlighted below. If so, it toggles play/pause. 

![Touch Area](http://i.imgur.com/KuOOv1a.png)

If it is touched in any other part of the record, it will begin a click and drag interaction in which the user can seek forward or backward in the song by spinning the record around. This is managed by calculating the number of degrees that the user's finger has traveled around the center of the record. When the finger is lifted, playback will resume. If the user reaches the beginning or end of the song in this way, the app will seek forward or backward in the playlist. 
