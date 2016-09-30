package service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import rules.CheckCertNoRule;
import rules.CheckNoItem;
import rules.CheckOtherNoRule;
import utils.EnumCertType;
import utils.ErrorStatus;
import utils.JdbcUtils;
import utils.TransLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CheckCertNoService extends Service<Integer> {
  private static Logger logger = TransLogger.getLogger(CheckCertNoService.class);

  private StringProperty partName = new SimpleStringProperty();

  public final String getPartName() {
    return partName.get();
  }

  public final void setPartName(String value) {
    partName.set(value);
  }

  public final StringProperty partNameProperty() {
    return partName;
  }

  protected Task<Integer> createTask() {
    return new Task<Integer>() {
      @Override
      public Integer call() throws SQLException {
        logger.info("开始时间 : " + (new java.util.Date()).toString());

        List<CheckNoItem> itemList = new ArrayList<>();

        int currPro = 0;
        Integer recordCount = JdbcUtils.getPartitionRecordCount(JdbcUtils.CHECK_CERTNO_TABLE_NAME,
          getPartName(), JdbcUtils.CHECK_CERTNO_CERT_TYPE);

        updateProgress(currPro, recordCount);
        updateTitle("线程" + getPartName() + " : " + recordCount + "条");
        Connection conn = null;
        Statement statement = null;
        PreparedStatement pstmtInvalid = null;
        PreparedStatement pstmtValid = null;
        PreparedStatement pstmtRemoveInvalid = null;
        ResultSet result = null;
        String sql = "";
        try {
          conn = JdbcUtils.getOracleConnection();

          sql = "SELECT /*+ parallel(t " + JdbcUtils.CHECK_CERTNO_PARALLEL + ") */ * FROM "
            + JdbcUtils.CHECK_CERTNO_TABLE_NAME + " PARTITION ("
            + getPartName() + ") t ";
          if (JdbcUtils.CHECK_CERTNO_CERT_TYPE.compareToIgnoreCase(EnumCertType.ALL.getCode()) != 0)
            sql += " where trim(certtype) = '" + JdbcUtils.CHECK_CERTNO_CERT_TYPE + "'";

          statement = conn.createStatement();
          result = statement.executeQuery(sql);
          while (result.next()) {
            updateProgress(++currPro, recordCount * 2);
            CheckNoItem item = new CheckNoItem();
            item.setPin(result.getInt("PIN"));
            item.setPersonId(result.getInt("PERSONID"));
            item.setFinanceCode(result.getString("FINANCECODE"));
            item.setName(result.getString("NAME"));
            item.setCertType(EnumCertType.getEnumCertType(result.getString("CERTTYPE")));
            item.setCertNo(result.getString("CERTNO"));
            item.setGetTime(result.getDate("GETTIME"));
            item.setRealCertType(item.getCertType());
            //类型不是身份证
            if (item.getCertType() != EnumCertType.SFZ &&
              item.getCertType() != EnumCertType.LSSFZ) {
              //真实类型是身份证的
              if (CheckCertNoRule.getInstance().isValidID(item.getCertNo()).size() == 0)
                item.setRealCertType(EnumCertType.SFZ);
            }

            itemList.add(item);
          }
          JdbcUtils.releaseStatement(statement, result);

          pstmtRemoveInvalid = conn.prepareStatement("delete from " + JdbcUtils.CHECK_CERTNO_TABLE_NAME + " where pin=?");
          pstmtValid = conn.prepareStatement("INSERT INTO /*+ APPEND*/ " + JdbcUtils.CHECK_CERTNO_TABLE_NAME +
            "_valid values (?,?,?,?,?,?,?,?,?)");
          pstmtInvalid = conn.prepareStatement("INSERT INTO /*+ APPEND*/ " + JdbcUtils.CHECK_CERTNO_TABLE_NAME +
            "_invalid values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
          conn.setAutoCommit(false);


          for (int i = 0; i < itemList.size(); i++) {
            try {
              updateProgress(++currPro, recordCount * 2);
              CheckNoItem item = itemList.get(i);

              List<ErrorStatus> retStatuses;
              if (item.getRealCertType() == EnumCertType.SFZ ||
                item.getRealCertType() == EnumCertType.LSSFZ)
                retStatuses = CheckCertNoRule.getInstance().isValidID(item.getCertNo());
              else
                retStatuses = CheckOtherNoRule.getInstance().isValidID(item);

              if (retStatuses.size() != 0) {
                if (JdbcUtils.CHECK_CERTNO_TABLE_NAME.contains("_invalid")) {
                  //源为错误表，仍错则跳出
                  continue;
                }
                pstmtInvalid.setInt(1, item.getPin());
                pstmtInvalid.setInt(2, item.getPersonId());
                pstmtInvalid.setString(3, item.getFinanceCode());
                pstmtInvalid.setString(4, item.getName());
                pstmtInvalid.setString(5, item.getCertType().getCode());
                pstmtInvalid.setString(6, item.getCertNo());
                pstmtInvalid.setDate(7, new java.sql.Date(item.getGetTime().getTime()));
                String msg = "";
                pstmtInvalid.setBoolean(8, false);
                pstmtInvalid.setBoolean(9, false);
                pstmtInvalid.setBoolean(10, false);
                pstmtInvalid.setBoolean(11, false);
                pstmtInvalid.setBoolean(12, false);
                pstmtInvalid.setBoolean(13, false);
                pstmtInvalid.setBoolean(14, false);

                for (int j = 0; j < retStatuses.size(); j++) {
                  if (retStatuses.get(j) == ErrorStatus.LENG_ERR)
                    pstmtInvalid.setBoolean(8, true);
                  if (retStatuses.get(j) == ErrorStatus.CHAR_ERR)
                    pstmtInvalid.setBoolean(9, true);
                  if (retStatuses.get(j) == ErrorStatus.BIRTH_ERR)
                    pstmtInvalid.setBoolean(10, true);
                  if (retStatuses.get(j) == ErrorStatus.REG_ERR)
                    pstmtInvalid.setBoolean(11, true);
                  if (retStatuses.get(j) == ErrorStatus.CHE_ERR)
                    pstmtInvalid.setBoolean(12, true);
                  if (retStatuses.get(j) == ErrorStatus.NO_NUM_ERR)
                    pstmtInvalid.setBoolean(13, true);
                  if (retStatuses.get(j) == ErrorStatus.ALL_SAME_ERR)
                    pstmtInvalid.setBoolean(14, true);

                  msg += retStatuses.get(j).getMsg() + ";";
                }
                pstmtInvalid.setString(15, msg);
                pstmtInvalid.addBatch();

              } else {
                if (JdbcUtils.CHECK_CERTNO_TABLE_NAME.contains("_invalid")) {
                  //源为错误表，无错则移除错误表，存入正确表
                  pstmtRemoveInvalid.setInt(1, item.getPin());
                  pstmtRemoveInvalid.addBatch();
                }

                pstmtValid.setInt(1, item.getPin());
                pstmtValid.setInt(2, item.getPersonId());
                pstmtValid.setString(3, item.getFinanceCode());
                pstmtValid.setString(4, item.getName());
                pstmtValid.setString(5, item.getCertType().getCode());
                pstmtValid.setString(6, item.getCertNo());
                pstmtValid.setDate(7, new java.sql.Date(item.getGetTime().getTime()));
                pstmtValid.setString(8, item.getRealCertType().getCode());
                //15位转18位
                if (item.getRealCertType() == EnumCertType.SFZ ||
                  item.getRealCertType() == EnumCertType.LSSFZ)
                  pstmtValid.setString(9, CheckCertNoRule.getInstance().covertTo18(item.getCertNo()).toUpperCase());
                else
                  pstmtValid.setString(9, item.getCertNo());

                pstmtValid.addBatch();
              }
            } catch (SQLIntegrityConstraintViolationException cve) {
              logger.info(" pin : " + itemList.get(i).getPin() + "已存在");
              continue;
            } catch (SQLException se) {
              logger.warning(sql);
              se.printStackTrace();
              logger.info(se.getMessage().trim() + " pin : " + itemList.get(i).getPin());
              continue;
            } catch (Exception e) {
              logger.warning(sql);
              e.printStackTrace();
              logger.info(e.getMessage().trim() + " pin : " + itemList.get(i).getPin());
              continue;
            }

            // 分段提交
            if ((i % 50000 == 0 && i != 0) || i == (itemList.size() - 1)) {
              pstmtRemoveInvalid.executeBatch();
              pstmtInvalid.executeBatch();
              pstmtValid.executeBatch();

              conn.commit();
              conn.setAutoCommit(false);// 开始事务
              pstmtRemoveInvalid = conn.prepareStatement("delete from " + JdbcUtils.CHECK_CERTNO_TABLE_NAME + " where pin=?");
              pstmtValid = conn.prepareStatement("INSERT INTO /*+ APPEND*/ " + JdbcUtils.CHECK_CERTNO_TABLE_NAME +
                "_valid values (?,?,?,?,?,?,?,?)");
              pstmtInvalid = conn.prepareStatement("INSERT INTO /*+ APPEND*/ " + JdbcUtils.CHECK_CERTNO_TABLE_NAME +
                "_invalid values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
            }
          }
          conn.commit();
        } catch (SQLException se) {
          se.printStackTrace();
          logger.warning(se.getMessage());
        } catch (Exception e) {
          e.printStackTrace();
          logger.warning(e.getMessage());
        } finally {
          try {
            JdbcUtils.releaseStatement(statement, result);
            JdbcUtils.releaseStatement(pstmtInvalid);
            JdbcUtils.releaseStatement(pstmtValid);
            JdbcUtils.releaseStatement(pstmtRemoveInvalid);
            JdbcUtils.releaseConn(conn);
            itemList.clear();
            logger.info("结束时间 : " + (new java.util.Date()).toString());
          } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.getMessage());
          }
        }
        itemList.clear();
        return 1;
      }

    };
  }

}

