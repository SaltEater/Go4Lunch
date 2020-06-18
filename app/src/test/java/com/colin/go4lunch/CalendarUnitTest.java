package com.colin.go4lunch;

import com.colin.go4lunch.controllers.fragments.SettingsFragment;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CalendarUnitTest {
    private Calendar mCalendarNow;
    private Calendar mCalendarMidDay;

    @Before
    public void configureDates() {
        mCalendarMidDay = SettingsFragment.configureMidDayCalendar();
        mCalendarNow = Calendar.getInstance();
    }

    @Test
    public void shouldReturnDifferenceBetweenMorningAndMidDay() {
        mCalendarNow.set(Calendar.HOUR_OF_DAY, 10);
        long delta = SettingsFragment.getDeltaCalendar(mCalendarNow, mCalendarMidDay);
        assertEquals(mCalendarMidDay.getTimeInMillis() - mCalendarNow.getTimeInMillis(), delta);
    }

    @Test
    public void shouldReturnDifferenceBetweenAfternoonAndNexMidDay() {
        mCalendarNow.set(Calendar.HOUR_OF_DAY, 15);

        long delta = SettingsFragment.getDeltaCalendar(mCalendarNow, mCalendarMidDay);

        assertEquals(mCalendarMidDay.getTimeInMillis() - mCalendarNow.getTimeInMillis(), delta);
        assertTrue(delta > 0);
    }

    @Test
    public void shouldWorkForAllTimeOfDay() {
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute <= 60; minute++) {
                for (int day = mCalendarNow.get(Calendar.DAY_OF_MONTH); day <= mCalendarNow.get(Calendar.DAY_OF_MONTH) + 1; day++) {
                    mCalendarNow.set(Calendar.DAY_OF_MONTH, day);
                    mCalendarNow.set(Calendar.HOUR_OF_DAY, hour);
                    mCalendarNow.set(Calendar.MINUTE, minute);
                    long delta = SettingsFragment.getDeltaCalendar(mCalendarNow, mCalendarMidDay);
                    assertEquals(mCalendarMidDay.getTimeInMillis() - mCalendarNow.getTimeInMillis(), delta);
                    assertTrue(delta > 0);
                }
            }
        }
    }
}
