package rules;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Administrator on 2016/6/8.
 */

@XmlRootElement
public class MergePerson {
  String certType;
  String certNo;
  Boolean isValid;
  List<MergeName> certNames;

  public String getCertType() {
    return certType;
  }

  public void setCertType(String certType) {
    this.certType = certType;
  }

  public String getCertNo() {
    return certNo;
  }

  public void setCertNo(String certNo) {
    this.certNo = certNo;
  }


  public List<MergeName> getCertNames() {
    return certNames;
  }

  public void setCertNames(List<MergeName> certNames) {
    this.certNames = certNames;
  }

  public Boolean getValid() {
    return isValid;
  }

  public void setValid(Boolean valid) {
    isValid = valid;
  }
}
