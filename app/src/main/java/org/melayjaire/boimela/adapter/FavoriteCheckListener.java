package org.melayjaire.boimela.adapter;

import org.melayjaire.boimela.model.Book;

public interface FavoriteCheckListener {

	void onFavoriteCheckedChange(Book book, boolean isFavorite);
}
