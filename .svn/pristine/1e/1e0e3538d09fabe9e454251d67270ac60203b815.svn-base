package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

@Data
@Entity
@Table(name = "project_design_alteration_opinion")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class ProjectDesignAlterationOpinion {

  private static final long serialVersionUID = 1L;
  @Id
  private Integer id;
  /*记录id*/
  private String project_design_alteration_id;
  /*设计院意见*/
  private String sdari_opinion;
  /*设计院回复人*/
  private String sdari_dispose_user;
  /*设计院回复时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern="yyyy-MM-dd")
  public String sdari_dispose_date;
  /*当前次数*/
  private String batch_number;

  /*状态 */
  private Integer status;

  public String statusString(){
    switch (this.status){
      case 3:
        return "已解决";
      case 2:
        return "部分未解决";
      case 1:
        return "未解决";
      default:
        return "";
    }
  }

  /*BIM意见*/
  private String bim_opinion;
  /*BIM回复人*/
  private String bim_dispose_user;
  /*BIM回复时间*/
  @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern="yyyy-MM-dd")
  private String bim_dispose_date;

}
