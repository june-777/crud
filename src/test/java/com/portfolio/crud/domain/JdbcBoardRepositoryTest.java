package com.portfolio.crud.domain;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class JdbcBoardRepositoryTest {

    Board testBoard;
    List<Board> testBoardsForFindAll;
    BoardRepository repository;

    @BeforeEach
    void init() {
        repository = initRepository();
    }

    @AfterEach
    @DisplayName("테스트용 게시판 삭제")
    void clear() {
        if (testBoardsForFindAll != null) {
            for (Board board : testBoardsForFindAll) {
                repository.delete(board.getId());
            }
        }
        if (testBoard != null) {
            repository.delete(testBoard.getId());
        }
    }

    @Test
    @DisplayName("게시판 생성")
    void create() {
        // given
        testBoard = new Board("익명1", "1234", "제목입니다!!.", "내용입니다.");
        // when
        Board saveBoard = repository.save(testBoard);
        // then
        assertThat(testBoard).isEqualTo(saveBoard);
        System.out.println(saveBoard);
    }

    @Test
    @DisplayName("게시판 조회")
    void read() {
        // given
        testBoard = new Board("익명1", "1234", "제목입니다.", "내용입니다.");
        Board saveBoard = repository.save(testBoard);

        Board findBoard = repository.findById(saveBoard.getId());
        assertThat(findBoard).isEqualTo(saveBoard);
    }

    @Test
    @DisplayName("게시판 수정")
    void update() {
        // given
        testBoard = new Board("익명1", "1234", "제목입니다.", "내용입니다.");
        Board saveBoard = repository.save(testBoard);

        Long updateId = saveBoard.getId();
        String updateTitle = "수정 제목입니다.";
        String updateContent = "수정 내용입니다.";

        // when
        repository.update(updateId, updateTitle, updateContent);
        Board updateBoard = repository.findById(updateId);
        // then
        assertThat(updateBoard.getTitle()).isEqualTo(updateTitle);
        assertThat(updateBoard.getContent()).isEqualTo(updateContent);
    }

    @Test
    @DisplayName("게시판 삭제")
    void delete() {
        // given
        testBoard = new Board("익명1", "1234", "제목입니다.", "내용입니다.");
        Board saveBoard = repository.save(testBoard);

        Long deleteId = saveBoard.getId();
        // when
        repository.delete(deleteId);
        // then
        assertThatThrownBy(() -> repository.findById(deleteId)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("게시판 전체조회")
    void findAll() {
        // given
        Board board1 = new Board("익명1", "1234", "제목입니다1.", "내용입니다.");
        Board board2 = new Board("익명2", "1234", "제목입니다2.", "내용입니다2.");
        Board board3 = new Board("익명3", "1234", "제목입니다3.", "내용입니다3.");
        repository.save(board1);
        repository.save(board2);
        repository.save(board3);
        // when
        testBoardsForFindAll = repository.findAll();
        // then
        assertThat(testBoardsForFindAll).contains(board1);
        assertThat(testBoardsForFindAll).contains(board2);
        assertThat(testBoardsForFindAll).contains(board3);
    }

    private BoardRepository initRepository() {
        String URL = "jdbc:mysql://localhost/crud";
        String USER = "root";
        String PASSWORD = "root1234";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        DataSource dataSource = new HikariDataSource(config);

        return new JdbcBoardRepository(dataSource);
    }
}