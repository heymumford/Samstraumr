package org.samstraumr.core;

public class OperationRecord {
    private final String operationName;
    private final boolean success;

    public OperationRecord(String operationName, boolean success) {
        this.operationName = operationName;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOperationName() {
        return operationName;
    }
}
