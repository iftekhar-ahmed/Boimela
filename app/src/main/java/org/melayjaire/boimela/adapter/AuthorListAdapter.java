package org.melayjaire.boimela.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.model.Author;
import org.melayjaire.boimela.utils.Utilities;
import org.melayjaire.boimela.view.BanglaTextView;

import java.util.List;

/**
 * Created by Iftekhar on 2/24/2015.
 */
public class AuthorListAdapter extends RecyclerView.Adapter<AuthorListAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    private List<Author> authors;
    private OnItemClickListener itemClickCallback;

    public AuthorListAdapter(Context context, List<Author> authors, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.authors = authors;
        itemClickCallback = onItemClickListener;
    }

    public void swapList(List<Author> authors) {
        if (authors != null) {
            this.authors.clear();
            for (Author author : authors) {
                this.authors.add(author);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Author author = authors.get(position);
        viewHolder.itemView.setTag(position);
        viewHolder.authorFace.setImageDrawable(TextDrawable
                .builder()
                .beginConfig()
                .withBorder(4).fontSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 26
                        , context.getResources().getDisplayMetrics()))
                .endConfig()
                .buildRound(Utilities.getBanglaSpannableString(String.valueOf(author.getName().charAt(0)), context)
                        , ColorGenerator.DEFAULT.getColor(author.getNameInEnglish())));
        viewHolder.name.setBanglaText(author.getName());
    }

    @Override
    public int getItemCount() {
        return authors.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        itemClickCallback.onItemClick(authors.get(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView authorFace;
        public BanglaTextView name;
        public BanglaTextView booksCount;

        public ViewHolder(View itemView) {
            super(itemView);
            authorFace = (ImageView) itemView.findViewById(R.id.imageView_author);
            name = (BanglaTextView) itemView.findViewById(R.id.textView_author);
            booksCount = (BanglaTextView) itemView.findViewById(R.id.textView_book_count);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Author author);
    }
}
