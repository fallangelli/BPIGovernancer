package service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import processor.ExtractResultPorcessor;
import utils.JdbcUtils;
import utils.TransLogger;

import java.sql.SQLException;
import java.util.logging.Logger;

public class ExtractResultService extends Service<Integer> {
  private static Logger logger = TransLogger.getLogger(ExtractResultService.class);


  protected Task<Integer> createTask() {
    return new Task<Integer>() {
      @Override
      public Integer call() throws SQLException {
        logger.info("开始时间 : " + (new java.util.Date()).toString());
        updateProgress(0, 4);
        updateTitle("执行相似度提取中...");
        Boolean result = ExtractResultPorcessor.extractResults();
        updateProgress(1, 4);
        updateTitle("执行 同号两名 提取中...");
        Boolean result1 = ExtractResultPorcessor.extractSim2Table();
        updateProgress(2, 4);
        updateTitle("执行 同号三名 提取中...");
        Boolean result2 = ExtractResultPorcessor.extractSim3Table();
        updateProgress(3, 4);
        updateTitle("执行 同号四名 提取中...");
        Boolean result3 = ExtractResultPorcessor.extractSim4Table();
        updateProgress(4, 4);
        String msg;
        if (result && result1 && result2 && result3) {
          msg = "执行完成！结果请查看表 " + JdbcUtils.EXTRACT_RESULT_TABLE_NAME + "_merge_valid 和 " +
            JdbcUtils.EXTRACT_RESULT_TABLE_NAME + "_merge_invalid";
        } else {
          msg = "提取结果出现错误！！";
        }
        updateTitle(msg);

        return null;
      }
    };
  }
}
