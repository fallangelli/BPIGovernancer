package processor;

import utils.JdbcUtils;
import utils.TransLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2016/3/21.
 */
public class CheckCertNoProcessor {

  public static List<Integer> ALL_REGION = new ArrayList<Integer>();


  private static Logger logger = TransLogger.getLogger(CheckCertNoProcessor.class);

  static {
    loadRegion();
  }

  public CheckCertNoProcessor() {
    createDescTable();
  }

  public static void createDescTable() {
    Connection conn = null;
    CallableStatement stmt = null;

    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return;


      stmt = conn.prepareCall("call new_check_certno_desc_table ( ? )");
      stmt.setString(1, JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      stmt.executeUpdate();

    } catch (SQLException e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
      logger.warning(e.getMessage());

    } catch (Exception e) {
      logger.warning("create table " + JdbcUtils.CHECK_CERTNO_TABLE_NAME);
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

  private static boolean loadRegion() {
    Connection conn = null;
    Statement statement = null;
    ResultSet result = null;
    String sql = "";
    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return false;

      sql = "SELECT * FROM DIC_REGION";
      statement = conn.createStatement();
      statement.setQueryTimeout(43200);
      result = statement.executeQuery(sql);
      while (result.next()) {
        ALL_REGION.add(result.getInt("reg_code"));
      }
      return true;
    } catch (SQLException e) {
      logger.warning(sql);
      logger.severe(e.getMessage());
    } catch (Exception e) {
      logger.warning(sql);
      logger.severe(e.getMessage());
    } finally {
      try {
        JdbcUtils.releaseStatement(statement, result);
        JdbcUtils.releaseConn(conn);
      } catch (Exception e) {
        logger.severe(e.getMessage());
      }
    }
    return false;
  }


}
