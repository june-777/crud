package com.portfolio.crud.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class JdbcBoardRepository implements BoardRepository {

    private final DataSource dataSource;

    @Autowired
    public JdbcBoardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Board save(Board board) {
        String sql = "insert into board(author, password, title, content) values(?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, board.getAuthor());
            pstmt.setString(2, board.getPassword());
            pstmt.setString(3, board.getTitle());
            pstmt.setString(4, board.getContent());
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            log.info("rs = {}", rs.getStatement());
            if (rs.next()) {
                board.setId(rs.getBigDecimal(1).longValue());
            }
            log.info("board = {}", board);
            return board;
        } catch (SQLException e) {
            log.info("db error - save", e);
            throw new IllegalStateException(e);
        } finally {
            closeResource(con, pstmt, rs);
        }
    }

    @Override
    public Board findById(Long id) {
        String sql = "select * from board where id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Board board = new Board(
                        rs.getString("author"),
                        rs.getString("password"),
                        rs.getString("title"),
                        rs.getString("content"));
                board.setId(rs.getLong("id"));
                log.info("find board = {}", board);
                return board;
            } else {
                throw new IllegalStateException("해당 id의 게시판이 없습니다 = " + id);
            }
        } catch (SQLException e) {
            log.info("db error - find", e);
            throw new IllegalStateException(e);
        } finally {
            closeResource(con, pstmt, rs);
        }
    }

    @Override
    public List<Board> findAll() {
        String sql = "select * from board";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            List<Board> boards = new ArrayList<>();
            while (rs.next()) {
                Board board = new Board(
                        rs.getString("author"),
                        rs.getString("password"),
                        rs.getString("title"),
                        rs.getString("content"));
                board.setId(rs.getLong("id"));
                boards.add(board);
            }
            return boards;
        } catch (SQLException e) {
            log.info("db - error", e);
            throw new IllegalStateException(e);
        } finally {
            closeResource(con, pstmt, rs);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from board where id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.info("db error - delete", e);
        } finally {
            closeResource(con, pstmt, rs);
        }
    }

    @Override
    public void update(Long id, String title, String content) {
        String sql = "update board set title = ?, content = ? where id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setLong(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.info("db error - update", e);
        } finally {
            closeResource(con, pstmt, rs);
        }
    }

    private void closeResource(Connection con, PreparedStatement pstmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(pstmt);
        JdbcUtils.closeConnection(con);
    }
}