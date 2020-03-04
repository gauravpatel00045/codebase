package com.example.mvc.codebase.interfaces;

import android.view.View;

/*
 * This interface for all click events in Application
 */

public interface ClickEvent {
    /**
     * This method identify view's click event and perform action.
     *
     * @param view (View) : view where user touch or click
     */
     void onClickEvent(View view);

}
