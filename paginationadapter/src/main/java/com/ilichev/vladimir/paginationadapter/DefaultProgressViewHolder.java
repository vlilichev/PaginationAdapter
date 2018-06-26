package com.ilichev.vladimir.paginationadapter;

import android.view.View;
import android.widget.ProgressBar;

class DefaultProgressViewHolder extends StateHolder {

    private ProgressBar progressBar;

    DefaultProgressViewHolder(View itemView) {
        super(itemView);

        progressBar = itemView.findViewById(R.id.paginationadapter_default_progress);
    }

    @Override
    public void render() {
        if (state == PageState.LOADING) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
