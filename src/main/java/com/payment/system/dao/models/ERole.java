package com.payment.system.dao.models;

/**
 * Role is enumeration representing the possible roles in a system.
 * Merchant is a standard user of the system.
 * Administrator is user that have rights to delete a Merchant user from the system.
 */

public enum ERole {
    ADMINISTRATOR,
    MERCHANT
}
