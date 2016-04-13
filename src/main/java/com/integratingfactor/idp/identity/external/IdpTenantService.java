package com.integratingfactor.idp.identity.external;

import com.integratingfactor.idp.identity.core.model.IdpIdentityDetails;

public interface IdpTenantService {

    void resolvePendingApprovals(IdpIdentityDetails identity);

}
