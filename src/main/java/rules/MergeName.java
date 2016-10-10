package rules;

import utils.TransLogger;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.logging.Logger;

@XmlRootElement
public class MergeName implements Comparable<MergeName> {
  private static Logger logger = TransLogger.getLogger(MergeName.class);

  Integer pin;
  Integer personID;
  String financeCode;
  String certType;
  String realCertType;
  String certNo;
  String certNo_18;
  Date time;
  String name;
  String pinyin;
  NAME_TYPE type;
  String H_Name;
  String HS_Name;
  String HSB_Name;
  String HSBC_Name;
  String HSBCO_Name;
  String H_Pinyin;
  String HS_Pinyin;
  String HSB_Pinyin;
  String HSBC_Pinyin;
  String HSBCO_Pinyin;
  Long similarity;

  @Override
  public int compareTo(MergeName o) {
    try {
      NAME_TYPE otherType = o.getType();

      if (NormalizeRule.getNameForm(this.getName()) == NormalizeRule.getNameForm(o.getName())) {
        if (otherType.getLevel().compareTo(this.getType().getLevel()) == 0) {
          //种类也相等，看时间
          Date otherTime = o.getTime();
          if (this.getTime() == null && otherTime == null)
            return 0;
          else if (this.getTime() == null && otherTime != null)
            return 1;
          else if (this.getTime() != null && otherTime == null)
            return -1;
          else if (this.getTime() != null && otherTime != null)
            return otherTime.compareTo(this.getTime());
        }

        return otherType.getLevel().compareTo(this.getType().getLevel());
      } else if (NormalizeRule.getNameForm(o.getName()) == NormalizeRule.NAME_FORM.HZ &&
        NormalizeRule.getNameForm(this.getName()) == NormalizeRule.NAME_FORM.YW) {
        return 1;
      } else if (NormalizeRule.getNameForm(o.getName()) == NormalizeRule.NAME_FORM.YW &&
        NormalizeRule.getNameForm(this.getName()) == NormalizeRule.NAME_FORM.HZ) {
        return -1;
      } else if (NormalizeRule.getNameForm(o.getName()) == NormalizeRule.NAME_FORM.OTHER) {
        return -1;
      } else if (NormalizeRule.getNameForm(this.getName()) == NormalizeRule.NAME_FORM.OTHER) {
        return 1;
      }
    } catch (Exception e) {
      logger.severe("name type is null!" + o.getCertNo() + ":" + o.getName());
      logger.severe(e.getMessage());
      logger.severe(o.toString());
      logger.severe(this.toString());
      logger.severe(o.getType().toString());
      logger.severe(this.getType().toString());
    }
    return 0;
  }

  public String toString() {
    return "pin:" + pin + "|" +
      "personID:" + personID + "|" +
      "financeCode:" + financeCode + "|" +
      "certType:" + certType + "|" +
      "realCertType:" + realCertType + "|" +
      "certNo:" + certNo + "|" +
      "certNo_18:" + certNo_18 + "|" +
      "time:" + time + "|" +
      "name:" + name + "|" +
      "pinyin:" + pinyin + "|" +
      "type:" + type + "|" +
      "H_Name:" + H_Name + "|" +
      "HS_Name:" + HS_Name + "|" +
      "HSB_Name:" + HSB_Name + "|" +
      "HSBC_Name:" + HSBC_Name + "|" +
      "HSBCO_Name:" + HSBCO_Name + "|" +
      "H_Pinyin:" + H_Pinyin + "|" +
      "HS_Pinyin:" + HS_Pinyin + "|" +
      "HSB_Pinyin:" + HSB_Pinyin + "|" +
      "HSBC_Pinyin:" + HSBC_Pinyin + "|" +
      "HSBCO_Pinyin:" + HSBCO_Pinyin + "|" +
      "similarity:" + similarity;
  }

  public Integer getPin() {
    return pin;
  }

  public void setPin(Integer pin) {
    this.pin = pin;
  }

  public Integer getPersonID() {
    return personID;
  }

  public void setPersonID(Integer personID) {
    this.personID = personID;
  }

  public String getFinanceCode() {
    return financeCode;
  }

  public void setFinanceCode(String financeCode) {
    this.financeCode = financeCode;
  }

  public String getCertType() {
    return certType;
  }

  public void setCertType(String certType) {
    this.certType = certType;
  }

  public String getRealCertType() {
    return realCertType;
  }

  public void setRealCertType(String realCertType) {
    this.realCertType = realCertType;
  }

