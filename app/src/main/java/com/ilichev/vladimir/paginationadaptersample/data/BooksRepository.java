package com.ilichev.vladimir.paginationadaptersample.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BooksRepository {

    private List<Book> books = new ArrayList<>();

    public BooksRepository() {
        generateBooks();
    }

    public List<Book> load(int offset, int count) {
        Random r = new Random();
        boolean error = r.nextBoolean();
        if (error) {
            return null;
        }

        List<Book> newBooks = new ArrayList<>();
        for (int i = offset; i < offset + count && i < books.size(); i++) {
            newBooks.add(books.get(i));
        }
        return newBooks;
    }

    private void generateBooks() {
        for (int i = 0; i < 30; i++) {
            Book book = new Book();
            book.title = "Book " + i;
            book.author = "Author " + i;
            books.add(book);
        }
    }
}
