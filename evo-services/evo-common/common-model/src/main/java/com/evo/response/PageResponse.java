package com.evo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> extends Response<List<T>> {
    private PageableResponse page;

    public PageResponse() {
        super();
    }

    public PageResponse(List<T> data, int pageIndex, int pageSize, long total) {
        super();
        this.page = new PageableResponse(pageIndex, pageSize, total);
        this.data = data;
        this.success(); // Sets 200 OK status
    }

    public static <T> PageResponse<T> of(List<T> data, int pageIndex, int pageSize, long total) {
        return new PageResponse<>(data, pageIndex, pageSize, total);
    }

    public static <T> PageResponse<T> failPaging(String message) {
        PageResponse<T> response = new PageResponse<>();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setStatusDesc(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setMessage(message);
        return response;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageableResponse implements Serializable {
        private int pageIndex;
        private int pageSize;
        private long total;

        public PageableResponse(int pageIndex, int pageSize, long total) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            this.total = total;
        }
    }
}
