package com.hoffnungland.db.corner.dbconn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class JdbcTestSupport {

	private JdbcTestSupport() {
	}

	static TestConnection connection() {
		return new TestConnection();
	}

	static TestResultSet resultSet(Map<String, String>... rows) {
		return new TestResultSet(Arrays.asList(rows));
	}

	static Map<String, String> row(String... keyValues) {
		Map<String, String> row = new HashMap<String, String>();
		for (int idx = 0; idx < keyValues.length; idx += 2) {
			row.put(keyValues[idx], keyValues[idx + 1]);
		}
		return row;
	}

	static final class TestConnection {
		private final Deque<TestResultSet> preparedResults = new ArrayDeque<TestResultSet>();
		private final Deque<TestResultSet> callableResults = new ArrayDeque<TestResultSet>();
		final List<String> preparedSql = new ArrayList<String>();
		final List<String> callableSql = new ArrayList<String>();
		final List<TestPreparedStatement> preparedStatements = new ArrayList<TestPreparedStatement>();
		final List<TestCallableStatement> callableStatements = new ArrayList<TestCallableStatement>();
		Boolean autoCommitValue;
		int closeCalls;
		int commitCalls;
		int rollbackCalls;

		private final Connection proxy = (Connection) Proxy.newProxyInstance(
				Connection.class.getClassLoader(),
				new Class<?>[] {Connection.class},
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) {
						String name = method.getName();
						if ("prepareStatement".equals(name)) {
							String sql = (String) args[0];
							preparedSql.add(sql);
							TestPreparedStatement statement = new TestPreparedStatement(sql, preparedResults.pollFirst());
							preparedStatements.add(statement);
							return statement.proxy();
						}
						if ("prepareCall".equals(name)) {
							String sql = (String) args[0];
							callableSql.add(sql);
							TestCallableStatement statement = new TestCallableStatement(sql, callableResults.pollFirst());
							callableStatements.add(statement);
							return statement.proxy();
						}
						if ("setAutoCommit".equals(name)) {
							autoCommitValue = (Boolean) args[0];
							return null;
						}
						if ("close".equals(name)) {
							closeCalls++;
							return null;
						}
						if ("commit".equals(name)) {
							commitCalls++;
							return null;
						}
						if ("rollback".equals(name)) {
							rollbackCalls++;
							return null;
						}
						return defaultValue(method, proxy, this, args);
					}
				});

		Connection proxy() {
			return this.proxy;
		}

		void enqueuePreparedResult(TestResultSet resultSet) {
			this.preparedResults.addLast(resultSet);
		}

		void enqueueCallableResult(TestResultSet resultSet) {
			this.callableResults.addLast(resultSet);
		}
	}

	static class TestPreparedStatement {
		final String sql;
		final TestResultSet resultSet;
		int closeCalls;
		int executeQueryCalls;

		private final PreparedStatement proxy = (PreparedStatement) Proxy.newProxyInstance(
				PreparedStatement.class.getClassLoader(),
				new Class<?>[] {PreparedStatement.class},
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) {
						String name = method.getName();
						if ("executeQuery".equals(name)) {
							executeQueryCalls++;
							return resultSet == null ? null : resultSet.proxy();
						}
						if ("getResultSet".equals(name)) {
							return resultSet == null ? null : resultSet.proxy();
						}
						if ("close".equals(name)) {
							closeCalls++;
							return null;
						}
						if ("execute".equals(name)) {
							return Boolean.TRUE;
						}
						return defaultValue(method, proxy, this, args);
					}
				});

		TestPreparedStatement(String sql, TestResultSet resultSet) {
			this.sql = sql;
			this.resultSet = resultSet;
		}

		PreparedStatement proxy() {
			return this.proxy;
		}
	}

	static final class TestCallableStatement extends TestPreparedStatement {
		private final CallableStatement proxy = (CallableStatement) Proxy.newProxyInstance(
				CallableStatement.class.getClassLoader(),
				new Class<?>[] {CallableStatement.class},
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) {
						String name = method.getName();
						if ("executeQuery".equals(name)) {
							executeQueryCalls++;
							return resultSet == null ? null : resultSet.proxy();
						}
						if ("getResultSet".equals(name)) {
							return resultSet == null ? null : resultSet.proxy();
						}
						if ("close".equals(name)) {
							closeCalls++;
							return null;
						}
						if ("execute".equals(name)) {
							return Boolean.TRUE;
						}
						return defaultValue(method, proxy, this, args);
					}
				});

		TestCallableStatement(String sql, TestResultSet resultSet) {
			super(sql, resultSet);
		}

		@Override
		CallableStatement proxy() {
			return this.proxy;
		}
	}

	static final class TestResultSet {
		private final List<Map<String, String>> rows;
		private int index = -1;
		int closeCalls;

		private final ResultSet proxy = (ResultSet) Proxy.newProxyInstance(
				ResultSet.class.getClassLoader(),
				new Class<?>[] {ResultSet.class},
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) {
						String name = method.getName();
						if ("next".equals(name)) {
							index++;
							return index < rows.size();
						}
						if ("getString".equals(name)) {
							String columnLabel = (String) args[0];
							return rows.get(index).get(columnLabel);
						}
						if ("close".equals(name)) {
							closeCalls++;
							return null;
						}
						return defaultValue(method, proxy, this, args);
					}
				});

		TestResultSet(List<Map<String, String>> rows) {
			this.rows = rows;
		}

		ResultSet proxy() {
			return this.proxy;
		}
	}

	private static Object defaultValue(Method method, Object proxy, Object handler, Object[] args) {
		String name = method.getName();
		if ("toString".equals(name)) {
			return handler.getClass().getSimpleName();
		}
		if ("hashCode".equals(name)) {
			return Integer.valueOf(System.identityHashCode(proxy));
		}
		if ("equals".equals(name)) {
			return Boolean.valueOf(proxy == args[0]);
		}

		Class<?> returnType = method.getReturnType();
		if (!returnType.isPrimitive()) {
			return null;
		}
		if (boolean.class.equals(returnType)) {
			return Boolean.FALSE;
		}
		if (byte.class.equals(returnType)) {
			return Byte.valueOf((byte) 0);
		}
		if (short.class.equals(returnType)) {
			return Short.valueOf((short) 0);
		}
		if (int.class.equals(returnType)) {
			return Integer.valueOf(0);
		}
		if (long.class.equals(returnType)) {
			return Long.valueOf(0L);
		}
		if (float.class.equals(returnType)) {
			return Float.valueOf(0F);
		}
		if (double.class.equals(returnType)) {
			return Double.valueOf(0D);
		}
		if (char.class.equals(returnType)) {
			return Character.valueOf('\0');
		}
		return null;
	}
}
