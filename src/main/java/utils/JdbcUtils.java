package utils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public class JdbcUtils {
  public static String COUNT_PARALLEL = null;

  public static String CHECK_CERTNO_TABLE_NAME = null;
  public static String CHECK_CERTNO_CERT_TYPE = null;
  public static String CHECK_CERTNO_PARALLEL = null;

  public static String MERGE_TABLE_NAME = null;
  public static String MERGE_CERT_TYPE = null;
  public static String MERGE_PARALLEL = null;

  public static String EXTRACT_RESULT_TABLE_NAME = null;
  public static Integer EXTRACT_RESULT_TYPE_THRESHOLD = null;
  public static Integer EXTRACT_RESULT_BIT_THRESHOLD = null;
  public static Integer EXTRACT_RESULT_DIS_THRESHOLD = null;
  public static Integer EXTRACT_RESULT_JW_THRESHOLD = null;

  public static String CHECK_DIC_CODE_TABLE_NAME = null;
  public static char[] INVALID_CHARS = null;
  private static Logger logger = TransLogger.getLogger(JdbcUtils.class);

  static {
    loadPorperties();
  }

  public static void loadPorperties() {
    try {
      //读取db.properties文件中的数据库连接信息
      InputStream in = JdbcUtils.class.getClassLoader().getResourceAsStream("config.properties");
      Properties prop = new Properties();
      prop.load(in);

      COUNT_PARALLEL = prop.getProperty("count_parallel");

      //获取目标表名
      CHECK_CERTNO_TABLE_NAME = prop.getProperty("check_certno_table_name");
      //获取目标类型
      CHECK_CERTNO_CERT_TYPE = prop.getProperty("check_certno_cert_type");
      CHECK_CERTNO_PARALLEL = prop.getProperty("check_certno_parallel");

      //获取目标表名
      MERGE_TABLE_NAME = prop.getProperty("merge_table_name");
      //获取目标类型
      MERGE_CERT_TYPE = prop.getProperty("merge_cert_type");
      MERGE_PARALLEL = prop.getProperty("merge_parallel");

      //获取目标表名
      EXTRACT_RESULT_TABLE_NAME = prop.getProperty("extract_result_table_name");
      //获取阈值
      EXTRACT_RESULT_TYPE_THRESHOLD = Integer.parseInt(prop.getProperty("extract_result_type_threshold"));
      //获取阈值
      EXTRACT_RESULT_BIT_THRESHOLD = Integer.parseInt(prop.getProperty("extract_result_bit_threshold"));
      //获取阈值
      EXTRACT_RESULT_DIS_THRESHOLD = Integer.parseInt(prop.getProperty("extract_result_dis_threshold"));
      //获取阈值
      EXTRACT_RESULT_JW_THRESHOLD = Integer.parseInt(prop.getProperty("extract_result_jw_threshold"));


      //获取目标类型
      CHECK_DIC_CODE_TABLE_NAME = prop.getProperty("check_dic_code_table_name");


    } catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public static Connection getOracleConnection() throws InterruptedException {
    try {
      DataSource dataSource = DataSourceUtil.getInstance().getDataSource();
      Connection conn = dataSource.getConnection();
      return conn;
    } catch (Exception e) {
      logger.severe(e.getMessage());
      e.printStackTrace();
    }

    return null;
  }

  public static Integer getPartitionRecordCount(String tableName, String partName, String type) {
    Connection conn = null;
    Statement statement = null;
    ResultSet result = null;
    String sql = "";
    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return -1;

      sql = "SELECT /*+ parallel(t " + COUNT_PARALLEL + ") */ count(*) FROM " +
        tableName + " PARTITION (" + partName + ") t where certtype = '" + type + "'";
      statement = conn.createStatement();
      statement.setQueryTimeout(43200);
      result = statement.executeQuery(sql);
      if (result.next()) {
        return result.getInt(1);
      }
      return 0;
    } catch (SQLException e) {
      logger.warning(sql);
      logger.warning(e.getMessage());
    } catch (Exception e) {
      logger.warning(sql);
      logger.warning(e.getMessage());

    } finally {
      try {
        JdbcUtils.releaseStatement(statement, result);
        JdbcUtils.releaseConn(conn);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return 0;
  }


  public static void releaseStatement(Statement st) {

    if (st != null) {
      try {
        //关闭负责执行SQL命令的Statement对象
        st.close();
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }

  }

  public static void releaseStatement(Statement st, ResultSet rs) {
    if (rs != null) {
      try {
        //关闭存储查询结果的ResultSet对象
        rs.close();
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
      rs = null;
    }
    if (st != null) {
      try {
        //关闭负责执行SQL命令的Statement对象
        st.close();
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }

  }


  public static void releaseConn(Connection conn) {
    if (conn != null) {
      try {
        //关闭Connection数据库连接对象
        conn.close();
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
  }

}
