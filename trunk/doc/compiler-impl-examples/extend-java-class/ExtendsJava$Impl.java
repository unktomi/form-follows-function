import javax.swing.*;

import org.f3.runtime.annotation.OverridesJava;

/**
 * ExtendsJava$Impl
 *
 * @author Brian Goetz
 */

interface But$Intf {
    public String getText();
    public String super$getText();
}

interface SubBut$Intf extends But$Intf {
}

class But extends JButton implements But$Intf {
    // Usual initialization code

    @OverridesJava
    public String getText() {
        return getText(this);
    }

    public static String getText(But$Intf receiver) {
        return receiver.super$getText();
    }

    public String super$getText() {
        return super.getText();
    }
}

class SubBut extends JButton implements But$Intf {
    // Usual initialization code

    @OverridesJava
    public String getText() {
        return But.getText(this);
    }

    public String super$getText() {
        return super.getText();
    }
}
