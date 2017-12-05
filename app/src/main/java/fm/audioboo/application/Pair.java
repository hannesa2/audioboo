/**
 * This file is part of Audioboo, an android program for audio blogging.
 * Copyright (C) 2011 Audioboo Ltd. All rights reserved.
 * <p>
 * Author: Jens Finkhaeuser <jens@finkhaeuser.de>
 * <p>
 * $Id$
 **/

package fm.audioboo.application;

/**
 * Funny, the things you take for granted from C++.
 **/
public class Pair<F, S> {
    public F mFirst;
    public S mSecond;

    public Pair(F first, S second) {
        mFirst = first;
        mSecond = second;
    }
}
