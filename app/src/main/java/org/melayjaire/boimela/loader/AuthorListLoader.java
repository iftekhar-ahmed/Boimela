package org.melayjaire.boimela.loader;

import android.content.Context;

import org.melayjaire.boimela.R;
import org.melayjaire.boimela.data.BookDataSource;
import org.melayjaire.boimela.model.Author;
import org.melayjaire.boimela.search.SearchCriteria;
import org.melayjaire.boimela.search.SearchFilter;

import java.util.List;

/**
 * Created by Iftekhar on 2/24/2015.
 */
public class AuthorListLoader extends SimpleListLoader<Author> {

    private BookDataSource dataSource;
    private SearchCriteria searchCriteria;
    private SearchFilter searchFilter;

    public AuthorListLoader(Context context, BookDataSource dataSource,
                            SearchCriteria searchCriteria, SearchFilter searchFilter) {
        super(context);
        this.dataSource = dataSource;
        this.searchCriteria = searchCriteria;
        this.searchFilter = searchFilter;
    }

    @Override
    public List<Author> loadInBackground() {
        if (dataSource.isEmpty()) {
            dataSource.insertBulk(R.raw.books);
        }
        return dataSource.getAllAuthors();
    }
}
