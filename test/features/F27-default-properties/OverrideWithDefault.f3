/* Feature test #27 -- Default override test
 * Tests to make sure that defaults can also be created by overriding non-default variables
 *
 * @test
 * @run
 */

class Text {
  public var content:String;
}

class Scene {
  public var content:Text[];
}

class Stage {
  public var scene:Scene;
}

class XText extends Text {
  override default var content;
}

class XScene extends Scene {
  override default var content;
}

class XStage extends Stage {
  override default var scene;
}

XStage {
  XScene {
    XText {
      "Hello World"
    }
  }
}
