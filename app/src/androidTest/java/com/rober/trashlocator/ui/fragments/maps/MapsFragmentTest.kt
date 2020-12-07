package com.rober.trashlocator.ui.fragments.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.TypeTextAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import com.rober.trashlocator.R
import com.rober.trashlocator.ToastMatcher
import com.rober.trashlocator.launchFragmentInHiltContainer
import com.rober.trashlocator.utils.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@HiltAndroidTest
class MapsFragmentTest :
    AndroidJUnitRunner() { //Don't launch them once at all because it crashes, launch one test manually

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unRegisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun writeOnSearchPlace_andVerifyItsWritten() = runBlockingTest {
        launchFragmentInHiltContainer<MapsFragment>()
        val stringToTest = "Madrid"
        onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))
        onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))
    }

    @Test
    fun writeOnSearchPlace_clickOnRecyclerView_moveCameraPosition_displaysToastNotFoundDataSet() =
        runBlockingTest {
            //Given
            launchFragmentInHiltContainer<MapsFragment>()
            val stringToTest = "Madrid"
            onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))

            //When
            onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))

            //Then
            onView(withId(R.id.rowAddress)).check(matches(isDisplayed()))
            onView(withId(R.id.rowAddress)).perform(click())

            onView(withText(InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.dataset_not_found))).inRoot(
                ToastMatcher()
            ).check(
                matches(
                    isDisplayed()
                )
            )
        }

    @Test
    fun writeOnSearchPlace_clickOnRecyclerView_moveCameraPosition_displaysToastFoundDataSet() =
        runBlockingTest {
            //Given
            launchFragmentInHiltContainer<MapsFragment>()
            val stringToTest = "SantaCruzTenerife"

            onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))

            //When
            onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))

            //Then
            onView(withId(R.id.rowAddress)).perform(click())

            onView(withText(InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.dataset_found))).inRoot(
                ToastMatcher()
            ).check(
                matches(
                    isDisplayed()
                )
            )
        }
}