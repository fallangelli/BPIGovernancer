package rules;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/21.
 */
@XmlRootElement
public class WapperName {
  List<MergeName> nameList = new ArrayList<>();

  public List<MergeName> getNameList() {
    return nameList;
  }

  public void setNameList(List<MergeName> nameList) {
    this.nameList = nameList;
  }
}
