package sksa.aa.tweaker.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sksa.aa.tweaker.MainActivity;

/**
 * Knows both layouts of Google Play Services' phenotype.db:
 * <ul>
 *     <li>LEGACY: overrides in {@code FlagOverrides}, defaults in {@code Flags}.</li>
 *     <li>NEW (GMS 26.x+): overrides in {@code flag_overrides}, packages referenced through {@code config_packages}.</li>
 * </ul>
 * The tweaks are written against the legacy layout. On the new layout {@link #rewriteCommand(String)}
 * translates them, so every existing code path keeps working on both.
 */
public final class PhenotypeDb {

    public static final String DB_DIR = "/data/data/com.google.android.gms/databases";
    public static final String DB = DB_DIR + "/phenotype.db";

    public enum Schema { UNKNOWN, LEGACY, NEW }

    private static volatile Schema schema = Schema.UNKNOWN;

    /** Every package the tweaks write to. Reverts never touch overrides of other packages. */
    private static final String OUR_PACKAGES = "'com.google.android.gms.car', 'com.google.android.projection.gearhead'";

    /** Column list of the INSERTs we generate. Also used to recognise our own triggers. */
    private static final String NEW_INSERT_PREFIX =
            "INSERT OR REPLACE INTO flag_overrides (config_package_id, config_package_name, account_id, active, name, value, type, source) VALUES ";

    private static final int TYPE_LONG = 1;
    private static final int TYPE_BOOL = 2;
    private static final int TYPE_DOUBLE = 3;
    private static final int TYPE_STRING = 4;

    /** {@code <dir>/sqlite3 [-flags] /data/data/com.google.android.gms/databases/phenotype.db '<sql>'} */
    private static final Pattern SQLITE_COMMAND = Pattern.compile(
            "^\\s*(\\S+/sqlite3)((?:\\s+-\\w+)*)\\s+" + Pattern.quote(DB) + "\\s+'([^']*)'\\s*$", Pattern.DOTALL);

