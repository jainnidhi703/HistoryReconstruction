import java.util.prefs.Preferences;

/**
 * Class used to store and retrieve preferences
 */
public class Settings {

    public static final String STORE_DIR = "store_dir";
    public static final String EXPORT_FILE = "export_file";

    static Preferences prefs = Preferences.userRoot().node(Globals.PREFERENCES_NODE);

    /**
     * @return Index Store directory
     */
    public static String getStoreDir() {
        return prefs.get(STORE_DIR, "");
    }

    /**
     * Sets Index Store Directory
     * @param dir Index Store Dir
     */
    public static void setStoreDir(String dir) {
        prefs.put(STORE_DIR, dir);
    }

    /**
     * Sets Export file path
     * @param path export file path
     */
    public static void setExportFile(String path) {
        prefs.put(EXPORT_FILE, path);
    }

    /**
     * @return returns export file path
     */
    public static String getExportFile() {
        return prefs.get(EXPORT_FILE, "");
    }

    /**
     * Generic store method
     * @param key preference_name to store
     * @param value preference_value to store
     */
    public static void store(String key, String value) {
        prefs.put(key, value);
    }

    /**
     * Generic get method
     * @param key preference_name to get
     * @return preference_value string
     */
    public static String get(String key) {
        return prefs.get(key, "");
    }
}
