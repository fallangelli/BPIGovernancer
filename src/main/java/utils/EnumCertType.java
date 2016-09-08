package utils;

/**
 * Created by Administrator on 2016/9/5.
 */

public enum EnumCertType {

  ALL("ALL"),
  SFZ("0"),
  HKB("1"),
  HZ("2"),
  JUNGZ("3"),
  SBZ("4"),
  GAJMTXZ("5"),
  TWTBTXZ("6"),
  LSSFZ("7"),
  WGRJLZ("8"),
  JINGGZ("9"),
  XGSFZ("A"),
  AMSFZ("B"),
  TWSFZ("C"),
  QT("X");

  private String value;

  private EnumCertType(String value) {
    this.value = value;
  }

  public String getValue() {

    return this.value;

  }


}
