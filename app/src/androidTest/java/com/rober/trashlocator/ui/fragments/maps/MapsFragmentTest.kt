package com.rober.trashlocator.ui.fragments.maps


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.TypeTextAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
//import com.rober.trashlocator.MainCoroutineRule
import com.rober.trashlocator.R
import com.rober.trashlocator.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MapsFragmentTest {
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

//    @ExperimentalCoroutinesApi
//    @get:Rule
//    val coroutineRule = MainCoroutineRule()

//    @get:Rule
//    val fragmentScenario = launchFragmentInContainer<MapsFragment>(null, R.style.Theme_TrashLocator)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun writeOnSearchPlace_AndVerifyItsWritten() {
        launchFragmentInHiltContainer<MapsFragment>()
        val stringToTest = "Madrid"
        onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))
        onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))
    }

//    @ExperimentalCoroutinesApi
//    @Test
//    fun testLaunchFragmentInHiltContainer() {
//        var fragment: MapsFragment? = null
//        launchFragmentInHiltContainer<MapsFragment>() {
//            fragment = this
//        }
//
//
//        val stringToTest = "Madrid"
//        onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction(stringToTest))
//        onView(withId(R.id.ETsearchLocation)).check(matches(withText(stringToTest)))
//
//        val listAddresses = fragment?.viewModel?.listAddressesLocation?.getOrAwaitValue(10)
//        Log.i("MapsFragmentTest", "${listAddresses?.getContentIfNotHandled()}")
//
//
////        onView(withId(R.id.ETsearchLocation)).perform(TypeTextAction("Cáceres"))
////        val device = UiDevice.getInstance(getInstrumentation())
////        val uiObject = device.findObject(UiSelector().description("Plaza Mayor, 19, Cáceres"))
////        assertThat(true).isEqualTo(true)
//    }
}