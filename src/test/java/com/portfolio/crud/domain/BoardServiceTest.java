package com.portfolio.crud.domain;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;

class BoardServiceTest {

    BoardRepository boardRepository;
    BoardService boardService;

    Board testBoard;

    @BeforeEach
    void init() {
        boardRepository = initRepository();
        boardService = new BoardService(boardRepository);
    }

    @AfterEach
    @DisplayName("테스트용 게시판 삭제")
    void clear() {
        boardRepository.delete(testBoard.getId());
    }

    @Nested
    class SuccessTest {
        @Test
        @DisplayName("게시판 삭제")
        void removeBoard() {
            // given
            testBoard = new Board("작가1", "1231", "제목!!", "내용입니드아이");
            Board saveBoard = boardRepository.save(testBoard);
            // when
            boardService.removeBoard(saveBoard.getId(), saveBoard.getPassword());
            // then
            assertThatThrownBy(() -> boardRepository.findById(saveBoard.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("게시판 수정")
        void modifyBoard() {
            // given
            testBoard = new Board("작가1", "1231", "제목!!", "내용입니드아이");
            Board saveBoard = boardRepository.save(testBoard);
            String modifyTitle = "제목 이걸로 수정";
            String modifyContent = "내용 수정 슥슥";
            // when
            boardService.modifyBoard(saveBoard.getId(), saveBoard.getPassword(), modifyTitle, modifyContent);
            Board findBoard = boardRepository.findById(saveBoard.getId());
            // then
            assertThat(findBoard.getTitle()).isEqualTo(modifyTitle);
            assertThat(findBoard.getContent()).isEqualTo(modifyContent);
        }
    }

    @Nested
    class FailTest {
        @Test
        @DisplayName("게시판 삭제 - 잘못된 게시판 비밀번호 입력")
        void removeBoardWithWrongPassword() {
            // given
            testBoard = new Board("작가1", "1231", "제목!!", "내용입니드아이");
            Board saveBoard = boardRepository.save(testBoard);
            // when
            String wrongPassword = "1232";
            // then
            assertThatThrownBy(() -> boardService.removeBoard(saveBoard.getId(), wrongPassword))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("게시판 수정 - 잘못된 게시판 비밀번호 입력")
        void modifyBoardWithWrongPassword() {
            // given
            testBoard = new Board("작가1", "1231", "제목!^_^!", "내용~~");
            Board saveBoard = boardRepository.save(testBoard);
            String wrongPassword = "1234";
            String modifyTitle = "제목 이걸로 수정";
            String modifyContent = "내용 수정 슥슥";
            // when
            boardService.modifyBoard(saveBoard.getId(), wrongPassword, modifyTitle, modifyContent);
            Board findBoard = boardRepository.findById(saveBoard.getId());
            // then
            assertThat(findBoard.getTitle()).isNotEqualTo(modifyTitle);
            assertThat(findBoard.getContent()).isNotEqualTo(modifyContent);
        }

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