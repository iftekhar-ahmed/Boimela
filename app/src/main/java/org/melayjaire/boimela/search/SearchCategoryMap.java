package org.melayjaire.boimela.search;

import android.content.Context;

import org.melayjaire.boimela.R;

import java.util.HashMap;
import java.util.Map;

public class SearchCategoryMap {

    Map<String, SearchType> nameCategoryMap;

    public SearchCategoryMap(Context context) {
        nameCategoryMap = new HashMap<String, SearchType>();
        createMap(context);
    }

    private void createMap(Context context) {
        nameCategoryMap.put(context.getString(R.string.title), SearchType.Title);
        nameCategoryMap.put(context.getString(R.string.author), SearchType.Author);
        nameCategoryMap.put(context.getString(R.string.publisher), SearchType.Publisher);
        nameCategoryMap.put(context.getString(R.string.category), SearchType.Category);
        nameCategoryMap.put(context.getString(R.string.new_book), SearchType.NewBook);
        nameCategoryMap.put(context.getString(R.string.favorite_books), SearchType.Favorites);
    }

    public SearchType obtain(String categoryName) {

        return nameCategoryMap.get(categoryName);
    }
}
