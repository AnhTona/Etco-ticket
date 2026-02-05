package com.esco.etco.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Vui lòng nhập tên sự kiện")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @NotBlank(message = "Vui Lòng nhập số giấy phép")
    private String permitNumber;

    @NotBlank(message = "Vui lòng nhập ngày cấp")
    private Instant permitIssuedAt;

    @NotBlank(message = "Vui lòng nhập nơi cấp")
    private String permitIssuedBy;

    @NotBlank(message = "vui lòng nhập địa chỉ")
    private String location;

    @NotBlank(message = "Vui lòng chọn ngày giờ bắt đầu")
    private Instant startTime;

    @NotBlank(message = "Vui lòng chọn ngày giờ kết thúc")
    private Instant endTime;

    private boolean isActive;
    private boolean isPublished;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY )
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY )
    private List<EventImage> images;
}
