/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.jsqlbox;

import com.github.drinkjava2.jdbpro.DbProConfig;
import com.github.drinkjava2.jdialects.Dialect;

/**
 * SqlBoxContextConfig class is used to store constructor parameters for build
 * SqlBoxContext
 * 
 * @author Yong Zhu
 * @since 1.0.1
 */
public class SqlBoxContextConfig extends DbProConfig {
	private Dialect dialect = SqlBoxContext.getGlobalNextDialect();
	private SqlMapperGuesser sqlMapperGuesser = SqlBoxContext.getGlobalNextSqlMapperGuesser();

	public SqlBoxContextConfig() {
		super();
		if (this.getShardingTools() == null) {
			this.setShardingTools(SqlBoxContext.getGlobalNextShardingTools());
		}
	}

	public Dialect getDialect() {
		return dialect;
	}

	public SqlBoxContextConfig setDialect(Dialect dialect) {
		this.dialect = dialect;
		return this;
	}

	public SqlMapperGuesser getSqlMapperGuesser() {
		return sqlMapperGuesser;
	}

	public void setSqlMapperGuesser(SqlMapperGuesser sqlMapperGuesser) {
		this.sqlMapperGuesser = sqlMapperGuesser;
	}

}