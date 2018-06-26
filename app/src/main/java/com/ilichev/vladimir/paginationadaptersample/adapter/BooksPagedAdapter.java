package com.ilichev.vladimir.paginationadaptersample.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ilichev.vladimir.paginationadapter.ItemHolder;
import com.ilichev.vladimir.paginationadapter.PaginationAdapter;
import com.ilichev.vladimir.paginationadapter.PagingConfig;
import com.ilichev.vladimir.paginationadaptersample.R;
import com.ilichev.vladimir.paginationadaptersample.data.Book;

import java.util.List;

public class BooksPagedAdapter extends PaginationAdapter<Book> {

    private Context uiContext;

    public BooksPagedAdapter(@NonNull List<Book> data, PagingConfig config, Context context) {
        super(data, config);
        this.uiContext = context;
    }

    @Override
    public ItemHolder<Book> createItemViewHolder(@NonNull ViewGroup parent) {
        View v = LayoutInflater.from(uiContext).inflate(R.layout.book_list_item, parent, false);
        return new BookViewHolder(v);
    }
}
