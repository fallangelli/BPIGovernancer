package rules;

import utils.ErrorStatus;
import utils.TransLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2016/3/21.
 */
public class CheckOtherNoRule {

  private final static int MIN_CARD_NUMBER_LENGTH = 3;

  private final static CheckOtherNoRule INSTANCE = new CheckOtherNoRule();
  private static Logger logger = TransLogger.getLogger(CheckOtherNoRule.class);

  private CheckOtherNoRule() {
  }

  public static CheckOtherNoRule getInstance() {
    return INSTANCE;
  }

  public static void main(String[] args) {
    String id = "abcd";
    List<ErrorStatus> errors = CheckOtherNoRule.getInstance().isValidID(id);
    id = "___#####";
    errors = CheckOtherNoRule.getInstance().isValidID(id);
    id = "00000000";
    errors = CheckOtherNoRule.getInstance().isValidID(id);
    id = "11";
    errors = CheckOtherNoRule.getInstance().isValidID(id);
    id = "12abcd";
    errors = CheckOtherNoRule.getInstance().isValidID(id);
    id = "d12";
    errors = CheckOtherNoRule.getInstance().isValidID(id);
    id = "123213";
    errors = CheckOtherNoRule.getInstance().isValidID(id);
    id = "a1汉字4c2d2";
    errors = CheckOtherNoRule.getInstance().isValidID(id);
    id = "&^*^%";
    errors = CheckOtherNoRule.getInstance().isValidID(id);
    id = "$#^&12abcd";
    errors = CheckOtherNoRule.getInstance().isValidID(id);
  }


  public List<ErrorStatus> isValidID(String id) {
    List<ErrorStatus> retStatuses = new ArrayList<>();
    if (!checkSize(id))
      retStatuses.add(ErrorStatus.LENG_ERR);
    if (!checkContainNum(id))
      retStatuses.add(ErrorStatus.NO_NUM_ERR);
    if (!checkAllSame(id))
      retStatuses.add(ErrorStatus.ALL_SAME_ERR);


    return retStatuses;
  }

  //必须包含数字
  private boolean checkContainNum(String id) {
    if (id.matches("(.*)?\\d+(.*)?")) return true;
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
  private boolean checkSize(String id) {
    if (id.length() >= MIN_CARD_NUMBER_LENGTH)
      return true;
    else
      return false;
  }

}
