package test.config;

import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.BeanBox;
import com.github.drinkjava2.jsqlbox.Dao;
import com.github.drinkjava2.jsqlbox.SqlBoxContext;

import test.config.JBeanBoxConfig.DefaultSqlBoxContextBox;
import test.config.JBeanBoxConfig.TxInterceptorBox;

/**
 * This is a configuration class, equal to XML in Spring
 *
 */
public class TestPrepare {

	/**
	 * Drop and rebuild all tables
	 */
	public static void prepareDatasource_setDefaultSqlBoxConetxt_recreateTables() {
		BeanBox.defaultContext.close();
		BeanBox.defaultContext.setAOPAround("test.\\w*.\\w*", "tx_\\w*", new TxInterceptorBox(), "invoke");
		SqlBoxContext.setDefaultSqlBoxContext(BeanBox.getBean(DefaultSqlBoxContextBox.class));

		System.out.println("Drop and re-create all tables for a new test ...");
		if (Dao.getDefaultDatabaseType().isOracle()) {
			Dao.executeQuiet("DROP TRIGGER TGR_2");
			Dao.executeQuiet("DROP SEQUENCE SEQ_2");
			Dao.executeQuiet("DROP TRIGGER TGR_1");
			Dao.executeQuiet("DROP SEQUENCE SEQ_1");
		}
		Dao.executeQuiet("drop table users");
		Dao.executeQuiet("drop table users2");

		if (Dao.getDefaultDatabaseType().isH2()) {
			Dao.execute("create table users ", //
					"(id integer auto_increment ,", //
					"constraint const1 primary key (ID),", //
					"username Varchar (50) ,", //
					"Phone_Number Varchar (50) ,", //
					"Address Varchar (50) ,", //
					"Alive Boolean, ", //
					"Age Integer )ENGINE=InnoDB DEFAULT CHARSET=utf8;");

			Dao.execute("create table users2", //
					"(id integer auto_increment ,", //
					"constraint const2 primary key (ID),", //
					"username Varchar (50) ,", //
					"Phone_Number Varchar (50) ,", //
					"Address Varchar (50) ,", //
					"Alive Boolean, ", //
					"Age Integer ) ");
		}

		if (Dao.getDefaultDatabaseType().isMySql()) {
			Dao.execute("create table users ", //
					"(id integer auto_increment ,", //
					"constraint const1 primary key (ID),", //
					"username Varchar (50) ,", //
					"Phone_Number Varchar (50) ,", //
					"Address Varchar (50) ,", //
					"Alive Boolean, ", //
					"Age Integer )ENGINE=InnoDB DEFAULT CHARSET=utf8;");

			Dao.execute("create table users2", //
					"(id integer auto_increment ,", //
					"constraint const2 primary key (ID),", //
					"username Varchar (50) ,", //
					"Phone_Number Varchar (50) ,", //
					"Address Varchar (50) ,", //
					"Alive Boolean, ", //
					"Age Integer )ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		}

		if (Dao.getDefaultDatabaseType().isOracle()) {
			Dao.execute("CREATE TABLE USERS", //
					"(ID INTEGER,", //
					"USERNAME VARCHAR (50) ,", //
					"PHONE_NUMBER VARCHAR (50) ,", //
					"ADDRESS VARCHAR (50) ,", //
					"ALIVE INTEGER, ", //
					"AGE INTEGER)");
			Dao.execute("CREATE TABLE USERS2", //
					"(ID INTEGER,", //
					"USERNAME VARCHAR (50) ,", //
					"PHONE_NUMBER VARCHAR (50) ,", //
					"ADDRESS VARCHAR (50) ,", //
					"ALIVE INTEGER, ", //
					"AGE INTEGER)");
			Dao.execute(
					"CREATE SEQUENCE SEQ_1 MINVALUE 1 MAXVALUE 99999999 START WITH 1 INCREMENT BY 1 NOCYCLE CACHE 10");
			Dao.execute(
					"CREATE TRIGGER TGR_1 BEFORE INSERT ON USERS FOR EACH ROW BEGIN SELECT SEQ_1.NEXTVAL INTO:NEW.ID FROM DUAL; END;");
			Dao.execute(
					"CREATE SEQUENCE SEQ_2 MINVALUE 1 MAXVALUE 99999999 START WITH 1 INCREMENT BY 1 NOCYCLE CACHE 10");
			Dao.execute(
					"CREATE TRIGGER TGR_2 BEFORE INSERT ON USERS2 FOR EACH ROW BEGIN SELECT SEQ_2.NEXTVAL INTO:NEW.ID FROM DUAL; END;");
		}
		Dao.refreshMetaData();
	}

	/**
	 * Close BeanBox Context, c3p0 close method will be called before context be closed
	 */
	public static void closeDatasource_closeDefaultSqlBoxConetxt() {
		BeanBox.defaultContext.close();// This will close HikariDataSource because preDestroy method set to "Close"
		SqlBoxContext.getDefaultSqlBoxContext().close();
	}

	@Test
	public void testCreateTables() {
		System.out.println("===============================Testing TestPrepare===============================");
		prepareDatasource_setDefaultSqlBoxConetxt_recreateTables();
		Assert.assertEquals(0, (int) Dao.queryForInteger("select count(*) from users"));
		Assert.assertEquals(0, (int) Dao.queryForInteger("select count(*) from users2"));
		closeDatasource_closeDefaultSqlBoxConetxt();
	}

	public static void main(String[] args) {
		prepareDatasource_setDefaultSqlBoxConetxt_recreateTables();
		Assert.assertEquals(0, (int) Dao.queryForInteger("select count(*) from users"));
		Assert.assertEquals(0, (int) Dao.queryForInteger("select count(*) from users2"));
		closeDatasource_closeDefaultSqlBoxConetxt();
	}
}
