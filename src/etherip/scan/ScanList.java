/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.scan;

import static etherip.EtherNetIP.logger;

import java.util.TimerTask;
import java.util.logging.Level;

import etherip.Status.ConnectionFailListener;
import etherip.Status.StsConnection;
import etherip.Tag;
import etherip.TagList;
import etherip.protocol.Connection;

/**
 * List of tags that are processed (read or written)
 *
 * @author Kay Kasemir
 */
public class ScanList extends TimerTask {
    final private double period;
    final private Connection connection;
    int numAttempt;
    private StsConnection stsConnection;

    final private TagList tags = new TagList();

    private volatile boolean aborted = false;

    public ScanList(final double period, final Connection connection, int numAttempt, ConnectionFailListener failListener) {
        this.period = period;
        this.stsConnection = new StsConnection(numAttempt);
        this.stsConnection.setListeners(failListener);
        this.connection = connection;
        this.numAttempt = numAttempt;
    }

    public Tag add(final String tag_name) {
        return this.tags.add(tag_name);
    }

    @Override
    public void run() {
        logger.log(Level.FINE, "Scan list {0} sec", this.period);
        try {
            this.tags.process(this.connection);
            stsConnection.resetAttempt();
        } catch (final Exception ex) {
            if (this.aborted) {
                return;
            }
            logger.log(Level.WARNING,
                    "Scan list " + this.period + " sec process failed", ex);
            stsConnection.setAttempt();
        }
    }

    @Override
    public boolean cancel() {
        this.aborted = true;
        return super.cancel();
    }
}