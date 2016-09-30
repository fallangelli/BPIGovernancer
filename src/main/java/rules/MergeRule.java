package rules;

import com.alibaba.druid.util.StringUtils;
import utils.JdbcUtils;
import utils.TransLogger;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/3/21.
 */
public class MergeRule {
  private final static int[] VERIFY_CODE_WEIGHT = {7, 9, 10, 5, 8, 4, 2, 1,
    6, 3, 7, 9, 10, 5, 8, 4, 2};// 18位身份证中，各个数字的生成校验码时的权值
  private final static Date MINIMAL_BIRTH_DATE = new Date(-2209017600000L); // 身份证的最小出生日期,1900年1月1日
  private final static int NEW_CARD_NUMBER_LENGTH = 18;
  private final static int OLD_CARD_NUMBER_LENGTH = 15;
  private final static MergeRule INSTANCE = new MergeRule();
  private static Logger logger = TransLogger.getLogger(MergeRule.class);
  String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"};

  //  Regex reg15 = new Regex(@"^[1-9]d{7}((0[1-9])|(1[0-2]))(([0[1-9]|1d|2d])|3[0-1])d{2}([0-9]|x|X){1}$");
//  Regex reg18 = new Regex(@"^[1-9]\d{5}[1-9]\d{3}((0\[1-9]))|((1[0-2]))(([0\[1-9]|1\d|2\d])|3[0-1])\d{3}([0-9]|x|X){1}$");
  private MergeRule() {
  }

  public static MergeRule getInstance() {
    return INSTANCE;
  }

  public static void main(String[] args) {
    Long b = Long.valueOf("700000", 8);
    MergeName.NAME_TYPE a = MergeName.NAME_TYPE.getNameType((long) 8);
  }

  private static String getCompareName(MergeName toMerge) {
    MergeName.NAME_TYPE type = toMerge.getType();
    String retName;
    switch (type) {
      case H:
        retName = toMerge.getH_Name();
        break;
      case HS:
        retName = toMerge.getHS_Name();
        break;
      case HSB:
        retName = toMerge.getHSB_Name();
        break;
      case HSBC:
        retName = toMerge.getHSBC_Name();
        break;
      case HSBCO:
      case OTHER:
      default:
        retName = toMerge.getHSBCO_Name();
        break;
    }
    return retName;
  }

  private static String getComparePinyin(MergeName toMerge) {
    MergeName.NAME_TYPE type = toMerge.getType();
    String retPinyin;
    switch (type) {
      case H:
        retPinyin = toMerge.getH_Pinyin().toLowerCase();
        break;
      case HS:
        retPinyin = toMerge.getHS_Pinyin().toLowerCase();
        break;
      case HSB:
        retPinyin = toMerge.getHSB_Pinyin().toLowerCase();
        break;
      case HSBC:
        retPinyin = toMerge.getHSBC_Pinyin().toLowerCase();
        break;
      case HSBCO:
        retPinyin = toMerge.getHSBCO_Pinyin().toLowerCase();
        break;
      case OTHER:
      default:
        retPinyin = toMerge.getHSBCO_Pinyin().toLowerCase();
        break;
    }
    return retPinyin.toLowerCase();
  }

  public static void doMerge(MergePerson mergePerson) {
    List<MergeName> nameList = mergePerson.getCertNames();
    if (nameList == null)
      return;
    MergeName base = nameList.get(0);
    base.setSimilarity((long) 0);
    String baseName = getCompareName(base);
    String basePinyin = getComparePinyin(base);
    String baseOriName = base.getName();
    String baseOriPinyin = base.getPinyin();

    for (int i = 1; i < nameList.size(); i++) {
      MergeName name = nameList.get(i);
      MergeName.NAME_TYPE type = name.getType();
      char simiDelta = '0';

      String compOriName = name.getName();
      String compOriPinyin = name.getPinyin();
      boolean matchOriName = baseOriName.compareToIgnoreCase(compOriName) == 0 ? true : false;
      boolean matchOriPinyin = baseOriPinyin.compareToIgnoreCase(compOriPinyin) == 0 ? true : false;
      if (matchOriName || matchOriPinyin) {
        simiDelta = '7';
      } else {
        String compName = getCompareName(name);
        String compPinyin = getComparePinyin(name);
        if (StringUtils.isEmpty(compName) || StringUtils.isEmpty(compPinyin) ||
          StringUtils.isEmpty(baseName) || StringUtils.isEmpty(basePinyin)) {
          simiDelta = '1';
        } else {

          boolean matchName = false;
          boolean matchPinyin = false;
          if (name.getType() == MergeName.NAME_TYPE.HSB) {
            String[] subName = compName.split(",");
            String[] subPinyin = compPinyin.split(",");
            for (String sub : subName) {
              if (sub.compareToIgnoreCase(baseName) == 0) {
                matchName = true;
                break;
              }
            }
            for (String sub : subPinyin) {
              if (sub.compareToIgnoreCase(basePinyin) == 0) {
                matchName = true;
                break;
              }
            }
          } else {
            matchName = baseName.compareToIgnoreCase(compName) == 0 ? true : false;
            matchPinyin = basePinyin.compareToIgnoreCase(compPinyin) == 0 ? true : false;
          }
          if (!matchName && !matchPinyin)
            simiDelta = '1';
          else if (matchName && !matchPinyin)
            simiDelta = '3';
          else if (!matchName && matchPinyin)
            simiDelta = '4';
          else if (matchName && matchPinyin)
            simiDelta = '6';
        }
      }
      String oriSimi = Long.toOctalString(type.getLevel());
      oriSimi = oriSimi.replace('0', '6');

      String simi = simiDelta + oriSimi.substring(1, oriSimi.length());

      name.setSimilarity(Long.valueOf(simi));
    }

    checkValid(mergePerson);
  }

  private static boolean checkValid(MergePerson mergePerson) {
    Boolean retValid = true;
    for (int i = 0; i < mergePerson.getCertNames().size(); i++) {
      MergeName curr = mergePerson.getCertNames().get(i);
      //基准字比较type
      if (curr.getSimilarity().compareTo((long) 0) == 0) {
        boolean compType = JdbcUtils.EXTRACT_RESULT_TYPE_THRESHOLD.compareTo(Integer.parseInt(curr.getType().toString())) > 0;
        //如果类型阈值大于类型值，无效
        if (compType) {
          retValid = false;
          break;
        }
      } else {
        Integer bitThreshold = Integer.parseInt(curr.getSimilarity().toString().substring(0, 1));
        boolean compBit = JdbcUtils.EXTRACT_RESULT_BIT_THRESHOLD.compareTo(bitThreshold) > 0;
        //如果首位阈值大于首位，无效
        if (compBit) {
          retValid = false;
          break;
        }
      }
    }
    mergePerson.setValid(retValid);
    return retValid;
  }

  private boolean tryRemoveChar(String oriName, String desName) {

    String regex = "([\u4e00-\u9fa5]+)";
    String tmpTar1 = "";
    Matcher matcher1 = Pattern.compile(regex).matcher(oriName);
    while (matcher1.find()) {
      tmpTar1 += matcher1.group(0);
    }

    String tmpTar2 = "";
    Matcher matcher2 = Pattern.compile(regex).matcher(desName);
    while (matcher2.find()) {
      tmpTar2 += matcher2.group(0);
    }
    System.out.println(tmpTar1 + ":" + tmpTar2);

    return true;
  }


}
