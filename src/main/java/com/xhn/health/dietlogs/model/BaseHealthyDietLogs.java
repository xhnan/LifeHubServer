package com.xhn.health.dietlogs.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xhn.base.mybatis.handler.VectorTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 饮食记录与营养摄入明细表（healthy_diet_logs）基础实体。
 */
@Data
@TableName(value = "healthy_diet_logs", autoResultMap = true)
@Schema(description = "饮食记录与营养摄入明细表")
public class BaseHealthyDietLogs {

    @Schema(description = "饮食记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "进餐具体时间")
    @TableField("meal_time")
    private LocalDateTime mealTime;

    @Schema(description = "餐别（如 breakfast、lunch、dinner、snack）")
    @TableField("meal_type")
    private String mealType;

    @Schema(description = "食物明细文本")
    @TableField("food_items")
    private String foodItems;

    @Schema(description = "总摄入热量（千卡）")
    @TableField("total_calories")
    private BigDecimal totalCalories;

    @Schema(description = "蛋白质摄入量（克）")
    @TableField("protein_g")
    private BigDecimal proteinG;

    @Schema(description = "碳水化合物摄入量（克）")
    @TableField("carbs_g")
    private BigDecimal carbsG;

    @Schema(description = "脂肪摄入量（克）")
    @TableField("fat_g")
    private BigDecimal fatG;

    @Schema(description = "食物明细向量（vector 文本，256 维）")
    @TableField(value = "food_embedding", typeHandler = VectorTypeHandler.class)
    private List<Double> foodEmbedding;

    @Schema(description = "数据写入时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
