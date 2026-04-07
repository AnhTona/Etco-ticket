package com.esco.etco.entity;

import com.esco.etco.util.SecurityUtil;
import com.esco.etco.util.constant.EventStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Set;

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

    // CẬP NHẬT: Thêm CascadeType.ALL và orphanRemoval để tự động xóa Vé khi xóa Event
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    // CẬP NHẬT: Thêm CascadeType.ALL và orphanRemoval để tự động xóa Ảnh khi xóa Event
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventImage> images;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "producer_id")
    private Producer producer;

    @Enumerated(EnumType.STRING)
    private EventStatusEnum status;

    // Lưu ý: @ElementCollection mặc định sẽ được xóa khi thực thể cha bị xóa
    @ElementCollection
    @CollectionTable(name = "event_artists", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "artist_name")
    private Set<String> artists;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("system");
        this.createdAt = Instant.now();
        updateStatus();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("system");
        this.updatedAt = Instant.now();
        updateStatus();
    }

    private void updateStatus() {
        Instant now = Instant.now();
        if (this.startTime != null && this.endTime != null) {
            if (now.isBefore(this.startTime)) {
                this.status = EventStatusEnum.UPCOMING;
            } else if (now.isAfter(this.endTime)) {
                this.status = EventStatusEnum.COMPLETED;
            } else {
                this.status = EventStatusEnum.ONGOING;
            }
        } else if (this.status == null) {
            this.status = EventStatusEnum.UPCOMING;
        }
    }
}
