package com.evo.exception;

import com.evo.constants.ErrConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {
    protected int status;
    protected String errCode;
    protected String errDesc;

    public AppException(ErrConstants errConstants) {
        this.status = errConstants.getStatus().value();
        this.errCode = errConstants.getErrCode();
        this.errDesc = errConstants.getErrDesc();
    }
}
