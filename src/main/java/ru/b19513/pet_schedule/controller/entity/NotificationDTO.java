package ru.b19513.pet_schedule.controller.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@Schema(description = "Абстрактный класс уведомления")
public class NotificationDTO implements Serializable {
    @Schema(description = "Id уведомления")
    private long id;
    @Schema(description = "Id группы, в которой есть это уведомление")
    private long groupId;
    @Schema(description = "Активно ли уведомление")
    private boolean enabled;
    @Schema(description = "Комментарий к уведомлению")
    private String comment;
}