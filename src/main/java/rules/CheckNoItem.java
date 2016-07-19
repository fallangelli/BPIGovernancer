package rules;

import java.util.Date;

/**
 * Created by Administrator on 2016/6/29.
 */
public class CheckNoItem {
  Integer pin;
  Integer personid;
  String FINANCECODE;
  String name;
  String certtype;
  String certno;
  Date getTime;

  public Integer getPin() {
    return pin;
  }

  public void setPin(Integer pin) {
    this.pin = pin;
  }

  public Integer getPersonid() {
    return personid;
  }

  public void setPersonid(Integer personid) {
    this.personid = personid;
  }

  public String getFINANCECODE() {
    return FINANCECODE;
  }

  public void setFINANCECODE(String FINANCECODE) {
    this.FINANCECODE = FINANCECODE;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCerttype() {
    return certtype;
  }

  public void setCerttype(String certtype) {
    this.certtype = certtype;
  }

  public String getCertno() {
    return certno;
  }

  public void setCertno(String certno) {
    this.certno = certno;
  }

  public Date getGetTime() {
    return getTime;
  }

  public void setGetTime(Date getTime) {
    this.getTime = getTime;
  }
}
