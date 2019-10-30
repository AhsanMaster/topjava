package ru.javawebinar.topjava.model;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NamedQueries({
        @NamedQuery(name = Meal.DELETE, query = "delete from Meal meals where meals.id=:id and meals.user.id=:userId"),
        @NamedQuery(name = Meal.GET, query = "select meals from Meal meals where meals.user.id=:userId and meals.id=:id"),
        @NamedQuery(name = Meal.GET_ALL_SORTED, query = "select meals from Meal meals where meals.user.id=:userId order by meals.dateTime desc"),
        @NamedQuery(name = Meal.GET_BETWEEN, query = "select meals from Meal meals where meals.user.id=:userId and (meals.dateTime BETWEEN :startDate and :endDate)order by meals.dateTime desc")
})
@Entity
@Table(name = "meals", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_Id", "date_time"}, name = "meals_unique_user_datetime_idx")})
public class Meal extends AbstractBaseEntity {
    public static final String DELETE = "Meal.delete";
    public static final String GET = "Meal.get";
    public static final String GET_ALL_SORTED = "Meal.getAllSorted";
    public static final String GET_BETWEEN = "Meal.getBetween";

    @Column(name = "date_time", nullable = false)
    @NotNull
    @DateTimeFormat
    private LocalDateTime dateTime;

    @Column(name = "description")
    @Size(min = 2, max = 100, message = "Description's length from 2 to 150 ")
    private String description;

    @Column(name = "calories", nullable = false)
    @Range(min = 10, max = 10_000, message = "Calories min 10 max 10 000")
    private int calories;

    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private User user;

    public Meal() {
    }

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this(null, dateTime, description, calories);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
