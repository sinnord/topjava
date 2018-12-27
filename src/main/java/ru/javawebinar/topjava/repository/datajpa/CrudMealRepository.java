package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    @Override
    @Transactional
    Meal save(Meal entity);

    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id = :id and m.user.id =:userId")
    int delete(@Param("id") int id, @Param("userId") int userId);

    Meal getByIdAndUserId(Integer id, Integer userId);

    List<Meal> findAllByUserIdOrderByDateTimeDesc(Integer userId);

    List<Meal> findAllByDateTimeBetweenAndUserIdOrderByDateTimeDesc(@NotNull LocalDateTime startDate, @NotNull LocalDateTime endDate, Integer userId);
}
