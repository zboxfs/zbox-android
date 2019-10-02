package io.zbox.fs.test.suite;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.File;
import java.security.SecureRandom;

import io.zbox.fs.Env;

@RunWith(Suite.class)
@Suite.SuiteClasses({ RepoTest.class, PersistTest.class, FileTest.class, DirTest.class })
public class TestSuite {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static SecureRandom random = new SecureRandom();
    private static int RANDOM_STR_LEN = 8;

    static File testDir;

    @BeforeClass
    public static void before() {
        Env.init(Env.LOG_DEBUG);

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File base = context.getFilesDir();
        testDir = new File(base, "zbox_test");
        if (testDir.exists()) {
            deleteDirRecursively(testDir);
        }
        testDir.mkdirs();
    }

    @AfterClass
    public static void after() {
        if (testDir.exists()) {
            deleteDirRecursively(testDir);
        }
    }

    public static String randomString() {
        StringBuilder sb = new StringBuilder(RANDOM_STR_LEN);
        for (int i = 0; i < RANDOM_STR_LEN; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }

        return sb.toString();
    }

    public static String makeFileRepoUri() {
         return "file://" + testDir + "/" + randomString();
    }

    public static String makeMemRepoUri() {
        return "mem://" + randomString();
    }

    private static void deleteDirRecursively(File dir) {
        if (dir.isDirectory()) {
            for (File child : dir.listFiles()) {
                deleteDirRecursively(child);
            }
        }
        dir.delete();
    }

}
