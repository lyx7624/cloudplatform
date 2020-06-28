package com.zcyk.exception;

import com.zcyk.dto.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;

/**
 * @author WuJieFeng
 * @date 2019/10/17 14:40
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class AllExceptionHandler {
    @ExceptionHandler(value = DataAccessException.class)
    public ResultData exceptionHandler(HttpServletRequest request,Exception e){
        log.error("数据库错误",e);
        return new ResultData().setStatus("405").setMsg("不合法或不正确的输入");
    }

    @ExceptionHandler(value = FileNotFoundException.class)
    public ResultData fileExceptionHandler(HttpServletRequest request,Exception e){
        log.error("文件缺失",e);
        return new ResultData().setStatus("406").setMsg("文件已损坏,请重新上传");
    }

    @ExceptionHandler(value = Exception.class)
    public ResultData ExceptionHandler(HttpServletRequest request,Exception e){
        log.error("未归类的异常",e);
        return new ResultData().setStatus("500").setMsg("网络错误，请稍后再试");
    }

    @ExceptionHandler(value = BadSqlGrammarException.class)
    public ResultData badSqlHandler(HttpServletRequest request,Exception e){
        log.error("数据库语句错误",e);
        return new ResultData().setStatus("500").setMsg("数据错误，请稍后重试");
    }
    

    @ExceptionHandler(value = ZZJException.class)
    public ResultData ZZJHandler(HttpServletRequest request,Exception e){
        return new ResultData().setStatus("500").setMsg(e.getMessage()==null?"筑智建络错误，请稍后重试":e.getMessage());
    }

    @ExceptionHandler(value = JWException.class)
    public ResultData JWHandler(HttpServletRequest request,Exception e){
        return new ResultData().setStatus("500").setMsg(e.getMessage()==null?"建委网络错误，请稍后重试":e.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResultData IllegalArgumentHandler(HttpServletRequest request,Exception e){
        log.error("不合法或不正确的参数",e);
        return new ResultData().setStatus("500").setMsg("不合法或不正确的输入");
    }

    @ExceptionHandler(value = IndexOutOfBoundsException.class)
    public ResultData IndexOutOfBoundsHandler(HttpServletRequest request,Exception e){
        log.error("超出范围，数据不够",e);
        return new ResultData().setStatus("500").setMsg("数据错误，请联系管理员");
    }
    @ExceptionHandler(value = NullPointerException.class)
    public ResultData NullPointerHandler(HttpServletRequest request,Exception e){
        log.error("空指针异常",e);
        return new ResultData().setStatus("500").setMsg("数据已不存在");
    }

    @ExceptionHandler(value = YunFileException.class)
    public ResultData YunFilePointerHandler(HttpServletRequest request,Exception e){
        return new ResultData().setStatus("500").setMsg(e==null?"云资料错误":e.getMessage());
    }


}
