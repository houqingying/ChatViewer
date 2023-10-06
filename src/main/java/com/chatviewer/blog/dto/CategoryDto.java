package com.chatviewer.blog.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * @author ChatViewer
 */
@Data
public class CategoryDto {

    /**
     * 保存CategoryId
     */
    @JsonSerialize(using = ToStringSerializer.class)
    Long value;

    /**
     * CategoryName
     */
    String label;

    List<CategoryDto> children;
}
