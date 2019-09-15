package io.zbox;

import android.util.Log;

import org.junit.Test;

import java.util.StringTokenizer;

import static org.junit.Assert.*;

import io.zbox.fs.Env;
import io.zbox.fs.Repo;
import io.zbox.fs.RepoOpener;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_zbox() {
        /*String property = System.getProperty("java.library.path");
        StringTokenizer parser = new StringTokenizer(property, ";");
        while (parser.hasMoreTokens()) {
            System.err.println(parser.nextToken());
        }*/


        Env.init();

        RepoOpener opener = new RepoOpener();
        opener.create(true);

        try {
            //Repo repo = opener.open("zbox://DjqSXTe6khbUmuJQmUSRfjqM@7am6mKWr43p28P", "pwd");
            Repo repo = opener.open("mem://foo", "pwd");
            repo.close();
        } catch (Exception err) {
            Log.e("xxx", err.toString());
        }
    }
}