# HCL Notes Calendar Sync
This Java app can be used to sync your HCL Notes calendar events to your Google calendar.

## Run build on your local Windows machine
`java -jar notes-calendar-sync.jar <path-to-your-config-file>`

An example of the config file can be found [here](example.config.properties).

Make sure that you use a 32 bit Java version and add the Notes installation directory to your PATH environment variable.


## macOS
The following library must be set to be able to use the Notes API.
`DYLD_LIBRARY_PATH=/Applications/HCL Notes.app/Contents/MacOS`
