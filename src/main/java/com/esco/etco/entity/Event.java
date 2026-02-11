package com.esco.etco.entity;

import com.esco.etco.util.SecurityUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Vui lòng nhập ngày cấp")
    private Instant permitIssuedAt;

    @NotBlank(message = "Vui lòng nhập nơi cấp")
    private String permitIssuedBy;

    @NotBlank(message = "vui lòng nhập địa chỉ")
    private String location;

    @NotNull(message = "Vui lòng chọn ngày giờ bắt đầu")
    private Instant startTime;

    @NotNull(message = "Vui lòng chọn ngày giờ kết thúc")
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

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.updatedAt = Instant.now();
    }
}
