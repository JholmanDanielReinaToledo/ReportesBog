package utilities.database;

import io.ebean.Model;
import io.vavr.collection.List;

public class Page<U extends Model> {
  private List<U> entities;
  private String name;
  private Integer totalCount;
  private Integer totalPageCount;

  public Page(List<U> entities, Integer totalCount, Integer totalPageCount, String name) {
    this.name = name;
    this.entities = entities;
    this.totalCount = totalCount;
    this.totalPageCount = totalPageCount;
  }

  public List<U> getEntities() {
    return entities;
  }

  public Page<U> setEntities(List<U> entities) {
    this.entities = entities;
    return this;
  }

  public Integer getTotalCount() {
    return totalCount;
  }

  public Page<U> setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
    return this;
  }

  public Integer getTotalPageCount() {
    return totalPageCount;
  }

  public Page<U> setTotalPageCount(Integer totalPageCount) {
    this.totalPageCount = totalPageCount;
    return this;
  }

  public String getName() {
    return name;
  }

  public Page<U> setName(String name) {
    this.name = name;
    return this;
  }
}
