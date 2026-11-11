package com.example.penjualanrajut;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private Runnable callback;
    public SimpleItemSelectedListener(Runnable callback) { this.callback = callback; }
    @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { callback.run(); }
    @Override public void onNothingSelected(AdapterView<?> parent) {}
}