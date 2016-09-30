package rules;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/3/21.
 */
public class NormalizeRule {

  public static MergeName doNormalize(MergeName mergeName) throws SQLException {
    if (getNameForm(mergeName.getName()) == NAME_FORM.HZ) {
      return mergeHZName(mergeName);
    } else if (getNameForm(mergeName.getName()) == NAME_FORM.YW) {
      return mergeYWName(mergeName);
    } else {
      mergeName.setType(MergeName.NAME_TYPE.OTHER);
      return mergeName;
    }
  }

  public static MergeName.NAME_TYPE getNameType(String oriName) {
    if (getNameForm(oriName) == NAME_FORM.HZ) {
      return getHZNameType(oriName);
    } else if (getNameForm(oriName) == NAME_FORM.YW) {
      return getYWNameType(oriName);
    } else {
      return MergeName.NAME_TYPE.OTHER;
    }
  }

  public static MergeName.NAME_TYPE getHZNameType(String oriName) {
    Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
    Matcher m = p.matcher(oriName);
    if (!m.find()) {
      return MergeName.NAME_TYPE.OTHER;
    }

    String tmpName = oriName.replaceAll("([\u4e00-\u9fa5]+)", "");
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.H;
    }

    tmpName = removeS(tmpName);
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.HS;
    }

    tmpName = removeSB(tmpName);
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.HSB;
    }

    tmpName = removeSBC(tmpName);
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.HSBC;
    }

    tmpName = removeSBCO(tmpName);
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.HSBCO;
    }

    return MergeName.NAME_TYPE.OTHER;
  }

  public static MergeName.NAME_TYPE getYWNameType(String oriName) {
    Pattern p = Pattern.compile("[a-zA-Z]");
    Matcher m = p.matcher(oriName);
    if (!m.find()) {
      return MergeName.NAME_TYPE.OTHER;
    }

    String tmpName = oriName.replaceAll("([a-zA-Z]+)", "");
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.H;
    }

    tmpName = removeS(tmpName);
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.HS;
    }

    tmpName = removeSB(tmpName);
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.HSB;
    }

    tmpName = removeSBN(tmpName);
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.HSBC;
    }

    tmpName = removeSBCO(tmpName);
    if (tmpName.length() == 0) {
      return MergeName.NAME_TYPE.HSBCO;
    }

    return MergeName.NAME_TYPE.OTHER;
  }

  private static MergeName mergeHZName(MergeName mergeName) {
    MergeName.NAME_TYPE type = getNameType(mergeName.getName());
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
        String tmpName1 = NormalizeRule.removeS(tmpName).replace("(", "");
        tmpName1 = tmpName1.replace(")", "");
        tmpName1 = tmpName1.replace("（", "");
        tmpName1 = tmpName1.replace("）", "");
        tmpName1 = tmpName1.replace("[", "");
        tmpName1 = tmpName1.replace("]", "");
        tmpName1 = tmpName1.replace("{", "");
        tmpName1 = tmpName1.replace("}", "");

        tmpName = NormalizeRule.removeSB(tmpName);

        mergeName.setHSB_Name(tmpName + "," + tmpName1);
        mergeName.setHSB_Pinyin(NormalizeRule.getPY(tmpName + "," + tmpName1));
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

  private static MergeName mergeYWName(MergeName mergeName) {
    MergeName.NAME_TYPE type = getNameType(mergeName.getName());
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
        String tmpName1 = NormalizeRule.removeS(tmpName).replace("(", "");
        tmpName1 = tmpName1.replace(")", "");
        tmpName1 = tmpName1.replace("（", "");
        tmpName1 = tmpName1.replace("）", "");
        tmpName1 = tmpName1.replace("[", "");
        tmpName1 = tmpName1.replace("]", "");
        tmpName1 = tmpName1.replace("{", "");
        tmpName1 = tmpName1.replace("}", "");

        tmpName = NormalizeRule.removeSB(tmpName);

        mergeName.setHSB_Name(tmpName + "," + tmpName1);
        mergeName.setHSB_Pinyin(NormalizeRule.getPY(tmpName + "," + tmpName1));
        break;
      case HSBC:
        tmpName = NormalizeRule.removeSBN(tmpName);
        mergeName.setHSBC_Name(tmpName);
        mergeName.setHSBC_Pinyin(NormalizeRule.getPY(tmpName));
        break;
      case HSBCO:
      case OTHER:
      default:
        tmpName = NormalizeRule.removeSBNO(tmpName);
        mergeName.setHSBCO_Name(tmpName);
        mergeName.setHSBCO_Pinyin(NormalizeRule.getPY(tmpName));
        break;
    }

    mergeName.setSimilarity((long) -1);

    return mergeName;
  }

  //去空格
  public static String removeS(String oriName) {
    String retVal = oriName.replace(" ", "");
    retVal = retVal.replace("　", "");
    return retVal;
  }

  //去空格/括号对及其中的内容
  public static String removeSB(String oriName) {
    String retVal = removeS(oriName);
    Pattern p = Pattern.compile("((?<=\\().+?(?=\\)))");
    Matcher m = p.matcher(retVal);
    retVal = m.replaceAll("");

    p = Pattern.compile("(?<=\\（)(.+?)(?=\\）)");
    m = p.matcher(retVal);
    retVal = m.replaceAll("");


    p = Pattern.compile("(?<=\\[)(.+?)(?=\\])");
    m = p.matcher(retVal);
    retVal = m.replaceAll("");

    p = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
    m = p.matcher(retVal);
    retVal = m.replaceAll("");

    retVal = retVal.replace("()", "");
    retVal = retVal.replace("（）", "");
    retVal = retVal.replace("[]", "");
    retVal = retVal.replace("{}", "");
    return retVal;
  }


  //去空格/括号对及其中的内容/英文字母
  public static String removeSBC(String oriName) {
    String retVal = removeSB(oriName);

    retVal = retVal.replaceAll("[0-9a-zA-Z]", "");
    retVal = retVal.replaceAll("[０]", "");
    retVal = retVal.replaceAll("[１]", "");
    retVal = retVal.replaceAll("[２]", "");
    retVal = retVal.replaceAll("[３]", "");
    retVal = retVal.replaceAll("[４]", "");
    retVal = retVal.replaceAll("[５]", "");
    retVal = retVal.replaceAll("[６]", "");
    retVal = retVal.replaceAll("[７]", "");
    retVal = retVal.replaceAll("[８]", "");
    retVal = retVal.replaceAll("[９]", "");
    return retVal;
  }

  //去空格/括号对及其中的内容/数字
  public static String removeSBN(String oriName) {
    String retVal = removeSB(oriName);

    retVal = retVal.replaceAll("[0-9]", "");
    retVal = retVal.replaceAll("[０]", "");
    retVal = retVal.replaceAll("[１]", "");
    retVal = retVal.replaceAll("[２]", "");
    retVal = retVal.replaceAll("[３]", "");
    retVal = retVal.replaceAll("[４]", "");
    retVal = retVal.replaceAll("[５]", "");
    retVal = retVal.replaceAll("[６]", "");
    retVal = retVal.replaceAll("[７]", "");
    retVal = retVal.replaceAll("[８]", "");
    retVal = retVal.replaceAll("[９]", "");
    return retVal;
  }

  //去空格/括号对及其中的内容/英文字母/其他特殊字符
  public static String removeSBCO(String oriName) {
    String tmpTar1 = removeSBC(oriName);

    String regex = "([\u4e00-\u9fa5]+)";
    String retVal = "";
    Matcher matcher1 = Pattern.compile(regex).matcher(tmpTar1);
    while (matcher1.find()) {
      retVal += matcher1.group(0);
    }

    return retVal;
  }

  //去空格/括号对及其中的内容/英文字母/其他特殊字符
  public static String removeSBNO(String oriName) {
    String tmpTar1 = removeSBN(oriName);

    String regex = "([a-zA-Z]+)";
    String retVal = "";
    Matcher matcher1 = Pattern.compile(regex).matcher(tmpTar1);
    while (matcher1.find()) {
      retVal += matcher1.group(0);
    }

    return retVal;
  }

  public static NAME_FORM getNameForm(String name) {
    Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
    Matcher m = p.matcher(name);
    if (m.find()) {
      return NAME_FORM.HZ;
    } else {
      p = Pattern.compile("[a-zA-Z]");
      m = p.matcher(name);
      if (m.find()) {
        return NAME_FORM.YW;
      } else {
        return NAME_FORM.OTHER;
      }
    }
  }

  public static String getPY(String name) {

    return PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE)
      .replace("　", "").replace(" ", "").toLowerCase();
//    return Cn2Spell.converterToSpell(name);
  }

  public static void main(String[] args) {
    String oriName = "“邓 小#秋% (a bc)abFRT c[ 等]\" (小)\"　王强（中石油）　　　YU JUN  {  ADSF的饿}测(asdf测}";
    System.out.println(NormalizeRule.removeS(oriName));
    System.out.println(NormalizeRule.removeSB(oriName));
    System.out.println(NormalizeRule.removeSBC(oriName));
    System.out.println(NormalizeRule.removeSBCO(oriName));
    System.out.println(NormalizeRule.getPY("吴？城"));

    System.out.println(NormalizeRule.getPY("邓小#秋%"));

    System.out.println(NormalizeRule.getPY("邓小 秋"));


    oriName = "sdfjbsdf";
    System.out.println(NormalizeRule.getNameForm(oriName));
    System.out.println(NormalizeRule.getYWNameType(oriName));

    oriName = "sdfjb123213sdasdfasdff";
    System.out.println(NormalizeRule.getNameForm(oriName));
    System.out.println(NormalizeRule.getYWNameType(oriName));
  }

  public enum NAME_FORM {
    HZ,// "包含汉字的"
    YW,// "不含汉字，含英文的"
    OTHER
  }


}
