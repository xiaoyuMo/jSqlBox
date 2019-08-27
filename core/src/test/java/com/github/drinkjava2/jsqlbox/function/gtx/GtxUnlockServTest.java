package com.github.drinkjava2.jsqlbox.function.gtx;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jdialects.id.UUID25Generator;
import com.github.drinkjava2.jsqlbox.SqlBoxContext;
import com.github.drinkjava2.jsqlbox.function.jtransactions.Usr;
import com.github.drinkjava2.jsqlbox.gtx.GtxConnectionManager;
import com.github.drinkjava2.jsqlbox.gtx.GtxId;
import com.github.drinkjava2.jsqlbox.gtx.GtxLock;
import com.github.drinkjava2.jsqlbox.gtx.GtxUnlockServ;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Global TX Test
 * 
 * @author Yong Zhu
 * @since 2.0.7
 */
public class GtxUnlockServTest {
	SqlBoxContext[] ctx = new SqlBoxContext[3];

	private static DataSource newTestDataSource(String dbName) {
		HikariDataSource ds = new HikariDataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setJdbcUrl("jdbc:h2:mem:" + UUID25Generator.getUUID25()
				+ ";MODE=MYSQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0");
		ds.setUsername("sa");
		ds.setPassword("");
		return ds;
	}

	@Before
	public void init() {
		SqlBoxContext.resetGlobalVariants();
		SqlBoxContext.setGlobalNextAllowShowSql(true);

		SqlBoxContext lock = new SqlBoxContext(newTestDataSource("GxLocker"));
		lock.setName("lock");
		lock.executeDDL(lock.toCreateDDL(GtxId.class));
		lock.executeDDL(lock.toCreateDDL(GtxLock.class));
		lock.executeDDL(lock.toCreateGtxLogDDL(Usr.class));

		GtxConnectionManager lockCM = new GtxConnectionManager(lock);
		for (int i = 0; i < 3; i++) {
			ctx[i] = new SqlBoxContext(newTestDataSource("GxDb" + i));
			ctx[i].setName("db");
			ctx[i].setDbCode(i);
			ctx[i].setConnectionManager(lockCM);
			ctx[i].setMasters(ctx);
			ctx[i].executeDDL(ctx[i].toCreateDDL(GtxId.class));
			ctx[i].executeDDL(ctx[i].toCreateDDL(Usr.class));
		}

	}

	public void commitFailNoLockTest() {
		ctx[0].startTrans();
		try {
			new Usr().insert(ctx[0]);
			new Usr().insert(ctx[1]);
			new Usr().insert(ctx[1]);
			new Usr().insert(ctx[2]);
			ctx[2].setForceCommitFail(); // force ctx[1] commit fail
			ctx[0].commitTrans(); // exception will throw
		} catch (Exception e) {
			ctx[0].rollbackTrans();
		}

	}

	@Test
	public void gtxUnlockServTest() {
		GtxUnlockServTest t = new GtxUnlockServTest();
		t.init();
		t.commitFailNoLockTest();
		Assert.assertEquals(1, t.ctx[0].eCountAll(Usr.class));
		Assert.assertEquals(2, t.ctx[1].eCountAll(Usr.class));
		Assert.assertEquals(0, t.ctx[2].eCountAll(Usr.class));
		GtxUnlockServ.start(t.ctx[0], 1, 2);
		Assert.assertEquals(0, t.ctx[0].eCountAll(Usr.class));
		Assert.assertEquals(0, t.ctx[1].eCountAll(Usr.class));
		Assert.assertEquals(0, t.ctx[2].eCountAll(Usr.class));
	}

}
