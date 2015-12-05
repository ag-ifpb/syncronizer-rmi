package ag.ifpb.pod.rmi.core;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TeacherTO implements Serializable{
  private int code;
  private String name;
  private String abbrev;
  private boolean active;
  public int getCode() {
    return code;
  }
  public void setCode(int code) {
    this.code = code;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getAbbrev() {
    return abbrev;
  }
  public void setAbbrev(String abbrev) {
    this.abbrev = abbrev;
  }
  public boolean isActive() {
    return active;
  }
  public void setActive(boolean active) {
    this.active = active;
  }
}
