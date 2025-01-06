package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseSchedule;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Day;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.CourseRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
//@Rollback(false)
public class CourseServiceTest {

    @Autowired
    CourseService courseService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    UserRepository userRepository;


    @Test
    public void 강의등록() throws Exception{
        // given
        User user = new User();
        userRepository.save(user);

        List<CourseSchedule> schedules = new ArrayList<>();

        CourseSchedule schedule1 = createSchedule("MON", "명신관 702호", LocalTime.of(10, 30), LocalTime.of(11, 45));

        CourseSchedule schedule2 = createSchedule("WED", "명신관 702호", LocalTime.of(10, 30), LocalTime.of(11, 45));

        // when
        Long saveId = courseService.saveCourse(user.getId(), "테스트강의", LocalDate.of(2025, 2, 10),2025,1,"FFFFFF", schedules);


        // then
        assertEquals(saveId, courseRepository.findOne(saveId).get().getId());
    }

    @Test(expected = IllegalStateException.class)
    public void 강의_시간대_유효성_검증() throws Exception{
        // given
        User user = new User();
        userRepository.save(user);

        List<CourseSchedule> schedules = new ArrayList<>();

        CourseSchedule schedule1 = createSchedule("MON", "명신관 702호", LocalTime.of(10, 30), LocalTime.of(9, 45));

        // when
        Long saveId = courseService.saveCourse(user.getId(), "테스트강의", LocalDate.of(2025, 2, 10),2025,1,"FFFFFF", schedules);


        // then
        fail("예외가 발생해야 한다.");
    }


    @Test(expected = IllegalStateException.class)
    public void 강의시간대_중복() throws Exception{
        // given
        User user1 = new User();
        userRepository.save(user1);

        List<CourseSchedule> schedules = new ArrayList<>();

        CourseSchedule schedule1 = createSchedule("MON", "명신관 702호", LocalTime.of(10, 30), LocalTime.of(11, 45));

        List<CourseSchedule> schedules2 = new ArrayList<>();

        CourseSchedule schedule2 = createSchedule("MON", "명신관 702호", LocalTime.of(10, 30), LocalTime.of(11, 45));

        // when
        courseService.saveCourse(user1.getId(), "테스트강의1", LocalDate.of(2025, 2, 10),2025,1,"FFFFFF", schedules);
        courseService.saveCourse(user1.getId(), "테스트강의2", LocalDate.of(2025, 2, 10),2025,1,"FFFFFF", schedules2);

        // then
        fail("예외가 발생해야 한다.");
    }

    private CourseSchedule createSchedule(String day, String location, LocalTime startTime, LocalTime endTime){
        CourseSchedule schedule = new CourseSchedule();
        schedule.setDay(day);
        schedule.setLocation(location);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        return schedule;
    }
}