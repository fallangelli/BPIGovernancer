package rules;

import utils.EnumCertType;

import java.util.Date;

/**
 * Created by Administrator on 2016/6/29.
 */
public class CheckNoItem {
  Integer pin;
  Integer personId;
  String financeCode;
  String name;
  EnumCertType certType;
  EnumCertType realCertType;
  String certNo;
  Date getTime;

  public Integer getPin() {
    return pin;
  }

  public void setPin(Integer pin) {
    this.pin = pin;
  }

  public Integer getPersonId() {
    return personId;
  }

  public void setPersonId(Integer personId) {
    this.personId = personId;
  }

  public String getFinanceCode() {
    return financeCode;
  }

  public void setFinanceCode(String financeCode) {
    this.financeCode = financeCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EnumCertType getCertType() {
    return certType;
  }

  public void setCertType(EnumCertType certType) {
    this.certType = certType;
  }

  public EnumCertType getRealCertType() {
    return realCertType;
  }

  public void setRealCertType(EnumCertType realCertType) {
    this.realCertType = realCertType;
  }

  public String getCertNo() {
    return certNo;
  }

  public void setCertNo(String certNo) {
    this.certNo = certNo;
  }

  public Date getGetTime() {
    return getTime;
  }

  public void setGetTime(Date getTime) {
    this.getTime = getTime;
  }
}
