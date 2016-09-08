package utils;

public enum ErrorStatus {
  LENG_ERR("长度无效"),
  CHAR_ERR("包含非法字符"),
  BIRTH_ERR("出生日期格式错误"),
  REG_ERR("无效行政区划"),
  CHE_ERR("验证码错误"),
  ALL_SAME_ERR("全部由相同字符组成"),
  NO_NUM_ERR("必须包含数字");

  private String msg;

  ErrorStatus(String msg) {
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }
}
