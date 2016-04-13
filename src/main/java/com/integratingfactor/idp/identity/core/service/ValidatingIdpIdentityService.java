package com.integratingfactor.idp.identity.core.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.integratingfactor.idp.common.db.exceptions.DbException;
import com.integratingfactor.idp.common.service.exceptions.IdpServiceException;
import com.integratingfactor.idp.identity.core.model.IdpIdentityDetails;
import com.integratingfactor.idp.identity.core.model.IdpIdentityStatus;
import com.integratingfactor.idp.identity.core.model.IdpIdentityType;
import com.integratingfactor.idp.identity.core.model.IdpIdentityVerification;
import com.integratingfactor.idp.identity.core.model.IdpUserIdentities;
import com.integratingfactor.idp.identity.db.service.DaoIdentityService;
import com.integratingfactor.idp.identity.external.IdpAppProperties;
import com.integratingfactor.idp.identity.external.IdpHttpSecurityOverride;
import com.integratingfactor.idp.identity.external.IdpTenantService;
import com.integratingfactor.idp.identity.external.IdpUser;
import com.integratingfactor.idp.identity.external.IdpUserProfile;
import com.integratingfactor.idp.util.mail.IdpSendMailClient;
import com.integratingfactor.idp.util.mail.MailDetails;

/**
 * this is an IDP Identity service implementation that will validate the
 * identity for ownership before it can be associated with user's account
 * 
 * @author gnulib
 *
 */
public class ValidatingIdpIdentityService implements IdpIdentityService {
    private static Logger LOG = Logger.getLogger(ValidatingIdpIdentityService.class.getName());

    @Autowired
    DaoIdentityService dbIdentity;

    @Autowired
    IdpSendMailClient emailClient;

    @Autowired
    IdpAppProperties appProperties;

    @Autowired
    IdpTenantService tenantService;

    @Override
    public String getAccountIdByIdentity(IdpIdentityType type, String id) {
        if (StringUtils.isEmpty(id))
            return null;
        try {
            // convert all identities to lower case to make them case
            // insensitive in lookup
            return dbIdentity.readIdentity(type, id.toLowerCase()).getAccountId();
        } catch (DbException e) {
            LOG.warning("Failed to get identity: " + e.getMessage());
        }
        return null;
    }

    private void writeIdentity(String accountId, IdpIdentityType type, String id) {
        IdpIdentityDetails identity = new IdpIdentityDetails();
        identity.setAccountId(accountId);
        identity.setType(type);
        // convert all identities to lower case to make them case insensitive in
        // lookups later
        identity.setId(id.toLowerCase());
        try {
            LOG.info("Writing identity " + identity.getType() + ":" + identity.getId() + " for account "
                    + identity.getAccountId());
            dbIdentity.writeIdentity(identity);
            // resolve any pending approvals for this identity with tenant
            // service
            tenantService.resolvePendingApprovals(identity);
        } catch (DbException e) {
            LOG.warning("Failed to write identity: " + e.getError());
        }
    }

    // number of days in milliseconds = 1000L x secs/hr x hr/day x days
    public static final Long VerificationExpiryTime = 1000L * 3600 * 24 * 2;

    private boolean writeVerification(String code, String accountId, IdpIdentityType type, String id,
            IdpIdentityStatus status) {
        IdpIdentityVerification verification = new IdpIdentityVerification();
        verification.setCode(code);
        verification.setStatus(status);
        verification.setExpiry(new Date().getTime() + VerificationExpiryTime);
        IdpIdentityDetails identity = new IdpIdentityDetails();
        identity.setAccountId(accountId);
        identity.setType(type);
        identity.setId(id);
        verification.setIdentity(identity);
        try {
            dbIdentity.writeVerification(verification);
            return true;
        } catch (DbException e) {
            LOG.warning("Failed to add new identity to verification: " + e.getError());
            return false;
        }
    }

    public static final String Subject = "Please verify your email";
    public static final String WelcomeMessageTemplate = "Dear $USER,\n\n" + "Welcome to $APP_NAME!\n\n"
            + "Please confirm your email by clicking at $HOST_NAME" + IdpHttpSecurityOverride.IdentityVerificationUrl
            + "?code=$CODE\n\n" + "Thanks,\n$EMAIL_NAME";
    public static final String PwResetMessageTemplate = "Dear $USER,\n\n"
            + "We heard you were having trouble accessing your account!\n\n"
            + "Please reset your password by clicking at $HOST_NAME" + IdpHttpSecurityOverride.PasswordResetUrl
            + "?code=$CODE\n\n" + "Thanks,\n$EMAIL_NAME";

