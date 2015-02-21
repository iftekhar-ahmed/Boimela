package org.melayjaire.boimela.ui;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.model.Book;
import org.melayjaire.boimela.utils.Utilities;

public class BookDetailFragment extends DialogFragment {

    private Book book;

    private static final String ARG_BOOK = "_book";

    public BookDetailFragment() {
    }

    public static BookDetailFragment newInstance(Book book) {
        BookDetailFragment bookDetailFragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOK, book);
        bookDetailFragment.setArguments(args);
        return bookDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null) {
            book = extras.getParcelable(ARG_BOOK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);
        ImageView imageViewBookTitle = (ImageView) rootView.findViewById(R.id.imageView_book_cover);
        imageViewBookTitle.setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .withBorder(4).fontSize(40)
                .endConfig()
                .buildRect(Utilities.getBanglaSpannableString(book.getTitle(), getActivity())
                        , ColorGenerator.DEFAULT.getColor(book.getId())));
        TextView textViewBookAuthor = (TextView) rootView.findViewById(R.id.textView_author_name);
        textViewBookAuthor.setText(Utilities.getBanglaSpannableString(book.getAuthor(), getActivity()));
        TextView textViewBookPublisher = (TextView) rootView.findViewById(R.id.textView_publisher_name);
        textViewBookPublisher.setText(Utilities.getBanglaSpannableString(book.getPublisher(), getActivity()));
        TextView textViewBookPrice = (TextView) rootView.findViewById(R.id.textView_price_value);
        textViewBookPrice.setText(Utilities.getBanglaSpannableString(getString(R.string.taka) + book.getPrice(), getActivity()));
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
