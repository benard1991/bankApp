package com.bankapplication.dto;

import com.bankapplication.util.PaginationInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPageResponse<T> {
    private List<T> data;
    private PaginationInfo pagination;
}
