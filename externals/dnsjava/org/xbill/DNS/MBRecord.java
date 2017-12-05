// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * Mailbox Record  - specifies a host containing a mailbox.
 *
 * @author Brian Wellington
 */

public class MBRecord extends SingleNameBase {

    MBRecord() {
    }

    /**
     * Creates a new MB Record with the given data
     *
     * @param mailbox The host containing the mailbox for the domain.
     */
    public MBRecord(Name name, int dclass, long ttl, Name mailbox) {
        super(name, Type.MB, dclass, ttl, mailbox, "mailbox");
    }

    Record
    getObject() {
        return new MBRecord();
    }

    /**
     * Gets the mailbox for the domain
     */
    public Name
    getMailbox() {
        return getSingleName();
    }

    public Name
    getAdditionalName() {
        return getSingleName();
    }

}
