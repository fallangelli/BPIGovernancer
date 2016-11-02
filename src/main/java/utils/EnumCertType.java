package utils;

/**
 * Created by Administrator on 2016/9/5.
 */

public enum EnumCertType {

  ALL("ALL", 99),
  SFZ("0", 99),
  HKB("1", 4),
  HZ("2", 4),
  JUNGZ("3", 4),
  SBZ("4", 4),
  GAJMTXZ("5", 4),
  TWTBTXZ("6", 4),
  LSSFZ("7", 99),
  WGRJLZ("8", 4),
  JINGGZ("9", 4),
  XGSFZ("A", 4),
  AMSFZ("B", 4),
  TWSFZ("C", 4),
  QT("X", 4);

  private String code;

  private Integer minLength;

  EnumCertType(String code, Integer minLenth) {
    this.code = code;
    this.minLength = minLenth;
  }

  public static EnumCertType getEnumCertType(String type) {
    if (type.compareToIgnoreCase(EnumCertType.SFZ.getCode()) == 0)
      return EnumCertType.SFZ;
    if (type.compareToIgnoreCase(EnumCertType.HKB.getCode()) == 0)
      return EnumCertType.HKB;
    if (type.compareToIgnoreCase(EnumCertType.HZ.getCode()) == 0)
      return EnumCertType.HZ;
    if (type.compareToIgnoreCase(EnumCertType.JUNGZ.getCode()) == 0)
      return EnumCertType.JUNGZ;
    if (type.compareToIgnoreCase(EnumCertType.SBZ.getCode()) == 0)
      return EnumCertType.SBZ;
    if (type.compareToIgnoreCase(EnumCertType.GAJMTXZ.getCode()) == 0)
      return EnumCertType.GAJMTXZ;
    if (type.compareToIgnoreCase(EnumCertType.TWTBTXZ.getCode()) == 0)
      return EnumCertType.TWTBTXZ;
    if (type.compareToIgnoreCase(EnumCertType.LSSFZ.getCode()) == 0)
      return EnumCertType.LSSFZ;
    if (type.compareToIgnoreCase(EnumCertType.WGRJLZ.getCode()) == 0)
      return EnumCertType.WGRJLZ;
    if (type.compareToIgnoreCase(EnumCertType.JINGGZ.getCode()) == 0)
      return EnumCertType.JINGGZ;
    if (type.compareToIgnoreCase(EnumCertType.XGSFZ.getCode()) == 0)
      return EnumCertType.XGSFZ;
    if (type.compareToIgnoreCase(EnumCertType.AMSFZ.getCode()) == 0)
      return EnumCertType.AMSFZ;
    if (type.compareToIgnoreCase(EnumCertType.TWSFZ.getCode()) == 0)
      return EnumCertType.TWSFZ;
    else
      return EnumCertType.QT;
  }

  public String getCode() {

    return this.code;

  }

  public Integer getMinLenth() {

    return this.minLength;

  }


}
