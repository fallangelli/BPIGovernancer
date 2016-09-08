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
public class ExtractResultPorcessor {
  private static Logger logger = TransLogger.getLogger(ExtractResultPorcessor.class);

  private ExtractResultPorcessor() {
  }

  public static boolean extractResults() {
    if (JdbcUtils.EXTRACT_RESULT_TYPE != 0 && JdbcUtils.EXTRACT_RESULT_TYPE != 1)
      return true;

    Connection conn = null;
    CallableStatement stmt = null;

    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return false;


      stmt = conn.prepareCall("CALL extract_results ( ?, ?, ? )");
      stmt.setString(1, JdbcUtils.EXTRACT_RESULT_TABLE_NAME);
      stmt.setInt(2, JdbcUtils.EXTRACT_RESULT_TYPE_THRESHOLD);
      stmt.setInt(3, JdbcUtils.EXTRACT_RESULT_BIT_THRESHOLD);
      stmt.executeUpdate();
      stmt.close();

    } catch (SQLException e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      logger.warning(e.getMessage());
      return false;
    } catch (Exception e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      logger.warning(e.getMessage());
      return false;
    } finally {
      try {
        JdbcUtils.releaseStatement(stmt);
        JdbcUtils.releaseConn(conn);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return true;
  }


  public static boolean extractSim2Table() {
    if (JdbcUtils.EXTRACT_RESULT_TYPE != 0 && JdbcUtils.EXTRACT_RESULT_TYPE != 2)
      return true;

    Connection conn = null;
    CallableStatement stmt = null;

    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return false;

      stmt = conn.prepareCall("CALL SIM_TABLE_2 ( ?, ?, ?)");
      stmt.setString(1, JdbcUtils.EXTRACT_RESULT_TABLE_NAME);
      stmt.setInt(2, JdbcUtils.SIM_2_DIS_THRESHOLD);
      stmt.setInt(3, JdbcUtils.SIM_2_JW_THRESHOLD);
      stmt.executeUpdate();
      stmt.close();

    } catch (SQLException e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      logger.warning(e.getMessage());
      return false;
    } catch (Exception e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      logger.warning(e.getMessage());
      return false;
    } finally {
      try {
        JdbcUtils.releaseStatement(stmt);
        JdbcUtils.releaseConn(conn);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return true;
  }


  public static boolean extractSim3Table() {
    if (JdbcUtils.EXTRACT_RESULT_TYPE != 0 && JdbcUtils.EXTRACT_RESULT_TYPE != 3)
      return true;

    Connection conn = null;
    CallableStatement stmt = null;

    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return false;

      stmt = conn.prepareCall("CALL SIM_TABLE_3 ( ?, ?, ?, ?, ?, ?, ? )");
      stmt.setString(1, JdbcUtils.EXTRACT_RESULT_TABLE_NAME);
      stmt.setInt(2, JdbcUtils.SIM_3_1_2_DIS_THRESHOLD);
      stmt.setInt(3, JdbcUtils.SIM_3_1_3_DIS_THRESHOLD);
      stmt.setInt(4, JdbcUtils.SIM_3_2_3_DIS_THRESHOLD);
      stmt.setInt(5, JdbcUtils.SIM_3_1_2_JW_THRESHOLD);
      stmt.setInt(6, JdbcUtils.SIM_3_1_3_JW_THRESHOLD);
      stmt.setInt(7, JdbcUtils.SIM_3_2_3_JW_THRESHOLD);
      stmt.executeUpdate();
      stmt.close();
    } catch (SQLException e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      logger.warning(e.getMessage());
      return false;
    } catch (Exception e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      logger.warning(e.getMessage());
      return false;
    } finally {
      try {
        JdbcUtils.releaseStatement(stmt);
        JdbcUtils.releaseConn(conn);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return true;
  }

  public static boolean extractSim4Table() {
    if (JdbcUtils.EXTRACT_RESULT_TYPE != 0 && JdbcUtils.EXTRACT_RESULT_TYPE != 4)
      return true;

    Connection conn = null;
    CallableStatement stmt = null;

    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return false;

      stmt = conn.prepareCall("CALL SIM_TABLE_4 ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
      stmt.setString(1, JdbcUtils.EXTRACT_RESULT_TABLE_NAME);
      stmt.setInt(2, JdbcUtils.SIM_4_1_2_DIS_THRESHOLD);
      stmt.setInt(3, JdbcUtils.SIM_4_1_3_DIS_THRESHOLD);
      stmt.setInt(4, JdbcUtils.SIM_4_1_4_DIS_THRESHOLD);
      stmt.setInt(5, JdbcUtils.SIM_4_2_3_DIS_THRESHOLD);
      stmt.setInt(6, JdbcUtils.SIM_4_2_4_DIS_THRESHOLD);
      stmt.setInt(7, JdbcUtils.SIM_4_3_4_DIS_THRESHOLD);
      stmt.setInt(8, JdbcUtils.SIM_4_1_2_JW_THRESHOLD);
      stmt.setInt(9, JdbcUtils.SIM_4_1_3_JW_THRESHOLD);
      stmt.setInt(10, JdbcUtils.SIM_4_1_4_JW_THRESHOLD);
      stmt.setInt(11, JdbcUtils.SIM_4_2_3_JW_THRESHOLD);
      stmt.setInt(12, JdbcUtils.SIM_4_2_4_JW_THRESHOLD);
      stmt.setInt(13, JdbcUtils.SIM_4_3_4_JW_THRESHOLD);
      stmt.executeUpdate();
      stmt.close();
    } catch (SQLException e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      logger.warning(e.getMessage());
      return false;
    } catch (Exception e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      logger.warning(e.getMessage());
      return false;
    } finally {
      try {
        JdbcUtils.releaseStatement(stmt);
        JdbcUtils.releaseConn(conn);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return true;
  }
}
