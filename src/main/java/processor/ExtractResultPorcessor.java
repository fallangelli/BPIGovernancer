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
    Connection conn = null;
    CallableStatement stmt = null;

    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return false;

      stmt = conn.prepareCall("call extract_results ( ?, ?, ?, ?, ?)");
      stmt.setString(1, JdbcUtils.EXTRACT_RESULT_TABLE_NAME);
      stmt.setInt(2, JdbcUtils.EXTRACT_RESULT_TYPE_THRESHOLD);
      stmt.setInt(3, JdbcUtils.EXTRACT_RESULT_BIT_THRESHOLD);
      stmt.setInt(4, JdbcUtils.EXTRACT_RESULT_DIS_THRESHOLD);
      stmt.setInt(5, JdbcUtils.EXTRACT_RESULT_JW_THRESHOLD);

      stmt.executeUpdate();

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
