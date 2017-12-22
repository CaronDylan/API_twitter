package minitwitti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class User {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	String id;

	@Persistent
	List<String> follows;
	@Persistent
	List<String> twitsMsg;
	@Persistent
	List<Date> twitsDate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getFollows() {
		return follows;
	}
	public void setFollows(List<String> follows) {
		this.follows = follows;
	}
	public void follows(String id) {
		if (follows == null) {
			follows = new ArrayList<String>();
		}
		follows.add(id);
	}
	
	public List<String> getMsg() {
		return twitsMsg;
	}
	public void setMsg(List<String> twitsMsg) {
		this.twitsMsg = twitsMsg;
	}
	
	public List<Date> getDate() {
		return twitsDate;
	}
	public void setDate(List<Date> twitsDate) {
		this.twitsDate = twitsDate;
	}
	
	public void twits(String msg) {
		if (twitsMsg == null) {
			twitsMsg = new ArrayList<String>();
			twitsDate = new ArrayList<Date>();
		}
		twitsMsg.add(msg);
		twitsDate.add(new Date());
	}

}
