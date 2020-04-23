package com.zcyk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author WuJieFeng
 * @date 2019/11/19 14:54
 */
@Data
@Entity
@Table(name = "model_cost")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Model_cost {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select uuid()")
    private String id;
    /*项目id*/
    private String project_id;
    /*模型表id*/
    private String mid;
    /*构件名*/
    private String component_name;
    /*单价*/
    private BigDecimal uint_price;
    /*构件总价*/
    private BigDecimal total_prices;
    /*数量*/
    private BigDecimal amount;
    /*创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    /*状态*/
    private Integer statu;
}
