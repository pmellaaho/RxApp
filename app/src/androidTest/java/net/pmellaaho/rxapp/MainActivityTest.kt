package net.pmellaaho.rxapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers.pathContains
import net.pmellaaho.rxapp.ui.MainActivity
import okhttp3.OkHttpClient
import org.junit.After
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
    var composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    lateinit var resource: IdlingResource

    @Before
    fun setUp() {
        //be sure to reset it before each test!
        RESTMockServer.reset()
        hiltRule.inject()
        resource = OkHttp3IdlingResource.create("okhttp", client)
        IdlingRegistry.getInstance().register(resource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(resource)
    }

    @Test
    fun listWithTwoContributors() {
        // GIVEN
        RESTMockServer.whenGET(pathContains("square/retrofit/contributors"))
            .thenReturnFile("contributors.json")

        // WHEN - activityScenarioRule automatically launches Activity before each test
        clickOn(R.id.startBtn)

        // THEN
        composeTestRule.onList().assertExists()
        composeTestRule.onList().onChildren().assertCountEquals(2)

        composeTestRule.onList().onChildren().assertAny(hasText("JakeWharton"))
        composeTestRule.onList().onChildren().assertAny(hasText("1098"))

        composeTestRule.onList().onChildren().assertAny(hasText("swankjesse"))
        composeTestRule.onList().onChildren().assertAny(hasText("281"))

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")
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

typealias MainActivityTestRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
internal fun MainActivityTestRule.onList() = onNode(hasAnyChild(hasClickAction()))