package net.pmellaaho.rxapp

import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers.pathContains
import net.pmellaaho.rxapp.ui.MainActivity
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Inject
    lateinit var client: OkHttpClient

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        //be sure to reset it before each test!
        RESTMockServer.reset()
        hiltRule.inject()
        IdlingRegistry.getInstance().register(OkHttp3IdlingResource.create("okhttp", client))
    }

    @Test
    fun listWithTwoContributors() {
        // GIVEN
        RESTMockServer.whenGET(pathContains("square/retrofit/contributors"))
            .thenReturnFile("contributors.json")

        // WHEN - activityScenarioRule automatically launches Activity before each test
        clickOn(R.id.startBtn)

        // THEN
        assertDisplayedAtPosition(R.id.recyclerView, 0, "JakeWharton")
        assertDisplayedAtPosition(R.id.recyclerView, 0, "1098")

        assertDisplayedAtPosition(R.id.recyclerView, 1, "swankjesse")
        assertDisplayedAtPosition(R.id.recyclerView, 1, "281")
    }

    @Test
    fun errorFromNetwork() {
        // GIVEN
        RESTMockServer.whenGET(pathContains("square/retrofit/contributors"))
            .thenReturnEmpty(503)

        // WHEN
        clickOn(R.id.startBtn)

        // THEN
        assertDisplayed(R.id.errorText)
    }
}