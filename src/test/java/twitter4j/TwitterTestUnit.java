package twitter4j;

import junit.framework.TestCase;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class TwitterTestUnit extends TestCase {
    private Twitter twitterAPI1 = null;
    private Twitter twitterAPI2 = null;

    public TwitterTestUnit(String name) {
        super(name);
    }
    String id1,id2,pass1,pass2;
    protected void setUp() throws Exception {
        super.setUp();
        Properties p = new Properties();
        p.load(new FileInputStream("test.properties"));
        id1 = p.getProperty("id1");
        id2 = p.getProperty("id2");
        pass1 = p.getProperty("pass1");
        pass2 = p.getProperty("pass2");
        twitterAPI1 = new Twitter(id1,pass1);
        twitterAPI1.setRetryCount(3);
        twitterAPI1.setRetryIntervalSecs(10);
        twitterAPI2 = new Twitter(id2,pass2);
         twitterAPI2.setRetryCount(3);
        twitterAPI2.setRetryIntervalSecs(10);

//        twitterAPI1.setBaseURL("http://127.0.0.1:8080/");
        twitterAPI1.forceUsePost(true);
//        twitterAPI2.setBaseURL("http://127.0.0.1:8080/");
        twitterAPI2.forceUsePost(true);
    }

    protected void tearDown() throws Exception {
        twitterAPI1 = null;
        super.tearDown();
    }

    public void testGetPublicTimeline() throws Exception {
        List<Status> statuses;
        statuses = twitterAPI1.getPublicTimeline();
        assertTrue("size", 5 < statuses.size());
        statuses = twitterAPI1.getPublicTimeline("12345");
        assertTrue("size", 5 < statuses.size());

    }

    public void testGetFriendsTimeline()throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String dateStr = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DATE);


        String id1status = dateStr+":id1";
        String id2status = dateStr+":id2";
        Status status = twitterAPI1.update(id1status);
        assertEquals(id1status, status.getText());
        Thread.sleep(3000);
        Status status2 = twitterAPI2.update(id2status);
        assertEquals(id2status, status2.getText());

        List<Status> actualReturn;

        actualReturn = twitterAPI1.getFriendsTimeline();
        assertTrue(actualReturn.size() > 0);
        actualReturn = twitterAPI1.getFriendsTimeline(new Date(0));
        assertTrue(actualReturn.size() > 0);

        actualReturn = twitterAPI2.getFriendsTimeline(id1);
        assertTrue(actualReturn.size() > 0);


        actualReturn = twitterAPI1.getFriendsTimeline(id2, new Date(0));
        assertTrue(actualReturn.size() > 0);
