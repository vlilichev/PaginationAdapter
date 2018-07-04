package com.ilichev.vladimir.paginationadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * This class allow easy to display paged lists. Behavior depends on three main arguments.
 * First one is data limit, it tells to adapter when to stop for requesting more data. To set this value use {@link PagingConfig.Builder#setDataLimit(int)}.
 * Second is page size, it defines how much adapter display and requesting data at one time. Use {@link PagingConfig.Builder#setPageSize(int)} to set it.
 * Third is load threshold, it defines at what distance from end start to prefetch new page. Use {@link PagingConfig.Builder#setLoadThreshold(int)} to set it.
 * Adapter use default views to display loading progress and error message. For more control client can override
 * {@link #createStateProgressViewHolder(ViewGroup)} and {@link #createStateErrorViewHolder(ViewGroup)} for custom behavior.
 * @param <E> Data class that wil be displayed in {@link ItemHolder}
 */
public abstract class PaginationAdapter<E> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<E> data;

    // This fields configurable. See PagingConfig class for default values
    private int dataLimit;
    private int pageSize;
    private int loadThreshold;

    private int distance;
    private int currentPage;

    private PageState currentSate = PageState.LOADED;

    private LoadNextPartCallback loadNextPartCallback;
    private LoadNextPageCallback loadNextPageCallback;

    // It is not good enough to set view types like this.
    // Try to rethink this approach to avoid collisions. Maybe try to get it from R.layout file.
    private static final int ITEM_VIEW_TYPE = 42;
    private static final int FOOTER_PROGRESS_VIEW_TYPE = 43;
    private static final int FOOTER_ERROR_VIEW_TYPE = 44;

    public PaginationAdapter(@NonNull List<E> data) {
        this(data, new PagingConfig.Builder().build());
    }

    public PaginationAdapter(@NonNull List<E> data, @NonNull PagingConfig config) {
        this.data = data;
        dataLimit = config.getDataLimit();
        pageSize = config.getPageSize();
        loadThreshold = config.getLoadThreshold();

        distance = 0 - pageSize;
        currentPage = distance / pageSize + 1;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new PagedScrollListener());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE:
                return createItemViewHolder(parent);
            case FOOTER_PROGRESS_VIEW_TYPE:
                return createStateProgressViewHolder(parent);
            case FOOTER_ERROR_VIEW_TYPE:
                return createStateErrorViewHolder(parent);
            default:
                throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder<E> h = (ItemHolder) holder;
            h.setItem(data.get(position));
        } else if (holder instanceof StateHolder) {
            StateHolder h = (StateHolder) holder;
            h.setState(currentSate);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == data.size()) {
            if (currentSate == PageState.LOADING) {
                return FOOTER_PROGRESS_VIEW_TYPE;
            } else if (currentSate == PageState.ERROR) {
                return FOOTER_ERROR_VIEW_TYPE;
            }
        }
        return ITEM_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        if (currentSate == PageState.LOADING || currentSate == PageState.ERROR) {
            return data.size() + 1;
        } else {
            return data.size();
        }
    }

    /**
     * Factory method for creating a {@link RecyclerView.ViewHolder} that displays item model.
     * Your ViewHolder must extend {@link ItemHolder}.
     * {@link PaginationAdapter} will call {@link ItemHolder#render()} when item get ready to display.
     * @param parent Parent view for item. See {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * @return Item VIewHolder
     */
    protected abstract ItemHolder<E> createItemViewHolder(@NonNull ViewGroup parent);

    /**
     * Override this factory method to provide {@link PaginationAdapter} custom progress {@link RecyclerView.ViewHolder} in
     * footer. Note your ViewHolder must extend {@link StateHolder}.
     * @param parent Parent view for item. See {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * @return ViewHolder to display progress in footer.
     */
    protected StateHolder createStateProgressViewHolder(@NonNull ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_progress_item, parent, false);
        return new DefaultProgressViewHolder(v);
    }

    /**
     * Override this factory method to provide {@link PaginationAdapter} custom error message {@link RecyclerView.ViewHolder} in
     * footer. Note your ViewHolder must extend {@link StateHolder}.
     * @param parent Parent view for item. See {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * @return ViewHolder to display error message in footer
     */
    protected StateHolder createStateErrorViewHolder(@NonNull ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_error_item, parent, false);
        return new DefaultErrorViewHolder(v, delegateImpl);
    }

    /**
     * Sets a new page to adapter.
     * Note it is client responsibility to set new page with the same size as it was
     * configured on {@link PaginationAdapter} initialization.
     * Adapter support auto detect limit it means that if new page size less than initial page size
     * adapter stops trying to request for new data.
     * @param newData Items list for new page
     */
    public void setData(@NonNull List<E> newData) {
        currentSate = PageState.LOADED;
        // Remove footer
        notifyItemRemoved(data.size());

        int start = data.size();
        data.addAll(newData);

        incrementPage();
        detectLimit(newData);

        notifyItemRangeInserted(start, newData.size());
    }

    /**
     * Inserts new item to specified position.
     * Insertion will drop the last list item, it means that dropped item should be first
     * in new requested page.
     * @param item Item to insert.
     * @param position Item position in paged list.
     * @throws IllegalArgumentException If (position < 0) or (position > data size)
     */
    public void insert(@NonNull E item, int position) {
        if (position < 0 || position > data.size()) {
            throw new IllegalArgumentException("Position must be from 0 to list size");
        }

        data.add(position, item);
        notifyItemInserted(position);

        data.remove(data.size() - 1);
        notifyItemRemoved(data.size());
    }

    /**
     * Sets a error to adapter.
     * It will show error footer with error message. See {@link #createStateErrorViewHolder(ViewGroup)} to customize error message.
     * To leave error state client should call {@link #retry()}.
     */
    public void setError() {
        currentSate = PageState.ERROR;
        // Notify for footer
        notifyItemChanged(data.size());
    }

    /**
     * Tries to leave error state and requests data one more time.
     */
    public void retry() {
        if (currentSate == PageState.ERROR) {
            loadMore();
        }
    }

    /**
     * Removes item from paged list by position.
     * After item removing the last page of paged will be deleted and
     * adapter will request last page ones again.
     * @param position Position of item to remove.
     */
    public void remove(int position) {
        if (data.isEmpty()) {
            return;
        }
        if (position < 0 || position > data.size()) {
            throw new IllegalArgumentException("Position must be from 0 to list size");
        }

        data.remove(position);
        notifyItemRemoved(position);

        int removeFrom = data.size() - pageSize + 1;
        for (int i = data.size() - 1, j = 0; j < pageSize - 1; i--, j++) {
            data.remove(i);
        }
        notifyItemRangeRemoved(removeFrom, pageSize);

        decrementPage();
        loadMore();
    }

    /**
     * Removes item from paged list
     * After item removing the last page of paged will be deleted and
     * adapter will request last page ones again.
     * @param item Item to remove.
     */
    public void remove(@NonNull E item) {
        int indx = data.indexOf(item);
        if (data.isEmpty() || indx == -1) {
            return;
        }
        remove(indx);
    }

    /**
     * Resets adapter to its initial state and clears all data.
     * No matter what previous state was, adapter will wait for first page.
     * The next page that will be set through {@link #setData(List)} will be first page.
     */
    public void reset() {
        PageState prev = currentSate;
        currentSate = PageState.LOADED;

        int size = data.size();
        if (prev == PageState.LOADING || prev == PageState.ERROR) {
            // Remove footer
            notifyItemRemoved(size);
        }

        data.clear();
        notifyItemRangeRemoved(0, size);

        distance = 0 - pageSize;
        currentPage = distance / pageSize + 1;
    }

    /**
     * Sets callback for requesting data by distance (offset) and page size (count).
     * @param loadNextPartCallback Callback to set.
     */
    public void setLoadNextPartCallback(LoadNextPartCallback loadNextPartCallback) {
        this.loadNextPartCallback = loadNextPartCallback;
    }

    /**
     * Sets callback for requesting data by page number.
     * @param loadNextPageCallback Callback to set.
     */
    public void setLoadNextPageCallback(LoadNextPageCallback loadNextPageCallback) {
        this.loadNextPageCallback = loadNextPageCallback;
    }

    /**
     * Callback for requesting data by distance (offset) and page size (count).
     */
    public interface LoadNextPartCallback {

        /**
         * Called when adapter wants to load one more page.
         * @param distance Offset in current list.
         * @param pageSize Page size.
         */
        void loadNextPage(int distance, int pageSize);
    }

    /**
     * Callback for requesting data by page number.
     */
    public interface LoadNextPageCallback {

        /**
         * Called when adapter wants to load one more page.
         * @param page Page number.
         */
        void loadNextPage(int page);
    }

    private void incrementPage() {
        distance += pageSize;
        calculateCurrentPage();
    }

    private void decrementPage() {
        distance -= pageSize;
        calculateCurrentPage();
    }

    private void calculateCurrentPage() {
        currentPage = distance / pageSize + 1;
    }

    private void detectLimit(List<E> newData) {
        if (data.size() >= dataLimit || newData.size() < pageSize) {
            currentSate = PageState.REACHED_LIMIT;
        }
    }

    private void loadMore() {
        if (loadNextPartCallback != null) {
            loadNextPartCallback.loadNextPage(distance + pageSize, pageSize);
        } else if (loadNextPageCallback != null) {
            loadNextPageCallback.loadNextPage(currentPage + 1);
        }

        PageState prev = currentSate;
        currentSate = PageState.LOADING;
        if (prev == PageState.LOADED) {
            notifyItemInserted(data.size());
        } else if (prev == PageState.ERROR) {
            notifyItemChanged(data.size());
        }
    }

    private final DefaultErrorViewHolder.RetryDelegate delegateImpl = this::retry;

    protected class PagedScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (currentSate == PageState.REACHED_LIMIT
                    || currentSate == PageState.LOADING
                    || currentSate == PageState.ERROR
                    || dy == 0) {
                return;
            }

            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof LinearLayoutManager) {
                int totalItemCount = manager.getItemCount();
                int lastVisibleItemPosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItemPosition + loadThreshold)) {
                    loadMore();
                }
            }

            // TODO: GridLayoutManager
        }
    }
}
