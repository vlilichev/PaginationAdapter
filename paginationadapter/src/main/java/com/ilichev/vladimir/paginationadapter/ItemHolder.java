package com.ilichev.vladimir.paginationadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ItemHolder<E> extends RecyclerView.ViewHolder {

    // Item to render
    protected E item;

    public ItemHolder(View itemView) {
        super(itemView);
    }

    void setItem(E item) {
        this.item = item;
        render();
    }

    protected abstract void render();
}
