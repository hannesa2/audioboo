/**
 * This file is part of Audioboo, an android program for audio blogging.
 * Copyright (C) 2011 Audioboo Ltd. All rights reserved.
 * <p>
 * Author: Jens Finkhaeuser <jens@finkhaeuser.de>
 * <p>
 * $Id$
 **/

package fm.audioboo.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Client class for talking to AudiobooService's UploadService.
 **/
public class UploadClient {
    /***************************************************************************
     * Private constants
     **/
    private static final String LTAG = "UploadClient";
    /***************************************************************************
     * Private data
     **/
    private WeakReference<Context> mContext;
    private WeakReference<BindListener> mBindListener;
    // Service stub
    private volatile IUploadService mStub = null;
    // Service connection
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mStub = IUploadService.Stub.asInterface(service);

            // Notify bind success.
            BindListener listener = mBindListener.get();
            if (null != listener) {
                listener.onBound(UploadClient.this);
            }
        }


        public void onServiceDisconnected(ComponentName className) {
            mStub = null;
        }
    };

    private UploadClient(Context context, BindListener listener) {
        mContext = new WeakReference<Context>(context);
        mBindListener = new WeakReference<BindListener>(listener);
    }


    /***************************************************************************
     * Management functions
     **/

    /**
     * Returns true if binding was successful, false otherwise. Shortly after a
     * successful bind, BindListener's onBound() method will be called with
     * a UploadClient instance through which you can communicate with the
     * service.
     **/
    public static boolean bindService(Context context, BindListener listener) {
        UploadClient bpc = new UploadClient(context, listener);
        return bpc.bindService();
    }


    /**
     * Always unbind each UploadClient object.
     **/
    public void unbindService() {
        if (null == mStub) {
            return;
        }

        Context ctx = mContext.get();
        if (null == ctx) {
            return;
        }

        ctx.unbindService(mConnection);
    }

    private boolean bindService() {
        Context ctx = mContext.get();
        if (null == ctx) {
            Log.e(LTAG, "No context to bind with service with.");
            return false;
        }
        return ctx.bindService(
                new Intent(IUploadService.class.getName()),
                mConnection, Context.BIND_AUTO_CREATE);
    }

    /***************************************************************************
     * Upload implementation
     **/
    public void processQueue() {
        try {
            mStub.processQueue();
        } catch (RemoteException ex) {
            Log.e(LTAG, "Exception " + ex.getMessage());
        }
    }


    /***************************************************************************
     * Implement BindListener to receive a UploadClient instance that's
     * bound to IUploadService.
     **/
    public interface BindListener {
        public void onBound(UploadClient client);
    }
}
