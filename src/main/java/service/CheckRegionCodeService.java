package service;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import processor.CheckRegionCodeProcessor;
import utils.JdbcUtils;
import utils.TransLogger;

import java.sql.*;
import java.util.logging.Logger;

public class CheckRegionCodeService extends Service<Integer> {
  private static Logger logger = TransLogger.getLogger(CheckRegionCodeService.class);


  private IntegerProperty indexStart = new SimpleIntegerProperty();
  private IntegerProperty indexEnd = new SimpleIntegerProperty();

  public final Integer getIndexStart() {
    return indexStart.get();
  }

  public final void setIndexStart(Integer value) {
    indexStart.set(value);
  }

  public final IntegerProperty indexStartProperty() {
    return indexStart;
  }

  public final Integer getIndexEnd() {
    return indexEnd.get();
  }

  public final void setIndexEnd(Integer value) {
    indexEnd.set(value);
  }

  public final IntegerProperty indexEndProperty() {
    return indexEnd;
  }


  protected Task<Integer> createTask() {
    final Integer _start = getIndexStart();
    final Integer _end = getIndexEnd();

    return new Task<Integer>() {
      @Override
      public Integer call() throws SQLException {
        if (_end <= _start)
          return null;

        int currPro = 0;
        updateProgress(currPro, _end - _start);
        Connection conn = null;
        Statement statement = null;
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String sql = "";
        try {
          conn = JdbcUtils.getOracleConnection();
          logger.info("连接成功！");
          sql = "SELECT *  FROM (" +
            "             SELECT a.*, ROWNUM rn FROM (" +
            "             SELECT * FROM " + JdbcUtils.CHECK_DIC_CODE_TABLE_NAME + " " +
            "               WHERE  is_valid is null) a" +
            "         WHERE ROWNUM <= " + _end + ")\n" +
            " WHERE rn > " + _start;

          statement = conn.createStatement();
          result = statement.executeQuery(sql);
          statement.setQueryTimeout(43200);
          pstmt = conn.prepareStatement("update " + JdbcUtils.CHECK_DIC_CODE_TABLE_NAME +
            " set region_name = ?, is_valid = ? where region_code = ?");


          while (result.next()) {
            updateProgress(++currPro, _end - _start);
            String regCode = result.getString("region_code");
            if (regCode.length() != 6) {
              logger.warning("行政区划代码长度不为6 : " + regCode);
              continue;
            }
            String regName = result.getString("region_name");
            String fullCode = regCode + "198901228410";
            String name = CheckRegionCodeProcessor.checkRegionId5(fullCode);

            try {
              if (name != null && name.length() > 0) {
                if (regName != null && regName.length() > name.length())
                  pstmt.setString(1, regName);
                else
                  pstmt.setString(1, name);
                pstmt.setBoolean(2, true);
                pstmt.setString(3, regCode);
                pstmt.execute();
                //conn.commit();
              } else {
                if (regName != null && regName.length() > name.length())
                  pstmt.setString(1, regName);
                else
                  pstmt.setString(1, name);

                pstmt.setBoolean(2, false);
                pstmt.setString(3, regCode);
                pstmt.execute();
                conn.commit();
              }
            } catch (SQLException se) {
              logger.warning(sql);
              se.printStackTrace();
              continue;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          logger.warning(sql);
          logger.warning(e.getMessage());
        } finally {
          try {
            JdbcUtils.releaseStatement(pstmt);
            JdbcUtils.releaseStatement(statement, result);
            JdbcUtils.releaseConn(conn);
          } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.getMessage());
          }
        }
        return null;
      }
    };
  }
}
