package com.integratingfactor.idp.identity.core.model;

import java.io.Serializable;

public class IdpIdentityVerification implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2845096278332486762L;

    private String code;

    private IdpIdentityDetails identity;

    private IdpIdentityStatus status;

    private Long expiry;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public IdpIdentityStatus getStatus() {
        return status;
    }

    public void setStatus(IdpIdentityStatus status) {
        this.status = status;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public IdpIdentityDetails getIdentity() {
        return identity;
    }

    public void setIdentity(IdpIdentityDetails identity) {
        this.identity = identity;
    }

}