    private boolean verifyEmail(String name, String id, String code, String template) {
        MailDetails email = new MailDetails();
        email.setFrom(new MailDetails.Address(appProperties.getAppEmailAddress(), appProperties.getAppName()));
        email.setTo(new MailDetails.Address(id, name));
        email.setSub(Subject);
        email.setMsg(
                template.replace("$USER", name).replace("$CODE", code)
                .replace("$APP_NAME", appProperties.getAppName())
                .replace("$EMAIL_NAME", appProperties.getAppEmailName())
                .replace("$HOST_NAME", appProperties.getAppHostName()));
        try {
            emailClient.sendEmail(email);
        } catch (UnsupportedEncodingException | MessagingException e) {
            LOG.warning("Failed to send verification email: " + e.getMessage());
            code = null;
        }
        return code != null;
    }

    @Override
    public IdpUserIdentities getIdpUserIdentities(String accountId) {
        try {
            return dbIdentity.readUserIdentities(accountId);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new IdpServiceException(e.getError());
        }
    }

    @Override
    public void registerIdpUserIdentities(IdpUser user) {
        LOG.info("Registering identities for account id: " + user.getAccountId());
        // walk through each of the provided identity
        for (Entry<IdpIdentityType, Set<String>> kv : user.getIdentities().getIdentities().entrySet()) {
            switch (kv.getKey()) {
            case USERNAME:
                // this type does not needs any verification, just store as is
                for (String id : kv.getValue()) {
                    writeIdentity(user.getAccountId(), kv.getKey(), id);
                }
                break;
            case EMAIL:
                // add to verification and then send verification email
                for (String id : kv.getValue()) {
                    String code = UUID.randomUUID().toString();
                    if (writeVerification(code, user.getAccountId(), kv.getKey(), id,
                            IdpIdentityStatus.UNCLAIMED)) {
                        if (verifyEmail(
                                // user.getProfile().getProfile().get(IdpUserProfileFields.firstName)
                                // + " "
                                // +
                                // user.getProfile().getProfile().get(IdpUserProfileFields.lastName),
                                user.getProfile() + " " + user.getProfile(),
                                id, code, WelcomeMessageTemplate)) {
                            // will also write to associate with account,
                            // however it will be removed if not verified by
                            // expiry
                            writeIdentity(user.getAccountId(), kv.getKey(), id);
                        }
                    }
                }
                break;
            case FACEBOOK:
            case GOOGLE:
            case MOBILE:
                // do not support these for now, so just skip
                break;
            }
        }
    }

    @Override
    public IdpIdentityDetails verifyIdentity(String code) {
        try {
            IdpIdentityVerification verification = dbIdentity.readVerification(code);
            // this is a match, so remove verification from DB. One time
            // use only!!!
            dbIdentity.deleteVerification(code);
            // make sure that the verification has not expired
            if (verification.getExpiry() >= new Date().getTime()) {
                // looks good return back identity
                return verification.getIdentity();
            } else {
                // verification has expired
                LOG.info("Verification expired already, identity cannot be used!!!");
                dbIdentity.deleteIdentity(verification.getIdentity());
            }
        } catch (DbException e) {
            LOG.warning("Did not find any verification for the code: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void resetPassword(IdpUserProfile user) {
        // for now just implement a simple password reset email, later will need
        // to do a multi factor verification/notification
        try {
            IdpUserIdentities identity = dbIdentity.readUserIdentities(user.getAccountId());
            // generate a new verification code
            String code = UUID.randomUUID().toString();
            // write verification code with username identity type
            if (writeVerification(code, identity.getAccountId(), IdpIdentityType.USERNAME,
                    identity.getIdentities().get(IdpIdentityType.USERNAME).iterator().next(),
                    IdpIdentityStatus.RESET)) {
                // send email notification to all registered emails
                for (String id : identity.getIdentities().get(IdpIdentityType.EMAIL)) {
                    // verifyEmail(user.getProfile().get(IdpUserProfileFields.firstName)
                    // + " "
                    // + user.getProfile().get(IdpUserProfileFields.lastName),
                    verifyEmail(user.getProfile() + " " + user.getProfile(),
                            id, code, PwResetMessageTemplate);
                }
            }
        } catch (DbException e) {
            LOG.warning("Failed to get identities for account: " + user.getAccountId());
        }
    }

    @Override
    public void removeUser(String accountId) {
        LOG.info("Cleaning up all identities for user account: " + accountId);
        dbIdentity.deleteUserIdentities(accountId);
    }

}
