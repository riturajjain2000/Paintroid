package at.tugraz.ist.paintroid.test.junit.command;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;
import at.tugraz.ist.paintroid.test.junit.stubs.BaseCommandStub;
import at.tugraz.ist.paintroid.test.utils.PaintroidAsserts;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;

public class BaseCommandTest extends AndroidTestCase {

	private BaseCommandStub mBaseCommand;
	protected PrivateAccess mPrivateAccess = new PrivateAccess();
	private Bitmap mBitmap;

	public BaseCommandTest() {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mBaseCommand = new BaseCommandStub();
		mBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		mPrivateAccess.setMemberValue(BaseCommand.class, mBaseCommand, "mBitmap", mBitmap);
	}

	@Test
	public void testFreeResources() {
		File cacheDir = PaintroidApplication.APPLICATION_CONTEXT.getCacheDir();
		File storedBitmap = new File(cacheDir.getAbsolutePath(), "test");
		try {
			assertFalse(storedBitmap.exists());

			mPrivateAccess.setMemberValue(BaseCommand.class, mBaseCommand, "mStoredBitmap", storedBitmap);
			mBaseCommand.freeResources();
			assertNull(mPrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mBitmap"));

			File restoredBitmap = (File) mPrivateAccess
					.getMemberValue(BaseCommand.class, mBaseCommand, "mStoredBitmap");
		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		}

		try {
			storedBitmap.createNewFile();
			assertTrue(storedBitmap.exists());
			mBaseCommand.freeResources();
			assertFalse(storedBitmap.exists());
			assertNull(mPrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mBitmap"));
		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		}

	}

	@Test
	public void testStoreBitmap() {
		File storedBitmap = null;
		try {
			mPrivateAccess.setMemberValue(BaseCommand.class, mBaseCommand, "mStoredBitmap", storedBitmap);
			mBaseCommand.storeBitmapStub();
			assertNull(mPrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mBitmap"));

			storedBitmap = (File) mPrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mStoredBitmap");
			assertNotNull(storedBitmap);
			assertNotNull(storedBitmap.getAbsolutePath());
			Bitmap restoredBitmap = BitmapFactory.decodeFile(storedBitmap.getAbsolutePath());
			PaintroidAsserts.assertBitmapEquals(restoredBitmap, mBitmap);

		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		} finally {
			if (storedBitmap != null) {
				if (storedBitmap.delete() == false)
					fail("Failed to delete the stored bitmap(0)");
			} else {
				fail("Failed to delete the stored bitmap(1)");
			}

		}

	}
}