    private static final Pattern LEGACY_INSERT = Pattern.compile(
            "INSERT\\s+OR\\s+REPLACE\\s+INTO\\s+FlagOverrides\\s*" +
                    "\\(\\s*packageName\\s*,\\s*flagType\\s*,\\s*name\\s*,\\s*user\\s*,\\s*(\\w+)\\s*,\\s*committed\\s*\\)\\s*" +
                    "VALUES\\s*\\(\\s*\"([^\"]*)\"\\s*,\\s*-?\\d+\\s*,\\s*\"([^\"]*)\"\\s*,\\s*\"[^\"]*\"\\s*,\\s*" +
                    "(\"[^\"]*\"|[^,\\s)]+)\\s*,\\s*-?\\d+\\s*\\)\\s*;",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern DELETE_FROM_FLAGS = Pattern.compile(
            "DELETE\\s+FROM\\s+Flags\\b[^;]*;");

    private static final Pattern SELECT_FROM_FLAGS = Pattern.compile(
            "SELECT\\s+DISTINCT\\s+\\w+\\s+FROM\\s+Flags\\s+WHERE\\s+name\\s*=\\s*\"([^\"]*)\"\\s*;?");

    private static final Pattern SELECT_ALL_OVERRIDES = Pattern.compile(
            "SELECT\\s+\\*\\s+FROM\\s+FlagOverrides\\s*;?");

    private static final Pattern DELETE_ALL_OVERRIDES = Pattern.compile(
            "DELETE\\s+FROM\\s+FlagOverrides\\s*;");

    private static final Pattern LEGACY_TABLE_DDL = Pattern.compile(
            "(DROP|CREATE)\\s+TABLE\\s+FlagOverrides\\b[^;]*;");

    private static final Pattern TRIGGER_TARGET = Pattern.compile(
            "AFTER\\s+DELETE\\s+ON\\s+FlagOverrides\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern TRIGGER_TABLE_FILTER = Pattern.compile(
            "tbl_name\\s*=\\s*\"FlagOverrides\"");

    private PhenotypeDb() {
    }

    // ---------------------------------------------------------------------------------------------
    // Schema detection
    // ---------------------------------------------------------------------------------------------

    public static Schema getSchema(String sqlite3) {
        if (schema == Schema.UNKNOWN) {
            String tables = MainActivity.runSuRaw(sqlite3 + " -batch " + DB + " " + shellQuote(
                    "SELECT name FROM sqlite_master WHERE type = 'table' AND name IN ('FlagOverrides', 'flag_overrides');"
            )).getInputStreamLog();
            // The new layout wins: a leftover FlagOverrides table next to it is ignored by Play Services.
            if (tables.contains("flag_overrides")) {
                schema = Schema.NEW;
            } else if (tables.contains("FlagOverrides")) {
                schema = Schema.LEGACY;
            }
        }
        return schema;
    }

    public static boolean isNewSchema(String appDirectory) {
        return getSchema(sqlite3Path(appDirectory)) == Schema.NEW;
    }

    public static String sqlite3Path(String appDirectory) {
        return appDirectory + "/sqlite3";
    }

    // ---------------------------------------------------------------------------------------------
    // Translation of legacy commands
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns {@code cmd} unchanged unless it is a sqlite3 call on phenotype.db and the device uses the new
     * layout; in that case the SQL is translated with {@link #translateLegacySql(String)}.
     */
    public static String rewriteCommand(String cmd) {
        Matcher m = SQLITE_COMMAND.matcher(cmd);
        if (!m.matches() || getSchema(m.group(1)) != Schema.NEW) {
            return cmd;
        }
        return command(m.group(1), translateLegacySql(m.group(3)));
    }

    /** Builds a sqlite3 call on phenotype.db that waits for Play Services' locks instead of failing. */
    public static String command(String sqlite3, String sql) {
        return sqlite3 + " -batch -cmd " + shellQuote(".timeout 5000") + " " + DB + " " + shellQuote(sql);
    }

    public static String translateLegacySql(String sql) {
        Matcher m = LEGACY_INSERT.matcher(sql);
        StringBuffer out = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(out, Matcher.quoteReplacement(translateInsert(m.group(1), m.group(2), m.group(3), m.group(4))));
        }
        m.appendTail(out);
        String result = out.toString();

        // Flags no longer exists. One failing statement would abort everything after it.
        result = DELETE_FROM_FLAGS.matcher(result).replaceAll("");
        result = replace(SELECT_FROM_FLAGS, result,
                "SELECT value FROM flag_overrides WHERE account_id = 0 AND name = %s LIMIT 1;");
        // flag_overrides also holds Play Services' own overrides: never wipe the table.
        result = DELETE_ALL_OVERRIDES.matcher(result).replaceAll("");
        result = LEGACY_TABLE_DDL.matcher(result).replaceAll("");
        result = SELECT_ALL_OVERRIDES.matcher(result).replaceAll(Matcher.quoteReplacement(
                "SELECT p.name, o.config_package_name, o.name, o.value, o.type FROM flag_overrides o " +
                        "LEFT JOIN config_packages p ON p.config_package_id = o.config_package_id " +
                        "WHERE p.name IN (" + OUR_PACKAGES + ") OR o.config_package_name IN (" + OUR_PACKAGES + ");"));
        result = TRIGGER_TARGET.matcher(result).replaceAll("AFTER DELETE ON flag_overrides");
        result = TRIGGER_TABLE_FILTER.matcher(result).replaceAll("tbl_name = 'flag_overrides'");
        return result;
    }

    private static String replace(Pattern pattern, String input, String format) {
        Matcher m = pattern.matcher(input);
        StringBuffer out = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(out, Matcher.quoteReplacement(String.format(format, sqlString(m.group(1)))));
        }
        m.appendTail(out);
        return out.toString();
    }

