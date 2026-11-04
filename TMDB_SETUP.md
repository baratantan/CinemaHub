# TMDB catalog and approved playback source configuration

CinemaHub loads popular movie metadata and searches from [The Movie Database (TMDB)](https://www.themoviedb.org/). TMDB data is cached in the existing Room movie table.

## TMDB API key

1. Request an API key from your TMDB account's API settings.
2. Add it to `~/.gradle/gradle.properties` or CI Gradle secrets:

   ```properties
   TMDB_API_KEY=your_tmdb_v3_api_key
   ```

3. Sync Gradle and build/run the app. Do not commit the key to the repository. Follow TMDB's branding and attribution requirements before distribution.

## Configure approved Firebase playback sources

1. Configure Firebase using `FIREBASE_API_KEY`, `FIREBASE_APP_ID`, and `FIREBASE_PROJECT_ID` in local Gradle properties or CI secrets.
2. Enable **Authentication → Anonymous** and **Cloud Firestore**, and deploy the repository's `firestore.rules`.
3. Populate the Firestore collection named `approvedStreams` (or set `FIREBASE_STREAMS_COLLECTION` to another collection name in Gradle properties). Use each catalog item's stable numeric ID as the document ID. A document has this shape:

   ```json
   {
     "streams": [
       {
         "url": "https://licensed-provider.example/path/to/direct-video.m3u8",
         "quality": "Auto",
         "serverName": "Licensed provider"
       }
     ]
   }
   ```

4. The Android app reads these documents after anonymous Firebase sign-in. The deployed rules allow authenticated reads only for `approvedStreams` and prohibit client writes. Manage entries through a trusted Firebase console or server-side process; never grant client write access to playback URLs.
5. The app accepts HTTPS URLs only and plays them directly with Media3/ExoPlayer. Use only direct streams that you own or are authorized to distribute.

The player uses Media3 rather than an embedded WebView, so web pages, scripts, and their pop-up windows are not loaded as part of playback. No WebView implementation exists in the player to configure; if an approved-provider embed is added later, it should use a dedicated WebView with multiple windows disabled and navigation restricted to an explicit host allowlist.
