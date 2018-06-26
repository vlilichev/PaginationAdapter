package com.ilichev.vladimir.paginationadaptersample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ilichev.vladimir.paginationadapter.PaginationAdapter;
import com.ilichev.vladimir.paginationadapter.PagingConfig;
import com.ilichev.vladimir.paginationadaptersample.adapter.BooksPagedAdapter;
import com.ilichev.vladimir.paginationadaptersample.data.Book;
import com.ilichev.vladimir.paginationadaptersample.data.BooksRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private BooksRepository booksRepository;

    private PaginationAdapter<Book> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        booksRepository = new BooksRepository();

        RecyclerView rv = findViewById(R.id.movie_rv_sample);

        PagingConfig pagingConfig = new PagingConfig.Builder()
                .setPageSize(20)
                .setLoadThreshold(2)
                .build();

        adapter = new BooksPagedAdapter(new ArrayList<>(), pagingConfig, this);
        adapter.setLoadNextPartCallback((offset, count) -> delayedLoad(booksRepository.load(offset, count)));

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);

        delayedLoad(booksRepository.load(0, pagingConfig.getPageSize()));
    }

    private void delayedLoad(final List<Book> newBooks) {
        Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(() -> {
            if (newBooks == null) {
                adapter.setError();
            } else {
                adapter.setData(newBooks);
            }
            }, 2000);
    }
}
