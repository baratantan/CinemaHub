# TMDB catalog configuration

CinemaHub loads popular movies and movie searches from [The Movie Database (TMDB)](https://www.themoviedb.org/). TMDB data is cached in the existing Room movie table so previously loaded movies remain available offline.

## Configure a TMDB API key

1. Create a TMDB account and request an API key from the account's API settings.
2. Add the key to your local Gradle properties file (`~/.gradle/gradle.properties`) or to your CI Gradle secrets:

   ```properties
   TMDB_API_KEY=your_tmdb_v3_api_key
   ```

3. Sync Gradle and build/run the app. Do not commit the key to the repository. TMDB API keys are client credentials embedded in an Android app, so restrict and rotate them through your TMDB account if needed.

Without a configured key, the app displays a setup/network message in the catalog instead of silently appearing blank.

## Attribution

This product uses the TMDB API but is not endorsed or certified by TMDB. Follow TMDB's branding and attribution requirements before distributing the app.
