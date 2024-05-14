package com.retailcloud.empmgt.model.payload;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PagedEntity<E> {
    private List<E> entityList;
    private int page;
    private long totalPages;
    private boolean hasNext;
    private boolean hasPrev;
}
