package com.wiredcraft.dao.queryModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Eric Yao
 * @date 2022-10-30
 */
@Data
public class PageQueryModel {

    /**
     * page size
     */
    @Schema(description = "page size", example = "10")
    public long size = 20;

    /**
     * current page, start from 1
     */
    @Schema(description = "current page", example = "1")
    public int current = 1;

    public long getSize() {
        return size;
    }

    public int getCurrent() {
        return current;
    }
}
