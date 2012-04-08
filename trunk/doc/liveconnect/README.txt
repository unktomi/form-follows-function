LiveConnect Support in F3
------------------------------------

New functionality in the Java Plug-In in Java SE 6 Update 10, as well
as in the F3 Runtime, allows the JavaScript on a web page to
interact with an applet written in F3. JavaScript can get
and set public variables, call public functions (including
script-level functions), and get and set sequence elements.

Full documentation of the functionality needs to be written, and part
of the forthcoming new LiveConnect specification which will be linked
from http://jdk6.dev.java.net/plugin2/ will discuss this topic. The
following is an overview:

A F3 Stage can be embedded in an applet using the
org.f3.runtime.adapter.Applet class. The "MainF3Class"
applet parameter indicates the F3 class to be run and which will
provide the Stage instance that is embedded in the applet. For
example,

  Test.f3:

    public var color = Color.YELLOW;
    public function setColor(red: Number,
                             green: Number,
                             blue: Number) : Void {
        color = Color { red: red, green: green, blue: blue };
    }

    Stage {
        scene: Scene {
            fill: Color.DARKGRAY
            content: [
                Circle {
                    centerX : 125
                    centerY : 125
                    radius: 100
                    fill: bind color
                }
            ]
        }
    }

  Test.html:

    <applet id="app" archive="..." code="org.f3.runtime.adapter.Applet" ...>
        <param name="MainF3Class" value="Test">
    </applet>

This applet is referred to via the name "app" in the example above and
can be referenced from JavaScript by this name or by calling
document.getElementById("app").

When using the new Java Plug-In in Java SE 6 update 10, the JavaScript
on the web page can call in to F3. Some examples of the
supported functionality:

  - Accessing public script-level variables and calling public
    script-level functions via a synthetic "script" field which is
    attached to the F3 applet object.

      app.script.color = app.Packages.f3.scene.paint.Color.RED;
      app.script.setColor(1.0, 0.0, 0.0);

  - Accessing public variables and calling public functions of F3
    objects.

  - Fetching elements of F3 sequences. F3 sequences returned
    to JavaScript look like JavaScript arrays, and support the
    "length" field and fetching using array index ("[0]") syntax.
    (Setting elements of F3 sequences is not currently supported.)

  - Descending in to the F3 scene graph from JavaScript, from the
    applet to the stage, scene and further.

      app.stage.scene.content[0].radius = 50;

  - Automatic data type conversions such as converting JavaScript
    arrays to F3 sequences. One example of the
    functionality this enables is passing animation data from the web
    page into the F3 program.

Examples of this functionality will be included in the F3 1.0 SDK.

Implementation note: the mechanisms in the new Java Plug-In enabling
this JavaScript / F3 bridge are fully general, and allow
any implementor of a language hosted on the JVM to use JavaScript on a
web page to interact with applets written in that language. Full
documentation of this inter-language LiveConnect bridge is forthcoming.
