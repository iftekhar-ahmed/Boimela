package org.melayjaire.boimela.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.utils.Utilities;
import org.melayjaire.boimela.view.BanglaTextView;

import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> implements
        OnCheckedChangeListener {

    private Context context;
    private List<Book> books;
    private FavoriteCheckedListener checkChangeCallback;

    public BookListAdapter(Context context, List<Book> books, FavoriteCheckedListener callback) {
        this.context = context;
        this.books = books;
        checkChangeCallback = callback;
    }

    public void swapList(List<Book> books) {
        if (books != null) {
            this.books.clear();
            for (Book book : books) {
                this.books.add(book);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkChangeCallback.onFavoriteCheckedChange(books.get((Integer) buttonView.getTag()), isChecked);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.favorite.setOnCheckedChangeListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Book book = books.get(position);
        viewHolder.imageViewBookCover.setImageDrawable(TextDrawable
                .builder()
                .beginConfig()
                .withBorder(4).fontSize(40)
                .endConfig()
                .buildRect(Utilities.getBanglaSpannableString(String.valueOf(book.getTitle().charAt(0)), context)
                        , ColorGenerator.DEFAULT.getColor(book.getId())));
        viewHolder.title.setBanglaText(book.getTitle());
        viewHolder.author.setBanglaText(book.getAuthor());
        viewHolder.publisher.setBanglaText(book.getPublisher());
        viewHolder.price.setBanglaText(context.getString(R.string.taka) + book.getPrice());
        viewHolder.favorite.setTag(position);
        viewHolder.favorite.setChecked(book.isFavorite());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewBookCover;
        public BanglaTextView title;
        public BanglaTextView author;
        public BanglaTextView publisher;
        public BanglaTextView price;
        public CheckBox favorite;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewBookCover = (ImageView) itemView.findViewById(R.id.imageView_book_cover);
            title = (BanglaTextView) itemView.findViewById(R.id.title);
            author = (BanglaTextView) itemView.findViewById(R.id.author);
            publisher = (BanglaTextView) itemView.findViewById(R.id.publisher);
            price = (BanglaTextView) itemView.findViewById(R.id.price);
            favorite = (CheckBox) itemView.findViewById(R.id.checkBox_favorite);
        }
    }

    public interface FavoriteCheckedListener {
        void onFavoriteCheckedChange(Book book, boolean isFavorite);
    }
}
