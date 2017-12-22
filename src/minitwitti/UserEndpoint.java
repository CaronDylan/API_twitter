package minitwitti;

import minitwitti.PMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import minitwitti.Message;

@Api(name = "userendpoint", namespace = @ApiNamespace(ownerDomain = "mycompany.com", ownerName = "mycompany.com", packagePath="services"))
public class UserEndpoint {

  /**
   * This method lists all the entities inserted in datastore.
   * It uses HTTP GET method and paging support.
   *
   * @return A CollectionResponse class containing the list of all entities
   * persisted and a cursor to the next page.
   */
  @SuppressWarnings({"unchecked", "unused"})
  @ApiMethod(name = "listUser")
  public CollectionResponse<User> listUser(
    @Nullable @Named("cursor") String cursorString,
    @Nullable @Named("limit") Integer limit) {

    PersistenceManager mgr = null;
    Cursor cursor = null;
    List<User> execute = null;

    try{
      mgr = getPersistenceManager();
      Query query = mgr.newQuery(User.class);
      if (cursorString != null && cursorString != "") {
        cursor = Cursor.fromWebSafeString(cursorString);
        HashMap<String, Object> extensionMap = new HashMap<String, Object>();
        extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
        query.setExtensions(extensionMap);
      }

      if (limit != null) {
        query.setRange(0, limit);
      }

      execute = (List<User>) query.execute();
      cursor = JDOCursorHelper.getCursor(execute);
      if (cursor != null) cursorString = cursor.toWebSafeString();

      // Tight loop for fetching all entities from datastore and accomodate
      // for lazy fetch.
      for (User obj : execute);
    } finally {
      mgr.close();
    }

    return CollectionResponse.<User>builder()
      .setItems(execute)
      .setNextPageToken(cursorString)
      .build();
  }

  /**
   * This method gets the entity having primary key id. It uses HTTP GET method.
   *
   * @param id the primary key of the java bean.
   * @return The entity with primary key id.
   */
  @ApiMethod(name = "getUser")
  public User getUser(@Named("id") String id) {
    PersistenceManager mgr = getPersistenceManager();
    User user  = null;
    try {
      user = mgr.getObjectById(User.class, id);
    } finally {
      mgr.close();
    }
    return user;
  }
  
  /**
   * This method gets the messages of the entities the entity follows. It uses HTTP GET method.
   *
   * @param id the primary key of the java bean.
   * @return The entity with primary key id.
   */
  @ApiMethod(name = "timeline", httpMethod = HttpMethod.GET)
  public List<Message> timeline(@Named("id") String id) {
	  User u = getUser(id);
	  if (u.follows == null) {
		  return null;
	  } else {
	  List<Message> messages = new ArrayList<Message>();
	  for (String f : u.getFollows()) {
		  User u2 = getUser(f);
		  if (u2.twitsMsg != null) {
			  for (int i = 0; i < u2.twitsMsg.size(); i++) {
				  messages.add(new Message(u2.twitsMsg.get(i), u2.twitsDate.get(i)));
			  }
		  }
	  }
	  Collections.sort(messages, new Comparator<Message>() {
		   public int compare(Message o1, Message o2) {
		      Date a = o1.date;
		      Date b = o2.date;
		      if (a.before(b)) 
		        return 1;
		      else if (a.after(b))
		         return -1;
		      else
		         return 0;
		   }
		});
	  return messages;
	  }
  }

  /**
   * This inserts a new entity into App Engine datastore. If the entity already
   * exists in the datastore, an exception is thrown.
   * It uses HTTP POST method.
   *
   * @param user the entity to be inserted.
   * @return The inserted entity.
   */
  @ApiMethod(name = "insertUser")
  public User insertUser(User user) {
    PersistenceManager mgr = getPersistenceManager();
    try {
      if(containsUser(user)) {
        throw new EntityExistsException("Object already exists");
      }
      mgr.makePersistent(user);
    } finally {
      mgr.close();
    }
    return user;
  }

  /**
   * This method is used for updating an existing entity. If the entity does not
   * exist in the datastore, an exception is thrown.
   * It uses HTTP PUT method.
   *
   * @param user the entity to be updated.
   * @return The updated entity.
   */
  @ApiMethod(name = "updateUser")
  public User updateUser(User user) {
    PersistenceManager mgr = getPersistenceManager();
    try {
      if(!containsUser(user)) {
        throw new EntityNotFoundException("Object does not exist");
      }
      mgr.makePersistent(user);
    } finally {
      mgr.close();
    }
    return user;
  }
  
  /**
   * This method is used for updating an existing entity. If the entity does not
   * exist in the datastore, an exception is thrown.
   * It uses HTTP PUT method.
   *
   * @param user the entity to be updated.
   * @return The updated entity.
   */
  @ApiMethod(name = "follows")
  public User follows(@Named("id1") String id1, @Named("id2") String id2) {
	User u1 = getUser(id1);
	u1.follows(id2);
	return updateUser(u1);
  }
  
  /**
   * This method is used for updating an existing entity. If the entity does not
   * exist in the datastore, an exception is thrown.
   * It uses HTTP PUT method.
   *
   * @param user the entity to be updated.
   * @return The updated entity.
   */
  @ApiMethod(name = "twits")
  public User twits(@Named("id") String id, @Named("msg") String msg) {
	User u = getUser(id);
	u.twits(msg);
	return updateUser(u);
  }

  /**
   * This method removes the entity with primary key id.
   * It uses HTTP DELETE method.
   *
   * @param id the primary key of the entity to be deleted.
   */
  @ApiMethod(name = "removeUser")
  public void removeUser(@Named("id") String id) {
    PersistenceManager mgr = getPersistenceManager();
    try {
      User user = mgr.getObjectById(User.class, id);
      mgr.deletePersistent(user);
    } finally {
      mgr.close();
    }
  }
  
  

  private boolean containsUser(User user) {
    PersistenceManager mgr = getPersistenceManager();
    boolean contains = true;
    try {
      mgr.getObjectById(User.class, user.getId());
    } catch (javax.jdo.JDOObjectNotFoundException ex) {
      contains = false;
    } finally {
      mgr.close();
    }
    return contains;
  }

  private static PersistenceManager getPersistenceManager() {
    return PMF.get().getPersistenceManager();
  }

}
