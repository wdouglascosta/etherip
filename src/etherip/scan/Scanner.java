/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.scan;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import etherip.Status.ConnectionFailListener;
import etherip.Tag;
import etherip.protocol.Connection;

/**
 * Timer-based periodic scanner for {@link ScanList Scan Lists}
 *
 * @author Kay Kasemir
 */
public class Scanner
{
    final private Connection connection;
    final Timer timer = new Timer("Scan Timer");
    private int numAttempt;
    private ConnectionFailListener failListener;

    /** Scan lists by scan period in ms */
    final Map<Long, ScanList> scan_lists = new HashMap<>();

    public Scanner(final Connection connection, int numAttempt, ConnectionFailListener failListener)
    {
        this.failListener = failListener;
        this.numAttempt = numAttempt;
        this.connection = connection;
    }

    private long convertToMillisec(final double seconds)
    {
        if (seconds <= 0.1)
        {
            return 100;
        }
        return (long) (seconds * 1000);
    }

    public Tag add(final double period_secs, final String tag_name)
    {
        // Locate suitable scan list
        final long ms = this.convertToMillisec(period_secs);
        ScanList list = this.scan_lists.get(ms);
        if (list == null)
        {
            list = new ScanList(period_secs, this.connection, numAttempt, failListener);
            this.scan_lists.put(ms, list);
            this.timer.schedule(list, ms, ms);
        }
        return list.add(tag_name);
    }

    public void stop()
    {
        this.timer.cancel();
        for (final ScanList list : this.scan_lists.values())
        {
            list.cancel();
        }
    }
}