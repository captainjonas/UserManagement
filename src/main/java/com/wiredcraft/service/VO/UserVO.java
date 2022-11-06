package com.wiredcraft.service.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Eric Yao
 * @date 2022-10-30
 */
@Data
public class UserVO {

    private Long id;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "day of birth(only 'yyyy-MM-dd' format supported)", example = "2022-10-23")
    private Date dob;

    private String address;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2022-10-23 18:53:44")
    private Date createdAt;


}
