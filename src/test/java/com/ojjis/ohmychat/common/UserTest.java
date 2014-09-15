package com.ojjis.ohmychat.common;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

/**
 * User Tester.
 *
 * @author <Authors name>
 * @since <pre>Nov 23, 2013</pre>
 * @version 1.0
 */
public class UserTest {
    private static String DEFAULT_NAME = "Hank";
    private static String DEFAULT_IP_ADDRESS = "192.168.1.1";

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {

    }

    /**
     *
     * Method: getName()
     *
     */
    @Test
    public void testGetName() throws Exception {
        String expectedName = "Hank";
        User u = new User(DEFAULT_NAME);

        assertEquals(expectedName, u.getName());
    }

    /**
     *
     * Method: setName(String name)
     *
     */
    @Test
    public void testSetName() throws Exception {
        User u = new User(DEFAULT_NAME);
        u.setName("Burt");

        assertNotSame(DEFAULT_NAME, u.getName());
    }


    /**
     *
     * Method: setOnline(boolean online)
     *
     */
    @Test
    public void testSetOnline() throws Exception {
        /*User u = new User(DEFAULT_NAME, DEFAULT_IP_ADDRESS);
        u.setOnline(false);
        assertEquals("Should be online",false, u.isOnline());*/
    }


} 
