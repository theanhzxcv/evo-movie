package com.evo.exception;

import com.evo.constans.ErrConstans;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {
    protected int status;
    protected String errCode;
    protected String errDesc;

    public AppException(ErrConstans errConstans) {
        this.status = errConstans.getStatus().value();
        this.errCode = errConstans.getErrCode();
        this.errDesc = errConstans.getErrDesc();
    }
}
