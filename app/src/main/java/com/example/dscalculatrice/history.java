package com.example.dscalculatrice;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class history  extends CursorAdapter {
    public history (Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_calcul, parent, false);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
// Get the calculation and result values from the cursor
        String calcul = cursor.getString(cursor.getColumnIndexOrThrow("calculation"));
        String result = cursor.getString(cursor.getColumnIndexOrThrow("result"));

        // Set the calculation and result values on the TextViews in the list item layout
        TextView caltextView = view.findViewById(R.id.caltextView);
        TextView resutldb = view.findViewById(R.id.resutldb);

        caltextView.setText(calcul);
        resutldb.setText(result);
    }
}

