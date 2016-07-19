package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/6/21.
 */
public class FileUtils {
  public static String getMergeValidFileName() throws IOException {

//    boolean isInvalidFile = JdbcUtils.MERGE_FILE_TABLE_NAME.contains("_mergeInvalid");
//    String validFileName;
//    if (isInvalidFile) {
//      SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
//      validFileName = "results/" + JdbcUtils.MERGE_FILE_TABLE_NAME.replace("_mergeInvalid", "_mergeValid")
//        + "_" + df.format(new Date());
//    } else {
//      validFileName = "results/" + JdbcUtils.MERGE_FILE_TABLE_NAME + "_mergeValid";
//    }


//    return validFileName;
    return "";
  }

  public static String getMergeInvalidFileName() throws IOException {
//    boolean isInvalidFile = JdbcUtils.MERGE_FILE_TABLE_NAME.contains("_mergeInvalid");
    String invalidFileName;
//    if (isInvalidFile) {
//      SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
//      invalidFileName = "results/" + JdbcUtils.MERGE_FILE_TABLE_NAME + "_" + df.format(new Date());
//    } else {
//      invalidFileName = "results/" + JdbcUtils.MERGE_FILE_TABLE_NAME + "_mergeInvalid";
//    }
//    return invalidFileName;
    return "";
  }

  private static void backupFile(String file) {
    File toBeRenamed = new File(file);
    //检查要重命名的文件是否存在，是否是文件
    if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
      return;
    }

    //备份文件
    File renameTo = new File(file);
    System.out.println("back up exists file: " + file);
    SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
    File backupFile = new File(file + "_" + df.format(new Date()));
    renameTo.renameTo(backupFile);

  }


}
