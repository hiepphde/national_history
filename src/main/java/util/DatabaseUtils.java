package util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class DatabaseUtils {
    public static void safeSetString (PreparedStatement ps, int index, String value) throws SQLException {
        if (value != null) {
            ps.setString(index, value);
        } else {
            ps.setNull(index, Types.VARCHAR);
        }
    }

    public static void safeSetInt (PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null) {
            ps.setInt(index, value);
        } else  {
            ps.setNull(index, Types.INTEGER);
        }
    }

    public static void safeSetBool (PreparedStatement ps, int index, Boolean value) throws SQLException {
        if (ps == null) throw new IllegalArgumentException("PreparedStatement is null");
        if (value != null) {
            ps.setInt(index, value ? 1 : 0);
        } else  {
            ps.setNull(index, Types.INTEGER);
        }
    }

    public static void safeSetTimestamp(PreparedStatement ps, int index, Timestamp value) throws SQLException {
        if (value != null) {
            ps.setTimestamp(index, value);
        } else {
            ps.setNull(index, Types.TIMESTAMP);
        }
    }
}
