package com.bread.popupbread.domain.popup;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Check(constraints = "start_date <= end_date")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "popups")
public class Popup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long popupId;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 255)
    private String detailLocation;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    /**
     * 대표 이미지 키 (비정규화된 필드)
     */
    @Column(length = 255)
    private String coverImageKey;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "datetime(6)")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "datetime(6)")
    private LocalDateTime updatedDate;

    @Builder.Default
    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL)
    private List<PopupImage> popupImages = new ArrayList<>();

    public void addImage(PopupImage popupImage) {
        popupImages.add(popupImage);
        popupImage.setPopup(this);
    }
}
