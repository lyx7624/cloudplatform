package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Entity
@Table(name = "project_design_alteration_pic")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class ProjectDesignAlterationPic {

  private static final long serialVersionUID = 1L;
  @Id
  private Integer id;
  private String project_design_alteration_id;
  private String pic_url;
  private String pic_name;
  private String status;




}
