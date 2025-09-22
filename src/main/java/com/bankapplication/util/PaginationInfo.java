package com.bankapplication.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationInfo {

    private int pageSize;
    private int page;
    private int totalPages;
    private long totalElements;
}
