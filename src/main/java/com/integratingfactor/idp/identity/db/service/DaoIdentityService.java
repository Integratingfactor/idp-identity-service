package com.integratingfactor.idp.identity.db.service;

import com.integratingfactor.idp.common.db.exceptions.DbException;
import com.integratingfactor.idp.identity.core.model.IdpIdentityDetails;
import com.integratingfactor.idp.identity.core.model.IdpIdentityType;
import com.integratingfactor.idp.identity.core.model.IdpIdentityVerification;
import com.integratingfactor.idp.identity.core.model.IdpUserIdentities;

/**
 * DAO interface for IDP Identity data models
 * 
 * @author gnulib
 *
 */
public interface DaoIdentityService {
    /**
     * write identity details for a new identity, once the identity has been
     * claimed and verified
     * 
     * @param identity
     *            details of the identity to write
     */
    void writeIdentity(IdpIdentityDetails identity) throws DbException;

    /**
     * read an Identity details based on type and id
     * 
     * @param type
     *            type of the identity
     * @param id
     *            identifier for identity
     * @return identity details
     */
    IdpIdentityDetails readIdentity(IdpIdentityType type, String id) throws DbException;

    /**
     * write verification details as following:
     * <ul>
     * <li>For an unclaimed identity, verification status would be
     * IdpIdentityStatus.UNCLAIMED and both key and target would point to same
     * unclaimed identity.</li>
     * <li>For password reset, verification status would be
     * IdpIdentityStatus.RESET and both key and target would point to same
     * unclaimed identity.</li>
     * <li><strike>For identity aggregation (adding identity to existing account
     * with verified identities), verification status would be
     * IdpIdentityStatus.AGGREGATION, key would point to existing verified
     * identity and target would point to identity being aggregated</strike>.
     * There is no need for this, any aggregation will start from an
     * authenticated account only, and hence does not need multi factor
     * verification (may be only notification is needed). Will just use the 1st
     * option of IdpIdentityStatus.UNCLAIMED identities.</li>
     * </ul>
     * 
     * @param verification
     *            details of the verification
     */
    void writeVerification(IdpIdentityVerification verification) throws DbException;

    /**
     * read verification details for specified verification code
     * 
     * @param code
     *            the verification code
     * @return verification details
     * @throws DbException
     */
    IdpIdentityVerification readVerification(String code) throws DbException;

    /**
     * read all identities associated (and verified) by a user account
     * 
     * @param accountId
     *            account ID of the user account
     * @return user's identities
     * @throws DbException
     */
    IdpUserIdentities readUserIdentities(String accountId) throws DbException;

    /**
     * delete the verification that is no longer needed
     * 
     * @param code
     *            verification code for the verification
     */
    void deleteVerification(String code);

    void deleteIdentity(IdpIdentityDetails identity);

    void deleteUserIdentities(String accountId);
}
