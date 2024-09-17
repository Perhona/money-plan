package com.money_plan.api.domain.budget.entity;

import com.money_plan.api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "monthly_budget", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "year", "month"})})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyBudget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne  // FetchType 지정 안하는 경우, 즉시 로딩(Eager Loading)됨.
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "total_budget", nullable = false)
    private Long totalBudget;

    @OneToMany(mappedBy = "monthlyBudget", cascade = CascadeType.ALL, orphanRemoval = true) // FetchType을 설정하지 않는 경우, 기본적으로 Lazy Loading
    private List<CategoryBudget> categoryBudgets;


    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "modified_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime modifiedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    public void updateTotalBudget(Long totalBudget) {
        this.totalBudget = totalBudget;
    }
}
