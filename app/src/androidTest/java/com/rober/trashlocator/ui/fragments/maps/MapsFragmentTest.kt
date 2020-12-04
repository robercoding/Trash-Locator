package com.rober.trashlocator.ui.fragments.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.TypeTextAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnitRunner
import com.rober.trashlocator.R
import com.rober.trashlocator.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@HiltAndroidTest
class MapsFragmentTest : AndroidJUnitRunner() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun writeOnSearchPlace_andVerifyItsWritten() = runBlockingTest {
        launchFragmentInHiltContainer<MapsFragment>()
        val stringToTest = "Madrid"
        onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))
        onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))
    }

    @Test
    fun writeOnSearchPlace_clickOnRecyclerView_moveCameraPosition() = runBlockingTest {
        //Given
        launchFragmentInHiltContainer<MapsFragment>()
        val stringToTest = "Madrid"
        onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))

        //When
        onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))
        Thread.sleep(5000) //Waiting for geocoder response

        //Then
        onView(withId(R.id.recyclerLocation)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withSubstring("Madrid")), click()
            )
        )
    }
}