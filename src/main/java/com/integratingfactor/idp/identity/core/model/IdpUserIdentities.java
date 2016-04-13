package com.integratingfactor.idp.identity.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IdpUserIdentities {

    private String accountId;

    private Map<IdpIdentityType, Set<String>> identities;

    public IdpUserIdentities() {
        identities = new HashMap<IdpIdentityType, Set<String>>();
    }

    public boolean addIdentity(IdpIdentityType type, String id) {
        Set<String> set = identities.get(type);
        if (set == null) {
            set = new HashSet<String>();
            identities.put(type, set);
        }
        return set.add(id);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Map<IdpIdentityType, Set<String>> getIdentities() {
        return identities;
    }

    public void setIdentities(Map<IdpIdentityType, Set<String>> identities) {
        this.identities = identities;
    }

}
