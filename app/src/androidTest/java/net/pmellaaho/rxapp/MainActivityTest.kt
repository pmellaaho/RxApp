package net.pmellaaho.rxapp

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import net.pmellaaho.rxapp.network.NetworkModule
import net.pmellaaho.rxapp.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.util.*


//@UninstallModules(NetworkModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

//    @BindValue
//    @JvmField
//    @Named("baseUrl")
//    val baseUrl: String = RESTMockServer.getUrl()

//    val repository: ContributorsRepository = Mockito.mock(ContributorsRepository::class.java)

    private val hiltRule = HiltAndroidRule(this)
    private val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val rule = RuleChain
        .outerRule(hiltRule)
        .around(activityTestRule)


    /*
    @Singleton
    @Component(modules = [MockNetworkModule::class])
    interface MockNetworkComponent : NetworkComponent

    @Rule
    @JvmField
    var mActivityRule = ActivityTestRule(
        MainActivity::class.java,
        true,  // initialTouchMode
        false
    ) // launchActivity.
     */

    @Before
    fun setUp() {

        //be sure to reset it before each test!
        RESTMockServer.reset()

        hiltRule.inject()

        /*
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext
            .applicationContext as RxApp

        val testComponent = DaggerMainActivityTest_MockNetworkComponent.builder()
            .mockNetworkModule(MockNetworkModule())
            .build()
        app.setComponent(testComponent)
        model = testComponent.contributorsModel()
         */
    }

    @Test
    fun listWithTwoContributors() {

        RESTMockServer.whenGET(pathContains("square/retrofit/contributors"))
            .thenReturnFile("contributors.json");

        // GIVEN
//        val tmpList: MutableList<Contributor> = ArrayList()
//        tmpList.add(Contributor("Jesse", 600))
//        tmpList.add(Contributor("Jake", 200))

//        val testObservable = Observable.just<List<Contributor>>(tmpList)

//        runBlocking {
//            Mockito.`when`(
//                repository.getContributors(Matchers.anyString(), Matchers.anyString())
//            ).thenReturn(tmpList)
//        }

        // WHEN
        activityTestRule.launchActivity(Intent())
        clickOn(R.id.startBtn)

        // THEN
        assertDisplayedAtPosition(R.id.recyclerView, 0, "Jesse")
        assertDisplayedAtPosition(R.id.recyclerView, 0, "600")

        assertDisplayedAtPosition(R.id.recyclerView, 1, "Jake")
        assertDisplayedAtPosition(R.id.recyclerView, 1, "200")
    }

    /*
    @Test
    fun errorFromNetwork() {

        // GIVEN
        // create an Observable that emits nothing and then signals an error
        val errorEmittingObservable =
            Observable.error<List<Contributor>>(IllegalArgumentException())
        Mockito.`when`(
            model.getContributors(Matchers.anyString(), Matchers.anyString())
        )
            .thenReturn(errorEmittingObservable)

        // WHEN
        activityTestRule.launchActivity(Intent())
        clickOn(R.id.startBtn)

        // THEN
        assertDisplayed(R.id.errorText)
    }
     */
}