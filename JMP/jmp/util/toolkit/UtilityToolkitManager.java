package jmp.util.toolkit;

import java.util.ArrayList;
import java.util.List;

import jlib.util.IUtilityToolkit;

public class UtilityToolkitManager {

    public static final String DEFAULT_UTIL_TOOLKIT_NAME = DefaultUtilityToolkit.class.getSimpleName();

    private static DefaultUtilityToolkit DefaultUtilToolkit = new DefaultUtilityToolkit();
    private List<IUtilityToolkit> lst = null;

    private static UtilityToolkitManager instance = new UtilityToolkitManager();

    public static UtilityToolkitManager getInstance() {
        return instance;
    }

    private UtilityToolkitManager() {
        lst = new ArrayList<IUtilityToolkit>();
        addUtilityToolkit(DefaultUtilToolkit);
    }

    public void addUtilityToolkit(IUtilityToolkit kit) {
        lst.add(kit);
    }

    public IUtilityToolkit getUtilityToolkit(String className) {
        for (IUtilityToolkit kit : lst) {
            if (kit.getClass().getSimpleName().equals(className)) {
                return kit;
            }
        }
        return DefaultUtilToolkit;
    }

}
