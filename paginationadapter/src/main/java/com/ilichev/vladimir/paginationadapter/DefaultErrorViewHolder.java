package com.ilichev.vladimir.paginationadapter;

import android.view.View;
import android.widget.Button;

class DefaultErrorViewHolder extends StateHolder {

    private Button retryButton;
    private RetryDelegate delegateImpl;

    DefaultErrorViewHolder(View itemView, final RetryDelegate delegateImpl) {
        super(itemView);
        this.delegateImpl = delegateImpl;

        retryButton = itemView.findViewById(R.id.retry_page_load_button);
        retryButton.setOnClickListener(v -> delegateImpl.retry());
    }

    @Override
    protected void render() {
    }

    interface RetryDelegate {
        void retry();
    }
}
