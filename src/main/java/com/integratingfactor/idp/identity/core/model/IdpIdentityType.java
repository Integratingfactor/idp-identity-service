package com.integratingfactor.idp.identity.core.model;

public enum IdpIdentityType {
    USERNAME("username"), EMAIL("email"), MOBILE("mobile"), FACEBOOK("facebook"), GOOGLE("google");

    private String value;

    IdpIdentityType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    static public IdpIdentityType as(String value) {
        for (IdpIdentityType field : IdpIdentityType.values()) {
            if (field.value.equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
