package processor;

import utils.JdbcUtils;
import utils.TransLogger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2016/3/21.
 */
public class MergeProcessor {

  private static Logger logger = TransLogger.getLogger(MergeProcessor.class);


  public static void createDescTable() {
    Connection conn = null;
    CallableStatement stmt = null;
    String tableName = JdbcUtils.MERGE_TABLE_NAME;
    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return;

      stmt = conn.prepareCall("call new_merge_desc_table ( ? )");
      stmt.setString(1, tableName);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.warning("create table " + tableName);
      logger.warning(e.getMessage());
    } catch (Exception e) {
      logger.warning("create table " + tableName);
      logger.warning(e.getMessage());
    } finally {
      try {
        JdbcUtils.releaseStatement(stmt);
        JdbcUtils.releaseConn(conn);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
  }

  public static void createSourceTable() {
    Connection conn = null;
    CallableStatement stmt = null;
    String tableName = JdbcUtils.MERGE_TABLE_NAME;
    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return;

      stmt = conn.prepareCall("call new_duplicate_table ( ? )");
      stmt.setString(1, tableName);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.warning("create table " + tableName);
      logger.warning(e.getMessage());
    } catch (Exception e) {
      logger.warning("create table " + tableName);
      logger.warning(e.getMessage());
    } finally {
      try {
        JdbcUtils.releaseStatement(stmt);
        JdbcUtils.releaseConn(conn);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
  }
}
