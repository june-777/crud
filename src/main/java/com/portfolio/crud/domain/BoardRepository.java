package com.portfolio.crud.domain;

import java.util.List;

public interface BoardRepository {
    Board save(Board board);

    Board findById(Long id);

    List<Board> findAll();

    void delete(Long id);

    void update(Long id, String title, String content);
}