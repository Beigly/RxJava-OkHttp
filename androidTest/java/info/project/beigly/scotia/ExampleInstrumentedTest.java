package info.project.beigly.scotia;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("info.project.beigly.scotia", appContext.getPackageName());
    }
    @Test
    public void testNull() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertNotNull( appContext.getAssets());

    }
    @Test
    public void testperm() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertNotNull( appContext.checkCallingOrSelfPermission("Internet") );

    }
    @Test
    public void testresource() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertNotNull( appContext.getPackageResourcePath() );

    }
    @Test
    public void testAssertEquals() {

        assertEquals( MainActivity.RESULT_OK, 0);
    }

}
