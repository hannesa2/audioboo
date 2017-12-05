// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * Mailbox Rename Record  - specifies a rename of a mailbox.
 *
 * @author Brian Wellington
 */

public class MRRecord extends SingleNameBase {

    MRRecord() {
    }

    /**
     * Creates a new MR Record with the given data
     *
     * @param newName The new name of the mailbox specified by the domain.
     *                domain.
     */
    public MRRecord(Name name, int dclass, long ttl, Name newName) {
        super(name, Type.MR, dclass, ttl, newName, "new name");
    }

    Record
    getObject() {
        return new MRRecord();
    }

    /**
     * Gets the new name of the mailbox specified by the domain
     */
    public Name
    getNewName() {
        return getSingleName();
    }

}
