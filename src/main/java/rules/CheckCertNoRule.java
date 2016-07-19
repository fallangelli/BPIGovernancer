package rules;

import processor.CheckCertNoProcessor;
import utils.TransLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/3/21.
 */
public class CheckCertNoRule {
  private final static int[] VERIFY_CODE_WEIGHT = {7, 9, 10, 5, 8, 4, 2, 1,
    6, 3, 7, 9, 10, 5, 8, 4, 2};// 18位身份证中，各个数字的生成校验码时的权值
  private final static Date MINIMAL_BIRTH_DATE = new Date(-2209017600000L); // 身份证的最小出生日期,1900年1月1日
  private final static int NEW_CARD_NUMBER_LENGTH = 18;
  private final static int OLD_CARD_NUMBER_LENGTH = 15;
  private final static CheckCertNoRule INSTANCE = new CheckCertNoRule();
  private static Logger logger = TransLogger.getLogger(CheckCertNoRule.class);
  String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"};

  //  Regex reg15 = new Regex(@"^[1-9]d{7}((0[1-9])|(1[0-2]))(([0[1-9]|1d|2d])|3[0-1])d{2}([0-9]|x|X){1}$");
//  Regex reg18 = new Regex(@"^[1-9]\d{5}[1-9]\d{3}((0\[1-9]))|((1[0-2]))(([0\[1-9]|1\d|2\d])|3[0-1])\d{3}([0-9]|x|X){1}$");
  private CheckCertNoRule() {
  }

  public static CheckCertNoRule getInstance() {
    return INSTANCE;
  }

  public static void main(String[] args) {
    String id = "33022219660926001?";
    CheckCertNoRule.getInstance().isValidID(id);
  }

  public String covertTo18(String id) throws Exception {
    if (id != null && id.length() == 15) {
      StringBuilder sb = new StringBuilder();
      sb.append(id.substring(0, 6))
        .append("19")
        .append(id.substring(6));
      sb.append(getVerifyCode(sb.toString()));
      return sb.toString();
    } else {
      return id;
    }
  }

  private char getVerifyCode(String idCardNumber) throws Exception {
    if (idCardNumber == null || idCardNumber.length() < 17) {
      throw new Exception("不合法的身份证号码");
    }
    char[] Ai = idCardNumber.toCharArray();
    int[] Wi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    char[] verifyCode = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    int S = 0;
    int Y;
    for (int i = 0; i < Wi.length; i++) {
      S += (Ai[i] - '0') * Wi[i];
    }
    Y = S % 11;
    return verifyCode[Y];
  }

  public List<errorStatus> isValidID(String id) {
    List<errorStatus> retStatuses = new ArrayList<>();
    if (!checkSize(id))
      retStatuses.add(errorStatus.LENG_ERR);
    if (!checkCharacter(id))
      retStatuses.add(errorStatus.CHAR_ERR);
    if (!checkBirthday(id))
      retStatuses.add(errorStatus.BIRTH_ERR);
    if (!checkCheckCode(id))
      retStatuses.add(errorStatus.CHE_ERR);
    if (!checkRegion(id))
      retStatuses.add(errorStatus.REG_ERR);

    return retStatuses;
  }

  //18位数字+X或x;15位全数字
  private boolean checkCharacter(String id) {
    if (id.matches("^\\d{17}([0-9]|x|X)$|^\\d{15}$")) return true;
    return false;
  }

  //18位数字+X或x;15位全数字
  private boolean checkBirthday(String id) {
    String year = "";
    String month = "";
    String day = "";
    //15位
    if (id.length() == OLD_CARD_NUMBER_LENGTH) {
      Pattern pattern = Pattern.compile("^(\\d{6})(\\d{2})(\\d{2})(\\d{2})(\\d{3})$");
      Matcher matcher = pattern.matcher(id);
      if (matcher.find()) {
        year = "19" + matcher.group(2);
        month = matcher.group(3);
        day = matcher.group(4);
      } else {
        logger.fine("身份证号出生日期无法正则匹配:" + id);
        return false;
      }
    } else if (id.length() == NEW_CARD_NUMBER_LENGTH) {
      Pattern pattern = Pattern.compile("^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\w{4})$");
      Matcher matcher = pattern.matcher(id);
      if (matcher.find()) {
        year = matcher.group(2);
        month = matcher.group(3);
        day = matcher.group(4);
      } else {
        logger.fine("身份证号无法正则提取:" + id);
        return false;
      }
    } else {
      return false;
    }

    String strDate = year + month + day;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    try {
      Date ndate = format.parse(strDate);
      String str = format.format(ndate);
      boolean result = false;
      if (str.equals(strDate)) {
        result = true;
        result = result && ndate.before(new Date());
        result = result && ndate.after(MINIMAL_BIRTH_DATE);
      }
      return result;
    } catch (ParseException e) {
      logger.fine("出生日期校验错误:" + id);
      logger.warning(e.getMessage());
      return false;
    }
  }

  private boolean checkCheckCode(String id) {
    //15位无校验位
    if (id.length() == OLD_CARD_NUMBER_LENGTH)
      return true;
    else if (id.length() == NEW_CARD_NUMBER_LENGTH) {
      try {
        int TotalmulAiWi = 0;
        char pszSrc[] = id.toCharArray();
        String Ai = id.substring(0, 17);
        for (int i = 0; i < 17; i++) {
          TotalmulAiWi += (int) (pszSrc[i] - '0') * VERIFY_CODE_WEIGHT[i];
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (Ai.compareToIgnoreCase(id) == 0)
          return true;
        else
          return false;
      } catch (Exception e) {
        logger.warning("计算校验位错误:" + id);
        logger.severe(e.getMessage());
        return false;
      }
    } else
      return true;
  }

  //长度15或18
  private boolean checkRegion(String id) {
    try {
      Integer regCode = Integer.parseInt(id.substring(0, 6));
      if (CheckCertNoProcessor.ALL_REGION.indexOf(regCode) > -1) {
        return true;
      } else
        return false;
    } catch (Exception e) {
      logger.warning("提取行政区划代码异常:" + id);
      return false;
    }
  }

  //长度15或18
  private boolean checkSize(String id) {
    if (id.length() == 15 || id.length() == 18)
      return true;
    else
      return false;
  }

  public static enum errorStatus {
    LENG_ERR("长度无效"),
    CHAR_ERR("包含非法字符"),
    BIRTH_ERR("出生日期格式错误"),
    REG_ERR("无效行政区划"),
    CHE_ERR("验证码错误");

    private String msg;

    private errorStatus(String msg) {
      this.msg = msg;
    }

    public String getMsg() {
      return msg;
    }
  }

}
