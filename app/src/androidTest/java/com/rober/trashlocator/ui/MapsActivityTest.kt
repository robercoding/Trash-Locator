package com.rober.trashlocator.ui

import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import com.rober.trashlocator.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MapsActivityTest {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MapsActivity::class.java)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun startApp_WithMapsFragmentAsStartDestination() {
        val scenario = activityScenarioRule.scenario
        var navcontroller : NavController? = null
        scenario.onActivity {mapsActivity ->
            navcontroller = mapsActivity.findNavController()
        }

        assertThat(navcontroller?.currentDestination?.id).isEqualTo(R.id.mapsFragment)
    }

    @Test
    fun clickOnDrawerMaps_NavigateToMapsFragment() {
        val scenario = activityScenarioRule.scenario
        var navcontroller : NavController? = null
        scenario.onActivity {mapsActivity ->
            navcontroller = mapsActivity.findNavController()
        }

        //Since start destination is MapsFragment we navigate to other fragment and then navigate to Maps
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.aboutAppFragment)).perform(click())

        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.mapsFragment)).perform(click())

        assertThat(navcontroller?.currentDestination?.id).isEqualTo(R.id.mapsFragment)
    }

    @Test
    fun clickOnDrawerAbout_NavigateToAboutFragment() {
        val scenario = activityScenarioRule.scenario
        var navcontroller : NavController? = null
        scenario.onActivity {mapsActivity ->
            navcontroller = mapsActivity.findNavController()
        }

        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.aboutAppFragment)).perform(click())

        assertThat(navcontroller?.currentDestination?.id).isEqualTo(R.id.aboutAppFragment)
    }

    @Test
    fun clickOnDrawerTrashStats_NavigateToTrashStatsFragment() {
        val scenario = activityScenarioRule.scenario
        var navcontroller : NavController? = null
        scenario.onActivity {mapsActivity ->
            navcontroller = mapsActivity.findNavController()
        }

        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.trashStatsFragment)).perform(click())

        assertThat(navcontroller?.currentDestination?.id).isEqualTo(R.id.trashStatsFragment)
    }

    @Test
    fun clickOnDrawerContact_NavigateToContactFragment() {
        val scenario = activityScenarioRule.scenario
        var navcontroller : NavController? = null
        scenario.onActivity {mapsActivity ->
            navcontroller = mapsActivity.findNavController()
        }

        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.contactFragment)).perform(click())

        assertThat(navcontroller?.currentDestination?.id).isEqualTo(R.id.contactFragment)
    }

    @Test
    fun clickOnDrawerSettings_NavigateToSettingsFragment() {
        val scenario = activityScenarioRule.scenario
        var navcontroller : NavController? = null
        scenario.onActivity {mapsActivity ->
            navcontroller = mapsActivity.findNavController()
        }

        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open()).check(matches(isOpen()))
        onView(withId(R.id.settingsFragment)).perform(click())

        assertThat(navcontroller?.currentDestination?.id).isEqualTo(R.id.settingsFragment)
    }

    @Test
    fun clickOnDrawerSSDS(){

    }
}