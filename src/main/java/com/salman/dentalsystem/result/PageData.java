package com.salman.dentalsystem.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class PageData<T> {
    private int totalPages;
    private long totalElements;
    private boolean firstPage;
    private boolean lastPage;
    private int page;
    private int size;
    private List<T> content;
}
