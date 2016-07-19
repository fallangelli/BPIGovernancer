package service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import rules.*;
import utils.JdbcUtils;
import utils.TransLogger;

import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class MergeWorkService extends Service<Integer> {
  private static Logger logger = TransLogger.getLogger(MergeWorkService.class);
  List<MergePerson> personList = new ArrayList<>();
  private StringProperty partName = new SimpleStringProperty();
  private FileWriter os;

  public List<MergePerson> getPersonList() {
    return personList;
  }

  public void setPersonList(List<MergePerson> personList) {
    this.personList = personList;
  }

  public final String getPartName() {
    return partName.get();
  }

  public final void setPartName(String value) {
    partName.set(value);
  }

  public final StringProperty partNameProperty() {
    return partName;
  }

  public FileWriter getOs() {
    return os;
  }

  public void setOs(FileWriter os) {
    this.os = os;
  }

  private MergeName getMergeNameFromDBResult(ResultSet result) throws SQLException {
    MergeName mergeName = new MergeName();
    mergeName.setPin(result.getInt("PIN"));
    mergeName.setPersonID(result.getInt("PERSONID"));
    mergeName.setFinanceCode(result.getString("FINANCECODE"));
    mergeName.setCertType(result.getString("CERTTYPE"));
    mergeName.setCertNo(result.getString("CERTNO"));
    mergeName.setCertNo_18(result.getString("CERTNO_18"));
    mergeName.setTime(result.getDate("GETTIME"));
    mergeName.setName(result.getString("NAME"));
    mergeName.setPinyin(NormalizeRule.getPY(result.getString("NAME")));

    MergeName.NAME_TYPE type = NormalizeRule.getNameType(mergeName.getName());
    mergeName.setType(type);
    String tmpName = mergeName.getName();
    switch (type) {
      case H:
        mergeName.setH_Name(tmpName);
        mergeName.setH_Pinyin(NormalizeRule.getPY(tmpName));
        break;
      case HS:
        tmpName = NormalizeRule.removeS(tmpName);
        mergeName.setHS_Name(tmpName);
        mergeName.setHS_Pinyin(NormalizeRule.getPY(tmpName));
        break;
      case HSB:
        tmpName = NormalizeRule.removeSB(tmpName);
        mergeName.setHSB_Name(tmpName);
        mergeName.setHSB_Pinyin(NormalizeRule.getPY(tmpName));
        break;
      case HSBC:
        tmpName = NormalizeRule.removeSBC(tmpName);
        mergeName.setHSBC_Name(tmpName);
        mergeName.setHSBC_Pinyin(NormalizeRule.getPY(tmpName));
        break;
      case HSBCO:
      case OTHER:
      default:
        tmpName = NormalizeRule.removeSBCO(tmpName);
        mergeName.setHSBCO_Name(tmpName);
        mergeName.setHSBCO_Pinyin(NormalizeRule.getPY(tmpName));
        break;
    }
    mergeName.setSimilarity((long) -1);
    return mergeName;
  }

  private void addNameToWapperPerson(MergeName name) {
    if (getPersonList().size() > 0 &&
      name.getCertNo_18().compareToIgnoreCase(getPersonList().get(getPersonList().size() - 1).getCertNo()) == 0) {
      getPersonList().get(getPersonList().size() - 1).getCertNames().add(name);
    } else {
      MergePerson newPersn = new MergePerson();
      newPersn.setCertNo(name.getCertNo_18());
      newPersn.setCertType(name.getCertType());
      newPersn.setCertNames(new ArrayList<MergeName>());
      newPersn.getCertNames().add(name);
      getPersonList().add(newPersn);
    }
  }

  private void sortNameTypes() {
    for (int i = 0; i < getPersonList().size(); i++) {
      MergePerson person = getPersonList().get(i);
      Collections.sort(person.getCertNames());
    }
  }

  private void saveResultToDB(PreparedStatement pstmt, MergeName mergeName) throws SQLException {
    pstmt.setInt(1, mergeName.getPin());
    pstmt.setInt(2, mergeName.getPersonID());
    pstmt.setString(3, mergeName.getFinanceCode());
    pstmt.setString(4, mergeName.getCertType());
    pstmt.setString(5, mergeName.getCertNo());
    pstmt.setString(6, mergeName.getCertNo_18());
    pstmt.setDate(7, new java.sql.Date(mergeName.getTime().getTime()));
    pstmt.setString(8, mergeName.getName());
    pstmt.setString(9, mergeName.getPinyin());
    pstmt.setString(10, mergeName.getType().toString());
    pstmt.setString(11, mergeName.getH_Name());
    pstmt.setString(12, mergeName.getH_Pinyin());
    pstmt.setString(13, mergeName.getHS_Name());
    pstmt.setString(14, mergeName.getHS_Pinyin());
    pstmt.setString(15, mergeName.getHSB_Name());
    pstmt.setString(16, mergeName.getHSB_Pinyin());
    pstmt.setString(17, mergeName.getHSBC_Name());
    pstmt.setString(18, mergeName.getHSBC_Pinyin());
    pstmt.setString(19, mergeName.getHSBCO_Name());
    pstmt.setString(20, mergeName.getHSBCO_Pinyin());
    pstmt.setLong(21, mergeName.getSimilarity());
    try {
      pstmt.addBatch();
    } catch (SQLIntegrityConstraintViolationException cve) {
      logger.info(" pin : " + mergeName.getPin() + "已存在");
    }
  }

  private void saveAllResultsToXml(WapperName wapperName) {
//    try {
//      File f = new File("results");
//      f.mkdir();
//      String resultFileName = "results/" + JdbcUtils.MERGE_TABLE_NAME + "_normalize" + getIndexStart() + "-" + getIndexEnd();
//      os = new FileWriter(resultFileName, true);
//      logger.info("开始写入文件 : " + resultFileName);
//
//      JAXBContext context = JAXBContext.newInstance(WapperName.class);
//      Marshaller marshaller = context.createMarshaller();
//      marshaller.marshal(wapperName, os);
//      os.write("\r\n");
//
//    } catch (IOException e) {
//      logger.severe(e.getMessage());
//    } catch (JAXBException e) {
//      logger.severe(e.getMessage());
//    } finally {
//      try {
//        if (os != null)
//          os.close();
//        logger.info("文件已关闭！结束时间 : " + (new java.util.Date()).toString());
//      } catch (Exception e) {
//        logger.severe(e.getMessage());
//      }
//    }
  }


  protected Task<Integer> createTask() {
    return new Task<Integer>() {
      @Override
      public Integer call() throws SQLException {
        logger.info("开始时间 : " + (new java.util.Date()).toString());

        Integer recordCount = JdbcUtils.getPartitionRecordCount(JdbcUtils.MERGE_TABLE_NAME + "_duplicate",
          getPartName(), JdbcUtils.MERGE_CERT_TYPE);
        updateProgress(0, 3);
        updateTitle("线程" + getPartName() + " : " + recordCount + "条");

        Connection conn = null;
        Statement statement = null;
        PreparedStatement pstmt = null;
        ResultSet result = null;
        String sql = "";
        try {
          conn = JdbcUtils.getOracleConnection();
          if (conn == null) {
            logger.severe("无法得到数据库连接");
            return -1;
          }


          sql = "SELECT /*+ parallel(t " + JdbcUtils.MERGE_PARALLEL + ") */ * FROM "
            + JdbcUtils.MERGE_TABLE_NAME + "_duplicate PARTITION ("
            + getPartName() + ")  t where certtype = '" + JdbcUtils.MERGE_CERT_TYPE + "'" +
            " order by certno_18";
          statement = conn.createStatement();

          pstmt = conn.prepareStatement("INSERT INTO /*+ APPEND*/ " + JdbcUtils.MERGE_TABLE_NAME +
            "_merge values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

          result = statement.executeQuery(sql);
          conn.setAutoCommit(false);

          int count = 0;
          int total = recordCount * 3;
          while (result.next()) {
            updateProgress(count++, recordCount);
            MergeName name = getMergeNameFromDBResult(result);
            MergeName merged = NormalizeRule.doNormalize(name);
            addNameToWapperPerson(merged);
          }

          JdbcUtils.releaseStatement(statement, result);


          int size = getPersonList().size();
          if (size <= 0) {
            return 1;
          }

          logger.fine("线程" + getPartName() + "开始排序");
          sortNameTypes();

          total = recordCount + size * 2;
          for (int i = 0; i < size; i++) {
            updateProgress(recordCount + i, total);
            MergePerson mergePerson = getPersonList().get(i);
            MergeRule.doMerge(mergePerson);
          }

          logger.fine("线程" + getPartName() + "开始写回");
          for (int i = 0; i < getPersonList().size(); i++) {
            updateProgress(recordCount + size + i, total);
            MergePerson mergePerson = getPersonList().get(i);
            for (int j = 0; j < mergePerson.getCertNames().size(); j++) {
              saveResultToDB(pstmt, mergePerson.getCertNames().get(j));
            }
            // 分段提交
            if ((i % 10000 == 0 && i != 0) || i == (getPersonList().size() - 1)) {
              Thread.sleep(4000);
              pstmt.executeBatch();
              conn.commit();
              conn.setAutoCommit(false);// 开始事务
              pstmt = conn.prepareStatement("INSERT INTO /*+ APPEND*/ " + JdbcUtils.MERGE_TABLE_NAME +
                "_merge values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            }
          }
          conn.commit();

        } catch (SQLException se) {
          logger.warning(sql);
          se.printStackTrace();
        } catch (Exception e) {
          logger.warning(sql);
          e.printStackTrace();
        } finally {
          try {
            JdbcUtils.releaseStatement(statement, result);
            JdbcUtils.releaseStatement(pstmt);
            JdbcUtils.releaseConn(conn);
            personList.clear();
          } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
          }
        }
        personList.clear();
        return null;
      }
    };
  }
}