    private static String translateInsert(String column, String pkg, String name, String rawValue) {
        boolean quoted = rawValue.startsWith("\"");
        String value = quoted ? rawValue.substring(1, rawValue.length() - 1) : rawValue;

        int type;
        String sqlValue;
        switch (column.toLowerCase()) {
            case "intval":
                type = TYPE_LONG;
                sqlValue = isNumber(value) ? value : sqlString(value);
                break;
            case "boolval":
                type = TYPE_BOOL;
                sqlValue = isNumber(value) ? value : sqlString(value);
                break;
            case "floatval":
                type = TYPE_DOUBLE;
                sqlValue = !isNumber(value) ? sqlString(value)
                        : value.matches(".*[.eE].*") ? value : value + ".0";
                break;
            case "stringval":
                type = TYPE_STRING;
                sqlValue = sqlString(value);
                break;
            default:
                // extensionVal (serialized protos): the new layout's encoding is unknown, a wrong guess could
                // break Android Auto, so leave the flag alone.
                return "/* skipped " + column + " override " + name.replace("*/", "") + " */";
        }

        String packageId = "(SELECT config_package_id FROM config_packages WHERE name = " + sqlString(pkg) + ")";
        // Exactly one of config_package_id / config_package_name must be set (CHECK constraint). Fall back
        // to the name when Play Services has not registered the package, so a trigger can never fail.
        return NEW_INSERT_PREFIX + "(" + packageId + ", CASE WHEN " + packageId + " IS NULL THEN " + sqlString(pkg) + " END, 0, 1, "
                + sqlString(name) + ", " + sqlValue + ", " + type + ", 0);";
    }

    private static boolean isNumber(String value) {
        return value.matches("-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?");
    }

    // ---------------------------------------------------------------------------------------------
    // New-layout statements
    // ---------------------------------------------------------------------------------------------

    /**
     * Drops the given tweak triggers and deletes only the overrides they wrote. A flag belongs to a trigger
     * when its name appears quoted in the trigger's SQL, which is how {@link #translateInsert} writes it.
     */
    public static String revertSql(List<String> triggers) {
        if (triggers.isEmpty()) {
            return "";
        }
        StringBuilder names = new StringBuilder();
        StringBuilder drops = new StringBuilder();
        for (String trigger : triggers) {
            names.append(names.length() == 0 ? "" : ", ").append(sqlString(trigger));
            drops.append("DROP TRIGGER IF EXISTS \"").append(trigger.replace("\"", "\"\"")).append("\";\n");
        }
        return "CREATE TEMP TABLE aat_revert AS SELECT sql FROM sqlite_master WHERE type = 'trigger' AND name IN (" + names + ");\n"
                + drops
                + "DELETE FROM flag_overrides WHERE account_id = 0"
                + " AND EXISTS (SELECT 1 FROM aat_revert r WHERE instr(r.sql, '''' || flag_overrides.name || '''') > 0)"
                + " AND (config_package_id IN (SELECT config_package_id FROM config_packages WHERE name IN (" + OUR_PACKAGES + "))"
                + " OR config_package_name IN (" + OUR_PACKAGES + "));\n"
                + "DROP TABLE aat_revert;";
    }

    public static String revertCommand(String appDirectory, String trigger) {
        List<String> triggers = new ArrayList<>();
        triggers.add(trigger);
        return command(sqlite3Path(appDirectory), revertSql(triggers));
    }

    /** Names of the triggers this app created on flag_overrides. */
    public static List<String> ourTriggers(String appDirectory) {
        String names = MainActivity.runSuRaw(command(sqlite3Path(appDirectory),
                "SELECT name FROM sqlite_master WHERE type = 'trigger' AND tbl_name = 'flag_overrides'"
                        + " AND instr(sql, " + sqlString(NEW_INSERT_PREFIX.trim()) + ") > 0;"
        )).getInputStreamLog();
        List<String> result = new ArrayList<>();
        for (String name : names.split("\n")) {
            if (!name.trim().isEmpty()) {
                result.add(name.trim());
            }
        }
        return result;
    }

    /**
     * sqlite3 runs as root; with WAL it also creates phenotype.db-wal and -shm. Give every file back to
     * Play Services (user AND group) and restore the SELinux labels, or Play Services cannot write its db.
     */
    public static String restoreOwnershipCommand() {
        return "OWNER=$(stat -c %u:%g " + DB_DIR + "); chown $OWNER " + DB + "*; restorecon -R " + DB_DIR;
    }

    // ---------------------------------------------------------------------------------------------
    // Quoting
    // ---------------------------------------------------------------------------------------------

    public static String sqlString(String value) {
        return "'" + value.replace("'", "''") + "'";
    }

    public static String shellQuote(String value) {
        return "'" + value.replace("'", "'\\''") + "'";
    }
}
