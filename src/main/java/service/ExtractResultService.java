package service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
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
        updateProgress(0, 1);
        logger.info("开始时间 : " + (new java.util.Date()).toString());
        Boolean result = ExtractResultPorcessor.extractResults();
        String msg = "";
        if (result) {
          msg = "执行完成！结果请查看表 " + JdbcUtils.EXTRACT_RESULT_TABLE_NAME + "_merge_valid 和 " +
            JdbcUtils.EXTRACT_RESULT_TABLE_NAME + "_merge_invalid";
        } else {
          msg = "提取结果出现错误！！";
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("完成");
        alert.setHeaderText("提取结果");
        alert.setContentText(msg);
        alert.showAndWait();
        updateProgress(1, 1);
        return null;
      }
    };
  }
}
