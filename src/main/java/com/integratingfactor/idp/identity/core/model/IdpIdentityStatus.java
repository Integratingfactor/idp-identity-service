package com.integratingfactor.idp.identity.core.model;

public enum IdpIdentityStatus {
    RESET("reset"), AGGREGATION("aggregation"), VERIFIED("verified"), UNCLAIMED("unclaimed");

    private String value;

    IdpIdentityStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    static public IdpIdentityStatus as(String value) {
        for (IdpIdentityStatus field : IdpIdentityStatus.values()) {
            if (field.value.equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
