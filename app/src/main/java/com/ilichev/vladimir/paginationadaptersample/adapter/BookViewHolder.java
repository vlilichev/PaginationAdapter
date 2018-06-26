package com.ilichev.vladimir.paginationadaptersample.adapter;

import android.view.View;
import android.widget.TextView;

import com.ilichev.vladimir.paginationadapter.ItemHolder;
import com.ilichev.vladimir.paginationadaptersample.R;
import com.ilichev.vladimir.paginationadaptersample.data.Book;

public class BookViewHolder extends ItemHolder<Book> {

    private TextView title;
    private TextView author;

    public BookViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.book_title_sample_text_view);
        author = itemView.findViewById(R.id.book_author_sample_text_view);
    }

    @Override
    protected void render() {
        title.setText(item.title);
        author.setText(item.author);
    }
}
