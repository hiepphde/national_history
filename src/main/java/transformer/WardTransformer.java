package transformer;

import model.Ward;
import util.OperationConstants;

import java.sql.PreparedStatement;

import static util.DatabaseUtils.*;

public class WardTransformer {

    public static boolean hasAfterData(Object record) {
        if (record instanceof Ward) {
            Ward ward = (Ward) record;
            return ward.getAfter() != null;
        }
        return false;
    }

    public static boolean isInsertOrReadOperation(Object record) {
        if (record instanceof Ward) {
            Ward ward = (Ward) record;
            String op = ward.getOp();
            return OperationConstants.INSERT_OP.equals(op) || OperationConstants.READ_OP.equals(op);
        }
        return false;
    }

    public static boolean isUpdateOperation(Object record) {
        if (record instanceof Ward) {
            Ward ward = (Ward) record;
            String op = ward.getOp();
            return OperationConstants.UPDATE_OP.equals(op);
        }
        return false;
    }

    public static boolean isDeleteOperation(Object record) {
        if (record instanceof Ward) {
            Ward ward = (Ward) record;
            String op = ward.getOp();
            return OperationConstants.DELETE_OP.equals(op);
        }
        return false;
    }

    public void setInsertParameters(PreparedStatement ps, Object record) throws Exception {
        if (record instanceof Ward) {
            Ward wardRecord = (Ward) record;
            Ward.After after = wardRecord.getAfter();

            if (after == null) {
                throw new IllegalArgumentException("Ward.after is null on update record: " + wardRecord);
            }

            safeSetString(ps, 1, after.getId());
            safeSetString(ps, 2, after.getWard());
            safeSetString(ps, 3, after.getCode());
            safeSetString(ps, 4, after.getEng_name());
            safeSetString(ps, 5, after.getLevel());
            safeSetString(ps, 6, after.getDistrict_id());
            safeSetString(ps, 7, after.getDeleted_flag());
            safeSetTimestamp(ps, 8, after.getValid_date());
        }
    }

    public void setUpdateParameters(PreparedStatement ps, Object record) throws Exception {
        if (record instanceof Ward) {
            Ward wardRecord = (Ward) record;
            Ward.After after = wardRecord.getAfter();

            if (after == null) {
                throw new IllegalArgumentException("Ward.after is null on update record: " + wardRecord);
            }

            safeSetString(ps, 8, after.getId());
            safeSetString(ps, 1, after.getWard());
            safeSetString(ps, 2, after.getCode());
            safeSetString(ps, 3, after.getEng_name());
            safeSetString(ps, 4, after.getLevel());
            safeSetString(ps, 5, after.getDistrict_id());
            safeSetString(ps, 6, after.getDeleted_flag());
            safeSetTimestamp(ps, 7, after.getValid_date());
        }
    }

    public void setDeleteParameters(PreparedStatement ps, Object record) throws Exception {
        if (record instanceof Ward) {
            Ward wardRecord = (Ward) record;
            Ward.Before before = wardRecord.getBefore();

            if (before == null) {
                throw new IllegalArgumentException("Ward.before is null on update record: " + wardRecord);
            }

            safeSetString(ps, 1, before.getId());
        }
    }
}
