package org.melayjaire.boimela.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.view.BanglaTextView;

/**
 * Created by Iftekhar on 02/01/2015.
 */
public abstract class SingleItemRVCursorAdapter extends CursorRecyclerViewAdapter<SingleItemRVCursorAdapter.ViewHolder> {

    public SingleItemRVCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public abstract void onBind(ViewHolder viewHolder, Cursor cursor);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        onBind(viewHolder, cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public BanglaTextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (BanglaTextView) itemView.findViewById(R.id.textView_title);
        }
    }
}
