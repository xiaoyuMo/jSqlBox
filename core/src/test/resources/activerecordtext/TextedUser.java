package activerecordtext;

import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.Assert;

import com.github.drinkjava2.helloworld.UsuageAndSpeedTest.User;
import com.github.drinkjava2.jsqlbox.TextUtils;
import com.github.drinkjava2.jsqlbox.annotation.Handler;
import com.github.drinkjava2.jsqlbox.annotation.Sql;
import com.github.drinkjava2.jsqlbox.handler.EntityListHandler;

/**
 * This is a sample to show put SQL in multiple line Strings(Text), a
 * build-helper-maven-plugin in pom.xml is required, detail see Readme.md
 * 
 * @author Yong Zhu
 */
public class TextedUser extends User {

	@Sql("insert into users (name,address) values(?,?)")
	public void insertOneUser(String name, String address) {
		this.guess(name, address);
	};

	public void updateAllUser(String name, String address) {
		this.guess(name, address);
	};
	/*- 
	 update users 
	      set name=?, address=?
	*/

	@Handler(MapListHandler.class)
	public List<Map<String, Object>> selectUsers(String name, String address) {
		return this.guess(name, address);
	};
	/*- 
	 select * from users 
	     where name=? and address=?
	*/

	public void deleteUsers(String name, String address) {
		this.guess(name, address);
	};
	/*- 
	 delete from users where name=? or address=?
	*/

	@Handler(MapListHandler.class)
	public List<Map<String, Object>> selectUsersByText(String name, String address) {
		return this.guess(name, address);
	};
	/*-
	   select * 
	   from 
	   users 
	      where 
	         name=:name 
	         and address=:address
	 */

	public List<User> selectUsersByText2(String name, String address) {
		return this.ctx().nQuery(new EntityListHandler(User.class), this.sqlString(), name, address);
	}
	/*-
	   select u.** 
	   from 
	   users u
	      where 
	         u.name=? and address=?
	 */

	public static void main(String[] args) {
		String javaSourceCode = TextUtils.getJavaSourceCodeUTF8(TextedUser.class);
		System.out.println(javaSourceCode);
		Assert.assertTrue(javaSourceCode.length() > 0);
	}

}
