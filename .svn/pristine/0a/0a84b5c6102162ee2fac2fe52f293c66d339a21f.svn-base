package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Entity
@Table(name = "yunfile_vip")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YunfileVip {

  @Id
  private String account;
  private String name;
  @JsonIgnore
  private String company_id;
  @JsonIgnore
  private String password;

  private Integer status;

  @JsonIgnore
  private Long buy_date;
  @JsonIgnore
  private Long due_date;

  @Transient
  private Integer due_time;



}
