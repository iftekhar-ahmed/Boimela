package org.melayjaire.boimela.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.utils.Utilities;

import java.util.List;

public class BookListAdapter extends ArrayAdapter<Book> implements
        OnCheckedChangeListener {

    private Context context;
    private FavoriteCheckListener checkChangeCallback;
    private int layoutResourceId;
    // private String colors[] = new String[] { "#ead585", "#33B5E5" };

    public BookListAdapter(Context context, int layout, List<Book> objects,
                           FavoriteCheckListener callback) {
        super(context, layout, objects);
        this.context = context;
        layoutResourceId = layout;
        checkChangeCallback = callback;
    }

    @Override
    public int getViewTypeCount() {

        if (getCount() != 0)
            return getCount();

        return 1;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    private class ViewHolder {
        TextView title;
        TextView author;
        TextView publisher;
        TextView price;
        CheckBox favorite;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Book book = getItem(position);

        View inflatedView = LayoutInflater.from(context).inflate(
                layoutResourceId, parent, false);

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflatedView;

            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.publisher = (TextView) convertView
                    .findViewById(R.id.publisher);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.favorite = (CheckBox) convertView
                    .findViewById(R.id.favorite);
            holder.favorite.setOnCheckedChangeListener(this);

            holder.price.setTypeface(Utilities.getBanglaFont(context));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.favorite.setTag(position);

        holder.title.setText(Utilities.getBanglaSpannableString(book.getTitle(), context));
        holder.author.setText(Utilities.getBanglaSpannableString(book.getAuthor(), context));
        holder.publisher.setText(Utilities.getBanglaSpannableString(book.getPublisher(), context));
        holder.price.setText(context.getString(R.string.taka) + " " + book.getPrice());
        holder.favorite.setChecked(book.isFavorite());

        // int colorPos = position % colors.length;
        // convertView.setBackgroundColor(Color.parseColor(colors[colorPos]));
        return convertView;
    }

    public void swapList(List<Book> books) {
        clear();
        if (books != null)
            for (Book book : books)
                add(book);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        checkChangeCallback.onFavoriteCheckedChange(
                getItem((Integer) buttonView.getTag()), isChecked);
    }

}
