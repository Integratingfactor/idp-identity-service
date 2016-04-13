package com.integratingfactor.idp.identity.core.model;

import java.io.Serializable;

public class IdpIdentityDetails implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -1496177376093019756L;

    private IdpIdentityType type;
    
    private String id;
    
    private String accountId;

    public IdpIdentityType getType() {
        return type;
    }

    public void setType(IdpIdentityType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "IdpIdentityDetails [type=" + type + ", id=" + id + "]";
    }

}
