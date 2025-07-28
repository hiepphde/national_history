package util;

public final class SqlUtils {
    public static class SqlWard {
        public static final String INSERT_SQL =
                "INSERT INTO WARDS (ID, WARD, CODE, ENG_NAME, LEVEL_NAME, DISTRICT_ID, DELETED_FLAG, VALID_DATE) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        public static final String UPDATE_SQL =
                "UPDATE WARDS SET WARD = ?, CODE = ?, ENG_NAME = ?, LEVEL_NAME = ?, DISTRICT_ID = ?, DELETED_FLAG = ?, " +
                        "VALID_DATE = ? WHERE ID = ?";

        public static final String DELETE_SQL =
                "DELETE FROM WARDS WHERE ID = ?";
    }
}
