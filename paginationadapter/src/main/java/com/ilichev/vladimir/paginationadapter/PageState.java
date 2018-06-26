package com.ilichev.vladimir.paginationadapter;

public enum PageState {
  //  +-----+  +------+  +--------+
  //  |     |  |      |  |        |
  //  |     v  |      v  |        v
    ERROR, LOADING, LOADED, REACHED_LIMIT
  //    ^      | ^       |
  //    |      | |       |
  //    +------+ +-------+
}
