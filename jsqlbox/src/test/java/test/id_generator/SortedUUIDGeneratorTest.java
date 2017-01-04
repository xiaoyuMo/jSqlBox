package test.id_generator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.jsqlbox.Dao;
import com.github.drinkjava2.jsqlbox.id.SortedUUIDGenerator;
import com.github.drinkjava2.jsqlbox.id.TableGenerator;
import com.github.drinkjava2.jsqlbox.id.UUIDAnyGenerator;

import test.config.TestPrepare;
import test.config.po.User;

public class SortedUUIDGeneratorTest {

	@Before
	public void setup() {
		System.out.println(
				"===============================Testing SortedUUIDGeneratorTest===============================");
		TestPrepare.prepareDatasource_setDefaultSqlBoxConetxt_recreateTables();
	}

	@After
	public void cleanUp() {
		TestPrepare.closeDatasource_closeDefaultSqlBoxConetxt();
	}

	public static class TableGeneratorBox extends BeanBox {
		{
			this.setConstructor(TableGenerator.class, "T", "PK", "PV", "V", 1, 50);
		}
	}

	public static class UUIDAnyGeneratorBox extends BeanBox {
		{
			this.setConstructor(UUIDAnyGenerator.class, 20);
		}
	}

	public static class SortedUUIDBox extends BeanBox {
		{
			this.setConstructor(SortedUUIDGenerator.class, TableGeneratorBox.class, UUIDAnyGeneratorBox.class, 29);
		}
	}

	@Test
	public void insertUserInMysql() {
		if (!Dao.getDefaultDatabaseType().isMySql())
			return;
		User u = new User();
		Dao.executeQuiet("drop table t");
		Dao.executeQuiet("create table t (pk varchar(5),v int(6)) ENGINE=InnoDB DEFAULT CHARSET=utf8");
		u.box().configIdGenerator("userName", BeanBox.getBean(SortedUUIDBox.class));
		for (int i = 0; i < 60; i++)
			u.insert();
		Assert.assertEquals(60, (int) Dao.queryForInteger("select count(*) from ", u.table()));
	}

	@Test
	public void insertUserInOracle() {
		if (!Dao.getDefaultDatabaseType().isOracle())
			return;
		User u = new User();
		Dao.executeQuiet("drop table T");
		Dao.executeQuiet("CREATE TABLE T (PK VARCHAR(5),V INTEGER) ");
		u.box().configIdGenerator("userName", BeanBox.getBean(SortedUUIDBox.class));
		for (int i = 0; i < 60; i++)
			u.insert();
		Assert.assertEquals(60, (int) Dao.queryForInteger("select count(*) from ", u.table()));
	}

}