/**
 * This file is part of AudioBoo, an android program for audio blogging.
 * Copyright (C) 2009 BestBefore Media Ltd. All rights reserved.
 *
 * Author: Jens Finkhaeuser <jens@finkhaeuser.de>
 *
 * $Id$
 **/

package fm.audioboo.app;

import android.app.ListActivity;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;

import android.content.res.Configuration;

import android.view.View;

import java.util.LinkedList;

import android.util.Log;

/**
 * FIXME
 **/
public class RecentBoosActivity extends ListActivity
{
  /***************************************************************************
   * Private constants
   **/
  // Log ID
  private static final String LTAG  = "RecentBoosActivity";


  /***************************************************************************
   * Data members
   **/
  // API instance
  private API     mApi;

  // Content
  private BooList mBoos;

  /***************************************************************************
   * Implementation
   **/
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.recent_boos);
    View v = findViewById(R.id.recent_boos_empty);
    getListView().setEmptyView(v);
  }



  @Override
  public void onStart()
  {
    super.onStart();
    Log.d(LTAG, "Start");
  }



  @Override
  public void onResume()
  {
    super.onResume();
    Log.d(LTAG, "Resume");

    // FIXME only if no boos have been loaded yet.
    if (null != mBoos) {
      return;
    }

    // Start loading recent Boos.
    if (null == mApi) {
      mApi = new API();
    }
    mApi.fetchRecentBoos(new Handler(new Handler.Callback() {
      public boolean handleMessage(Message msg)
      {
        if (API.ERR_SUCCESS == msg.what) {
          onReceiveRecentBoos((BooList) msg.obj);
        }
        else {
          onRecentBoosError(msg.what, (String) msg.obj);
        }
        return true;
      }

    }));
  }



  @Override
  public void onConfigurationChanged(Configuration config)
  {
    // Ignore when the keyboard opens to the extent that we don't fetch boos
    // again.
    super.onConfigurationChanged(config);
  }



  private void onReceiveRecentBoos(BooList boos)
  {
    Log.d(LTAG, "Got response!");

    mBoos = boos;

    BooListAdapter adapter = new BooListAdapter(this, R.layout.recent_boos_item, mBoos);
    setListAdapter(adapter);
//    getListView().setTextFilterEnabled(true);
  }



  private void onRecentBoosError(int code, String msg)
  {
  }
}
