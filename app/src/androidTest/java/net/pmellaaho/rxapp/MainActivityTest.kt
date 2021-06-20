package net.pmellaaho.rxapp

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import dagger.Component
import io.reactivex.Observable
import net.pmellaaho.rxapp.model.Contributor
import net.pmellaaho.rxapp.model.ContributorsModel
import net.pmellaaho.rxapp.network.NetworkComponent
import net.pmellaaho.rxapp.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mockito
import java.util.*
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    var model: ContributorsModel? = null

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

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext
            .applicationContext as RxApp
        val testComponent = DaggerMainActivityTest_MockNetworkComponent.builder()
            .mockNetworkModule(MockNetworkModule())
            .build()
        app.setComponent(testComponent)
        model = testComponent.contributorsModel()
    }

    @Test
    fun listWithTwoContributors() {

        // GIVEN
        val tmpList: MutableList<Contributor> = ArrayList()
        tmpList.add(Contributor("Jesse", 600))
        tmpList.add(Contributor("Jake", 200))
        val testObservable = Observable.just<List<Contributor>>(tmpList)
        Mockito.`when`(
            model!!.getContributors(Matchers.anyString(), Matchers.anyString())
        )
            .thenReturn(testObservable)

        // WHEN
        mActivityRule.launchActivity(Intent())
        clickOn(R.id.startBtn)

        // THEN
        assertDisplayedAtPosition(R.id.recyclerView, 0, "Jesse")
        assertDisplayedAtPosition(R.id.recyclerView, 0, "600")

        assertDisplayedAtPosition(R.id.recyclerView, 1, "Jake")
        assertDisplayedAtPosition(R.id.recyclerView, 1, "200")
    }

    @Test
    fun errorFromNetwork() {

        // GIVEN
        // create an Observable that emits nothing and then signals an error
        val errorEmittingObservable =
            Observable.error<List<Contributor>>(IllegalArgumentException())
        Mockito.`when`(
            model!!.getContributors(Matchers.anyString(), Matchers.anyString())
        )
            .thenReturn(errorEmittingObservable)

        // WHEN
        mActivityRule.launchActivity(Intent())
        clickOn(R.id.startBtn)

        // THEN
        assertDisplayed(R.id.errorText)
    }
}