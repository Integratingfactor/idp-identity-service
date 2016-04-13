package com.integratingfactor.idp.identity.core.service;

import com.integratingfactor.idp.identity.core.model.IdpIdentityDetails;
import com.integratingfactor.idp.identity.core.model.IdpIdentityType;
import com.integratingfactor.idp.identity.core.model.IdpUserIdentities;
import com.integratingfactor.idp.identity.external.IdpUser;
import com.integratingfactor.idp.identity.external.IdpUserProfile;

/**
 * Service interface for IDP User Identity related services
 * 
 * @author gnulib
 *
 */
public interface IdpIdentityService {

    /**
     * get account id, if exists, for the specified identity id of the specified
     * identity type
     * 
     * @param type
     *            type of the identity
     * @param id
     *            identity identifier
     * @return account id, if exists, null otherwise
     */
    String getAccountIdByIdentity(IdpIdentityType type, String id);

    /**
     * register a user's identities in the system
     * 
     * @param user
     *            details about the user
     */
    void registerIdpUserIdentities(IdpUser user);

    /**
     * verify an identity based on verification code
     * 
     * @param code
     *            verification code provided by user
     * @param accountId
     *            account ID of the authenticated user that provided
     *            verification code
     * @return valid identity if verification success, null otherwise
     * @return
     */
    IdpIdentityDetails verifyIdentity(String code);

    /**
     * get user's identities based on account id
     * 
     * @param accountId
     *            account id of the user
     * @return identities of the user, if exists, null otherwise
     */
    IdpUserIdentities getIdpUserIdentities(String accountId);

    /**
     * initiate a password reset and notification for a specified using all
     * known identities
     * 
     * @param idpUserProfile
     *            user's account id
     */
    void resetPassword(IdpUserProfile idpUserProfile);

    /**
     * a fail safe clean up method to remove any out of sync identities when
     * account ID is not valid
     * 
     * @param accountId
     *            a user account id that needs to be cleaned up
     */
    void removeUser(String accountId);
}
