package rules;

import utils.EnumCertType;
import utils.ErrorStatus;
import utils.TransLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2016/3/21.
 */
public class CheckOtherNoRule {
  private final static CheckOtherNoRule INSTANCE = new CheckOtherNoRule();
  private static Logger logger = TransLogger.getLogger(CheckOtherNoRule.class);

  private CheckOtherNoRule() {
  }

  public static CheckOtherNoRule getInstance() {
    return INSTANCE;
  }

  public static void main(String[] args) {
    CheckNoItem item = new CheckNoItem();
    String id = "abcd";
    item.setCertNo(id);
    item.setCertType(EnumCertType.SFZ);
    List<ErrorStatus> errors = CheckOtherNoRule.getInstance().isValidID(item);
    id = "___#####";
    item.setCertNo(id);
    errors = CheckOtherNoRule.getInstance().isValidID(item);
    id = "00000000";
    item.setCertNo(id);
    errors = CheckOtherNoRule.getInstance().isValidID(item);
    id = "11";
    item.setCertNo(id);
    errors = CheckOtherNoRule.getInstance().isValidID(item);
    id = "12abcd";
    item.setCertNo(id);
    errors = CheckOtherNoRule.getInstance().isValidID(item);
    id = "d12";
    item.setCertNo(id);
    errors = CheckOtherNoRule.getInstance().isValidID(item);
    id = "123213";
    item.setCertNo(id);
    errors = CheckOtherNoRule.getInstance().isValidID(item);
    id = "a1汉字4c2d2";
    item.setCertNo(id);
    errors = CheckOtherNoRule.getInstance().isValidID(item);
    id = "&^*^%";
    item.setCertNo(id);
    errors = CheckOtherNoRule.getInstance().isValidID(item);
    id = "$#^&12abcd";
    item.setCertNo(id);
    errors = CheckOtherNoRule.getInstance().isValidID(item);
  }


  public List<ErrorStatus> isValidID(CheckNoItem item) {
    List<ErrorStatus> retStatuses = new ArrayList<>();
    if (!checkSize(item))
      retStatuses.add(ErrorStatus.LENG_ERR);
    if (!checkContainNum(item.getCertNo()))
      retStatuses.add(ErrorStatus.NO_NUM_ERR);
    if (!checkAllSame(item.getCertNo()))
      retStatuses.add(ErrorStatus.ALL_SAME_ERR);


    return retStatuses;
  }

  //必须包含数字
  private boolean checkContainNum(String id) {
    if (id.matches("(.*)?\\d+(.*)?")) return true;
    else if (id.contains("０") || id.contains("１") || id.contains("２") ||
      id.contains("３") || id.contains("４") || id.contains("５") ||
      id.contains("６") || id.contains("７") || id.contains("８") ||
      id.contains("９"))
      return true;
    return false;
  }

  //不能全一样
  private boolean checkAllSame(String id) {
    char[] charArray = id.toCharArray();
    char base = charArray[0];
    for (char item : charArray) {
      if (item != base)
        return true;
    }
    return false;
  }


  //长度大于2
  private boolean checkSize(CheckNoItem item) {
    if (item.getCertNo().length() > item.getRealCertType().getMinLenth())
      return true;
    else
      return false;
  }

}
