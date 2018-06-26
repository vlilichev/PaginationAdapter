package com.ilichev.vladimir.paginationadapter;

import android.support.annotation.NonNull;

public final class PagingConfig {

    private final int dataLimit;
    private final int pageSize;
    private final int loadThreshold;

    private PagingConfig(int dataLimit, int pageSize, int loadThreshold) {
        this.dataLimit = dataLimit;
        this.pageSize = pageSize;
        this.loadThreshold = loadThreshold;
    }

    public int getDataLimit() {
        return dataLimit;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getLoadThreshold() {
        return loadThreshold;
    }

    public static final class Builder {

        // Init with default values
        private int dataLimit = Integer.MAX_VALUE;
        private int pageSize = 10;
        private int loadThreshold = 1;

        public Builder setDataLimit(int dataLimit) {
            this.dataLimit = dataLimit;
            return this;
        }

        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder setLoadThreshold(int loadThreshold) {
            this.loadThreshold = loadThreshold;
            return this;
        }

        @NonNull
        public PagingConfig build() {
            return new PagingConfig(dataLimit, pageSize, loadThreshold);
        }
    }
}
