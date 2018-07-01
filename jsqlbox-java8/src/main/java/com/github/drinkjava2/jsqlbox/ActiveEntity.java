package com.github.drinkjava2.jsqlbox;

import java.lang.reflect.Method;

import com.github.drinkjava2.jdbpro.PreparedSQL;
import com.github.drinkjava2.jdbpro.SqlItem;
import com.github.drinkjava2.jdbpro.SqlOption;
import com.github.drinkjava2.jdialects.ClassCacheUtils;
import com.github.drinkjava2.jdialects.TableModelUtils;
import com.github.drinkjava2.jdialects.model.ColumnModel;
import com.github.drinkjava2.jdialects.model.TableModel;

/**
 * ActiveEntity is a interface has default methods only supported for Java8+, so
 * in Java8 and above, a POJO can implements ActiveEntity interface to obtain
 * CRUD methods instead of extends ActiveRecord class
 */

public interface ActiveEntity extends ActiveRecordSupport {

	@Override
	public default SqlBox box() {
		SqlBox box = SqlBoxUtils.findBoxOfPOJO(this);
		if (box == null) {
			box = SqlBoxUtils.createSqlBox(SqlBoxContext.gctx(), this.getClass());
			SqlBoxUtils.bindBoxToPOJO(this, box);
		}
		return box;
	}

	@Override
	public default TableModel tableModel() {
		return TableModelUtils.entity2Model(this.getClass());
	}

	@Override
	public default ColumnModel columnModel(String colOrFieldName) {
		return tableModel().getColumn(colOrFieldName);
	}

	@Override
	public default String table() {
		return tableModel().getTableName();
	}

	@Override
	public default SqlBoxContext ctx() {
		SqlBox theBox = box();
		if (theBox.getContext() == null)
			theBox.setContext(SqlBoxContext.getGlobalSqlBoxContext());
		return theBox.getContext();
	}

	@Override
	public default ActiveRecordSupport useContext(SqlBoxContext ctx) {
		box().setContext(ctx);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public default <T> T insert(Object... optionalSqlItems) {
		SqlBoxContext ctx = ctx();
		if (ctx == null)
			throw new SqlBoxException(SqlBoxContext.NO_GLOBAL_SQLBOXCONTEXT_FOUND);
		ctx.insert(this, optionalSqlItems);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public default <T> T update(Object... optionalSqlItems) {
		SqlBoxContext ctx = ctx();
		if (ctx == null)
			throw new SqlBoxException(SqlBoxContext.NO_GLOBAL_SQLBOXCONTEXT_FOUND);
		ctx.update(this, optionalSqlItems);
		return (T) this;
	}

	@Override
	public default void delete(Object... optionalSqlItems) {
		SqlBoxContext ctx = ctx();
		if (ctx == null)
			throw new SqlBoxException(SqlBoxContext.NO_GLOBAL_SQLBOXCONTEXT_FOUND);
		ctx.delete(this, optionalSqlItems);
	}

	@Override
	public default <T> T load(Object... optionalSqlItems) {
		SqlBoxContext ctx = ctx();
		if (ctx == null)
			throw new SqlBoxException(SqlBoxContext.NO_GLOBAL_SQLBOXCONTEXT_FOUND);
		return ctx.load(this, optionalSqlItems);
	}

	@Override
	public default <T> T loadById(Object idOrIdMap, Object... optionalSqlItems) {
		SqlBoxContext ctx = ctx();
		if (ctx == null)
			throw new SqlBoxException(SqlBoxContext.NO_GLOBAL_SQLBOXCONTEXT_FOUND);
		return ctx.loadById(this, idOrIdMap, optionalSqlItems);
	}

	@SuppressWarnings("unchecked")
	@Override
	public default <T> T loadByQuery(Object... sqlItems) {
		SqlBoxContext ctx = ctx();
		if (ctx == null)
			throw new SqlBoxException(SqlBoxContext.NO_GLOBAL_SQLBOXCONTEXT_FOUND);
		return ctx.loadByQuery((Class<T>) this.getClass(), sqlItems);
	}

	@Override
	public default ActiveRecordSupport put(Object... fieldAndValues) {
		for (int i = 0; i < fieldAndValues.length / 2; i++) {
			String field = (String) fieldAndValues[i * 2];
			Object value = fieldAndValues[i * 2 + 1];
			Method writeMethod = ClassCacheUtils.getClassFieldWriteMethod(this.getClass(), field);
			try {
				writeMethod.invoke(this, value);
			} catch (Exception e) {
				throw new SqlBoxException(e);
			}
		}
		return this;
	}

	@Override
	public default ActiveRecordSupport putFields(String... fieldNames) {
		lastTimePutFieldsCache.set(fieldNames);
		return this;
	}

	@Override
	public default ActiveRecordSupport putValues(Object... values) {
		String[] fields = lastTimePutFieldsCache.get();
		if (values.length == 0 || fields == null || fields.length == 0)
			throw new SqlBoxException("putValues fields or values can not be empty");
		if (values.length != fields.length)
			throw new SqlBoxException("putValues fields and values number not match");
		for (int i = 0; i < fields.length; i++) {
			Method writeMethod = ClassCacheUtils.getClassFieldWriteMethod(this.getClass(), fields[i]);
			if (writeMethod == null)
				throw new SqlBoxException(
						"Not found writeMethod for '" + this.getClass() + "' class's method '" + fields[i] + "'");
			try {
				writeMethod.invoke(this, values[i]);
			} catch (Exception e) {
				throw new SqlBoxException(e);
			}
		}
		return this;
	}

	@Override
	public default <T> T guess(Object... params) {// NOSONAR
		return ctx().getSqlMapperGuesser().guess(ctx(), this, params);
	}

	@Override
	public default String guessSQL() {
		return ctx().getSqlMapperGuesser().guessSQL(ctx(), this);
	}

	@Override
	public default PreparedSQL guessPreparedSQL(Object... params) {
		return ctx().getSqlMapperGuesser().doGuessPreparedSQL(ctx(), this, params);
	}

	@Override
	public default SqlItem bind(Object... parameters) {
		return new SqlItem(SqlOption.BIND, parameters);
	}

	@Override
	public default String shardTB(Object... optionItems) {
		TableModel model = SqlBoxContextUtils.findTableModel(this.getClass(), optionItems);
		ColumnModel col = model.getShardTableColumn();
		if (col == null || col.getShardTable() == null || col.getShardTable().length == 0)
			throw new SqlBoxException("Not found ShardTable setting for '" + model.getEntityClass() + "'");
		Object shardKey1 = ClassCacheUtils.readValueFromBeanField(this, col.getColumnName());
		return SqlBoxContextUtils.getShardedTB(ctx(), model.getEntityClass(), shardKey1);
	}

	@Override
	public default SqlBoxContext shardDB(Object... optionItems) {
		TableModel model = SqlBoxContextUtils.findTableModel(this.getClass(), optionItems);
		ColumnModel col = model.getShardDatabaseColumn();
		if (col == null || col.getShardDatabase() == null || col.getShardDatabase().length == 0)
			throw new SqlBoxException("Not found ShardTable setting for '" + model.getEntityClass() + "'");
		Object shardKey1 = ClassCacheUtils.readValueFromBeanField(this, col.getColumnName());
		return SqlBoxContextUtils.getShardedDB(ctx(), model.getEntityClass(), shardKey1);
	}
}