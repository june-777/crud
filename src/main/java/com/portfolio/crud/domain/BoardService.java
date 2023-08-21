package com.portfolio.crud.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public void removeBoard(Long id, String password) {
        Board findBoard = boardRepository.findById(id);
        log.info("password = {}", password);
        log.info("findBoard = {}", findBoard);
        if (findBoard.getPassword().equals(password)) {
            boardRepository.delete(id);
        } else {
            throw new IllegalStateException("비밀번호가 틀립니다.");
        }
    }

}