/**
 * This file is part of AudioBoo, an android program for audio blogging.
 * Copyright (C) 2009 BestBefore Media Ltd.
 * Copyright (C) 2010,2011 AudioBoo Ltd.
 * All rights reserved.
 *
 * Author: Jens Finkhaeuser <jens@finkhaeuser.de>
 *
 * $Id$
 **/

package fm.audioboo.application;

import android.os.Bundle;

import android.content.res.Resources;

import android.widget.ListView;
import android.widget.ArrayAdapter;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import android.content.DialogInterface;
import android.app.Dialog;
import android.app.AlertDialog;

import android.util.Log;

/**
 * The BrowseActivity loads recent boos and displays them in a ListView.
 **/
public class BrowseActivity extends BooListActivity
{
  /***************************************************************************
   * Private constants
   **/
  // Log ID
  private static final String LTAG  = "BrowseActivity";

  // Action identifiers -- must correspond to the indices of the array
  // "recent_boos_actions" in res/values/localized.xml
  private static final int  ACTION_REFRESH  = 0;
  private static final int  ACTION_FILTER   = 1;

  // Index into filter array where the "followed" action is.
  private static final int  FOLLOWED_INDEX  = 1;

  // Dialog IDs
  private static final int  DIALOG_FILTERS  = 1;
  private static final int  DIALOG_ERROR    = Globals.DIALOG_ERROR;

  /***************************************************************************
   * Data members
   **/
  // Last error information - used and cleared in onCreateDialog
  private int               mErrorCode = -1;
  private API.APIException  mException;

  // Labels for filters
  private String[]          mFilterLabels;


  /***************************************************************************
   * BooListActivity implementation
   **/
  public int getViewId(int viewSpec)
  {
    switch (viewSpec) {
      case VIEW_ID_LAYOUT:
        return R.layout.browse_boos;

      case VIEW_ID_EMPTY_VIEW:
        return R.id.browse_boos_empty;

      case VIEW_ID_PLAYER:
        return R.id.browse_boos_player;

      case VIEW_ID_LOADING:
        return R.id.browse_boos_progress;

      case VIEW_ID_RETRY:
        return R.id.browse_boos_retry;

      default:
        return VIEW_ID_NONE;
    }
  }



  public int getInitAPI()
  {
    return API.BOOS_FEATURED;
  }



  public String getTitleString(int api)
  {
    return mFilterLabels[api];
  }



  @Override
  public void onError(int code, API.APIException exception)
  {
    super.onError(code, exception);

    // Store error variables.
    mErrorCode = code;
    mException = exception;
    showDialog(DIALOG_ERROR);
  }



  /***************************************************************************
   * Implementation
   **/
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    // Load filter labels
    mFilterLabels = getResources().getStringArray(R.array.browse_boos_filters);
  }



  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    String[] menu_titles = getResources().getStringArray(R.array.browse_boos_actions);
    final int[] menu_icons = {
      R.drawable.ic_menu_refresh,
      R.drawable.ic_menu_filter,
    };
    assert(menu_icons.length == menu_titles.length);

    for (int i = 0 ; i < menu_titles.length ; ++i) {
      menu.add(0, i, 0, menu_titles[i]).setIcon(menu_icons[i]);
    }
    return true;
  }



  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId()) {
      case ACTION_REFRESH:
        refresh();
        break;


      case ACTION_FILTER:
        showDialog(DIALOG_FILTERS);
        break;

      default:
        Toast.makeText(this, "Unreachable line reached.", Toast.LENGTH_LONG).show();
        return false;
    }

    return true;
  }



  protected Dialog onCreateDialog(int id)
  {
    Dialog dialog = null;
    Resources res = getResources();

    switch (id) {
      case DIALOG_ERROR:
        dialog = Globals.get().createDialog(this, id, mErrorCode, mException);
        mErrorCode = -1;
        mException = null;
        break;

      case DIALOG_FILTERS:
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
          .setTitle(res.getString(R.string.browse_boos_filter_title))
          .setItems(new String[] { "dummy" },
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which)
                  {
                    AlertDialog d = (AlertDialog) dialog;

                    // See if we're using the unmodified options or not.
                    int booType = which;
                    if (mFilterLabels.length != d.getListView().getCount()) {
                      if (booType >= FOLLOWED_INDEX) {
                        ++booType;
                      }
                    }

                    refresh(booType);
                  }
              }
            );
        dialog = builder.create();
        break;
    }

    return dialog;
  }



  protected void onPrepareDialog(int id, Dialog dialog)
  {
    Resources res = getResources();

    switch (id) {
      case DIALOG_FILTERS:
        AlertDialog ad = (AlertDialog) dialog;

        // Filter out 'followed' if the device is not linked
        String[] opts = mFilterLabels;
        API.Status status = Globals.get().getStatus();
        if (null == status || !status.mLinked) {
          opts = new String[mFilterLabels.length - 1];
          int offset = 0;
          for (int i = 0 ; i < opts.length ; ++i) {
            if (FOLLOWED_INDEX == i) {
              offset = 1;
            }
            opts[i] = mFilterLabels[i + offset];
          }
        }

        // Populate the dialog's list view.
        final ListView list = ad.getListView();
        list.setAdapter(new ArrayAdapter<CharSequence>(this,
            android.R.layout.select_dialog_item, android.R.id.text1, opts));
    }
  }
}