  public String getCertNo() {
    return certNo;
  }

  public void setCertNo(String certNo) {
    this.certNo = certNo;
  }

  public String getCertNo_18() {
    return certNo_18;
  }

  public void setCertNo_18(String certNo_18) {
    this.certNo_18 = certNo_18;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPinyin() {
    return pinyin;
  }

  public void setPinyin(String pinyin) {
    this.pinyin = pinyin;
  }

  public NAME_TYPE getType() {
    return type;
  }

  public void setType(NAME_TYPE type) {
    this.type = type;
  }

  public String getH_Name() {
    return H_Name;
  }

  public void setH_Name(String h_Name) {
    H_Name = h_Name;
  }

  public String getHS_Name() {
    return HS_Name;
  }

  public void setHS_Name(String HS_Name) {
    this.HS_Name = HS_Name;
  }

  public String getHSB_Name() {
    return HSB_Name;
  }

  public void setHSB_Name(String HSB_Name) {
    this.HSB_Name = HSB_Name;
  }

  public String getHSBC_Name() {
    return HSBC_Name;
  }

  public void setHSBC_Name(String HSBC_Name) {
    this.HSBC_Name = HSBC_Name;
  }

  public String getHSBCO_Name() {
    return HSBCO_Name;
  }

  public void setHSBCO_Name(String HSBCO_Name) {
    this.HSBCO_Name = HSBCO_Name;
  }

  public String getH_Pinyin() {
    return H_Pinyin;
  }

  public void setH_Pinyin(String h_Pinyin) {
    H_Pinyin = h_Pinyin;
  }

  public String getHS_Pinyin() {
    return HS_Pinyin;
  }

  public void setHS_Pinyin(String HS_Pinyin) {
    this.HS_Pinyin = HS_Pinyin;
  }

  public String getHSB_Pinyin() {
    return HSB_Pinyin;
  }

  public void setHSB_Pinyin(String HSB_Pinyin) {
    this.HSB_Pinyin = HSB_Pinyin;
  }

  public String getHSBC_Pinyin() {
    return HSBC_Pinyin;
  }

  public void setHSBC_Pinyin(String HSBC_Pinyin) {
    this.HSBC_Pinyin = HSBC_Pinyin;
  }

  public String getHSBCO_Pinyin() {
    return HSBCO_Pinyin;
  }

  public void setHSBCO_Pinyin(String HSBCO_Pinyin) {
    this.HSBCO_Pinyin = HSBCO_Pinyin;
  }

  public Long getSimilarity() {
    return similarity;
  }

  public void setSimilarity(Long similarity) {
    this.similarity = similarity;
  }

  //使用八进制数标识相似度，依次从高位到低位
  //各位 基准为8，拼音不相等-1，字符不相等再-1
  public enum NAME_TYPE {
    H(Long.valueOf("700000", 8)),// "只由汉字组成"|"只由英文组成"
    HS(Long.valueOf("070000", 8)),// "只由汉字/空格组成"|"只由英文/空格组成"
    HSB(Long.valueOf("007000", 8)),// "只由汉字/空格/括号对组成"|"只由英文/空格/括号对组成"
    HSBC(Long.valueOf("000700", 8)),// "只由汉字/空格/括号对/英文字母、数字组成"|"只由英文/空格/括号对/数字组成"
    HSBCO(Long.valueOf("000070", 8)),// "只由汉字/空格/英文字符/括号对/其他字符组成"|"只由英文/空格/数字/括号对/其他字符组成"
    OTHER(Long.valueOf("000007", 8));// "其他"

    private Long level;

    NAME_TYPE(Long level) {
      this.level = level;
    }

    static public NAME_TYPE getNameType(Long level) {
      if (Long.valueOf("700000", 8).compareTo(level) == 0)
        return NAME_TYPE.H;
      if (Long.valueOf("070000", 8).compareTo(level) == 0)
        return NAME_TYPE.HS;
      if (Long.valueOf("007000", 8).compareTo(level) == 0)
        return NAME_TYPE.HSB;
      if (Long.valueOf("000700", 8).compareTo(level) == 0)
        return NAME_TYPE.HSBC;
      if (Long.valueOf("000070", 8).compareTo(level) == 0)
        return NAME_TYPE.HSBCO;
      if (Long.valueOf("000007", 8).compareTo(level) == 0)
        return NAME_TYPE.OTHER;
      return NAME_TYPE.OTHER;
    }

    public String toString() {
      return Long.toOctalString(level);
    }

    public Long getLevel() {
      return level;
    }
  }
}
