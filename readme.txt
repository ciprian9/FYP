


My project is based on activity recognition starting different services on an android device.
The project can be found in my git repo at  : https://github.com/ciprian9/FYP/tree/master/MyApplication4

My Application4

Some files have been generated by the system and modified:

AndroidManifest.xml - Created by the system and modified by me to add required permissions for certain actions
                      new activities, services and receivers.

MainActivity - Class originally created by the idea to launch application, modified to start certain services and
	       activities.

Activity_Main.xml -Layout file for the MainActivity Code originally created by the IDE but modified to match a layout
                   of my choice


Files created by me :

Activity2 - test file for listing audio files and incorporation a media player, will be removed
            the code I still require will be moved.

ActivityRecognizedService - Class inheriting from IntentService to detect which activity is taking place
                            and call the relevant classes based on the activity

Constants - Interface created to hold constants for the program.

DataHandler - Class created to interact with the SQLite database, contains methods that create tables, insert, select
              and delete data from the database tables.

My_Delete_Playlist_Adapter - Class used to populate the screen with a secondary layout to combine components with a
                             listview .

Playlist_View - class to display contents of the playlist database and uses the above adapter to add delete buttons to 
                each row of the listview

Playlists - Class used to display all audio files present on the device and allows the saving of said files to the
            playlist database

Policy - meant to act as a superclass for policy services (issues were found with this idea so this needs to be rethough)

SmsReceiver - BroadcastReceiver inherited class used to trigger an action when the device receives a SMS message

WalkingOption - Class to reach to changes on the settings screen and save the user preferences to the settings table

WalkingPolicy - Service to run the different actions while the user is walking needs to be separated into separated services 

Rest of the layout files are used to create the screens and provide the components that the classes interact with


