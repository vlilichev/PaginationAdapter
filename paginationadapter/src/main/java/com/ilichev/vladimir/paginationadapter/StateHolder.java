package com.ilichev.vladimir.paginationadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class StateHolder extends RecyclerView.ViewHolder {

    protected PageState state;

    public StateHolder(View itemView) {
        super(itemView);
    }

    public void setState(PageState state) {
        this.state = state;
        render();
    }

    protected abstract void render();
}