//        actualReturn = twitterAPI1.getFriendsTimeline(id2, new Date());
//        assertTrue(actualReturn.size() == 0);
        actualReturn = twitterAPI1.getFriendsTimelineByPage(1);
        assertTrue(actualReturn.size() > 0);


    }
    public void testGetUserDetail() throws Exception{
        UserWithStatus uws = twitterAPI1.getUserDetail(id1);
        assertEquals(id1, uws.getName());
        assertTrue(0 <= uws.getFavouritesCount());
        assertTrue(0 <= uws.getFollowersCount());
        assertTrue(0 <= uws.getFriendsCount());
        assertTrue(0 <= uws.getStatusesCount());
        assertNotNull(uws.getProfileBackgroundColor());
        assertNotNull(uws.getProfileTextColor());
        assertNotNull(uws.getProfileLinkColor());
        assertNotNull(uws.getProfileSidebarBorderColor());
        assertNotNull(uws.getProfileSidebarFillColor());
        assertNotNull(uws.getProfileTextColor());
    }


    public void testGetUserTimeline_Show() throws Exception{
        List<Status> statuses;
        statuses = twitterAPI2.getUserTimeline(id1);
        assertTrue("size", 1 < statuses.size());
        statuses = twitterAPI2.getUserTimeline(id1, 10);
        assertTrue("size", 1 < statuses.size());
        statuses = twitterAPI1.getUserTimeline(15, new Date(0));
        assertTrue("size", 1 < statuses.size());
        statuses = twitterAPI1.getUserTimeline(id1, new Date(0));
        assertTrue("size", 1 < statuses.size());
        statuses = twitterAPI1.getUserTimeline(id1, 20, new Date(0));
        assertTrue("size", 1 < statuses.size());
    }
    public void testShow() throws Exception{
        Status status = twitterAPI2.show(1000);
        assertEquals(52,status.getUser().getId());
    }
    public void testUpdate() throws Exception{
        String date = new java.util.Date().toString()+"test";
        Status status = twitterAPI1.update(date);
        assertEquals("",date, status.getText());
    }
    public void testGetFriends() throws Exception{
        List<User> actualReturn = twitterAPI1.getFriends(id2);
        boolean found = false;
        for(User user: actualReturn){
            found = found || user.getName().equals(id1);
        }
        assertTrue(found);

        actualReturn = twitterAPI2.getFriends();
        found = false;
        for(User user: actualReturn){
            found = found || user.getName().equals(id1);
        }
        assertTrue(found);
        assertEquals(100,twitterAPI2.getFriends("akr",2).size());
    }
    public void testAccountMethods() throws Exception{
        assertTrue(twitterAPI1.verifyCredentials());
        assertFalse(new Twitter("doesnotexist","foobar").verifyCredentials());
        assertTrue(twitterAPI2.archive().size() > 20);
    }
    public void testFavoriteMethods() throws Exception{
        Status status = twitterAPI1.update("test");
        twitterAPI2.createFavorite(status.getId());
        assertTrue(twitterAPI2.favorites().size() >0);
        twitterAPI2.destroyFavorite(status.getId());
    }
    public void testFollowers() throws Exception{
        List<User> actualReturn = twitterAPI1.getFollowers();
        assertTrue(actualReturn.size() > 0);

        actualReturn = twitterAPI2.getFollowers();
        assertTrue(actualReturn.size() > 0);
    }
    public void testFeatured() throws Exception{
        List<User> actualReturn = twitterAPI1.getFeatured();
        assertTrue(actualReturn.size() > 9);
    }

    public void testGetDirectMessages() throws Exception{
        try {
            twitterAPI1.follow(id2);
        } catch (twitter4j.TwitterException te) {
        }
        try {
            twitterAPI2.follow(id1);
        } catch (twitter4j.TwitterException te) {
        }

        String expectedReturn = new Date()+":directmessage test";
//        twitterAPI2.sendDirectMessage(id1,expectedReturn);
        twitterAPI1.sendDirectMessage(id2,expectedReturn);
//        twitterAPI2.sendDirectMessage("yusukey",expectedReturn);
        List<DirectMessage> actualReturn = twitterAPI2.getDirectMessages();
        assertEquals("", expectedReturn, actualReturn.get(0).getText());
//        String expectedReturn = new Date()+":directmessage test";
        DirectMessage message = twitterAPI1.sendDirectMessage(id2,expectedReturn);
        assertEquals("", expectedReturn, message.getText());
        Thread.sleep(5000);
        actualReturn = twitterAPI2.getDirectMessages(new Date(System.currentTimeMillis()-(1000*60*100)));
        assertEquals("", expectedReturn, actualReturn.get(0).getText());
        assertEquals("", id2, actualReturn.get(0).getRecipient().getName());
        assertEquals("", id1, actualReturn.get(0).getSender().getName());

        //test for TFJ-4
        //http://yusuke.homeip.net/jira/browse/TFJ-4
        actualReturn = twitterAPI1.getDirectMessages(new Date());
        assertEquals(0,actualReturn.size());

        actualReturn = twitterAPI1.getDirectMessages();
        int size = actualReturn.size();
        message = twitterAPI1.deleteDirectMessage(actualReturn.get(0).getId());
        assertEquals(message.getId(),actualReturn.get(0).getId());
        assertTrue(10< twitterAPI1.getDirectMessages().size());

        actualReturn = twitterAPI1.getSentDirectMessages();
        assertTrue(5 < actualReturn.size());
        assertEquals(id1 , actualReturn.get(0).getSender().getName());
        assertEquals(id2 , actualReturn.get(0).getRecipient().getName());

        actualReturn = twitterAPI1.getDirectMessagesByPage(1);
        assertTrue(10< twitterAPI1.getDirectMessages().size());

    }
    public void testCreateDestroyFriend() throws Exception{
        User user;
        try {
            user = twitterAPI2.destroy(id1);
        } catch (TwitterException te) {
            //ensure destory id1 before the actual test
        }

        try {
            user = twitterAPI2.destroy(id1);
        } catch (TwitterException te) {
            assertEquals(403, te.getStatusCode());
        }
        user = twitterAPI2.create(id1);
        assertEquals(id1, user.getName());
        try {
            user = twitterAPI2.create(id2);
            fail("shouldn't be able to befrinend yourself");
        } catch (TwitterException te) {
            assertEquals(403, te.getStatusCode());
        }
        try {
            user = twitterAPI2.create("doesnotexist");
            fail("non-existing user");
        } catch (TwitterException te) {
            assertEquals(403, te.getStatusCode());
        }

    }
    public void testGetReplies() throws Exception{
        twitterAPI2.update("@"+id1+" reply to id1");
        List<Status> statuses = twitterAPI1.getReplies();
        assertTrue(statuses.size() > 0);
        assertTrue(-1 != statuses.get(0).getText().indexOf(" reply to id1"));

        statuses = twitterAPI1.getRepliesByPage(1);
        assertTrue(statuses.size() > 0);
        assertTrue(-1 != statuses.get(0).getText().indexOf(" reply to id1"));
    }

    public void testNotification() throws Exception {
        try {
            twitterAPI2.follow(id1);
        } catch (TwitterException te) {

        }
        twitterAPI2.leave(id1);
        try {
            twitterAPI2.leave(id1);
            fail("should fail");
        } catch (TwitterException te) {

        }
        twitterAPI2.follow(id1);
        try {
            twitterAPI2.follow(id1);
            fail("should fail");
        } catch (TwitterException te) {

        }
    }
}
