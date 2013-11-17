

	HOW TO RUN EXAMPLES ?


FIRST STEP
----------

To run the examples, you need at least Java 1.5 installed.

REMARK
Java 1.5 is requiered to run the examples (examples are written with Java 1.5).
BUT NativeFmodEx is FULLY compatible with Java 1.4.


[WIN32]
You can now run any examples using the appropriate batch files (*.bat).

[LINUX]
The script to run Linux examples is in runExamples.sh.
To use it, open a terminal and go to the folder that contains examples, then write this :
  sh runFmodEx.sh <example>
or
  sh runFmodDesigner.sh <example>

ie
  sh runFmodEx.sh PlaySound
  sh runFmodDesigner.sh SimpleEvent


TROUBLES
--------
If Java is not recognized, add it in your PATH or replace java in the script by the full path of java (ie 'c:/java/bin/java' instead of 'java').


PARTICULAR EXAMPLES
-------------------

  [CDPlayer]
You need to modify the bat file to specify your audio cd drive (my drive is f or /dev/cdrom1) :
 java ... Music.NativeFmodEx.Examples.CDPlayer f
 java ... Music.NativeFmodEx.Examples.CDPlayer /dev/cdrom1

  [DspPluginViewer]
You will probably remark that config dialog of plugins are drawn over all the other components.
The reason is that config dialog is a Canvas that is to say a 'heavy' component (awt components). All the other components
are 'light' components (swing components).
The reason of this can be found by reading the article "Mixing heavy and light components" at java.sun.com :
    http://java.sun.com/products/jfc/tsc/reference/techart/index.html
To solve this problem, do not use swing components with Canvas !
I've remark this problem after writting all the GUI with swing components (don't want to re-write all, but if someone have som time to 'lose' ...)

  [NetStream]
An internet connexion is requiered. You need to specify the location of the music to play in the bat file like this :
  java ... Music.NativeFmodEx.Examples.NetStream http://www.esstin.uhp-nancy.fr/~jouvi2/downloads/NativeFmod/jules.mp3

  [PitchDetection]
You need to play a music somewhere like for Recording example.

  [Recording]
To record something, you need to play a music somewhere (like a music player, a game ...).
If you have some troubles under Windows (record nothing), open Recording Panel of Windows and select wave.

  [UsePlugins]
Copy output_mp3.dll and lame_enc.dll into Examples folder.

  [Geometry]
This example uses OpenGL.
I've choose to port this example using JOGL (https://jogl.dev.java.net/). So, you need to install to use this example.

  [RealtimeTweaking]
Open RealtimeTweaking, play an event (for example highpass). Then open FMOD Designer and connect to RealtimeTweaking by :
Audition->Manage Connections... Add and enter 127.0.0.1 in IP Address and Connect.
In Events tab, select the event playing in RealtimeTweaking. Finally, modify the volume.
