package bihaiko.util.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionHandler implements InvocationHandler {

	private Connection _skeleton;
	private final Logger _logger;

	public ConnectionHandler(Driver targetDriver, Logger logger, String url, Properties info) throws SQLException {
		_logger = logger;
		_skeleton = targetDriver.connect(url, info);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();
		
		if(name.equals("createStatement")) return createStatement(proxy, method, args);
		if(name.equals("prepareStatement")) return prepareStatement(proxy, method, args);
		
		return method.invoke(_skeleton, args);
	}

	private PreparedStatement prepareStatement(Object proxy, Method method, Object[] args) throws SQLException {
		
		PreparedStatement pstatement;
		try {
			pstatement = (PreparedStatement) method.invoke(_skeleton, args);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
		return (PreparedStatement) Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[]{PreparedStatement.class}, 
				new PreparedStatementHandler(pstatement, _logger, ""+args[0]));
	}

	private Statement createStatement(Object proxy, Method method, Object[] args) throws SQLException {
		Statement statement;
		try {
			statement = (Statement) method.invoke(_skeleton, args);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
		return (Statement) Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[]{Statement.class}, 
				new StatementHandler(statement, _logger));
	}
}
