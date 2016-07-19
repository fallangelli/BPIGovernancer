package rules;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class WapperPerson {
  List<MergePerson> personList = new ArrayList<>();

  public List<MergePerson> getPersonList() {
    return personList;
  }

  public void setPersonList(List<MergePerson> personList) {
    this.personList = personList;
  }
}
