package gui;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.util.Collections;

public final class Theme {
    private Theme() {
    }

    // Uses the system look (Windows) and makes every font a bit bigger, easier to read at the counter
    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // keeps the default look if the system one is not available
        }
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        for (Object key : Collections.list(defaults.keys())) {
            Object value = defaults.get(key);
            if (value instanceof Font font) {
                UIManager.put(key, new FontUIResource(font.deriveFont(font.getSize2D() + 3f)));
            }
        }
    }
}
