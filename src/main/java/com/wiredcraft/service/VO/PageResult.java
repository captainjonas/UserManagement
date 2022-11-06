package com.wiredcraft.service.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * @author Eric Yao
 * @date 2022-10-30
 */
@Data
@Schema(description =  "pagination result model")
public class PageResult<T> {

    /**
     * reult data
     */
    private List<T> data;
    /*
     * current page
     */
    @Schema(description = "current page")
    private Long current;
    /**
     * page size
     */
    @Schema(description = "page size")
    private Long size;
    /**
     * total record count
     */
    @Schema(description = "total record count")
    private Long total;


}
