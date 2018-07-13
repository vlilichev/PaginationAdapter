# PaginationAdapter

RecyclerView adapter that allows display data with small portions

![](https://github.com/vlilichev/PaginationAdapter/blob/master/pic/prog.png)
![](https://github.com/vlilichev/PaginationAdapter/blob/master/pic/err.png)

### Setup

Available on jCenter:
```groovy
implementation 'com.ilichev.vladimir.paginationadapter:adapter:1.0.1'
```
### Usage

Create your adapter, extend it from ```PaginationAdapter``` and implement ```createItemViewHolder()```:
```java
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
```
Next step is creating ```ViewHolder``` that render single list item:
```java
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
```
And setup adapter to ```RecyclerView```:
```java
PaginationAdapter<Book> adapter = new BooksPagedAdapter(new ArrayList<>(), pagingConfig, this);
adapter.setLoadNextPartCallback((offset, count) -> booksRepository.load(offset, count));
rv.setAdapter(adapter);
```
For more control over the pagination behavior ```PagingConfig``` can be used:
```java
PagingConfig pagingConfig = new PagingConfig.Builder()
                .setPageSize(20)
                .setLoadThreshold(2)
                .setDataLimit(100)
                .build();
```
To customize progress and error footer override methods below and provide ```ViewHolder``` for progress and error state:
```java
@Override
protected StateHolder createStateProgressViewHolder(@NonNull ViewGroup parent) {
    // TODO: Provide progress ViewHolder
}

@Override
protected StateHolder createStateErrorViewHolder(@NonNull ViewGroup parent) {
    // TODO: Provide error ViewHolder
}
```